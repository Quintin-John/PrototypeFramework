/**
 * A DISSERTATION IT Artifact
 *
 * Submitted to The University of Liverpool in partial fulfillment of the
 * requirements for the degree of MASTER OF SCIENCE.
 *
 * I hereby certify that this dissertation constitutes my own product,
 * that where the language of others is set forth, quotation marks so indicate,
 * and that appropriate credit is given where I have used the language,
 * ideas, expressions, or writings of another.
 *
 * I declare that the dissertation describes original work that has not previously
 * been presented for the award of any other degree of any institution.
 */
package prototype.framework.core;

import java.io.File;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;

import javax.swing.event.EventListenerList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import prototype.framework.util.FileFunctions;
import prototype.framework.util.NetworkUtil;

import prototype.framework.processing.FeedForward;
import prototype.framework.processing.BackPropagation;
import prototype.framework.util.CommonFunctions;
import prototype.framework.util.DataFunctions;

/**
 *
 * @author Quintin-John Smith
 *
 * This is the main processing class of the neural network. this is used to
 * create a neural network, as well as perform forward feed and back propagation
 * as defined by the user. This class will reference the rest of the classes in
 * this application and will act as an interface to functions and methods
 * provided within the Neural Network Framework.
 */
public class Network implements INetworkEvent {

    /* protected local variables */
    protected final EventListenerList LISTENER_LIST;

    /* final local variables class references */
    private final FeedForward FEED_FORWARD;
    private final BackPropagation BACK_PROPAGATION;

    /* local variables data types */
    private Map<String, Object> network_map;
    private int matrix[];

    private boolean cancel;
    private boolean shuffleData;

    /* Constructor */
    @SuppressWarnings("unchecked")
    public Network() {

        /* Create new listener list.*/
        LISTENER_LIST = new EventListenerList();

        /* Setup class references.*/
        FEED_FORWARD = new FeedForward();
        BACK_PROPAGATION = new BackPropagation();

        /* Create new network map.*/
        network_map = new HashMap();

        /**
         * Add a listener to the feed forward function and broadcast events to
         * whoever is subscribed.
         */
        FEED_FORWARD.addProcessingListener((String message) -> {

            networkDebug(message);

        });

        /**
         * Add a listener to the back propagation function and broadcast events
         * to whoever is subscribed.
         */
        BACK_PROPAGATION.addProcessingListener((String message) -> {

            networkDebug(message);

        });
    }

    /**
     * ----------------------- Network Events Start --------------------------
     *
     *
     * Add event
     *
     * @param listener External event subscriber.
     */
    public void addNetworkListener(INetworkEvent listener) {
        LISTENER_LIST.add(INetworkEvent.class, listener);
    }

    /**
     * Remove event
     *
     * @param listener External event subscriber.
     */
    public void removeNetworkListener(INetworkEvent listener) {
        LISTENER_LIST.remove(INetworkEvent.class, listener);
    }

    /**
     * This will broadcast the MSE to whoever is subscribed.
     *
     * @param epoch Total number of total evolutions.
     * @param error The MSE total of the network.
     * @param target Target set for MSE.
     */
    @Override
    public void networkMSE(int epoch, double error, double target) {

        Object[] listeners = LISTENER_LIST.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == INetworkEvent.class) {
                ((INetworkEvent) listeners[i + 1]).networkMSE(epoch, error, target);
            }
        }
    }

    /**
     * This will broadcast a Debug message to whoever is subscribed.
     *
     * @param message The message passed from the system that will be broadcast
     * to the listeners subscribed.
     */
    @Override
    public void networkDebug(String message) {

        Object[] listeners = LISTENER_LIST.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == INetworkEvent.class) {
                ((INetworkEvent) listeners[i + 1]).networkDebug(message);
            }
        }
    }

    /**
     * This will broadcast a network validation message to whoever is
     * subscribed.
     *
     * @param pattern The pattern that is to be evaluated.
     * @param output Output computed via system.
     * @param target Actual Target.
     */
    @Override
    public void networkValidation(int pattern, double[] output, double[] target) {
        Object[] listeners = LISTENER_LIST.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == INetworkEvent.class) {
                ((INetworkEvent) listeners[i + 1]).networkValidation(
                        pattern, output, target
                );
            }
        }
    }

    /**
     * This will broadcast a network validation message to whoever is
     * subscribed.
     *
     * @param completed Boolean value if training has been completed without
     * errors.
     * @param cancel Was this canceled by the user?
     */
    @Override
    public void trainingComplete(boolean completed, boolean cancel) {
        Object[] listeners = LISTENER_LIST.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == INetworkEvent.class) {
                ((INetworkEvent) listeners[i + 1]).trainingComplete(completed, cancel);
            }
        }
    }

    /**
     * This will broadcast a network validation message to whoever is
     * subscribed.
     *
     * @param completed Boolean value if training has been completed without
     * errors.
     * @param cancel Was this canceled by the user?
     */
    @Override
    public void validationComplete(boolean completed, boolean cancel) {
        Object[] listeners = LISTENER_LIST.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == INetworkEvent.class) {
                ((INetworkEvent) listeners[i + 1]).validationComplete(completed, cancel);
            }
        }
    }

    /**
     * ----------------------- Network Events End --------------------------
     *
     *
     * ------------------------ Network Methods Start ----------------------
     *
     *
     * This will return the current network configuration.
     *
     * @return Returns an array of the network configuration.
     */
    public int[] getMatrix() {

        return this.matrix;

    }

    /**
     * This function will create a neural network based on the matrix parameters
     * passed.
     *
     * @param matrix Provide a matrix of the network as an array
     * @return System will return a Map containing network components.
     */
    public Map<String, Object> buildNetwork(int matrix[]) {

        /**
         * New network, clear the old defined matrix.
         */
        this.matrix = new int[matrix.length];
        this.matrix = matrix;

        /**
         * Clear out the cancel boolean if it was set in a previous session.
         */
        this.cancel = false;

        return NetworkUtil.buildNetwork(matrix, network_map);

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
    public boolean setNeuron(Map<String, Object> network, String name,
            double[] values, boolean bias) {

        return NetworkUtil.setNeuron(network, name, values, bias);

    }

    /**
     * This is used to initiate the forward feed function of the neural network
     * and will return a result based on vectors passed.
     *
     * @param matrix Matrix of network to be created, passed as an array.
     * @param vectors An array of input vectors.
     * @param network Map of current network configuration.
     * @return double[] Returns result computed based on given input.
     */
    public double[] startFeedForward(int matrix[], double[] vectors,
            Map<String, Object> network) {

        /**
         * Pass to core forward feed function and return results
         */
        return FEED_FORWARD.start(matrix, vectors, network);

    }

    /**
     * This function is used for on-line training of the neural network. Data
     * sets and Data targets are passed to the network and the training is
     * cycled through each pattern sequence until complete. A network square
     * error is then calculated against the expected target values.
     *
     * Training stops if the MSE target value for the network is reached
     * (returns true - network was trained), or if the maximum number of epochs
     * has been reached (returns false - network could not be trained).
     *
     * @param matrix Matrix of network to be created, passed as an array.
     * @param network Map of current network configuration.
     * @param dataSet An array of input vectors used for training.
     * @param dataTarget An array of input vectors targets used for training.
     * @param epochs Number of evolutions for the network.
     * @param mse Total Network Error
     * @param learningRate Learning Rate of Network.
     * @param momentum Momentum rate of Network.
     * @param weight_decay The weight decay rate.
     *
     * @return True if trained, False if not trained.
     */
    public boolean startTraining(int[] matrix, Map<String, Object> network,
            Map<Integer, double[]> dataSet, Map<Integer, double[]> dataTarget,
            int epochs, double mse, double learningRate, double momentum,
            double weight_decay) {

        /**
         * Clear out the cancel boolean if it was set in a previous session.
         */
        this.cancel = false;

        /**
         * Initialize counter and vectors.
         */
        int counter = 1;

        /**
         * Create network map of components.
         */
        network_map = network;

        int rnd[] = CommonFunctions.createSequence(dataSet.size());

        /**
         * loop until max epoch has been reached or the MSE target value has
         * been reached.
         */
        while (true) {

            double[] values = null;
            double[] targets = null;

            double sumSquaredError = 0.0;

            /**
             * Randomize order of training pattern after each iteration.
             */
            if (shuffleData) {
                rnd = CommonFunctions.shuffleArray(rnd);
            }

            /**
             * Cycle through each input pattern.
             */
            for (int pattern = 0; pattern < dataSet.size(); pattern++) {

                /**
                 * Ignore null values in data sets received.
                 */
                if ((dataSet.get(rnd[pattern]) != null) && (dataTarget.get(rnd[pattern]) != null)) {

                    values = new double[dataSet.get(rnd[pattern]).length];
                    targets = new double[dataTarget.get(rnd[pattern]).length];

                    /**
                     * Copy data to new arrays, so we don't change original data
                     * settings.
                     */
                    values = Arrays.copyOf(
                            dataSet.get(rnd[pattern]), dataSet.get(rnd[pattern]).length);

                    targets = Arrays.copyOf(
                            dataTarget.get(rnd[pattern]), dataTarget.get(rnd[pattern]).length);

                } else {

                    continue;

                }

                /**
                 * perform a standard feed forward and return computed value.
                 */
                startFeedForward(matrix, values, network_map);

                /**
                 * Perform a back propagation and adjust weights per neuron.
                 * Return MSE for specific pattern.
                 */
                double[] outputErrors = startBackPropagation(matrix, targets, network_map,
                        learningRate, momentum, weight_decay);

                /**
                 * Output errors are received per neuron in the output layer.
                 * Add them together to give a total MSE for the network.
                 */
                //for (int err = 0; err < outputErrors.length; err++) {
                sumSquaredError += Math.pow(DoubleStream.of(outputErrors).sum(), 2);
                //sumSquaredError += Math.pow(outputErrors[err], 2);

                //}
            }

            /**
             * Has the user canceled the training?
             */
            if (cancel) {

                /**
                 * Fire event.
                 */
                trainingComplete(false, true);

                return false;
            }

            /**
             * Is the total MSE less or equal to target value?
             */
            if (sumSquaredError < mse) {
                /**
                 * Fire event.
                 */
                trainingComplete(true, false);

                return true;
            }

            /**
             * Have we reached the maximum defined number of epochs for
             * training?
             */
            if (counter > epochs) {

                /**
                 * Fire event.
                 */
                trainingComplete(false, false);

                return false;
            }

            /**
             * Broadcast MSE to subscribers.
             */
            this.networkMSE(counter, sumSquaredError, mse);

            /* update number of epochs.*/
            counter++;
        }
    }

    /**
     * This function is used to perform a validation on the network using unseen
     * data not used in training. The objective is to establish how well the
     * network has generalized information based on training datasets given.
     *
     * @param matrix Matrix of network to be created, passed as an array.
     * @param network Map of current network configuration.
     * @param dataSet An array of input vectors used for training.
     * @param dataTarget An array of input vectors targets used for training.
     * @return Returns true once completed.
     */
    public boolean startValidation(int[] matrix, Map<String, Object> network,
            Map<Integer, double[]> dataSet, Map<Integer, double[]> dataTarget) {

        int counter = 1;

        double[] values;
        double[] targets;

        /**
         * Cycle through each pattern provided in the dataset.
         */
        for (int pattern = 0; pattern < dataSet.size(); pattern++) {

            /**
             * Ignore possible null values in datasets and targets.
             */
            if ((dataSet.get(pattern) != null) || (dataTarget.get(pattern) != null)) {

                values = new double[dataSet.get(pattern).length];
                targets = new double[dataTarget.get(pattern).length];

                /**
                 * Copy data to new arrays, so we don't change original data
                 * settings.
                 */
                values = Arrays.copyOf(dataSet.get(pattern), dataSet.get(pattern).length);
                targets = Arrays.copyOf(dataTarget.get(pattern), dataTarget.get(pattern).length);

            } else {

                continue;
            }

            /**
             * The user has canceled the validation session.
             */
            if (this.cancel == true) {
                validationComplete(false, true);
                return false;
            }

            /**
             * Perform standard feed forward and return results. One result per
             * pattern passed - per output neuron.
             */
            double result[] = startFeedForward(matrix, values, network);

            /**
             * Raise an event broadcasting expected target value Vs computed
             * results.
             */
            networkValidation(counter, targets, result);

            counter++;
        }

        validationComplete(true, false);

        return true;
    }

    /**
     * This is used for back propagation and will train the network during a
     * supervised on-line training sessions. Network weights per neuron are
     * updated based on computed error margin per neuron in each layer.
     *
     * @param matrix Matrix of network to be created, passed as an array.
     * @param targets An array of target vectors.
     * @param network Map of current network configuration.
     * @param learningRate Learning Rate of network.
     * @param momentum This is the momentum of the algorithm.
     *
     * @return Returns total error computed in network (Target - Computed
     * Output).
     */
    private double[] startBackPropagation(int matrix[], double[] targets,
            Map<String, Object> network, double learningRate, double momentum,
            double weight_decay) {

        /**
         * Clear out the cancel boolean if it was set in a previous session.
         */
        this.cancel = false;

        /**
         * Pass to core back propagation and return results.
         */
        return BACK_PROPAGATION.start(matrix, targets, network, learningRate, momentum);

    }

    /**
     * This function is used to save the network configuration to file.
     *
     * @param file Path and name of file to be saved.
     * @param network Map of current network configuration.
     * @param matrix Matrix of network to be created, passed as an array.
     *
     * @return boolean true/false for successful saving network file.
     */
    @SuppressWarnings("unchecked")
    public Boolean saveNetworkToFile(File file, Map<String, Object> network,
            int matrix[]) {

        /**
         * Pass to core file functions to set data.
         */
        return FileFunctions.saveNetworkToFile(file, network, matrix);

    }

    /**
     * This function will retrieve the network settings and create a network
     * based on save parameters stored in the file. A Map is parsed to the
     * caller in order to keep track of the network components.
     *
     * @param file Path and name of file to be loaded.
     *
     * @return Map Map of loaded network configuration.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> loadNetworkFromFile(File file) {

        /**
         * Pass to core file functions to get data.
         */
        Document doc;
        doc = FileFunctions.loadNetworkFromFile(file);

        if (doc == null) {

            /**
             * File does not exist.
             */
            return null;

        } else {

            try {

                doc.getDocumentElement().getNodeName();

                String[] matrix_size
                        = doc.getDocumentElement().getAttribute("size").split(",");

                /**
                 * Resize system matrix for new network configuration.
                 */
                this.matrix = new int[matrix_size.length];

                /**
                 * Strip out unwanted characters from saved algorithm.
                 */
                for (int x = 0; x < matrix_size.length; x++) {

                    matrix_size[x] = matrix_size[x].replace("[", "");
                    matrix_size[x] = matrix_size[x].replace(" ", "");
                    matrix_size[x] = matrix_size[x].replace("]", "");

                    matrix[x] = Integer.parseInt(matrix_size[x]);
                }

                /**
                 * Build the network with given matrix info.
                 */
                network_map = buildNetwork(matrix);

                /**
                 * Cycle through document node and update the values for each
                 * given neuron in the network.
                 */
                NodeList nList = doc.getElementsByTagName("Neuron");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        String name = eElement.getAttribute("id");

                        Boolean bias = Boolean.valueOf(
                                CommonFunctions.extractNodeValue(nNode, "bias", "value", 0));

                        int weights = Integer.parseInt(
                                CommonFunctions.extractNodeValue(nNode, "weights", "value", 0));

                        double weight[] = new double[weights];

                        for (int pos = 0; pos < weights; pos++) {
                            weight[pos] = Double.parseDouble(
                                    CommonFunctions.extractNodeValue(nNode, "weight", "value", pos));
                        }

                        setNeuron(network_map, name, weight, bias);
                    }
                }

            } catch (NumberFormatException | DOMException e) {

                /**
                 * Error, don't return anything.
                 */
                return null;
            }

            /**
             * No error, return created network.
             */
            return network_map;
        }
    }

    /**
     * This is passed to the core common functions to load data from a CSV file.
     *
     * @param file Path and name of file to be loaded.
     * @return Map of data loaded from CSV file..
     */
    public Map<Integer, double[]> loadCSVFile(String file) {

        return FileFunctions.loadCSVFile(file);
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
    public int getInuputNeurons(int matrix[], Map<String, Object> network) {

        return NetworkUtil.getNumInuputNeurons(matrix, network);

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
    public int getOutputNeurons(int matrix[], Map<String, Object> network) {

        return NetworkUtil.getNumOutputNeurons(matrix, network);

    }

    /**
     * This function will split the data into both training and validation data
     * based on a percentage provided by the user.
     *
     * @param trainSet The training set passed.
     * @param trainTarget The training data passed
     * @param valSet Validation set created from data
     * @param valTarget Validation targets created from data
     * @param percentage The percentage of the data split.
     */
    @SuppressWarnings("unchecked")
    public void createTrainingData(Map<Integer, double[]> trainSet,
            Map<Integer, double[]> trainTarget,
            Map<Integer, double[]> valSet,
            Map<Integer, double[]> valTarget, int percentage) {

        if (valSet == null) {
            valSet = new HashMap();
        }

        valSet.put(0, trainSet.get(0));

        if (valTarget == null) {
            valTarget = new HashMap();
        }

        valTarget.put(0, trainTarget.get(0));

        /**
         * CommonFunctions defined function.
         */
        DataFunctions.splitTrainingData(
                trainSet, trainTarget, valSet, valTarget, (100 - percentage));

    }

    /**
     * This function is used to normalize data based on Gaussian normalization
     * and will return a Map of data that has been normalized.
     *
     * @param data Map of data passed for normalization
     *
     * @return Map after normalization has been done.
     */
    public Map<Integer, double[]> normalizeData(Map<Integer, double[]> data) {

        /**
         * Call Data Functions and return the result.
         */
        return DataFunctions.normalizeData(data);

    }

    /**
     * This parameter will define if the network training and validation has
     * been canceled early by the user.
     *
     * @param value True or False
     */
    public void setCancel(boolean value) {

        this.cancel = value;

    }

    /**
     * This function is used to randomize the order that data will be presented
     * to the Neural Network during training. If set to true, than data will be
     * randomized in order of presentation after the completion of each epoch.
     *
     * @param value True or False
     */
    public void setShuffleData(boolean value) {

        this.shuffleData = value;

    }

    /**
     * ------------------------ Network Methods End -------------------------
     *
     *
     * -------------------------- Entry Point -------------------------------
     * Used by the compiler to set this as the entry point class in the class
     * library.
     *
     * @param args Do not use.
     */
    public static void main(String[] args) {

    }
}
