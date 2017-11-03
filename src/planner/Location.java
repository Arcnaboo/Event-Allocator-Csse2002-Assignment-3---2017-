package planner;

/**
 * An immutable class representing a location at either the start or end of a
 * traffic corridor.
 **/
public class Location implements Comparable<Location> {

    // the name of the location
    private String name;
    /* invariant: name != null */

    /**
     * Creates a new location with the given name.
     * 
     * @param name
     *            the name of the location
     * @throws NullPointerException
     *             if name is null
     */
    public Location(String name) {
        if (name == null) {
            throw new NullPointerException("Location name cannot be null.");
        }
        this.name = name;
    }

    /**
     * Returns the name of the location.
     * 
     * @return the name of the location
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns true if and only if (i) the given object is an instance of the
     * class Location with (ii) a name that is equal to this location's name
     * (according to the equals method of the String class).
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Location)) {
            return false;
        }
        Location other = (Location) object; // the location to compare
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
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
        return name != null;
    }

    /**
     * Locations are ordered in lexicographical order by their name.
     */
    @Override
    public int compareTo(Location other) {
        return name.compareTo(other.name);
    }

}
