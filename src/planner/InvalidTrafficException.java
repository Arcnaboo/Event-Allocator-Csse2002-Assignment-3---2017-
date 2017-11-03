package planner;

/**
 * An exception that is thrown to indicate an invalid quantity of traffic.
 */
@SuppressWarnings("serial")
public class InvalidTrafficException extends RuntimeException {

    /**
     * Constructs a new exception with null as its detail message.
     */
    public InvalidTrafficException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail error message
     */
    public InvalidTrafficException(String message) {
        super(message);
    }

}
