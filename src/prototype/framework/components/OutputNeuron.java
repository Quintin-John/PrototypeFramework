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

/**
 * @author Quintin-John Smith
 *
 * This class represents a Output Neuron Layer component.
 */
public class OutputNeuron extends Neuron {

    /**
     * Each Output neuron can receive input from n-number of neurons in the
     * layer above it.
     *
     * @param links Number of connections from layer above.
     */
    public OutputNeuron(int links) {
        super(links);
    }

    /**
     * This returns the Bias value of the neuron. Output neurons can never be
     * bias.
     *
     * @return Bias value of neuron.
     */
    @Override
    public boolean getBias() {
        return false;
    }

    /**
     * Change the weight for each input. Let W + n-input be the new (trained)
     * weight and WAB be the initian weight.
     *
     * Error = -(target - output) * (output * (1 - output)) * input above
     * neuron. New weight value = (weight value - learning rate) * Error.
     *
     * Notice that it is the output of the connecting neuron. We update all the
     * weights in the output layer in this way.
     *
     * Reference:
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
     * @param target Expected Target value.
     * @param learningRate Learning rate of network.
     * @param momentum This is the momentum of the algoritm.
     */
    public void updateWeights(double target, double learningRate, double momentum) {

        /**
         * Weight from output neuron to hidden n-neuron one weight per
         * connection.
         */
        double[] weight = super.getWeight();

        /**
         * Output of the connecting n-neuron is input to this neuron.
         */
        double[] input = super.getInput();
        double output = super.getOutput();

        /**
         * Update weight per n-connection. 
         */
        for (int i = 0; i < weight.length; i++) {

            /**
             * Note that n-input below is the output from the neuron in the
             * layer above.
             *
             * Delta rule: -(target - output-neuron) x out(1-out-neuron) x
             * output-connection
             *
             */
            double delta = -(target - output) * (output * (1 - output) * input[i]);

            /**
             * To decrease the error we subtract the delta from the current
             * weight (optionally - multiplied by a learning rate.)
             */
            super.setWeight(i, (weight[i] - (learningRate * (momentum * delta))));
        }
    }
}
