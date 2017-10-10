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

import java.util.Random;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Quintin-John Smith
 *
 * This class contains a number of common functions that will be used by the
 * neural network.
 */
public class CommonFunctions {

    /**
     * This will extract the attribute of an XML node given the name, attribute
     * type and position.
     *
     * @param nNode This is the node in the XML Document.
     * @param name The name of the node we are looking for.
     * @param value The name of the attribute we are looking for.
     * @param pos The position of the attribute, if we are dealing with
     * multi-value.
     * @return Returns the value of the node attribute as a string value.
     */
    public static String extractNodeValue(Node nNode, String name, String value,
            int pos) {

        try {

            Element eElement = (Element) nNode;
            Element item = (Element) eElement.getElementsByTagName(name).item(pos);
            
            return item.getAttribute(value);

        } catch (NumberFormatException | DOMException e) {

            return null;
        }
    }

    /**
     * This is an internal function that is used to get the values and
     * attributes of each neuron in the network when saving algorithm data for
     * file.
     *
     * @param doc XML Document passed.
     * @param neuron Neuron Object
     * @return Node Returns new node based on values passed.
     */
    static Node getNeuron(Document doc, Object neuron) {
        Element element = doc.createElement("Neuron");

        /**
         * Set id attribute.
         */
        element.setAttribute("id", NeuronUtil.getNeuronName(neuron));

        element.appendChild(
                getNeuronWeights(
                        doc, "bias", "value",
                        Boolean.toString(NeuronUtil.getNeuronBias(neuron))));

        element.appendChild(
                getNeuronWeights(doc, "weights", "value",
                        Integer.toString(NeuronUtil.getNeuronWeight(neuron).length)));

        for (int x = 0; x < NeuronUtil.getNeuronWeight(neuron).length; x++) {
            
            element.appendChild(
                    getNeuronWeights(doc, "weight", "value",
                            Double.toString(NeuronUtil.getNeuronWeight(neuron)[x])));
            
        }

        return element;
    }

    /**
     * This is a utility method to create text node.
     *
     * @param doc XML Document
     * @param name name of node to be created.
     * @param attribute attribute of node.
     * @param value value of attribute.
     * @return Node Returns a Node created based on parsed values.
     */
    private static Node getNeuronWeights(Document doc, String name,
            String attribute, String value) {

        Element node = doc.createElement(name);
        node.setAttribute(attribute, value);

        return node;
    }

    /**
     * This will create a random Integer number within a given range.
     *
     * @param min Minimum value in range
     * @param max Maximum value in range
     *
     * @return returns a random number in range.
     */
    public static int randomIntRange(int min, int max) {

        int shifted = min + (int) (Math.random() * max);
        return shifted;
        
    }

    /**
     * Implementing Fisher–Yates shuffle.
     *
     * @param array An integer array of numbers to be shuffled.
     * @return Returns an array of shuffled numbers.
     */
    public static int[] shuffleArray(int[] array) {

        Random rnd = ThreadLocalRandom.current();
        
        for (int i = array.length - 1; i > 0; i--) {
            
            int index = rnd.nextInt(i + 1);
            /**
             * Simple swap.
             */
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
            
        }
        
        return array;
    }
    /**
     * This will create a sequenced array in numbered succession.
     * @param size Size of array to create.
     * @return Returns sequenced array.
     */
    public static int[] createSequence(int size){
        
        int[] array = new int[size];
        
        for (int num = 0; num < size; num++){
            
            array[num] = num;
        
        }
        
        return array;
    }
}
