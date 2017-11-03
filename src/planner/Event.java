package planner;

/**
 * <p>
 * An immutable class representing an event to be held in the municipality.
 * </p>
 * 
 * <p>
 * An event has a name and a size. The size denotes the number of people who are
 * expected to attend the event: it must be greater than zero.
 * </p>
 */
public class Event {

    // the name of the event
    private String name;
    // the number of people who are expected to attend the event
    private int size;

    /* invariant: name != null && size > 0 */

    /**
     * Creates a new event with the given name and size.
     * 
     * @param name
     *            the name of the event
     * @param size
     *            a positive integer denoting the number of people who are
     *            expected to attend the event
     * @throws NullPointerException
     *             if the given name is null
     * @throws IllegalArgumentException
     *             if size is less than or equal to zero
     */
    public Event(String name, int size) {
        if (name == null) {
            throw new NullPointerException("The event name cannot be null.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException(
                    "The event size must be greater than 0.");
        }
        this.name = name;
        this.size = size;
    }

    /**
     * Returns the name of the event.
     * 
     * @return the name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of people who are expected to attend the event.
     * 
     * @return the size of the event
     */
    public int getSize() {
        return size;
    }

    /**
     * <p>
     * This method returns a string of the form: <br>
     * <br>
     * 
     * "NAME (SIZE)" <br>
     * <br>
     * 
     * where NAME is the name of the event and SIZE is the number of people who
     * are expected to attend the event.
     * </p>
     */
    @Override
    public String toString() {
        return name + " (" + size + ")";
    }

    /**
     * Returns true if and only if the given object (i) is an instance of the
     * class Event, (ii) with a name that is equal to this event's name
     * (according to the equals method of the String class), and (iii) a size
     * that is equal to the size of this event.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object; // the event to compare
        return name.equals(other.name) && (size == other.size);
    }

    @Override
    public int hashCode() {
        // We create a polynomial hash-code based on name and size
        final int prime = 31; // an odd base prime
        int result = 1; // the hash code under construction
        result = prime * result + name.hashCode();
        result = prime * result + size;
        return result;
    }

    /**
     * <p>
     * Determines whether this class is internally consistent (i.e. it satisfies
     * its class invariant).
     * </p>
     * 
     * <p>
     * NOTE: This method is only intended for testing purposes.
     * </p>
     * 
     * @return true if this class is internally consistent, and false otherwise.
     */
    public boolean checkInvariant() {
        return name != null && size > 0;
    }
}
