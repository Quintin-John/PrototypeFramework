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
package prototype.framework.processing;

import java.util.EventListener;

/**
 *
 * @author Quintin-John Smith
 *
 * This Interface contains the methods that will be used to broadcast messages
 * to those subscribed.
 */
public interface IProcessingEvent extends EventListener {

    /* This event will be fired when sending debug information.*/
    void debugInfo(String message);

}
