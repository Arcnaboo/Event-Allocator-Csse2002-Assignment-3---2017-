package planner;

import java.util.*;

/**
 * <p>
 * A mutable class for recording the amount of traffic on traffic corridors.
 * </p>
 * 
 * <p>
 * The traffic on a corridor is measured in non-negative integer units,
 * representing people.
 * </p>
 */
public class Traffic {

    // Correct line separator for executing machine (used in toString method)
    private final static String LINE_SEPARATOR = System.getProperty(
            "line.separator");

    /*
     * A mapping from traffic corridors with traffic (i.e. corridors such that
     * this.getTraffic(c) > 0) to the amount of traffic currently associated
     * with them.
     */
    private TreeMap<Corridor, Integer> trafficMap;

    /*
     * invariant:
     * 
     * traffic !=null && !traffic.containsValue(null) &&
     * 
     * for each corridor c in traffic.keySet(), traffic.get(c) > 0
     * 
     * (Note: TreeMaps can't contain null keys so we don't need
     * !traffic.containsKey(null) as an invariant)
     */

    /**
     * <p>
     * Creates a new instance of the class in which every traffic corridor
     * initially has no (i.e. zero) traffic.
     * </p>
     * 
     * <p>
     * That is, for any non-null traffic corridor c, this.getTraffic(c) == 0.
     * </p>
     */
    public Traffic() {
        trafficMap = new TreeMap<>();
    }

    /**
     * <p>
     * Creates a new instance of this class that initially has the same traffic
     * as parameter initialTraffic.
     * </p>
     * 
     * <p>
     * The parameter initialTraffic should not be modified by this method.
     * Furthermore, future changes to the parameter initialTraffic should not
     * affect this instance of the class, and vice versa. That is, the new
     * instance of the class should be a deep copy of initialTraffic.
     * </p>
     * 
     * @param initialTraffic
     *            the initial traffic for this instance of the class
     * @throws NullPointerException
     *             if initialTraffic is null
     */
    public Traffic(Traffic initialTraffic) {
        trafficMap = new TreeMap<Corridor, Integer>(initialTraffic.trafficMap);
    }

    /**
     * <p>
     * Returns the amount of traffic on the given corridor.
     * </p>
     * 
     * <p>
     * The amount of traffic on a corridor is always non-negative, meaning that
     * this method always a returns an integer that is greater than or equal to
     * zero.
     * </p>
     * 
     * @param corridor
     *            the corridor whose associated amount of traffic will be
     *            returned
     * @return the amount of traffic on the given corridor
     * @throws NullPointerException
     *             if the parameter corridor is null
     */
    public int getTraffic(Corridor corridor) {
        if (corridor == null) {
            throw new NullPointerException("corridor cannot be null");
        }
        int currentAmount = 0; // the current amount of traffic on corridor
        if (trafficMap.get(corridor) != null) {
            currentAmount = trafficMap.get(corridor);
        }
        return currentAmount;
    }

    /**
     * Returns the set of all traffic corridors c for which this.getTraffic(c)
     * is greater than zero.
     * 
     * @return the set of traffic corridors with an amount of traffic that is
     *         greater than zero
     */
    public Set<Corridor> getCorridorsWithTraffic() {
        return new HashSet<>(trafficMap.keySet());
    }

    /**
     * <p>
     * Returns true if parameter other currently records the same traffic as
     * this traffic record, and false otherwise.
     * </p>
     * 
     * <p>
     * That is, it returns true if and only if for every traffic corridor, the
     * traffic currently on that corridor in this object equals the traffic
     * currently on that corridor in the other object.
     * </p>
     * 
     * @param other
     *            the Traffic object to compare
     * @return true if this object and other currently record the same traffic,
     *         and false otherwise
     * @throws NullPointerException
     *             if other is null
     */
    public boolean sameTraffic(Traffic other) {
        return trafficMap.equals(other.trafficMap);
    }

    /**
     * Returns true if the traffic on each corridor in this object is less than
     * or equal to the capacity of that corridor, and false otherwise.
     * 
     * @return true if the traffic on each corridor in this object is less than
     *         or equal to the capacity of that corridor, and false otherwise.
     */
    public boolean isSafe() {
        for (Corridor corridor : trafficMap.keySet()) {
            if (trafficMap.get(corridor) > corridor.getCapacity()) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Updates the traffic on the given corridor by adding parameter amount to
     * the existing traffic on the corridor.
     * </p>
     * 
     * <p>
     * Parameter amount may be either a negative or positive integer (or zero),
     * but an InvalidTrafficException will be thrown if the result of adding
     * amount to the current traffic on the corridor will result in a negative
     * quantity of traffic on that corridor.
     * </p>
     * 
     * @param corridor
     *            the corridor whose amount of traffic will be updated
     * @param amount
     *            the amount that will be added to the traffic on the given
     *            corridor
     * @throws NullPointerException
     *             if corridor is null
     * @throws InvalidTrafficException
     *             if the addition of amount and the current amount of traffic
     *             on the given corridor is negative (i.e. less than zero).
     */
    public void updateTraffic(Corridor corridor, int amount) {
        if (corridor == null) {
            throw new NullPointerException("Parameter corridor cannot be null");
        }
        int currentAmount = getTraffic(corridor);
        // check that the traffic would not become negative.
        if (currentAmount + amount < 0) {
            throw new InvalidTrafficException(
                    "Cannot have a negative amount of traffic.");
        }

        // update the traffic on the corridor by amount
        if (currentAmount + amount == 0) {
            trafficMap.remove(corridor);
        } else {
            trafficMap.put(corridor, currentAmount + amount);
        }
    }

    /**
     * <p>
     * This method adds all of the traffic defined by parameter extraTraffic to
     * this object.
     * </p>
     * 
     * <p>
     * That is, for each traffic corridor c, this method updates the traffic on
     * that corridor in this object by adding to it the traffic that parameter
     * extraTraffic associates with c.
     * </p>
     * 
     * <p>
     * (Unless this == extraTraffic) this method must not modify the given
     * parameter, and future modifications to this object should not affect the
     * parameter extraTraffic and vice versa.
     * </p>
     * 
     * @param extraTraffic
     *            the traffic to be added to this object
     * @throws NullPointerException
     *             if extraTraffic is null
     */
    public void addTraffic(Traffic extraTraffic) {
        for (Corridor corridor : extraTraffic.trafficMap.keySet()) {
            trafficMap.put(corridor, getTraffic(corridor) + extraTraffic
                    .getTraffic(corridor));
        }
    }

    /**
     * <p>
     * The string representation is the concatenation of strings of the form
     * <br>
     * <br>
     * 
     * "CORRIDOR: TRAFFIC" + LINE_SEPARATOR <br>
     * <br>
     * 
     * where CORRIDOR is the toString() representation of a traffic corridor c
     * for which this.getTraffic(c) is greater than zero, and TRAFFIC is its
     * corresponding amount of traffic, and LINE_SEPARATOR is the line separator
     * retrieved in a machine-independent way by calling
     * System.getProperty("line.separator").
     * </p>
     * 
     * <p>
     * In the string representation, the corridors should appear in the order of
     * their natural ordering (i.e. using the order defined by the compareTo
     * method in the Corridor class).
     * </p>
     * 
     * <p>
     * If there are no traffic corridors c for which this.getTraffic(c) is
     * greater than zero, then the string representation is the empty string "".
     * </p>
     * 
     * <p>
     * (Note that we have one line for each corridor with a non-zero amount of
     * traffic in this string representation, and no lines for corridors with a
     * zero amount of traffic.)
     * </p>
     */
    @Override
    public String toString() {
        String result = ""; // the string representation
        for (Corridor c : trafficMap.keySet()) {
            result += c + ": " + trafficMap.get(c) + LINE_SEPARATOR;
        }
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
        if (trafficMap == null) {
            return false;
        }
        if (trafficMap.containsValue(null)) {
            return false;
        }
        for (Corridor c : trafficMap.keySet()) {
            if (trafficMap.get(c) <= 0) {
                return false;
            }
        }
        return true;
    }

}
