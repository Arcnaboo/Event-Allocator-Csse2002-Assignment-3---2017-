package planner;

import java.util.*;

/**
 * Provides a method for finding a safe allocation of events to venues.
 */
public class Allocator {

    /**
     * <p>
     * Returns a safe allocation of events to venues, if there is at least one
     * possible safe allocation, or null otherwise.
     * </p>
     * 
     * <p>
     * NOTE: What it means for an allocation of events to venues to be safe is
     * defined in the assignment handout.
     * </p>
     * 
     * @require events != null && venues != null && !events.contains(null) &&
     *          !venues.contains(null) && events does not contain duplicate
     *          events && venues does not contain duplicate venues.
     * @ensure Returns a safe allocation of events to venues, if there is at
     *         least one possible safe allocation, or null otherwise.
     */
    public static Map<Event, Venue> allocate(List<Event> events,
            List<Venue> venues) {
        // DO NOT MODIFY THE IMPLEMENTATION OF THIS METHOD
        Set<Map<Event, Venue>> allocations = allocations(events, venues);
        if (allocations.isEmpty()) {
            // returns null to signify that there is no possible safe allocation
            return null;
        } else {
            // returns one (any one) of the possible safe allocations
            return allocations.iterator().next();
        }
    }

    /**
     * Returns the set of all possible safe allocations of events to venues.
     * 
     * @require events != null && venues != null && !events.contains(null) &&
     *          !venues.contains(null) && events does not contain duplicate
     *          events && venues does not contain duplicate venues.
     * @ensure Returns the set of all possible safe allocations of events to
     *         venues. (Note: if there are no possible allocations, then this
     *         method should return an empty set of allocations.)
     */
    private static Set<Map<Event, Venue>> allocations(List<Event> events,
            List<Venue> venues) {
        // set of possible allocations
        Set<Map<Event, Venue>> result = new HashSet<>();

        /* BASE CASE: no more events to allocate */
        if (events.isEmpty()) {
            result.add(new HashMap<Event, Venue>());
            return result;
        }

        /* RECURSIVE CASE: there is at least one more event to allocate. */
        // the event to be allocated next
        Event event = events.get(0);
        // the rest of the events to be allocated
        List<Event> remainingEvents = events.subList(1, events.size());
        for (int i = 0; i < venues.size(); i++) {
            // find possible safe allocations of event at the ith venue
            Venue venue = venues.get(i);
            if (venue.canHost(event)) {
                venues.remove(i); // remove venue from available venues
                Set<Map<Event, Venue>> allocations = allocations(
                        remainingEvents, venues);
                for (Map<Event, Venue> allocation : allocations) {
                    allocation.put(event, venue);
                    if (safeTraffic(allocation)) {
                        result.add(allocation);
                    }
                }
                venues.add(i, venue); // add venue back to available venues
            }
        }
        return result;
    }

    /**
     * Returns the traffic caused by the given allocation.
     * 
     * @requires allocation!=null && the keys in allocation are not null and
     *           each event in keySet of allocation maps to a non-null venue
     *           that can host that event.
     * @ensures returns the traffic caused by the given allocation.
     */
    private static Traffic getUsageOf(Map<Event, Venue> allocation) {
        Traffic result = new Traffic();
        for (Event event : allocation.keySet()) {
            Venue venue = allocation.get(event);
            result.addTraffic(venue.getTraffic(event));
        }
        return result;
    }

    /**
     * Returns true if the traffic caused by the given allocation is safe, and
     * false otherwise.
     * 
     * @requires allocation!=null && the keys in allocation are not null and
     *           each event in keySet of allocation maps to a non-null venue
     *           that can host that event.
     * @ensures returns whether or not the traffic in the given allocation is
     *          safe.
     */
    private static boolean safeTraffic(Map<Event, Venue> allocation) {
        Traffic traffic = getUsageOf(allocation);
        return traffic.isSafe();
    }

}
