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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Quintin-John Smith
 *
 * This class contains a number of common functions that will be used by the
 * neural network.
 */
public class DataFunctions {

    /**
     * This is used to split data uploaded into training and validation datasets
     * as provided by the user.
     *
     * @param trainSet This is the dataset given that will be converted into a
     * training set.
     * @param trainTarget This is the target values associated with the dataSet.
     * @param valSet This is passed as empty and will be populated by the system
     * to produce validation data.
     * @param valTarget This is passed as empty and will be populated by the
     * system to produce validation targets.
     * @param percentage The percentage of the data split between training and
     * validation data.
     */
    public static void splitTrainingData(Map<Integer, double[]> trainSet,
            Map<Integer, double[]> trainTarget,
            Map<Integer, double[]> valSet,
            Map<Integer, double[]> valTarget, int percentage) {

        /**
         * Get split percentage and amount of records that are to be used as
         * validation data.
         */
        int split = (int) (trainSet.size() * ((double) percentage) / 100);

        Map<Integer, double[]> tmpTrain = new HashMap<>(trainSet);
        Map<Integer, double[]> tmpTarget = new HashMap<>(trainTarget);

        trainSet.clear();
        trainTarget.clear();
        valSet.clear();
        valTarget.clear();

        int rnd[] = CommonFunctions.createSequence(tmpTrain.size());
        rnd = CommonFunctions.shuffleArray(rnd);

        for (int x = 0; x < split; x++) {

            double[] data = new double[tmpTrain.get(rnd[x]).length];

            data = Arrays.copyOf(tmpTrain.get(rnd[x]), tmpTrain.get(rnd[x]).length);
            valSet.put(x, data);

            double[] target = new double[tmpTarget.get(rnd[x]).length];
            target = Arrays.copyOf(tmpTarget.get(rnd[x]), tmpTarget.get(rnd[x]).length);

            valTarget.put(x, target);

            /**
             * Note, removing the item does not automatically shift the array
             * up. It simply removes the value and sets the element to null.
             */
            tmpTrain.remove(rnd[x]);
            tmpTarget.remove(rnd[x]);

        }

        int pos = 0;
        for (Map.Entry<Integer, double[]> entry : tmpTrain.entrySet()) {
            trainSet.put(pos, entry.getValue());
            pos++;
        }

        pos = 0;
        for (Map.Entry<Integer, double[]> entry : tmpTarget.entrySet()) {
            trainTarget.put(pos, entry.getValue());
            pos++;
        }
    }

    /**
     * The method performs a Gaussian normalization on the specified columns by
     * subtracting the column mean from each value and then dividing by the
     * column standard deviation. The resulting matrix is then scaled so that
     * all the values have roughly the same magnitude.
     *
     * Reference:
     *
     * Normalization constant for a 1D Gaussian. (2015). 1st ed. UBC Computer
     * Science Department, pp.1-5.
     *
     * @param data Data to be normalized.
     * @return Returns a Map of normalized data.
     */
    @SuppressWarnings("unchecked")
    public static Map<Integer, double[]> normalizeData(Map<Integer, double[]> data) {

        Map<Integer, double[]> data_map = new HashMap<>();

        /**
         * Copy map so we don't modify the original.
         */
        int size = data.get(0).length;

        for (int row = 0; row < data.size(); row++) {

            try {

                double[] value = new double[size];
                value = Arrays.copyOf(data.get(row), size);

                data_map.put(row, value);

            } catch (Exception ex) {

            }

        }

        /**
         * Normalize specified columns by computing (x - mean) / sd for each
         * value.
         *
         * Cycle through each column.
         */
        for (int col = 0; col < data_map.get(col).length; col++) {

            double sum = 0.0;

            /**
             * Cycle through each row.
             */
            for (int row = 0; row < data.size(); row++) {

                sum += data_map.get(row)[col];

            }

            double mean = sum / data.size();
            double sd = 0.0;

            /**
             * Cycle through each row.
             */
            for (int row = 0; row < data_map.size(); row++) {

                sum += (data_map.get(row)[col] - mean) * (data_map.get(row)[col] - mean);
                sd = Math.sqrt(sum / (data_map.size() - 1));

            }

            /**
             * Update column and row with new normalized value.
             */
            for (int row = 0; row < data.size(); row++) {

                data_map.get(row)[col] = (data_map.get(row)[col] - mean) / sd;

            }
        }

        /**
         * Return new map with normalized data.
         */
        return data_map;
    }
}
