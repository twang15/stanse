/**
 * @brief defines public class CheckerError, which is base class for all the
 *        exceptions which can be thrown in concrete checkers.
 * @author xtrtik2
 */
package cz.muni.stanse.checker;

/**
 * @brief Defines base class for all the exceptions which can be thrown
 *        in concrete checkers.
 * @see java.lang.Exception 
 */
public class CheckerException extends Exception {

    // public section 

    /**
     * @brief Initializes the class by exception description message.
     * 
     * Exception description message is directly passed to the base class. 
     * 
     * @param errorMessage Description of a error which occurs in the checker. 
     */
    public CheckerException(final String errorMessage) {
        super(errorMessage); 
    }

    /**
     * @brief Initializes the class by exception cause object.
     * 
     * Exception cause object is directly passed to the base class. 
     * 
     * @param cause Cause of the exception which occurs in the checker. 
     */
    public CheckerException(final Throwable cause) {
        super(cause); 
    }

    /**
     * @brief Initializes the class by exception description message and
     *        exception cause object.
     * 
     * Exception description message is directly passed to the base class. 
     * Exception cause object is directly passed to the base class as well. 
     * 
     * @param errorMessage Description of a error which occurs in the checker. 
     * @param cause Cause of the exception which occurs in the checker. 
     */
    public CheckerException(final String errorMessage, final Throwable cause) {
        super(errorMessage,cause); 
    }

    // private section

    /**
     * @brief Mandatory field necessitated by Serializable interface.
     *
     * This necessity was derived from Exception class.
     * 
     * @see java.io.Serializable 
     */
    private static final long serialVersionUID = new String("cz.muni.stanse." +
                                         "checker.CheckerException").hashCode();
}

