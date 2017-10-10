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
package prototype.framework.components;

import prototype.framework.base.component.Neuron;
import prototype.framework.util.NeuronUtil;

/**
 *
 * @author Quintin-John Smith
 * 
 * This class represents a Hidden Neuron Layer component.
 */
public class HiddenNeuron extends Neuron {

    private final double[] delta;

    /**
     * Each Hidden neuron can receive input from n-number of neurons in the
     * layer above it.
     *
     * @param links Number of connections into the Neuron.
     */
    public HiddenNeuron(int links) {
        super(links);
        delta = new double[links];
    }

    /**
     * Return calculated errors for each input connection.
     *
     * @return Returns Neuron Errors.
     */
    public double[] getErrors() {
        return delta;
    }

    /**
     * Reference.
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
     * We are going to use a similar process as we did for the output layer, but
     * slightly different to account for thr fact that the output of each neuron
     * in the hidden layer direcyly contributes to the output and error of the
     * neurons directly in the layer below it.
     *
     * We know that the output of all neurons in the layer below are related to
     * the contribution of this neuron and this needs to be taken into
     * consideration when calculating updated weights.
     *
     * @param target Final Target value of network.
     * @param output Output value of neuron.
     * @param weight Weight value of connection.
     *
     * @return New Error.
     */
    public double calculateError(double[] target, double[] weight, double[] output) {

        double[] error = new double[target.length];
        double[] derivative = new double[target.length];

        double delta = 0;

        for (int x = 0; x < target.length; x++) {

            /**
             * Calculate error change.
             */
            error[x] = -(target[x] - output[x]);

            /**
             * Get the derivative of the output from the layer above.
             */
            derivative[x] = output[x] * (1 - output[x]);

            /**
             * Sum up total effect of neuron to neurons below the chain.
             */
            delta += ((error[x] * derivative[x]) * weight[x]);

        }

        /**
         * Set the error for the next level.
         */
        super.setError(delta);
        return super.getError();
    }

    /**
     * Reference.
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
     * Change the weight for each input. Let W + n-input be the new (trained)
     * weight and WAB be the initial weight.
     *
     * W+AB = WAB + (ErrorB x OutputA) Notice that it is the output of the
     * connecting neuron. We update all the weights in the hidden layer in this
     * way.
     *
     * @param gradient The computed gradient that must be applied to weights.
     * @param learningRate Learning rate of system (given by user).
     * @param momentum This is the momentum of the algorithm.
     */
    public void updateWeights(double gradient, double learningRate, double momentum) {

        /**
         * Weight from output neuron in layer below to hidden n-neuron one
         * weight per connection.
         */
        double[] input = super.getInput();
        double[] weight = super.getWeight();

        /**
         * Output of the connecting n-neuron is input to this neuron.
         */
        double output = super.getOutput();

        /**
         * Note that n-input below is the output from the neuron in the layer
         * above.
         */
        for (int i = 0; i < weight.length; i++) {

            double delta = (gradient * (output * (1 - output)) * input[i]);

            super.setWeight(i, (weight[i] - (learningRate * momentum) * delta));
        }
    }

    /**
     * This will set the Bias value of the Neuron.
     *
     * @param isBias Sets Bias value of neuron
     */
    @Override
    public void setBias(boolean isBias) {

        if (isBias == false) {
            /**
             * Initialize weights with random numbers between -1 and 1.
             */
            double min = -1.0;
            double max = 1.0;

            for (int x = 0; x < this.getWeight().length; x++) {
                super.setWeight(x, NeuronUtil.randomDoubleRange(min, max));
            }
        }
        
        super.setBias(isBias);
    }
}
