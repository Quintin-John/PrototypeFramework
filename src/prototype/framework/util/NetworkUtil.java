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
import java.util.Map;
import prototype.framework.base.component.NeuronType;
import prototype.framework.factory.NeuronFactory;

/**
 *
 * @author Quintin-John Smith
 *
 * This class contains a number of common Network Utility functions that will be
 * used by the neural network.
 */
public class NetworkUtil {

    /**
     * This function will create a neural network based on the matrix parameters
     * passed.
     *
     * @param matrix Provide a matrix of the network as an array
     * @param network Map of current network configuration.
     * @return System will return a Map containing network components.
     */
    public static Map<String, Object> buildNetwork(int matrix[],
            Map<String, Object> network) {

        /**
         * New network, clear the old one.
         */
        network.clear();

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
                     * Load input neurons.
                     */
                    addNeuron(NeuronType.INPUT,(neurons + 1), 0, matrix, network);

                } else if (layers == (matrix.length - 1)) {

                    /**
                     * Load output neurons.
                     */
                    addNeuron(NeuronType.OUTPUT,(neurons + 1), layers, matrix, network);

                } else {

                    /**
                     * Load hidden neuron layers.
                     */
                    addNeuron(NeuronType.HIDDEN,(neurons + 1), layers, matrix, network);

                }
            }
        }

        return network;
    }

    /**
     * This will add a Neuron to the network as well as record the type (input,
     * output and hidden) and position within the network based on parameters
     * parsed.
     *
     * @param type Neuron Type is defined by Enum NeuronType.
     * @param number Number of neuron in given network layer.
     * @param layer Layer number in neural network.
     * @param matrix Matrix of network to be created, passed as an array.
     * @param network Map of current network configuration.
     */
    private static void addNeuron(NeuronType type, int number, int layer,
            int matrix[], Map<String, Object> network) {

        /**
         * Setup class references.
         */
        NeuronFactory neuron_factory = new NeuronFactory();

        /**
         * Initialize name variable to get component by name.
         */
        String name = null;
        Object neuron = null;

        switch (type) {
            case INPUT:
                /**
                 * Input Neurons are defined as level "a".
                 */
                name = "a-" + number;

                /**
                 * Get Neuron from Neuron Factory.
                 */
                neuron = neuron_factory.InputNeuron();
                NeuronUtil.setNeuronName(neuron, name);

                /**
                 * Record the position.
                 */
                network.put(name, neuron);

                break;

            case HIDDEN:
                /**
                 * Hidden Neurons are defined as level "b" ** Note level b -
                 * multi-dimensional. ***
                 */
                name = "b-" + number + "-" + layer;

                /**
                 * Get Neuron from Neuron Factory.
                 */
                neuron = neuron_factory.HiddenNeuron(matrix[layer - 1]);
                NeuronUtil.setNeuronName(neuron, name);

                /**
                 * Record the position.
                 */
                network.put(name, neuron);

                break;

            case OUTPUT:
                /**
                 * Output Neurons are defined as level "c".
                 */
                name = "c-" + number;

                /**
                 * Get Neuron from Neuron Factory.
                 */
                neuron = neuron_factory.OutputNeuron(matrix[layer - 1]);
                NeuronUtil.setNeuronName(neuron, name);

                /**
                 * Record the position.
                 */
                network.put(name, neuron);
                break;
        }
    }

    /**
     * This is used to return the amount of Output Neurons defined in the
     * network.
     *
     * @param matrix Matrix of network to be created, passed as an array.
     * @param network Map of current network configuration.
     *
     * @return Amount of non-bias input neurons.
     */
    public static int getNumOutputNeurons(int matrix[], Map<String, Object> network) {

        int value = 0;

        /**
         * Cycle through the network map and return number of output neurons
         * defined in the network.
         */
        for (int x = 0; x < matrix[matrix.length - 1]; x++) {

            String name = "c-" + (x + 1);
            if (network.get(name) != null) {
                
                value += 1;
                
            }
        }

        return value;
    }

    /**
     * This is used to return the amount of Input Neurons that are not defined
     * as bias.
     *
     * @param matrix Matrix of network to be created, passed as an array.
     * @param network Map of current network configuration.
     *
     * @return Amount of non-bias input neurons.
     */
    @SuppressWarnings("unchecked")
    public static int getNumInuputNeurons(int matrix[], Map<String, Object> network) {

        int value = 0;
        boolean bias = false;

        for (int x = 0; x < matrix[0]; x++) {

            String name = "a-" + (x + 1);
            if (network.get(name) != null) {

                Object neuron = network.get(name);
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

                    bias = (boolean) method.invoke(neuron);

                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {

                }

                /**
                 * Exclude bias neurons from the check against input vectors.
                 */
                if (bias != true) {
                    
                    value += 1;
                    
                }
            }
        }

        return value;
    }

    /**
     * This is used to set the Parameters for each Neuron in the network
     *
     * @param network Map of current network configuration.
     * @param name Name of neuron to be updated.
     * @param values weight values of neuron to be updated.
     * @param bias Used to define is neuron is bias or not.
     * @return boolean true/false for successful update of component.
     */
    public static Boolean setNeuron(Map<String, Object> network, String name,
            double[] values, Boolean bias) {

        /**
         * Does the neuron exist in the network.
         */
        if (network.get(name) != null) {
            /**
             * Set the bias for the input neuron.
             */
            NeuronUtil.setNeuronBias(network.get(name), bias);

            /**
             * Error is the weight array doesn't match the values passed.
             */
            if (values.length
                    != NeuronUtil.getNeuronWeight(network.get(name)).length) {

                return false;
            }

            /**
             * Update each of the weights for the neuron.
             */
            for (int weight = 0;
                    weight < NeuronUtil.getNeuronWeight(network.get(name)).length;
                    weight++) {

                NeuronUtil.setNeuronWeight(network.get(name), weight, values[weight]);
            }
            return true;

        } else {

            return false;

        }

    }
}
