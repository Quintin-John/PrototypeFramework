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
package prototype.framework.factory;

import prototype.framework.components.HiddenNeuron;
import prototype.framework.components.InputNeuron;
import prototype.framework.components.OutputNeuron;
import prototype.framework.base.component.Neuron;

/**
 *
 * @author Quintin-John Smith
 *
 * This class is the Neuron Factor that will be used to create different Neuron
 * Types based on what has been requested.
 */
public class NeuronFactory implements INeuron {

    /**
     * This creates a Input Neuron
     *
     * @return Input Neuron
     */
    @Override
    public Neuron InputNeuron() {

        return new InputNeuron();
    }

    /**
     * This creates a Hidden Neuron.
     *
     * @param connections Number of connections to layer above
     * @return Hidden Neuron
     */
    @Override
    public Neuron HiddenNeuron(int connections) {

        return new HiddenNeuron(connections);
    }

    /**
     * Return new Output Neuron.
     *
     * @param connections Number of connections to layer above
     * @return Output Neuron
     */
    @Override
    public Neuron OutputNeuron(int connections) {

        return new OutputNeuron(connections);
    }

}
