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
package prototype.framework.base.component;

import prototype.framework.util.NeuronUtil;

/**
 *
 * @author Quintin-John Smith
 * 
 * The base component that all Neurons inherit traits from.
 */
/**
 * This is the base object that neurons and their types will be built on.
 */
public class Neuron {

    /**
     * Private Final Variables.
     */
    private final double[] INPUTS;
    private final double[] NETINPUT;

    private final double[] WEIGHTS;
    private final double[] NETWEIGHT;

    /**
     * Private variables.
     */
    private double error;
    private double output;

    private String name;

    private boolean isBias;

    /**
     * Constructor - Create the neuron with the specified number of connections
     * that feed into it from the layer above.
     *
     * @param links Number of connections into the Neuron.
     */
    public Neuron(int links) {

        this.INPUTS = new double[links];
        this.NETINPUT = new double[links];

        this.WEIGHTS = new double[links];
        this.NETWEIGHT = new double[links];

        /**
         * Initialize weights with random numbers between -1 and 1.
         */
        double min = -1.0;
        double max = 1.0;

        for (int x = 0; x < links; x++) {
            this.WEIGHTS[x] = NeuronUtil.randomDoubleRange(min, max);
        }

        /**
         * By default neurons are created as non-bias. 
         */
        this.isBias = false;
    }

    /**
     * Sets Name value of neuron. Used for reference purposes in the network map
     * by the system.
     *
     * @param name Name of neuron
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Name value of neuron
     *
     * @return Returns name
     */
    public String getName() {
        return this.name;
    }

    /**
     * This returns a list of inputs per layer above. Note these inputs have not
     * passed through the activation function, nor have the weight values been
     * applied. Hence raw values from the layer above.
     *
     * @return Returns the input values of the neuron.
     */
    public double[] getInput() {
        return INPUTS;
    }

    /**
     * Sets input value of neuron per connection from the layer above.
     *
     * @param number The connection link on neuron.
     * @param input The input value.
     */
    public void setInput(int number, double input) {
        this.INPUTS[number] = input;
    }

    /**
     * Returns the Net Input of the Neuron. This returns the net input value per
     * connection from the layer above. Note that these values have passed
     * through the activation function.
     *
     * @return Returns net input per connection
     */
    public double[] getNetInput() {

        return this.NETINPUT;

    }

    /**
     * This will return the net weights before back propagation was done.
     *
     * @return Returns the net weights.
     */
    public double[] getNetWeight() {

        return this.NETWEIGHT;
    }

    /**
     * This will return the weight value per connection linked to each neuron in
     * the layer above.
     *
     * @return Gets the weights of the neuron.
     */
    public double[] getWeight() {
        return WEIGHTS;
    }

    /**
     * Sets weight value of neuron per connection.
     *
     * @param number The connection link on neuron.
     * @param weight The weight value.
     */
    public void setWeight(int number, double weight) {
        this.WEIGHTS[number] = weight;
    }

    /**
     * This is the computed error value of the neuron, calculated during the
     * back propagation process given a target value against computed output.
     *
     * @return Computed error value of neuron.
     */
    public double getError() {
        return this.error;
    }

    /**
     * This is used to externally set the error value of the neuron, used during
     * the back propagation process.
     *
     * @param error Set error value of neuron
     */
    public void setError(double error) {
        this.error = error;
    }

    /**
     * This will set the Bias value of the Neuron.
     *
     * @param isBias Sets Bias value of neuron
     */
    public void setBias(boolean isBias) {

        double bias_value = 1.0;
        double zero = 0.0;
        
        /**
         * Neuron will always push one as the input and a weight from the layer
         * above as 1 per connection, to allows pass through, as if getting a
         * response from the layer above.
         *
         */
        if (isBias == true) {
            for (int x = 0; x < INPUTS.length; x++) {
                if (x == 0) {
                    
                    this.setInput(x, bias_value);
                    this.setWeight(x, bias_value);
                    
                } else {
                    
                    this.setInput(x, zero);
                    this.setWeight(x, zero);
                }
            }
        } 

        /**
         * Update the status of the neuron.
         */
        this.isBias = isBias;
    }

    /**
     * This returns the Bias value of the neuron.
     *
     * @return Bias value of neuron.
     */
    public boolean getBias() {
        return this.isBias;
    }

    /**
     * Returns the output of the neuron after having run through an activation
     * function. Reference: Buntine, W. and Weigend, A. (1991). Bayesian
     * Back-Propagation. 1st ed. [ebook] Palo Alto: Complex Systems (NASA),
     * pp.603-643. Available at: http://www.complex-systems.com/pdf/05-6-4.pdf
     * [Accessed 12 Nov. 2015].
     *
     * @return Returns Output after Input and Activation Function.
     */
    public double getOutput() {

        /**
         * Bias Neurons simply push a value, no input or activation function.
         */
        if (this.isBias) {
            
            this.output = this.getInput()[0];
            return this.output;
            
        } else {
            
            /**
             * Otherwise run through the Input and Activation functions and
             * return a computed value based on inputs received for the neuron.
             */
            this.output = getActivationFunction(getInputFunction());
            return this.output;
            
        }
    }

    /**
     * The final output of the neuron without running it through the activation
     * and input function again as this will be used during back propagation.
     *
     * @return Returns the final output from the neuron.
     */
    public double getFinalOutPut() {
        return this.output;
    }

    /**
     * Reference. The Back Propagation Algorithm. (n.d.). 1st ed. [ ebook]
     * Aberdeen: Robert Gordon University, pp.16-27. Available at:
     * https://www4.rgu.ac.uk/files/chapter3%20-%20bp.pdf [Accessed 8 Nov.
     * 2015].
     *
     * Mazur, M. (2015). A Step by Step Back propagation Example. [online] Matt
     * Mazur. Available at:
     * http://mattmazur.com/2015/03/17/a-step-by-step-back propagation-example/
     * [Accessed 22 Nov. 2015].
     *
     * This is the INPUTS function of the neuron. f(x) += (n-INPUTS) (n-weight)
     * for each INPUTS and weight pair.
     *
     * @return Value computed from Input Function.
     */
    private double getInputFunction() {

        double sum = 0;

        for (int num_inputs = 0; num_inputs < getInput().length; num_inputs++) {

            /**
             * Summarization function is defined as: f(x) += (n-INPUTS x
             * n-weight).
             */
            sum += (this.getInput()[num_inputs] * this.getWeight()[num_inputs]);

            /**
             * Update net weights per connection for the neuron as this will be
             * used during back propagation.
             */
            this.NETWEIGHT[num_inputs] = (this.WEIGHTS[num_inputs]);

            /**
             * Bias neurons only have one value to return.
             */
            if (this.isBias == true) {
                break;
            }
        }

        return sum;
    }

    /**
     * Reference.
     *
     * The Back Propagation Algorithm. (n.d.). 1st ed. [ ebook] Aberdeen: Robert
     * Gordon University, pp.16-27. Available at:
     * https://www4.rgu.ac.uk/files/chapter3%20-%20bp.pdf [Accessed 8 Nov.
     * 2015].
     *
     * Buntine, W. and Weigend, A. (1991). Bayesian Back-Propagation. 1st ed.
     * [ebook] Palo Alto: Complex Systems (NASA), pp.603-643. Available at:
     * http://www.complex-systems.com/pdf/05-6-4.pdf [Accessed 12 Nov. 2015].
     *
     * Mazur, M. (2015). A Step by Step Back propagation Example. [online] Matt
     * Mazur. Available at:
     * http://mattmazur.com/2015/03/17/a-step-by-step-back propagation-example/
     * [Accessed 22 Nov. 2015].
     *
     * This is the out function of the neuron. Currently a Sigmoid function but
     * can be updated to include others tanh or log.
     *
     * @return Value computed from Activate Function.
     */
    private double getActivationFunction(double value) {

        /**
         * We are using a Sigmoid Activation function for this neuron.
         */
        return 1.0 / (1.0 + Math.pow(Math.E, -value));
    }

    /**
     * This is used to calculate the error for the output neuron Computation is
     * - Error = Output (1-Output)(Target – Output)
     *
     * Returning calculated error as part of a Sigmoid function based on
     * supplied target value.
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
     * @param target Final expected target value.
     * @return Computed error.
     */
    public double calculateError(double target) {

        /**
         * Calculate MSE. 
         */
        this.error = (Math.pow(target - getOutput(), 2) * 0.5);
        
        return this.error;
    }
}
