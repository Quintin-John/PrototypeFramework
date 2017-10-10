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

import prototype.framework.base.component.Neuron;

/**
 *
 * @author Quintin-John Smith
 *
 * The interface for the Neuron Factory that will return specific neuron types
 * as required.
 */
public interface INeuron {

    /* Returns an Input Neuron */
    Neuron InputNeuron();

    /* Returns a Hidden Neuron */
    Neuron HiddenNeuron(int connections);

    /* Returns an Output Neuron */
    Neuron OutputNeuron(int connections);
}
