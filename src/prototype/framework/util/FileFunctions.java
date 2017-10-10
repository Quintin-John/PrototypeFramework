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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
/**
 *
 * @author Quintin-John Smith
 * 
 * This class contains a number of common functions that will be used by the
 * neural network.
 */
public class FileFunctions {

    /**
     * This retrieve the saved XML file from a specified file path and return
     * the data as a document.
     *
     * @param file Path and name of file to be loaded.
     * @return Document Returns and XML Document.
     */
    public static Document loadNetworkFromFile(File file) {

        try {

            File fXmlFile = file;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            return doc;

        } catch (ParserConfigurationException | SAXException | IOException ex) {

            return null;
        }

    }

    /**
     * This function is used to save the current network configuration to an XML
     * file that could be exported from the system.
     *
     * @param file Path and name of file to be saved.
     * @param network Map of current network configuration.
     * @param matrix Matrix of network to be created, passed as an array.
     * @return boolean true/false for successful saving network file.
     */
    public static Boolean saveNetworkToFile(File file, Map<String, Object> network,
            int matrix[]) {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        String name;

        try {

            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            /**
             * Add elements to Document. 
             */
            Element rootElement = doc.createElement("Network");
            rootElement.setAttribute("size", Arrays.toString(matrix));
            doc.appendChild(rootElement);

            /**
             * How many Layers do we have in the neural network? 
             */
            for (int layers = 0; layers < matrix.length; layers++) {

                /**
                 * How many neurons do we have within each layer? 
                 */
                for (int neurons = 0; neurons < matrix[layers]; neurons++) {
                    if (layers == 0) {
                        /* Load input neurons */
                        name = "a-" + (neurons + 1);

                        if (network.get(name) != null) {
                            rootElement.appendChild(
                                    CommonFunctions.getNeuron(doc, network.get(name)));
                        }

                    } else if (layers == (matrix.length - 1)) {
                        /** 
                         * Load output neurons. 
                         */
                        name = "c-" + (neurons + 1);

                        if (network.get(name) != null) {
                            rootElement.appendChild(
                                    CommonFunctions.getNeuron(doc, network.get(name)));
                        }

                    } else {
                        /**
                         * Load hidden neuron layers. 
                         */
                        name = "b-" + (neurons + 1) + "-" + layers;

                        if (network.get(name) != null) {
                            rootElement.appendChild(
                                    CommonFunctions.getNeuron(doc, network.get(name)));
                        }
                    }
                }
            }

            /**
             * For output to file. 
             */
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            /**
             * Correct formatting.
             */
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            /**
             * Write to file. 
             */
            StreamResult sfile = new StreamResult(file);
            transformer.transform(source, sfile);

        } catch (ParserConfigurationException | DOMException e) {

            return false;

        } catch (TransformerConfigurationException ex) {

            return false;

        } catch (TransformerException ex) {

            return false;
        }

        return true;
    }

    /**
     * This is used to upload CSV files into the system for both Datasets and
     * Target values.
     *
     * @param File Path and name of file to be loaded.
     * @return Map Returns a Map of data loaded.
     */
    public static Map<Integer, double[]> loadCSVFile(String File) {

        /**
         * Initialize variables. 
         */
        String line;
        BufferedReader stream = null;
        
        List<List<String>> csvData = new ArrayList<>();

        try {

            /**
             * Read the file from the given parameter. 
             */
            stream = new BufferedReader(new FileReader(File));
            while ((line = stream.readLine()) != null) {

                /** 
                 * Convert commas into arrays for parsing data back.
                 */
                String[] splitted = line.split(",");

                List<String> dataLine = new ArrayList<>(splitted.length);
                dataLine.addAll(Arrays.asList(splitted));
                csvData.add(dataLine);

            }
            
        } catch (FileNotFoundException ex) {

            /* found error */
            return null;
            
        } catch (IOException ex) {

            /* found error */
            return null;

        } finally {

            if (stream != null) {
                
                try {

                    /* done reading, close the stream.*/
                    stream.close();

                } catch (IOException ex) {

                    /* found error */
                    return null;
                }
            }
        }

        /* return data to caller.*/
        return loadData(csvData);

    }

    /**
     * Internal function that will convert a nested List component into an array
     * that can be read by the system.
     *
     * @param input This contains multi-dimension lists of data to be parsed.
     * @return Map Returns a map of the data converted and ready for upload.
     */
    @SuppressWarnings("unchecked")
    private static Map<Integer, double[]> loadData(List<List<String>> input) {

        Map<Integer, double[]> arrayData = new HashMap();

        /**
         * Loop through the lists and create arrays.
         */
        for (int x = 0; x < input.size(); x++) {

            List<String> lineData = input.get(x);

            /**
             * Set the size of the arrays created.
             */
            double value[] = new double[lineData.size()];

            for (int i = 0; i < lineData.size(); i++) {
                
                value[i] = Double.parseDouble(lineData.get(i));
                
            }

            /**
             * Add data to map.
             */
            arrayData.put(x, value);
        }

        /**
         * Return data. 
         */
        return arrayData;
    }
}
