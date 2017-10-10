/**
 * A DISSERTATION IT Artifact
 *
 * Submitted to The University of Liverpool in partial fulfillment of the requirements
 *
 * for the degree of MASTER OF SCIENCE
 *
 * I hereby certify that this dissertation constitutes my own product,
 * that where the language of others is set forth, quotation marks so indicate,
 * and that appropriate credit is given where I have used the language,
 * ideas, expressions, or writings of another.
 *
 * I declare that the dissertation describes original work that has not previously
 * been presented for the award of any other degree of any institution.
 */
package prototype.framework.processing;

import java.util.Map;
import javax.swing.event.EventListenerList;

import prototype.framework.util.NeuronUtil;

/**
 *
 * @author Quintin-John Smith
 *
 * This class is used to perform back propagation on the neural network during
 * supervised on-line training and will adjust weights based on the gradient
 * descent method. Weights are updated during each cycle after each pattern has
 * been passed.
 */
public class BackPropagation implements IProcessingEvent {

    /**
     * private variables
     */
    final protected EventListenerList listenerList;

    /* Constructor.*/
    public BackPropagation() {
        
        listenerList = new EventListenerList();
    }

    /**
     * Add event
     *
     * @param listener External event subscriber.
     */
    public void addProcessingListener(IProcessingEvent listener) {
        listenerList.add(IProcessingEvent.class, listener);
    }

    /**
     * Remove event
     *
     * @param listener External event subscriber.
     */
    public void removeProcessingListener(IProcessingEvent listener) {
        listenerList.remove(IProcessingEvent.class, listener);
    }

    /**
     * broadcast debug info event to listeners
     *
     * @param message The message passed from the system that will be broadcast
     * to the listeners subscribed.
     */
    @Override
    public void debugInfo(String message) {
        
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == IProcessingEvent.class) {
                ((IProcessingEvent) listeners[i + 1]).debugInfo(message);
            }
        }
    }

    /**
     * This is used to update and adjust the neurons within the network at each
     * layer to correct for errors between the actual result and the target
     * expected result.
     *
     * The expected target values are passed in to this method as parameters and
     * is then updated with small weight changes, for this training iteration.
     *
     * This method also applied momentum, to ensure that the NeuralNetwork is
     * nurtured into proceeding in the correct direction, as we are trying to
     * avoid valleys local minima.
     *
     * NB !! *** This method we can train a network of any number of layers.
     *
     * Reference: Rojas, R. (1996). Neural Networks. 1st ed. [ebook] Berlin:
     * Springer-Verlag, pp.152-184. Available at:
     * http://page.mi.fu-berlin.de/rojas/neural/chapter/K7.pdf [Accessed 8 Nov.
     * 2015].
     *
     * The Back Propagation Algorithm. (n.d.). 1st ed. [ ebook] Aberdeen: Robert
     * Gordon University, pp.16-27. Available at:
     * https://www4.rgu.ac.uk/files/chapter3%20-%20bp.pdf [Accessed 8 Nov.
     * 2015].
     *
     * Mazur, M. (2015). A Step by Step Backpropagation Example. [online] Matt
     * Mazur. Available at:
     * http://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
     * [Accessed 22 Nov. 2015].
     *
     * Otair, M. and Salameh, W. (2005). Speeding Up Back-Propagation Neural
     * Networks. 1st ed. [ebook] http://proceedings.informingscience.org,
     * pp.168-173. Available at:
     * http://proceedings.informingscience.org/InSITE2005/P106Otai.pdf [Accessed
     * 9 Nov. 2015].
     *
     * Optical Backpropagation (OBP) - to speed up convergence rate.
     *
     * The steps of an OBP: 1. Apply the input example to the input units. 2.
     * Calculate the net-input values to the hidden layer units. 3. Calculate
     * the outputs from the hidden layer. 4. Calculate the net-input values to
     * the output layer units. 5. Calculate the outputs from the output units.
     * 6. Calculate the error term for the output units, but replace Newδ ̊pk
     * with δ ̊pk. (in all equations in appendix).
     *
     * 7. Calculate the error term for the output units, using Newδ ̊pk, also.
     * 8. Update weights on the output layer. 9. Update weights on the hidden
     * layer. 10. Repeat steps from step 1 to step 9 until the error (Ypk – Opk)
     * is acceptably small for each training vector pairs.
     *
     * @param matrix Matrix of Neural Network.
     * @param target Expected Target Values
     * @param network Map of neural network.
     * @param learningRate Learning Rate of Network.
     * @param momentum Moment rate used in training the network.
     * @return Returns the calculated Error for the Network.
     */
    public double[] start(int matrix[], double[] target,
            Map<String, Object> network, double learningRate, double momentum) {

        /**
         * Will push this info to whoever is listening on the debugInfo channel.
         */
        debugInfo("*** Back Propagation Start ***");
        
        double[] outputError = null;

        /**
         * Now we are working backwards from output neurons to input neurons.
         */
        for (int layers = (matrix.length - 1); layers > -1; layers--) {
            
            if (layers == (matrix.length - 1)) {

                /**
                 * Calculate Error from Output Layer.
                 */
                outputError = updateOutputLayer(matrix, layers, target, network,
                        learningRate, momentum);
                
            } else if (layers == (matrix.length - 2)) {

                /**
                 * Calculate Error Output -> Hidden Layer.
                 */
                updateOutputToHiddenLayer(matrix, layers,
                        network, learningRate, target, momentum);
                
            } else {

                /**
                 * Calculate Hidden -> Hidden Layer.
                 */
                updateHiddenToHiddenLayerError(matrix, layers,
                        network, learningRate, target, momentum);
            }
        }

        /**
         * Will push this info to whoever is listening on the debugInfo channel.
         */
        debugInfo("*** Back Propagation End ***");
        
        return outputError;
    }

    /**
     * This updates each Neuron in the output layer with new calculated weights
     * based on the target value provided.
     *
     * @param matrix Contains an array of the network matrix.
     * @param layers The current layer of the network we are dealing with.
     * @param target An array of target values.
     * @param network Map of the neural network containing all objects.
     * @param learningRate The defined learning rate used to train the network.
     * @param momentum The defined momentum rate of the neural network.
     */
    private double[] updateOutputLayer(int matrix[], int layers, double target[],
            Map<String, Object> network, double learningRate, double momentum) {
        
        double delta[] = new double[matrix[layers]];
        
        for (int neurons = 0; neurons < matrix[layers]; neurons++) {

            /**
             * Get neuron name in output layer.
             */
            String name = "c-" + (neurons + 1);
            if (network.get(name) != null) {

                /**
                 * Get the delta value for the output layer.
                 */
                delta[neurons] = NeuronUtil.calculateNeuronError(network.get(name), target[neurons]);

                /**
                 * Will push this info to whoever is listening on the debugInfo
                 * channel.
                 */
                debugInfo("Calculated error for neuron "
                        + NeuronUtil.getNeuronName(network.get(name))
                        + " is {" + delta[neurons] + "}");

                /**
                 * Update weights for neuron.
                 */
                NeuronUtil.updateNeuronWeights(
                        network.get(name), target[neurons], learningRate, momentum);

                /**
                 * Will push this info to whoever is listening on the debugInfo
                 * channel.
                 */
                debugInfo("Updating all weights on neuron "
                        + NeuronUtil.getNeuronName(network.get(name)));
            }
        }
        
        return delta;
    }

    /**
     * Back Propagate from Output Layer.
     *
     * Reference:
     *
     * Rojas, R. (1996). Neural Networks. 1st ed. [ebook] Berlin:
     * Springer-Verlag, pp.152-184. Available at:
     * http://page.mi.fu-berlin.de/rojas/neural/chapter/K7.pdf [Accessed 8 Nov.
     * 2015].
     *
     * The Back Propagation Algorithm. (n.d.). 1st ed. [ ebook] Aberdeen: Robert
     * Gordon University, pp.16-27. Available at:
     * https://www4.rgu.ac.uk/files/chapter3%20-%20bp.pdf [Accessed 8 Nov.
     * 2015].
     *
     * Mazur, M. (2015). A Step by Step Backpropagation Example. [online] Matt
     * Mazur. Available at:
     * http://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
     * [Accessed 22 Nov. 2015].
     *
     * For all hidden layers For every node in the layer 1. Calculate the node's
     * signal error 2. Update each node's weight in the network This updates
     * each Neuron in the first hidden layer with new calculated weights derived
     * from the output layer based on original target values provided.
     *
     * @param matrix Contains an array of the network matrix.
     * @param layers The current layer of the network we are dealing with.
     * @param target An array of target values.
     * @param network Map of the neural network containing all objects.
     * @param learningRate The defined learning rate used to train the network.
     * @param momentum The defined momentum rate of the neural network. private
     */
    double updateOutputToHiddenLayer(int matrix[], int layers,
            Map<String, Object> network, double learningRate, double target[], double momentum) {
        
        double error = 0;
        
        double neuronOutput[] = new double[matrix[layers + 1]];
        double neuronWeight[] = new double[matrix[layers + 1]];
        
        for (int neurons = 0; neurons < matrix[layers]; neurons++) {

            /**
             * get neuron name in hidden layer.
             */
            String name = "b-" + (neurons + 1) + "-" + layers;
            
            if (network.get(name) != null) {

                /**
                 * Will push this info to whoever is listening on the debugInfo
                 * channel.
                 */
                debugInfo("Computing weights for neuron "
                        + NeuronUtil.getNeuronName(network.get(name)));

                /**
                 * Get the sum of all connecting neurons affected by this
                 * neuron.
                 */
                getConnectingLayer(matrix, network, neuronOutput, neuronWeight, neurons, layers);

                /**
                 * Get error.
                 */
                error = NeuronUtil.calculateNeuronError(network.get(name),
                        target, neuronWeight, neuronOutput);

                /**
                 * Parse error.
                 */
                NeuronUtil.updateNeuronWeights(
                        network.get(name), error, learningRate, momentum);

                /**
                 * Will push this info to whoever is listening on the debugInfo
                 * channel.
                 */
                debugInfo("Calculated error for neuron "
                        + NeuronUtil.getNeuronName(network.get(name))
                        + " is {" + error + "}");
            }
        }

        /**
         * Return error for level.
         */
        return error;
    }

    /**
     * Back Propagate from Hidden Layer. * Reference:
     *
     * Rojas, R. (1996). Neural Networks. 1st ed. [ebook] Berlin:
     * Springer-Verlag, pp.152-184. Available at:
     * http://page.mi.fu-berlin.de/rojas/neural/chapter/K7.pdf [Accessed 8 Nov.
     * 2015].
     *
     * The Back Propagation Algorithm. (n.d.). 1st ed. [ ebook] Aberdeen: Robert
     * Gordon University, pp.16-27. Available at:
     * https://www4.rgu.ac.uk/files/chapter3%20-%20bp.pdf [Accessed 8 Nov.
     * 2015].
     *
     * Mazur, M. (2015). A Step by Step Backpropagation Example. [online] Matt
     * Mazur. Available at:
     * http://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
     * [Accessed 22 Nov. 2015].
     *
     * For all hidden layers For every node in the layer 1. Calculate the node's
     * signal error 2. Update each node's weight in the network.
     *
     * This updates each Neuron in the n-hidden layer with new calculated
     * weights derived from the hidden layer (n+1)-hidden layer based on
     * original target values provided to the output layer.
     *
     * @param matrix Contains an array of the network matrix.
     * @param layers The current layer of the network we are dealing with.
     * @param target An array of target values.
     * @param network Map of the neural network containing all objects.
     * @param learningRate The defined learning rate used to train the network.
     * @param momentum The defined momentum rate of the neural network. private
     */
    private double updateHiddenToHiddenLayerError(int matrix[], int layers,
            Map<String, Object> network, double learningRate, double target[], double momentum) {
        
        double error = 0;
        
        double neuronOutput[] = new double[matrix[layers + 1]];
        double neuronWeight[] = new double[matrix[layers + 1]];
        
        for (int neurons = 0; neurons < matrix[layers]; neurons++) {

            /**
             * Get neuron name in hidden layer.
             */
            String name = "b-" + (neurons + 1) + "-" + layers;
            
            if (network.get(name) != null) {

                /**
                 * Skip Bias Neurons as they are not connected to anything
                 * above.
                 */
                if (NeuronUtil.getNeuronBias(network.get(name))) {
                    debugInfo("Neuron "
                            + NeuronUtil.getNeuronName(network.get(name))
                            + " is Bias - Skipping.");
                    
                    continue;
                    
                }

                /**
                 * Will push this info to whoever is listening on the debugInfo
                 * channel.
                 */
                debugInfo("Computing weights for neuron " 
                        + NeuronUtil.getNeuronName(network.get(name)));

                /**
                 * Get the sum of all connecting neurons affected by this
                 * neuron.
                 */
                getConnectingLayer(matrix, network, neuronOutput, neuronWeight, neurons, layers);

                /**
                 * Get error. 
                 */
                error = NeuronUtil.calculateNeuronError(
                        network.get(name), target, neuronWeight, neuronOutput);


                /** 
                 * Parse error. 
                 */
                NeuronUtil.updateNeuronWeights(
                        network.get(name), error, learningRate, momentum);

                /**
                 * Will push this info to whoever is listening on the debugInfo
                 * channel.
                 */
                debugInfo("New weight values {"
                        + error
                        + "} updated for neuron "
                        + NeuronUtil.getNeuronName(network.get(name)));
            }
        }

        /**
         * Return error for layer.
         */
        return error;
    }

    /**
     * This will return the details of the hidden layer below that are affected
     * by the neuron in the layer above.
     *
     * @param matrix Matrix of Neural Network.
     * @param network Map of the network.
     * @param output Combined outputs from layer below.
     * @param weight Combined weights from the layer below.
     * @param position The position within the network matrix
     * @param layers The layer of the network that we are dealing with.
     */
    private void getConnectingLayer(int matrix[], Map<String, Object> network,
            double output[], double weight[], int position, int layers) {
        
        for (int neuron = 0; neuron < (matrix[layers + 1]); neuron++) {
            
            String name;
            
            if ((layers + 1) == (matrix.length - 1)) {

                /**
                 * get output layer neuron.
                 */
                name = "c-" + (neuron + 1);

                /**
                 * Does the neuron exist? 
                 */
                if (network.get(name) != null) {

                    /**
                     * Get original output from the neuron.
                     */
                    output[neuron] = NeuronUtil.getNeuronFinalOutPut(network.get(name));

                    /**
                     * Get original weights from the neuron.
                     */
                    double[] weights = NeuronUtil.getNeuronNetWeight(network.get(name));
                    weight[neuron] = weights[position];

                    /**
                     * Will push this info to whoever is listening on the
                     * debugInfo channel.
                     */
                    debugInfo("Getting info from neuron "
                            + NeuronUtil.getNeuronName(network.get(name)));
                    
                }
                
            } else {
                
                /**
                 * get hidden layer neuron.
                 */
                name = "b-" + (neuron + 1) + "-" + (layers + 1);

                /**
                 * Does the neuron exist? 
                 */
                if (network.get(name) != null) {
                    
                    if (NeuronUtil.getNeuronBias(network.get(name))) {
                        debugInfo("Neuron "
                                + NeuronUtil.getNeuronName(network.get(name))
                                + " is Bias - Skipping.");
                        continue;
                    }

                    /**
                     * Get original output from the neuron.
                     */
                    output[neuron] = NeuronUtil.getNeuronFinalOutPut(network.get(name));

                    /**
                     * Get original weights from the neuron.
                     */
                    double[] weights = NeuronUtil.getNeuronNetWeight(network.get(name));
                    weight[neuron] = weights[position];

                    /**
                     * Will push this info to whoever is listening on the
                     * debugInfo channel.
                     */
                    debugInfo("Getting info from neuron "
                            + NeuronUtil.getNeuronName(network.get(name)));
                }
            }
        }
    }

    /**
     * Appendinx A
     *
     * Assume there are m input units, n hidden units, and p output units.
     *
     * 1. Apply the input vector, Xp=(Xp1 , Xp2 , Xp3 ,..... , XpN ) to the
     * input units. 2. Calculate the net- input values to the hidden layer
     * units: nethpj=(NWhji•X ) i∑=1 pi. 3. Calculate the outputs from the
     * hidden layer: ipj = fhj(nethpj). 4. Move to the output layer. Calculate
     * the net-input values to each unit: netOpk=(LWO •i ) ∑ kjpj j =1. 5.
     * Calculate the outputs: Opk = foj(netopk) 6. Calculate the error terms for
     * the output units: δopk =(Ypk −Opk)• fo′k(netopk) Where, fo′k(netopk)=
     * fok(netopk)•(1− fok(netopk)) 7. Calculate the error terms for the hidden
     * units: δhpj = fh′j(nethpj)•(∑M δopk •Wokj) K=1
     *
     * Notice that the error terms on the hidden units are calculated before the
     * connection weights to the output-layer units have been updated.
     *
     * 8. Update weights on the output layer Wokj(t+1) =Wokj(t) +(η•δopk •ipj)
     * 9. Update weights on the Hidden layer Whji(t+1) =Whji(t) +(η•δhpj •Xi)
     *
     */
}
