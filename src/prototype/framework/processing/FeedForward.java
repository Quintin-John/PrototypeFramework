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
 * This class is used to perform a standard feed forward function within the
 * neural network, making use of the standard summing function and activation
 * function within each neuron before passing the values forward.
 */
public class FeedForward implements IProcessingEvent {

    /**
     * private variables
     */
    final protected EventListenerList listenerList;

    /**
     * Constructor
     */
    public FeedForward() {
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
     * Start the feed forward using current set weights and return computed
     * result.
     *
     * @param matrix Provide a matrix of the network as an array
     * @param vectors Input vectors for feed forward
     * @param network Map of the neural network.
     * @return Returns computed results.
     */
    public double[] start(int matrix[], double[] vectors, Map<String, Object> network) {

        /**
         * How many Layers do we have in the neural network?
         */
        for (int layers = 0; layers < matrix.length; layers++) {

            /**
             * How many neurons do we have within each layer?
             */
            for (int neurons = 0; neurons < matrix[layers]; neurons++) {

                if (layers == 0) {

                    /**
                     * Input Neurons - Load vectors.
                     */
                    pushInput(vectors, network, neurons);

                } else if (layers == (matrix.length - 1)) {

                    /**
                     * This is the output layer.
                     */
                    pushOutput(matrix, network, neurons, layers);

                } else {

                    /**
                     * n-Amount of Hidden layers.
                     */
                    pushHidden(matrix, network, neurons, layers);
                }
            }
        }

        /**
         * Pull final output from network.
         */
        return pullResult(matrix, network);
    }

    /**
     * Feeding vectors received into the input layer passing vector information
     * to each neuron in the input layer.
     *
     * @param vectors Input vectors for feed forward
     * @param network Map of the neural network.
     * @param position Position of Neuron in the network.
     */
    private void pushInput(double[] vectors, Map<String, Object> network,
            int position) {

        String name = "a-" + (position + 1);
        int pos;

        /**
         * ## Bug Fix ## Bug to correct the fact that bias neurons in the input
         * layer are not always the last in the network layer. This will check
         * if the bias value is set to false and then remove one value at a time
         * from the input vectors until all values have been parsed.
         */
        for (pos = 0; pos < vectors.length; pos++) {
            if (!Double.isNaN(vectors[pos])) {
                break;
            }
        }

        /**
         * See if the neuron exists within the network matrix.
         */
        if (network.containsKey(name)) {

            boolean bias = NeuronUtil.getNeuronBias(network.get(name));

            /**
             * Don't push to bias neurons.
             */
            if (bias != true) {

                /**
                 * Push output to the next level.
                 */
                NeuronUtil.setNeuronInput(network.get(name), 0, vectors[pos]);

                /**
                 * Will push this info to whoever is listening on the debugInfo
                 * channel.
                 */
                debugInfo("Setting input vectors "
                        + NeuronUtil.getNeuronName(network.get(name))
                        + " " + vectors[pos]);

                /**
                 * Set the Vector to Nan once used.
                 */
                vectors[pos] = Double.NaN;

            } else {

                /**
                 * Push output to the next level.
                 */
                NeuronUtil.setNeuronInput(network.get(name), 0, 1);

                /**
                 * Will push this info to whoever is listening on the debugInfo
                 * channel.
                 */
                debugInfo("Setting Bias "
                        + NeuronUtil.getNeuronName(network.get(name))
                );
            }

        }
    }

    /**
     * Push data from n-hidden layer or input layer.
     *
     * @param matrix Provide a matrix of the network as an array
     * @param network Map of the neural network.
     * @param neuron Position of neuron in the network.
     * @param layers Position of current layer in the network.
     */
    private void pushHidden(int matrix[], Map<String, Object> network,
            int neuron, int layers) {

        String name_hidden = "b-" + (neuron + 1) + "-" + layers;

        if (network.containsKey(name_hidden)) {

            /**
             * Don't push to Bias Neurons.
             */
            if (NeuronUtil.getNeuronBias(network.get(name_hidden))) {
                return;
            }

            for (int neurons = 0; neurons < matrix[layers - 1]; neurons++) {
                if (layers == 1) {

                    /**
                     * We are passing from the input to hidden.
                     */
                    String name_input = "a-" + (neurons + 1);

                    if (network.containsKey(name_input)) {

                        /**
                         * Don't push to Bias Neurons.
                         */
                        if (NeuronUtil.getNeuronBias(network.get(name_hidden)) != true) {

                            /**
                             * Push output to the next level.
                             */
                            NeuronUtil.setNeuronInput(network.get(name_hidden),
                                    neurons,
                                    NeuronUtil.getNeuronOutput(network.get(name_input))
                            );

                            /**
                             * Will push this info to whoever is listening on
                             * the debugInfo channel.
                             */
                            debugInfo("Pushing "
                                    + NeuronUtil.getNeuronName(network.get(name_input)) + " -> "
                                    + NeuronUtil.getNeuronName(network.get(name_hidden)) + " -{"
                                    + NeuronUtil.getNeuronOutput(network.get(name_input)) + "}");

                        }
                    }

                } else {

                    /**
                     * We are passing from the hidden to hidden.
                     */
                    String name_input = "b-" + (neurons + 1) + "-" + (layers - 1);

                    /* Are we passing from the hidden to hidden?*/
                    if (network.containsKey(name_input)) {

                        /**
                         * Push output to the next level.
                         */
                        NeuronUtil.setNeuronInput(network.get(name_hidden),
                                neurons,
                                NeuronUtil.getNeuronOutput(network.get(name_input))
                        );

                        /**
                         * Will push this info to whoever is listening on the
                         * debugInfo channel.
                         */
                        debugInfo("Pushing "
                                + NeuronUtil.getNeuronName(network.get(name_input)) + " -> "
                                + NeuronUtil.getNeuronName(network.get(name_hidden)) + " - {"
                                + NeuronUtil.getNeuronOutput(network.get(name_input)) + "}");

                    }
                }
            }
        }
    }

    /**
     * Push to output layer
     *
     * @param matrix Provide a matrix of the network as an array
     * @param network Map of the neural network.
     * @param neuron Position of neuron in the network.
     * @param layers Position of current layer in the network.
     */
    private void pushOutput(int matrix[], Map<String, Object> network,
            int neuron, int layers) {

        String name_out = "c-" + (neuron + 1);

        /**
         * Get the output neuron we are pulling data from.
         */
        if (network.containsKey(name_out)) {

            for (int neurons = 0; neurons < matrix[layers - 1]; neurons++) {

                String name_hidden = "b-" + (neurons + 1) + "-" + (layers - 1);

                if (network.containsKey(name_hidden)) {

                    /**
                     * Push output to the next level.
                     */
                    NeuronUtil.setNeuronInput(network.get(name_out),
                            neurons,
                            NeuronUtil.getNeuronOutput(network.get(name_hidden))
                    );

                    /**
                     * Will push this info to whoever is listening on the
                     * debugInfo channel.
                     */
                    debugInfo("Pushing "
                            + NeuronUtil.getNeuronName(network.get(name_hidden)) + " -> "
                            + NeuronUtil.getNeuronName(network.get(name_out)) + " - {"
                            + NeuronUtil.getNeuronOutput(network.get(name_hidden)) + "}");

                }
            }
        }

    }

    /**
     * Pull computation from final layer
     *
     * @param matrix Provide a matrix of the network as an array
     * @param network Map of the neural network.
     * @param neuron Position of neuron in the network.
     * @return Computed result
     */
    private double[] pullResult(int matrix[], Map<String, Object> network) {

        double result[] = new double[matrix[matrix.length - 1]];

        for (int layer = 0; layer < matrix[matrix.length - 1]; layer++) {

            /**
             * This is the Output Layer.
             */
            String name = "c-" + (layer + 1);

            /**
             * Check if the Neuron exists.
             */
            if (network.containsKey(name)) {

                /**
                 * Pull result from output neuron.
                 */
                result[layer] = NeuronUtil.getNeuronOutput(network.get(name));

                /**
                 * Will push this info to whomever is listening on the debugInfo
                 * channel.
                 */
                debugInfo("Final result from "
                        + NeuronUtil.getNeuronName(network.get(name))
                        + " = " + result[layer]);

            }
        }

        /**
         * Final computed output from network.
         */
        return result;
    }

}
