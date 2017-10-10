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
package prototype.framework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 *
 * @author Quintin-John Smith
 *
 * This class contains common functions that will be used by the neural network
 * to communicate and exchange information with Neurons..
 */
public class NeuronUtil {

    /**
     * This is used to generate random numbers in a specific range.
     *
     * @param min The minimum of the range value.
     * @param max The maximum of the range value.
     * @return Returns random number generated in range.
     */
    public static double randomDoubleRange(double min, double max) {

        Random random = new Random();

        double range = max - min;
        double scaled = random.nextDouble() * range;

        return scaled + min;
    }

    /**
     * This returns the Bias value of the neuron.
     *
     * @param neuron Neuron Object
     * @return Bias value of neuron.
     */
    @SuppressWarnings("unchecked")
    public static boolean getNeuronBias(Object neuron) {

        Class aClass = neuron.getClass();

        /**
         * We know the method is getBias, so get it and invoke it.
         */
        Method method = null;
        try {

            method = aClass.getMethod("getBias");

        } catch (NoSuchMethodException | SecurityException ex) {

        }

        try {

            return (boolean) method.invoke(neuron);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return false;
        }
    }

    /**
     * This sets the Bias value of the neuron.
     *
     * @param neuron Neuron Object
     * @param bias Bias value to be set.
     * @return True if success.
     */
    @SuppressWarnings("unchecked")
    public static boolean setNeuronBias(Object neuron, boolean bias) {

        Object[] params = new Object[1];
        Class aClass = neuron.getClass();

        /**
         * We know the method is getBias, so get it and invoke it.
         */
        Method method = null;
        try {

            method = aClass.getMethod("setBias", boolean.class);

        } catch (NoSuchMethodException | SecurityException ex) {

        }

        try {
            
            params[0] = bias;
            method.invoke(neuron, params);
            return true;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return false;
        }
    }

    /**
     * Sets input value of neuron per connection from the layer above.
     *
     * @param neuron Neuron object.
     * @param position The connection link on neuron.
     * @param vector The input value.
     * @return Returns true if update succeeded.
     */
    @SuppressWarnings("unchecked")
    public static boolean setNeuronInput(Object neuron, int position, double vector) {

        Method method = null;
        Object[] params = new Object[2];

        Class aClass = neuron.getClass();
        /**
         * We know the method is setInput, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("setInput", int.class, double.class);

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return false;
        }

        try {

            params[0] = position;
            params[1] = vector;
            method.invoke(neuron, params);

            return true;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return false;
        }
    }

    /**
     * Gets the Name value of neuron
     *
     * @param neuron Neuron object.
     * @return Returns name
     */
    @SuppressWarnings("unchecked")
    public static String getNeuronName(Object neuron) {

        Method method = null;
        Class aClass = neuron.getClass();
        /**
         * We know the method is getName, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("getName");

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return null;
        }

        try {

            return (String) method.invoke(neuron);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return null;
        }
    }

    /**
     * Returns the output of the neuron after having run through an activation
     * function. Reference: Buntine, W. and Weigend, A. (1991). Bayesian
     * Back-Propagation. 1st ed. [ebook] Palo Alto: Complex Systems (NASA),
     * pp.603-643. Available at: http://www.complex-systems.com/pdf/05-6-4.pdf
     * [Accessed 12 Nov. 2015].
     *
     * @param neuron Neuron object.
     * @return Returns Output after Input and Activation Function.
     */
    @SuppressWarnings("unchecked")
    public static double getNeuronOutput(Object neuron) {
        Method method = null;
        Class aClass = neuron.getClass();
        /**
         * We know the method is getOutput, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("getOutput");

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return Double.NaN;
        }

        try {

            return (double) method.invoke(neuron);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return Double.NaN;
        }
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
     * @param neuron Neuron object.
     * @param target The computed gradient that must be applied to weights.
     * @param learningRate Learning rate of system (given by user).
     * @param momentum This is the momentum of the algorithm.
     * @return Returns true is successful.
     */
    @SuppressWarnings("unchecked")
    public static boolean updateNeuronWeights(Object neuron,
            double target, double learningRate, double momentum) {

        Method method = null;
        Object[] params = new Object[3];

        Class aClass = neuron.getClass();
        /**
         * We know the method is updateWeights, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("updateWeights", double.class, double.class, double.class);

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return false;
            
        }

        try {

            params[0] = target;
            params[1] = learningRate;
            params[2] = momentum;
            method.invoke(neuron, params);

            return true;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return false;
        }

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
     * @param neuron Neuron object.
     * @param target Final expected target value.
     * @return Computed error.
     */
    @SuppressWarnings("unchecked")
    public static double calculateNeuronError(Object neuron, double target) {

        Method method = null;
        Object[] params = new Object[1];

        Class aClass = neuron.getClass();
        /**
         * We know the method is calculateError, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("calculateError", double.class);

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return Double.NaN;
        }

        try {

            params[0] = target;

            return (double) method.invoke(neuron, params);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return Double.NaN;
        }
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
     * @param neuron Neuron Object
     * @param target Final Target value of network.
     * @param output Output value of neuron.
     * @param weight Weight value of connection.
     *
     * @return New Error.
     */
    @SuppressWarnings("unchecked")
    public static double calculateNeuronError(Object neuron, double[] target, double[] weight, double[] output) {

        Method method = null;
        Object[] params = new Object[3];

        Class aClass = neuron.getClass();
        /**
         * We know the method is calculateError, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("calculateError", double[].class, double[].class, double[].class);

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return Double.NaN;
            
        }

        try {

            params[0] = target;
            params[1] = weight;
            params[2] = output;

            return (double) method.invoke(neuron, params);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return Double.NaN;
        }
    }

    /**
     * The final output of the neuron without running it through the activation
     * and input function again as this will be used during back propagation.
     *
     * @param neuron Neuron object
     * @return Returns the final output from the neuron.
     */
    @SuppressWarnings("unchecked")
    public static double getNeuronFinalOutPut(Object neuron) {

        Method method = null;
        Class aClass = neuron.getClass();
        /**
         * We know the method is getFinalOutPut, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("getFinalOutPut");

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return Double.NaN;
        }

        try {

            return (double) method.invoke(neuron);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return Double.NaN;
        }
    }

    /**
     * This will return the weight value per connection linked to each neuron in
     * the layer above.
     *
     * @param neuron Neuron object.
     * @return Gets the weights of the neuron.
     */
    @SuppressWarnings("unchecked")
    public static double[] getNeuronNetWeight(Object neuron) {

        Method method = null;
        Class aClass = neuron.getClass();
        
        /**
         * We know the method is getNetWeight, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("getNetWeight");

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return null;
        }

        try {

            return (double[]) method.invoke(neuron);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return null;
        }
    }

    /**
     * This will return the weight value per connection linked to each neuron in
     * the layer above.
     *
     * @param neuron Neuron object.
     * @return Gets the weights of the neuron.
     */
    @SuppressWarnings("unchecked")
    public static double[] getNeuronWeight(Object neuron) {

        Method method = null;
        Class aClass = neuron.getClass();
        
        /**
         * We know the method is getNetWeight, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("getWeight");

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return null;
        }

        try {

            return (double[]) method.invoke(neuron);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return null;
        }
    }

    /**
     * This will return the weight value per connection linked to each neuron in
     * the layer above.
     *
     * @param neuron Neuron object.
     * @param position The position in the weight matrix
     * @param value The value of the weight.
     * @return Gets the weights of the neuron.
     */
    @SuppressWarnings("unchecked")
    public static boolean setNeuronWeight(Object neuron, int position, double value) {

        Method method = null;
        Object[] params = new Object[2];
        Class aClass = neuron.getClass();
        
        /**
         * We know the method is getNetWeight, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("setWeight", int.class, double.class);

        } catch (NoSuchMethodException | SecurityException ex) {
            
            return false;
        }

        try {

            params[0] = position;
            params[1] = value;
            method.invoke(neuron, params);
            
            return true;

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

            return false;
        }
    }

    /**
     * This will set the Name of the Neuron.
     *
     * @param neuron Neuron object.
     * @param name Name of Neuron.
     */
    @SuppressWarnings("unchecked")
    public static void setNeuronName(Object neuron, String name) {

        Method method = null;
        Object[] params = new Object[1];
        Class aClass = neuron.getClass();
        /**
         * We know the method is getNetWeight, so get it and invoke it.
         */
        try {

            method = aClass.getMethod("setName", String.class);

        } catch (NoSuchMethodException | SecurityException ex) {

        }

        try {

            params[0] = name;
            method.invoke(neuron, params);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

        }
    }

}
