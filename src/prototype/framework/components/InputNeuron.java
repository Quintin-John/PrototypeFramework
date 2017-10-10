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
 * ideas, expressions, or writings of another
 *
 * I declare that the dissertation describes original work that has not previously
 * been presented for the award of any other degree of any institution.
 */
package prototype.framework.components;

import prototype.framework.base.component.Neuron;

/**
 *
 * @author Quintin-John Smith
 *
 * This class represents a Input Neuron Layer component.
 */
public class InputNeuron extends Neuron {

    /**
     * These neurons only have one input logic = each input neuron takes one
     * vector.
     */
    public InputNeuron() {
        super(1);
        super.setWeight(0, 1.0);
    }

    /**
     * Input neurons only take one input so return inputs received only in the
     * first array.
     *
     * No calculation is done with these guys, as they simply pass forward what
     * they received from the outside world.
     *
     * @return Output of Neuron
     */
    @Override
    public double getOutput() {

        /**
         * Only one input per Input Neuron.
         */
        return super.getInput()[0];
    }
}
