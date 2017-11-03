package planner.gui;

import planner.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The model for the event allocator program.
 */
public class EventAllocatorModel {

	// List of Events to store Event
	private List<Event> events;
	// List of Venues to store Venue
	private List<Venue> venues;
	// Traffic capacity of the Event Allocator
	private Traffic capacityTraffic;
	// Allocations Map, stores current allocations
	private Map<Event, Venue> allocations;
	// Set of Venues, if a Venue is in use it goes in this Set
	private Set<Venue> venuesInUse;
	// Set of all the allocations, for Print purposes
	private Set<Map<Event, Venue>> allAllocations;

	/**
	 * Initialises the model for the event allocator program.
	 */
	public EventAllocatorModel() {
		// initialise instance variables
		venuesInUse = new HashSet<>();
		events = new ArrayList<>();
		venues = new ArrayList<>();
		capacityTraffic = new Traffic();
		allocations = new HashMap<>();
		allAllocations = new HashSet<>();
	}

	/**
	 * Returns true if Venue is in use
	 * 
	 * @param venue
	 *            Venue object
	 * @return bool
	 */
	public boolean isVenueInUse(Venue venue) {
		return venuesInUse.contains(venue);
	}

	/**
	 * Returns this.events
	 * 
	 * @return List<Event> events
	 * @ensure /return List of Events in sorted order
	 */
	public List<Event> getEvents() {
		// create comaparator for sorting purposes
		Comparator<Event> comparator = new Comparator<Event>() {
			public int compare(Event event, Event otherEvent) {
				if (event.getName().equals(otherEvent.getName())) {
					return event.getSize() - otherEvent.getSize();
				}
				return event.getName().compareTo(otherEvent.getName());
			}
		};
		// sort events list and return
		Collections.sort(events, comparator);
		return events;
	}

	/**
	 * Returns this.venues
	 * 
	 * @return List<Venue> venues
	 * @ensure /return List of Venues in sorted order
	 */
	public List<Venue> getVenues() {
		// create comparator for sort
		Comparator<Venue> comparator = new Comparator<Venue>() {
			public int compare(Venue venue, Venue otherVenue) {
				if (venue.getName().equals(otherVenue.getName())) {
					return venue.getCapacity() - otherVenue.getCapacity();
				}
				return venue.getName().compareTo(otherVenue.getName());
			}
		};
		// sort & return
		Collections.sort(venues, comparator);
		return venues;
	}

	/**
	 * Adds Event into this.events
	 * 
	 * @param event
	 *            Event object
	 */
	private void addEvent(Event event) {
		if (!events.contains(event)) {
			events.add(event);
		}
	}

	/**
	 * Removes Event from this.events
	 * 
	 * @param event
	 */
	public void removeEvent(Event event) {
		if (events.contains(event)) {
			events.remove(event);
			if (allocations.keySet().contains(event)) {
				removeFromAllocation(event, allocations.get(event));
			}
		}
	}

	/**
	 * Loads Venues from File
	 * 
	 * @param fileName
	 *            FileName of the File
	 * @throws Exception
	 *             it may throw IOException or similar
	 */
	public void loadVenues(String fileName) throws Exception {
		List<Venue> venues = VenueReader.read(fileName);
		for (Venue venue : venues) {
			if (!this.venues.contains(venue)) {
				this.venues.add(venue);
			}
		}
	}

	/**
	 * Creates Event object with given name and size
	 * 
	 * @param name
	 *            name of the new Event
	 * @param size
	 *            size of the new Event
	 */
	public void createEvent(String name, int size) {
		Event event = new Event(name, size);
		this.addEvent(event);
	}

	/**
	 * Resets this.venues
	 */
	public void clearVenues() {
		venues = new ArrayList<>();
		resetAllAllocations();
	}

	/**
	 * Resets this.events
	 */
	public void clearEvents() {
		events = new ArrayList<>();
		resetAllAllocations();
	}

	/**
	 * Adds Venue into this.venues
	 * 
	 * @param venue
	 *            Venue object
	 */
	public void addVenue(Venue venue) {
		if (!venues.contains(venue)) {
			venues.add(venue);
		}
	}

	/**
	 * Removes Venue from this.venues
	 * 
	 * @param venue
	 */
	public void removeVenue(Venue venue) {
		if (venues.contains(venue)) {
			venues.remove(venue);
			if (allocations.containsValue(venue)) {
				for (Event event : allocations.keySet()) {
					if (allocations.get(event).equals(venue)) {
						removeFromAllocation(event, venue);
					}
				}
			}
		}
	}

	/**
	 * Resets all the Instance variables clears Traffic also
	 */
	public void resetAll() {
		venues = new ArrayList<>();
		events = new ArrayList<>();
		venuesInUse = new HashSet<>();
		allAllocations = new HashSet<>();
		resetAllAllocations();
		clearTraffic();
	}

	/**
	 * Returns this.allAllocations
	 * 
	 * @return Set<Map<Event,Venue>> set of maps
	 */
	public Set<Map<Event, Venue>> getAllAllocations() {

		putAllocationsToSet();
		return allAllocations;
	}

	/**
	 * Returns this.capacityTraffic
	 * 
	 * @return Traffic object
	 */
	public Traffic getCapacityTraffic() {
		return capacityTraffic;
	}

	/**
	 * Adds extraTraffic Traffic into this.capacityTraffic
	 * 
	 * @param extraTraffic
	 */
	private void addTraffic(Traffic extraTraffic) {
		capacityTraffic.addTraffic(extraTraffic);
	}

	/**
	 * Clears all previous allocations, executes Allocator.allocate() updates
	 * this.capacityTraffic
	 */
	public void runAllocation() {
		allocations = Allocator.allocate(events, venues);
		if (allocations != null) {
			System.out.println("hmm");
			venuesInUse = new HashSet<>();
			addVenuesInUse();
			clearTraffic();
			updateTraffic();
			putAllocationsToSet();
		} else {
			allocations = new HashMap<>();
		}
	}

	/**
	 * Puts every allocation in allocations Map to Set where each allocation has
	 * it's own unique Map
	 */
	private void putAllocationsToSet() {
		allAllocations = new HashSet<>();
		Comparator<Event> comparator = new Comparator<Event>() {
			public int compare(Event event, Event other) {
				return event.getName().compareTo(other.getName());
			}
		};
		List<Event> events = new ArrayList<>();
		for (Event event : allocations.keySet()) {
			events.add(event);
		}
		// not sure why not working
		Collections.sort(events, comparator);
		for (Event event : events) {
			Map<Event, Venue> map = new HashMap<>();
			map.put(event, allocations.get(event));
			allAllocations.add(map);
		}
	}

	/**
	 * Iterates over this.allocations.keySet() adds every value in the
	 * this.allocations to this.venuesInUse
	 */
	private void addVenuesInUse() {
		for (Event event : allocations.keySet()) {
			venuesInUse.add(allocations.get(event));
		}
	}

	/**
	 * returns this.allocations
	 * 
	 * @return Map<Event, Venue> allocations
	 */
	public Map<Event, Venue> getAllocation() {
		List<Event> events = new ArrayList<>();
		for (Event event : allocations.keySet()) {
			events.add(event);
		}
		Comparator<Event> comparator = new Comparator<Event>() {
			public int compare(Event event, Event other) {
				return event.getName().compareTo(other.getName());
			}
		};
		// zzz not sorting properly
		Collections.sort(events, comparator);
		Map<Event, Venue> sortedAllocations = new HashMap<>();
		for (Event event : events) {
			sortedAllocations.put(event, allocations.get(event));
		}
		return sortedAllocations;
	}

	/**
	 * Updates this.capacityTraffic with current allocations generated Traffic
	 */
	public void updateTraffic() {
		for (Event event : allocations.keySet()) {
			this.addTraffic(allocations.get(event).getTraffic(event));
		}
	}

	/**
	 * Reset this.capacityTraffic
	 */
	public void clearTraffic() {
		capacityTraffic = new Traffic();
	}

	/**
	 * Reset this.CapacityTraffic but inherit extraTraffic
	 * 
	 * @param extraTraffic
	 */
	public void clearTraffic(Traffic extraTraffic) {
		capacityTraffic = new Traffic(extraTraffic);
	}

	/**
	 * Manually allocate Event to Venue
	 * 
	 * @require Event.size() <= Venue.getCapacity() &&
	 *          this.capacityTraffic().isSafe()
	 * @param event
	 *            Event object
	 * @param venue
	 *            Venue object
	 */
	public void addToAllocation(Event event, Venue venue) {
		if (allocations.containsKey(event)
				|| allocations.containsValue(venue)) {
			throw new InvalidTrafficException("Event (" + event.getName()
					+ ") already allocated" + " to a Venue ("
					+ allocations.get(event).getName() + ").");
		}
		// check possibility of venu hosting that event
		if (venue.canHost(event)) {
			Traffic traffic = new Traffic(capacityTraffic);
			traffic.addTraffic(venue.getTraffic(event));
			if (traffic.isSafe()) {
				// if all good update model with new allocation
				capacityTraffic = new Traffic(traffic);
				// add to allocations
				allocations.put(event, venue);
				// rezerve the venue
				venuesInUse.add(venue);
				// add to allocation set
				Map<Event, Venue> map = new HashMap<>();
				map.put(event, venue);
				allAllocations.add(map);
			} else {
				throw new InvalidTrafficException("Can not allocate ("
						+ event.getName() + ") to (" + venue.getName()
						+ ") Traffic won't be safe.");
			}
		} else {
			throw new InvalidTrafficException("Event size ("
					+ event.getSize() + ") exceeds Venue capacity("
					+ venue.getCapacity() + ").");
		}
	}

	/**
	 * Removes given Event Venue allocation from the model
	 * 
	 * @param event
	 *            Event object
	 * @param venue
	 *            Venue object
	 */
	public void removeFromAllocation(Event event, Venue venue) {
		// first check key then remove alocation if value is venue
		if (allocations.containsKey(event)
				&& allocations.remove(event, venue)) {
			// remove from rezerved venues
			venuesInUse.remove(venue);
			// remove from the allocations set
			Map<Event, Venue> map = new HashMap<>();
			map.put(event, venue);
			allAllocations.remove(map);
			// decrease the previously generated traffic
			for (Corridor corridor : venue.getTraffic(event)
					.getCorridorsWithTraffic()) {
				capacityTraffic.updateTraffic(corridor, (-1
						* venue.getTraffic(event).getTraffic(corridor)));
			}
		}
	}

	/**
	 * Executes EventReader.read() reads given file, constucts Events
	 * 
	 * @require properly formatted events.txt
	 * @ensure unique Event objects in program memory
	 * @param fileName
	 *            events.txt file
	 * @throws Exception
	 *             may throw Exception from time to time
	 */
	public void readEventFile(File file) throws Exception {
		EventReader eventReader = new EventReader();
		List<Event> events = eventReader.read(file);
		for (Event event : events) {
			if (!this.events.contains(event)) {
				this.events.add(event);
			}
		}
	}

	/**
	 * Saves the current state of EventAllocatorModel
	 * 
	 * @param file
	 *            File object (*.arc)
	 * @throws Exception
	 *             may throw Exception from time to time
	 */
	public void saveEventAllocator(File file) throws Exception {
		EventAllocatorModelSave modelSave = new EventAllocatorModelSave();
		modelSave.saveEventAllocator(file);
	}

	/**
	 * Loads a previous state of EventAllocatorModel
	 * 
	 * @param file
	 *            File object (*.arc)
	 * @throws Exception
	 *             may throw exception sometimes
	 */
	public void loadEventAllocator(File file) throws Exception {
		EventAllocatorModelLoad modelLoad = new EventAllocatorModelLoad();
		modelLoad.loadEventAllocator(file);
	}

	/**
	 * Reads allocations.txt and updates this.allocations
	 * 
	 * @param fileName
	 *            filename of the allocations save
	 * @throws Exception
	 *             may throw exception
	 */
	public void readAllocationFile(String fileName) throws Exception {
		allocations = new HashMap<>();
		allAllocations = new HashSet<>();
		AllocationReader allocationReader = new AllocationReader();
		Map<Event, Venue> map = allocationReader.read(fileName);
		for (Event event : map.keySet()) {
			addToAllocation(event, map.get(event));
		}
	}

	/**
	 * Resets all allocations in the model
	 */
	public void resetAllAllocations() {
		allAllocations = new HashSet<>();
		allocations = new HashMap<>();
		clearTraffic();
		venuesInUse = new HashSet<>();
	}

	/**
	 * EventReader class, helper class in order to read and construct Event
	 * objects easily
	 * 
	 * @author arda
	 *
	 */
	private class EventReader {

		/**
		 * Reads the file and returns List of Events
		 * 
		 * @param fileName
		 *            events.txt
		 * @return List of Events
		 * @throws Exception
		 *             it may throw fileIo exception
		 */
		public List<Event> read(File file) throws Exception {
			List<Event> result = new ArrayList<>();
			Scanner in = new Scanner(new FileReader(file));
			int lineCounter = 0;
			while (in.hasNextLine()) {
				if (!in.hasNextLine() && lineCounter == 0) {
					break;
				}
				String line = in.nextLine();
				Scanner scanner = new Scanner(line);
				scanner.useDelimiter(":");
				String eventName = scanner.next();
				if (scanner.hasNextInt())
					result.add(new Event(eventName, scanner.nextInt()));

				scanner.close();

			}
			in.close();
			return result;
		}
	}

	/**
	 * AllocationReader class helper class in order to read allocations.txt
	 * 
	 * @author arda
	 *
	 */
	private class AllocationReader {

		/**
		 * Reads the file and returns Map where Events are keys and Venues they
		 * are allocated to are valus
		 * 
		 * @param fileName
		 *            allocations txt file
		 * @return hashMap of allocations
		 * @throws Exception
		 *             may throw file read exceptions
		 */
		public Map<Event, Venue> read(String fileName) throws Exception {
			// scanner for reading the file a line at a time
			Scanner in = new Scanner(new FileReader(fileName));
			// the number of the line being read
			AtomicInteger lineNumber = new AtomicInteger(0);
			// the venues that will be read from the file
			Map<Event, Venue> result = new HashMap<>();

			// read venues one at a time from the file
			while (in.hasNextLine()) {
				// the event info
				Event event = readEvent(lineNumber, in);
				// the name, capacity, and traffic of the venue being read
				String name = readVenueName(lineNumber, in);
				int capacity = readVenueCapacity(lineNumber, in);
				Traffic capacityTraffic = readTraffic(lineNumber, in,
						capacity);
				// the venue read
				Venue venue = new Venue(name, capacity, capacityTraffic);

				result.put(event, venue);
			}
			in.close();
			return result;
		}

		/**
		 * Consumes the next line from the scanner, returning the venue name
		 * read from that line.
		 * 
		 * @require in!=null && in is open for reading
		 * @ensure Consumes the next line from scanner, and returns the venue
		 *         name from that line (i.e. the whole line). The lineNumber is
		 *         incremented once for each line that is consumed from in.
		 * @throws FormatException
		 *             if there is no next line in the scanner, or the line is
		 *             the empty string "" (i.e. a venue name can't be the empty
		 *             string). The exception has a message that identifies the
		 *             lineNumber given, and describes the nature of the error.
		 */
		private String readVenueName(AtomicInteger lineNumber, Scanner in)
				throws FormatException {
			// the name of the venue to be read from the next line
			String name = null;
			if (in.hasNextLine()) {
				name = in.nextLine();
				lineNumber.incrementAndGet();
			} else {
				throw new FormatException(
						"Line " + lineNumber + ": venue name missing");
			}
			if (name.equals("")) {
				throw new FormatException("Line " + lineNumber
						+ ": venue name cannot be the empty string");
			}
			return name;
		}

		/**
		 * Consumes the next line from the scanner, returning event
		 * 
		 * @require in!=null && in is open for reading
		 * @ensure reads next line from scanner, and returns a event from that
		 *         line. line num also incremented
		 */
		private Event readEvent(AtomicInteger lineNumber, Scanner in) {
			String name = null;
			int size = 0;
			if (in.hasNextLine()) {
				String line = in.nextLine();
				lineNumber.incrementAndGet();
				Scanner lineScanner = new Scanner(line);
				lineScanner.useDelimiter(":");
				name = lineScanner.next();
				if (lineScanner.hasNextInt()) {
					size = lineScanner.nextInt();
				}
				lineScanner.close();
			}
			Event event = new Event(name, size);
			return event;
		}

		/**
		 * Consumes the next line from the scanner, returning the venue capacity
		 * read from that line.
		 * 
		 * @require in!=null && in is open for reading
		 * @ensure reads next line from scanner, and returns the venue capacity
		 *         from that line. The lineNumber is incremented once for each
		 *         line that is consumed from in.
		 * @throws FormatException
		 *             if there is no next line in the scanner, or the line does
		 *             not contain one positive integer denoting the venue
		 *             capacity. The exception has a message that identifies the
		 *             lineNumber given, and describes the nature of the error.
		 */
		private int readVenueCapacity(AtomicInteger lineNumber, Scanner in)
				throws FormatException {
			if (!in.hasNextLine()) {
				throw new FormatException("Line " + lineNumber
						+ ": venue capacity expected, but line is missing.");
			}

			// the capacity to be read the next line from the scanner
			int capacity = 0;
			try {
				// the line holding the capacity
				String capacityString = in.nextLine();
				lineNumber.incrementAndGet();
				capacity = Integer.parseInt(capacityString);
			} catch (NumberFormatException e) {
				throw new FormatException("Line " + lineNumber
						+ ": invalid venue capacity.");
			}
			if (capacity <= 0) {
				throw new FormatException("Line " + lineNumber
						+ ": capacity must be greater than or equal to zero.");
			}
			return capacity;
		}

		/**
		 * Consumes zero or more lines from the scanner, where each line denotes
		 * a corridor object and its traffic, until an empty line is consumed.
		 * Returns a traffic object containing the traffic read from each of the
		 * lines. Each of the traffic lines is of the form "START, END,
		 * CAPACITY: TRAFFIC" (e.g. "l0, l1, 100: 50").
		 *
		 * @require in!=null && in is open for reading
		 * @ensure Consumes zero or more lines from the scanner, each denoting
		 *         the amount of traffic on different corridors, until an empty
		 *         line is consumed, and returns the traffic read from those
		 *         lines. The lineNumber is incremented once for each line that
		 *         is consumed from the scanner in.
		 * @throws FormatException
		 *             If any one of the traffic lines read are incorrectly
		 *             formatted; if the end of the scanner is reached before an
		 *             empty line is found; if the same corridor appears in more
		 *             than one line; or if the traffic on a corridor exceeds
		 *             the venue capacity given, or its capacity. The exception
		 *             has a message that identifies the lineNumber given, and
		 *             describes the nature of the error.
		 */
		private Traffic readTraffic(AtomicInteger lineNumber, Scanner in,
				int venueCapacity) throws FormatException {
			// the traffic read from the scanner
			Traffic capacityTraffic = new Traffic();
			// the current line being read
			String line = getNextLine(lineNumber, in);
			while (!line.equals("")) {
				// scanner for that line
				Scanner lineScanner = new Scanner(line);
				lineScanner.useDelimiter(": ");
				try {
					// e.g. "l0, l1, 100: 50"
					Corridor corridor = readCorridor(lineNumber,
							lineScanner);
					int amount = readTraffic(lineNumber, lineScanner,
							corridor.getCapacity(), venueCapacity);

					if (lineScanner.hasNext()) {
						throw new FormatException("Line " + lineNumber
								+ ": extra information on line.");
					}
					if (capacityTraffic.getTraffic(corridor) > 0) {
						throw new FormatException("Line " + lineNumber
								+ ": corridor appears more than once.");
					}
					capacityTraffic.updateTraffic(corridor, amount);
				} finally {
					lineScanner.close();
				}
				line = getNextLine(lineNumber, in); // read the next line
			}
			return capacityTraffic;
		}

		/**
		 * Consumes and returns the next line from the given scanner.
		 *
		 * @require in!=null && in is open for reading
		 * @ensure Consumes and returns the next line from the given scanner.
		 *         The lineNumber is incremented once for each line that is
		 *         consumed from in.
		 * @throws FormatException
		 *             If there is no next line to read from the input. The
		 *             exception has a message that identifies the lineNumber
		 *             given, and describes the nature of the error.
		 */
		private String getNextLine(AtomicInteger lineNumber, Scanner in)
				throws FormatException {
			String line = null;
			if (in.hasNextLine()) {
				line = in.nextLine();
				lineNumber.incrementAndGet();
			} else {
				throw new FormatException("Line " + lineNumber
						+ ": empty line expected to complete venue.");
			}
			return line;
		}

		/**
		 * Consumes the next token from the lineScanner, and returns the
		 * associated corridor object. The token denoting the corridor should be
		 * of the form "START, END, CAPACITY" (e.g. "l0, l1, 100").
		 * 
		 * @require lineScanner!=null && lineScanner is open for reading
		 * @ensure Consumes the next token from the lineScanner and returns the
		 *         corridor represented by that token. lineNumber is unchanged
		 *         by this operation.
		 * @throws FormatException
		 *             If lineScanner doesn't have a next token, or if the
		 *             corridor is incorrectly formatted. The exception has a
		 *             message that identifies the lineNumber given, and
		 *             describes the nature of the error.
		 */
		private Corridor readCorridor(AtomicInteger lineNumber,
				Scanner lineScanner) throws FormatException {
			if (!lineScanner.hasNext()) {
				throw new FormatException("Line " + lineNumber
						+ ": invalid corridor and traffic");
			}
			// Comma-delimited scanner for reading the corridor
			Scanner scanner = new Scanner(lineScanner.next());
			scanner.useDelimiter(", ");
			try {
				String startName = (scanner.hasNext() ? scanner.next()
						: "");
				String endName = (scanner.hasNext() ? scanner.next() : "");
				int capacity = (scanner.hasNextInt() ? scanner.nextInt()
						: 0);

				if (startName.equals("") || endName.equals("")
						|| capacity <= 0 || startName.equals(endName)
						|| scanner.hasNext() || startName.contains(":")
						|| endName.contains(":") || startName.contains(",")
						|| endName.contains(",")) {
					throw new FormatException(
							"Line " + lineNumber + ": invalid corridor.");
				}
				return new Corridor(new Location(startName),
						new Location(endName), capacity);
			} finally {
				scanner.close();
			}
		}

		/**
		 * Consumes the next token from the lineScanner, and returns the amount
		 * of traffic (an integer) read from that token. The token denoting the
		 * amount of traffic should be a single integer greater than zero and
		 * less than or equal to corridorCapacity and the venueCapacity, with no
		 * proceeding or trailing white space.
		 * 
		 * @require lineScanner!=null && lineScanner is open for reading
		 * @ensure Consumes the next token from the lineScanner and returns the
		 *         amount of traffic represented by that token. lineNumber is
		 *         unchanged by this operation.
		 * @throws FormatException
		 *             If lineScanner doesn't have a next token, or if the token
		 *             corresponding to the amount of traffic is incorrectly
		 *             formatted, or out of bounds. The exception has a message
		 *             that identifies the lineNumber given, and describes the
		 *             nature of the error.
		 */
		private int readTraffic(AtomicInteger lineNumber,
				Scanner lineScanner, int corridorCapacity,
				int venueCapacity) throws FormatException {
			// the amount of traffic read from the next token
			if (!lineScanner.hasNextInt()) {
				throw new FormatException("Line " + lineNumber
						+ ": traffic is missing or incorrectly formatted.");
			}
			int amount = lineScanner.nextInt();
			if (amount <= 0) {
				throw new FormatException("Line " + lineNumber
						+ ": traffic is less than or equal to zero.");
			}
			if (amount > corridorCapacity) {
				throw new FormatException("Line " + lineNumber
						+ ": traffic exceeds the corridor capacity.");

			}
			if (amount > venueCapacity) {
				throw new FormatException("Line " + lineNumber
						+ ": traffic exceeds either the venue capacity.");

			}
			return amount;
		}
	}

	/**
	 * EventAllocatorModelLoad Class to help Load previous state of the Model
	 * 
	 * @author arda
	 *
	 */
	private class EventAllocatorModelLoad {

		/**
		 * Main public method that reads the save file and loads the previous
		 * state
		 * 
		 * @param file
		 *            save file (*.arc)
		 * @throws Exception
		 *             it may throw format Exception or Io
		 */
		public void loadEventAllocator(File file) throws Exception {
			// prepare for File Read
			FileReader reader = new FileReader(file);
			Scanner in = new Scanner(reader);
			Integer lineCounter = 0;
			// start reading
			String line = in.nextLine();
			lineCounter++;
			// if invalid file
			if (line.charAt(0) != '#' && lineCounter == 1) {
				in.close();
				throw new FormatException(
						"Invalid File: " + file.getName());
			}
			// if valid file
			if (line.equals("#"))
				// read the events and save as events.tmp
				getFileTxt(in, line, "events.tmp");

			if (line.equals("#"))
				// read the venues and save as venues.tmp
				getFileTxt(in, line, "venues.tmp");

			if (line.equals("#"))
				// read the allocations and save as allocations.tmp
				getFileTxt(in, line, "allocations.tmp");
			in.close();

			// use VenueReader(), EventReader() and AllocationReader()
			File eventFile = new File("events.tmp");
			readEventFile(eventFile);
			loadVenues("venues.tmp");
			readAllocationFile("allocations.tmp");
		}

		/**
		 * Helper method that reads certain part of the save file and writes the
		 * data to a tmp file
		 * 
		 * @param in
		 *            Scanner(File)
		 * @param line
		 *            String of current line we read
		 * @param lineCounter
		 *            lineCounter
		 * @throws Exception
		 *             it can throw exception
		 * @require in !=null && line != null
		 * @ensure read the Scanner line by line until treshold character write
		 *         it back to a tmp file
		 */
		private void getFileTxt(Scanner in, String line, String fileName)
				throws Exception {
			FileWriter fileWriter = new FileWriter(fileName);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			// print the line with \n aka println, thats the key
			while (in.hasNextLine()) {
				line = in.nextLine();
				// stop writing if we hit the control character
				if (line.equals("#"))
					break;
				printWriter.println(line);
			}
			printWriter.close();
		}
	}

	/**
	 * EventAllocatorModelSave class This class helps EventAllocatorModel to
	 * save it's current state
	 * 
	 * @author arda
	 *
	 */
	private class EventAllocatorModelSave {

		/**
		 * It reads current state of the EventAllocator saves everything into
		 * given file as regular text
		 * 
		 * @param file
		 *            File to be saved(*.arc)
		 * @throws Exception
		 *             it can throw exception
		 */
		public void saveEventAllocator(File file) throws Exception {
			FileWriter mainSave = new FileWriter(file);
			PrintWriter mainWriter = new PrintWriter(mainSave);
			// save a section then write the control character > #
			mainWriter.println("#");
			saveEvents(mainWriter);
			mainWriter.println("#");
			saveVenues(mainWriter);
			mainWriter.println("#");
			saveAllocations(mainWriter);
			mainWriter.println("#");
			mainWriter.close();

		}

		/**
		 * Reads EventAllocatorModel.events, writes them to file
		 * 
		 * @format EventName:EventSize
		 * @param eventsWriter
		 *            PrintWriter obj
		 */
		private void saveEvents(PrintWriter eventsWriter) {
			// straightforward simple event.txt format 'name:size'
			for (Event event : events) {
				eventsWriter
						.println(event.getName() + ":" + event.getSize());
			}
		}

		/**
		 * Reads EventAllocatorModel.venues, writes them to file
		 * 
		 * @format VenueName\nCapacity\nCapacityTraffic\n
		 * 
		 * @param venuesWriter
		 *            PrintWriter obj
		 */
		private void saveVenues(PrintWriter venuesWriter) {
			for (Venue venue : venues) {
				// format specificallt to match the VenueReader read format
				venuesWriter.println(venue.getName());
				venuesWriter.println(venue.getCapacity());
				int i = 0;
				int treshHold = venue
						.getTraffic(new Event("a", venue.getCapacity()))
						.getCorridorsWithTraffic().size();
				if (treshHold == 0) {
					venuesWriter.print("\n");
				}
				for (Corridor corridor : venue
						.getTraffic(new Event("" + i, venue.getCapacity()))
						.getCorridorsWithTraffic()) {
					venuesWriter
							.println(
									corridor.getStart().getName() + ", "
											+ corridor.getEnd().getName()
											+ ", " + corridor.getCapacity()
											+ ": " + venue
													.getTraffic(new Event(
															"" + i, venue
																	.getCapacity()))
													.getTraffic(corridor));
					i++;
					if (i == treshHold) {
						venuesWriter.print("\n");
					}
				}
			}
		}

		/**
		 * Reads EventAllocatorModel.allocations, writes them to file
		 * 
		 * @format EventName:EventSize\nVenueName\nCapacity\nCapacityTraffic\n
		 * 
		 * @param allocationsWriter
		 *            PrintWriter obj
		 */
		private void saveAllocations(PrintWriter allocationsWriter) {
			for (Event event : allocations.keySet()) {
				// format of txt is similar to venue reader
				// always convert problem to something you know how to solve...
				allocationsWriter
						.println(event.getName() + ":" + event.getSize());
				allocationsWriter
						.println(allocations.get(event).getName());
				allocationsWriter
						.println(allocations.get(event).getCapacity());
				for (Corridor corridor : allocations.get(event)
						.getTraffic(event).getCorridorsWithTraffic()) {
					allocationsWriter.println(corridor.getStart().getName()
							+ ", " + corridor.getEnd().getName() + ", "
							+ corridor.getCapacity() + ": "
							+ allocations.get(event).getTraffic(event)
									.getTraffic(corridor));
				}
				allocationsWriter.print("\n");
			}
		}
	}
}
