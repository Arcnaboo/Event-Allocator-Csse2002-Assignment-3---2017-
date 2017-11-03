package planner.gui;

import planner.*;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.*;

/**
 * The view for the event allocator program.
 */
public class EventAllocatorView {

	// the model of the event allocator
	private EventAllocatorModel model;

	// Root GUI
	private Scene scene;

	// Main Panes to use in the Application
	private BorderPane borderPane;
	private GridPane gridPane;
	private GridPane eventsGridPane;
	private GridPane venuesGridPane;
	private GridPane allocationsGridPane;

	// Row and Cols of the application main grid
	private final int columns = 1;
	private final int rows = 2;

	// some Fonts, same type different size and weight
	private Font font = Font.font("SanSerif", FontWeight.BOLD, 25);
	private Font largefont = Font.font("SanSerif", FontWeight.BOLD, 35);
	private Font smallFont = Font.font("SanSerif", FontWeight.MEDIUM, 20);
	private Font smallerFont = Font.font("SanSerif", FontWeight.NORMAL,
			15);
	private Font evenSmallFont = Font.font("SanSerif", FontWeight.THIN,
			12);

	// for main page lay out
	private Canvas[] canvass;

	// all the Buttons of the App
	private Button allocateEventToVenue;
	private Button resetAllAllocations;
	private Button removeSelectedAllocation;
	private Button computerAllocate;
	private Button menuEvents;
	private Button menuVenues;
	private Button menuAllocations;
	private Button menuHome;
	private Button eventAdd;
	private Button eventRemove;
	private Button eventsClear;
	private Button venueLoad;
	private Button venueClearAll;
	private Button venueRemove;

	// all the TextFields of the App
	private TextField eventName;
	private TextField eventSize;
	private TextField venueFileName;

	// to keep track of which GridPane we are displaying
	private int statusRegister = 0;

	// Some ListViews to Store and Display data
	private ListView<Event> eventListView;
	private ListView<Venue> venueListView;
	private ListView<Map<Event, Venue>> allocationsTable;

	// Some MenuItems for the FileMenu
	private MenuItem resetMenuItem;
	private MenuItem quitMenuItem;
	private MenuItem loadMenuItem;
	private MenuItem saveMenuItem;
	private MenuItem aboutMenuItem;

	/**
	 * Initialises the view for the event allocator program.
	 * 
	 * @param model
	 *            the model of the event allocator
	 */
	public EventAllocatorView(EventAllocatorModel model) {
		this.model = model;
		// prepare instance variables
		prepareInstance();
		// set grid constraitns to main grid
		setGridConstraints(gridPane, rows, columns, 1440, 700);
		// prepare layout
		prepareCanvas();
		// add few widgets of the lay out
		addAllSectionSkeleton();
		// print main menu
		printMainMenu();
		// print home page and wait for ActionEvent
		printHome();
	}

	/**
	 * Returns the scene for the event allocator application.
	 * 
	 * @return returns the scene for the application
	 */
	public Scene getScene() {
		borderPane = new BorderPane();
		borderPane.getChildren().add(gridPane);

		scene = new Scene(borderPane, 1440, 700);
		prepareFileMenu();
		return scene;
	}

	/**
	 * EventAllocatorView.displayError(Exception) Takes in Exception as
	 * parameter, Displays the Exception.getMessage() in Alert Pop-up
	 * 
	 * @param exception
	 *            Exception of any type
	 */
	public void displayError(Exception exception) {
		// create new Alert
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Event Allocator - Error");
		// set Alert texts to Exception's texts
		alert.setHeaderText(exception.getMessage());
		alert.setContentText(exception.toString());
		// display the error to user
		alert.showAndWait();
	}

	/**
	 * EventAllocatorView.areYouSure(String) Takes in String as parameter,
	 * Displays the String as Confirmation Alert returns True if user confirms
	 * 
	 * @return boolean value
	 */
	public boolean areYouSure(String message) {
		// create new Alert and ask user ok?
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("You are about to " + message);
		alert.setContentText("Are you ok with this?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			// return true if user clicks OK
			return true;
		}
		return false;
	}

	/**
	 * EventAllocatorView.readStatusRegister()
	 * 
	 * @return the value of this.statusRegister
	 */
	private int readStatusRegister() {
		return statusRegister;
	}

	/**
	 * EventAllocatorView.prepareInstance() Initialises most of the Instance
	 * variables
	 */
	private void prepareInstance() {

		// initialise main grid
		gridPane = cellGrid(1440, 700);
		gridPane.setPadding(new Insets(20, 0, 0, 0));

		// initialise the ListViews
		eventListView = new ListView<>();
		allocationsTable = new ListView<>();
		venueListView = new ListView<>();

		// initialise the TextFields
		venueFileName = new TextField("venues.txt");
		eventName = new TextField();
		eventSize = new TextField();

		// initialise the Buttons
		menuEvents = new Button("EVENTS");
		menuVenues = new Button("VENUES");
		menuAllocations = new Button("ALLOCATIONS");
		menuHome = new Button("HOME");
		venueLoad = new Button("LOAD");
		venueClearAll = new Button("Delete all Venues!");
		venueRemove = new Button("Remove Venue");
		eventAdd = new Button("Create Event");
		eventRemove = new Button("Remove Event");
		eventsClear = new Button("Delete all Events");
		allocateEventToVenue = new Button("Allocate");
		resetAllAllocations = new Button("Reset Allocations");
		removeSelectedAllocation = new Button("Remove Allocation");
		computerAllocate = new Button("Auto-Allocation");

		// set attributes to TextFields
		setAttributesTextField(eventName);
		setAttributesTextField(eventSize);
		setAttributesTextField(venueFileName);

		// initialize MenuItem's
		resetMenuItem = new MenuItem("Reset");
		aboutMenuItem = new MenuItem("About");
		quitMenuItem = new MenuItem("Quit");
		loadMenuItem = new MenuItem("Load");
		saveMenuItem = new MenuItem("Save As");
	}

	/**
	 * EventAllocatorView.cellGrid(int, int) Creates GridPane with the given
	 * pref sizes
	 * 
	 * @return new GridPane with size of prefWidth and prefHeight
	 * @requie prefWidth >= 5 && prefHeight >=5
	 * @ensure /return GridPane with prefWidth & prefHeight
	 */
	private GridPane cellGrid(int prefWidth, int prefHeight) {
		// create new GridPane
		GridPane gridPane = new GridPane();
		// set up its style
		gridPane.setStyle("-fx-background-color: #202020;");
		gridPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		gridPane.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		// use user defined width and height
		gridPane.setPrefSize(prefWidth, prefHeight);
		gridPane.setAlignment(Pos.TOP_LEFT);
		gridPane.setHgap(0);
		gridPane.setVgap(0);
		gridPane.setPadding(new Insets(5, 5, 5, 5));
		// return the grid
		return gridPane;

	}

	/**
	 * EventAllocatorView.setAttributesTextField(TextField) sets the attributes
	 * of a given TextField Arranges it's size, font and text alignment
	 * 
	 * @param textField
	 */
	private void setAttributesTextField(TextField textField) {
		// sets predefined visual attributes to TextField
		textField.setAlignment(Pos.CENTER_LEFT);
		textField.setFocusTraversable(false);
		textField.setMaxSize(75, 45);
		textField.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		textField.setFont(smallerFont);
		textField.setPrefSize(75, 45);
	}

	/**
	 * EventAllocatorView.printMainMenu() prepares a GridPane with 1 column and
	 * many rows, puts Buttons to some cells adds the newly created GridPane as
	 * a child to this.Gridpane
	 */
	private void printMainMenu() {
		// create new grid and set it up
		GridPane gridPane = cellGrid(200, 1900);
		gridPane.setPadding(new Insets(25, 25, 25, 25));
		setGridConstraints(gridPane, 20, 1, 200, 50);

		// set up the Label and a text fill on canvass
		Label menuLabel = new Label("Event Allocator");
		setLabelProperties(menuLabel, Pos.BASELINE_LEFT, smallFont,
				(double) 200);
		gridPane.add(menuLabel, 0, 0);
		Canvas canvas = new Canvas(200, 50);
		GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		graphicsContext.setFill(Color.LAWNGREEN);
		graphicsContext.fillRect(0, 0, 160, 50);
		graphicsContext.setFill(Color.BLACK);
		graphicsContext.strokeRect(0, 0, 160, 50);
		graphicsContext.setFont(font);
		graphicsContext.fillText("Main Menu", 16, 33);
		gridPane.add(canvas, 0, 1);

		// set attributes for menu buttons
		SetMenuButtonPro(menuHome, smallFont);
		SetMenuButtonPro(menuEvents, smallFont);
		SetMenuButtonPro(menuVenues, smallFont);
		SetMenuButtonPro(menuAllocations, smallFont);

		// add menu buttons to grid
		gridPane.add(menuHome, 0, 2);
		gridPane.add(menuEvents, 0, 3);
		gridPane.add(menuVenues, 0, 4);
		gridPane.add(menuAllocations, 0, 5);
		// add grid to main grid
		this.gridPane.add(gridPane, 0, 0);
	}

	/**
	 * EventAllocatorView.voidSetMenuButtonPro(Button, Font) updates the given
	 * Button's Font, background and text color
	 * 
	 * @param button
	 *            Button to be updated visually
	 */
	private void SetMenuButtonPro(Button button, Font chosenFont) {
		button.setFont(chosenFont);
		button.setTextFill(Color.LAWNGREEN);
		button.setStyle("-fx-base: #101010;");
	}

	/**
	 * EventAllocatorView.printHome() prepares a new GridPane fills it with some
	 * Label text adds the new GridPane as a child to this.GridPane
	 */
	public void printHome() {
		// set status to 0 meaning currently displaying home page
		statusRegister = 0;

		// create grid and set attributes
		GridPane gridPane = cellGrid(1240, 1900);
		setGridConstraints(gridPane, 6, 1, 1240, 150);

		// create labels and set attributes
		Label homeLabel = new Label("Event Allocator");
		Label csseLabel = new Label("CSSE 2002 / Sem I, 2017");
		Label arcNaboo = new Label("prepared by Arda 'Arc' Akgur");
		Label stuNo = new Label("43829114");
		setLabelProperties(homeLabel, Pos.BOTTOM_CENTER, largefont,
				(double) 1240);
		setLabelProperties(csseLabel, Pos.TOP_CENTER, largefont,
				(double) 1240);
		setLabelProperties(arcNaboo, Pos.BOTTOM_CENTER, font,
				(double) 1240);
		setLabelProperties(stuNo, Pos.TOP_CENTER, smallFont,
				(double) 1240);

		// fill the grid with empty Pane's
		fillThisGridWithPane(gridPane, 20, 1, 1240, 100);

		// add the labels to grid
		gridPane.add(homeLabel, 0, 0);
		gridPane.add(csseLabel, 0, 1);
		gridPane.add(arcNaboo, 0, 2);
		gridPane.add(stuNo, 0, 3);
		// add the grid to main grid
		this.gridPane.add(gridPane, 1, 0);
	}

	/**
	 * EventAllocatorView.eventAddButton() creates a new GridPane, adds buttons
	 * and textfields related to Events to new GridPane, then adds new GridPane
	 * as a child to eventsGridPane
	 */
	public void eventAddButton() {
		// check if currently displaying events
		if (readStatusRegister() == 1) {
			// prepare grid to display view
			GridPane gridPane = cellGrid(400, 300);
			fillThisGridWithPane(gridPane, 6, 2, 200, 50);
			setGridConstraints(gridPane, 6, 2, 200, 50);

			// prepare labels
			Label manageLabel = new Label("Manage Events");
			Label eventNameLabel = new Label("Event Name");
			Label eventSizeLabel = new Label("Event Size");
			setLabelProperties(manageLabel, Pos.CENTER_LEFT, font,
					(double) 200);

			setLabelProperties(eventNameLabel, Pos.CENTER_LEFT,
					smallerFont, (double) 100);
			setLabelProperties(eventSizeLabel, Pos.CENTER_LEFT,
					smallerFont, (double) 100);

			// add labels to grid
			gridPane.add(manageLabel, 0, 0);
			gridPane.add(eventNameLabel, 0, 1);
			gridPane.add(eventSizeLabel, 0, 2);

			// set Event TextFields width and add to grid
			eventName.setMinWidth(100);
			eventSize.setMinWidth(50);
			gridPane.add(eventName, 1, 1);
			gridPane.add(eventSize, 1, 2);

			// set Event related Button attributes and add to grid
			SetMenuButtonPro(eventAdd, smallerFont);
			SetMenuButtonPro(eventsClear, smallerFont);
			eventsClear.setTextFill(Color.RED);
			gridPane.add(eventAdd, 0, 3);
			gridPane.add(eventsClear, 1, 3);

			// add grid to main grid
			eventsGridPane.add(gridPane, 0, 0);
		}

	}

	/**
	 * EventAllocatorView.printEvents() creates a new GridPane, iterate over
	 * events fills the ListView with Events puts the ListView into new
	 * GridPane, then adds new GridPane as a child to eventsGridPane
	 */
	public void printEvents() {
		// check if currently displaying events
		if (readStatusRegister() == 1) {
			// prepare a grid
			GridPane gridPane = cellGrid(400, 300);
			fillThisGridWithPane(gridPane, 10, 2, 200, 50);
			setGridConstraints(gridPane, 10, 2, 200, 50);

			// prepare Event ListView to display
			eventListView.setPrefSize(400, 300);
			eventListView.setMaxHeight(300);
			updateEvents();

			// prepare Event Remove button and add to grid
			SetMenuButtonPro(eventRemove, smallFont);
			eventRemove.setTextFill(Color.RED);
			gridPane.add(eventRemove, 1, 0);

			GridPane midTop = cellGrid(400, 300);
			setGridConstraints(midTop, 6, 1, 400, 100);
			Label eventList = new Label("Events List");
			setLabelProperties(eventList, Pos.CENTER_LEFT, smallFont,
					(double) 100);
			midTop.add(eventList, 0, 0);
			midTop.add(eventListView, 0, 1, 1, 5);
			// add eventListView and grid to EventsGridPane
			eventsGridPane.add(midTop, 1, 0);
			eventsGridPane.add(gridPane, 1, 1);

			// information text
			Text informationText = new Text(
					"Create Event by filling the form,\n"
							+ "or select Event from Events List and Remove it.");
			informationText.setFill(Color.LAWNGREEN);
			informationText.setFont(evenSmallFont);
			eventsGridPane.add(informationText, 0, 1);
		}
	}

	/**
	 * EventAllocatorView.eventsArea() sets the status 1 meaning we are
	 * displaying Events initialises the eventsGridPane calls eventAddButton()
	 * and printEvents() adds eventsGridPane as a child to this.gridPane
	 * 
	 */
	public void eventsArea() {
		// change status to 1 > display Events
		statusRegister = 1;
		// prepare EventsGridpane
		eventsGridPane = cellGrid(1240, 900);
		setGridConstraints(eventsGridPane, 2, 3, 400, 300);
		fillThisGridWithPane(eventsGridPane, 2, 3, 400, 300);
		// print rest of the grid
		eventAddButton();
		printEvents();
		// add eventsGridPane to main grid
		this.gridPane.add(eventsGridPane, 1, 0);

	}

	/**
	 * EventAllocatorView.allocationsArea() sets the status 3 meaning we are
	 * displaying Allocations initialises the allocaitonsGridPane calls
	 * printAllocations(), allocationAddButton(), printCapacityTraffic() adds
	 * allocationsGridPane as a child to this.gridPane
	 * 
	 */
	public void allocationsArea() {
		// change status to 3 > displayin allocations
		statusRegister = 3;
		// prepare allocationsGridPane
		allocationsGridPane = cellGrid(1240, 900);
		fillThisGridWithPane(allocationsGridPane, 2, 3, 400, 300);
		// print rest of the grid
		printAllocations();
		allocationAddButton();
		printCapacityTraffic();
		// add allocationsGridPane to main grid
		this.gridPane.add(allocationsGridPane, 1, 0);
	}

	/**
	 * EventAllocatorView.updateEvents() resets the eventListView, updates it
	 * with current Events in model's List<Event>
	 */
	private void updateEvents() {
		// reset the ListView
		eventListView = new ListView<>();
		// iterate over model's Event list and add items to ListView
		for (Event event : model.getEvents()) {
			if (!eventListView.getItems().contains(event)) {
				eventListView.getItems().add(event);
			}
		}
	}

	/**
	 * EventAllocatorView.updateVenues() resets the venueListView, updates it
	 * with current Venues in model's List<Venue>
	 */
	private void updateVenues() {
		// reset the ListView
		venueListView = new ListView<>();
		// iterate over model's Venue list and add items to ListView
		for (Venue venue : model.getVenues()) {
			if (!venueListView.getItems().contains(venue)) {
				venueListView.getItems().add(venue);
			}
		}
	}

	/**
	 * EventAllocatorView.allocationAddButton() creates a new GridPane, adds
	 * buttons and similar widgets related to Allocations to new GridPane, then
	 * adds new GridPane as a child to allocationsGridPane
	 */
	public void allocationAddButton() {
		// check if currently displaying allocations
		if (readStatusRegister() == 3) {
			// update events and venue ListViews
			updateEvents();
			updateVenues();

			// prepare ListView for allocations
			// ListView<Map<Event,Venue>> this is for remove purposes
			allocationsTable = new ListView<>();
			allocationsTable.setPrefSize(400, 250);
			allocationsTable.setMaxSize(Double.MAX_VALUE,
					Double.MAX_VALUE);
			allocationsTable.setMinSize(Double.MIN_VALUE,
					Double.MIN_VALUE);

			// prepare a grids
			GridPane gridPane = cellGrid(400, 300);
			GridPane topLeftGrid = cellGrid(400, 300);
			setGridConstraints(gridPane, 6, 3, 400, 50);
			setGridConstraints(topLeftGrid, 2, 1, 400, 150);

			// prepare label
			Label allocationsLabel = new Label("Allocations List");
			setLabelProperties(allocationsLabel, Pos.CENTER, smallFont,
					(double) 400);
			updateAllocations();

			// prepare buttons
			SetMenuButtonPro(allocateEventToVenue, smallerFont);
			SetMenuButtonPro(computerAllocate, evenSmallFont);
			SetMenuButtonPro(removeSelectedAllocation, evenSmallFont);
			removeSelectedAllocation.setTextFill(Color.RED);
			SetMenuButtonPro(resetAllAllocations, evenSmallFont);
			resetAllAllocations.setTextFill(Color.RED);

			// add allocations ListView and Label to topleft grid
			topLeftGrid.add(allocationsLabel, 0, 0);
			topLeftGrid.add(allocationsTable, 0, 1);

			// add rest to bottom left grid
			gridPane.add(allocateEventToVenue, 0, 0);
			gridPane.add(eventListView, 1, 0, 1, 5);
			gridPane.add(venueListView, 2, 0, 1, 5);
			gridPane.add(computerAllocate, 0, 1);
			gridPane.add(removeSelectedAllocation, 0, 2);
			gridPane.add(resetAllAllocations, 0, 3);

			// add grids to allocationsGridPane
			allocationsGridPane.add(topLeftGrid, 0, 0);
			allocationsGridPane.add(gridPane, 0, 1);

		}
	}

	/**
	 * EventAllocatorView.updateAllocations() resets the venueListView, updates
	 * it with current Allocations in model's Set<Map<Event,Venue>>
	 */
	private void updateAllocations() {
		// irerate over the Set<Map<Event,Venue>> .. each map has single key
		for (Map<Event, Venue> map : model.getAllAllocations()) {
			// add map to allocations List View
			if (!allocationsTable.getItems().contains(map)) {
				allocationsTable.getItems().add(map);
				// remove allocated Event and Venue from the
				// event and venue ListView
				for (Event event : map.keySet()) {
					eventListView.getItems().remove(event);
					venueListView.getItems().remove(map.get(event));
				}
			}
		}
	}

	/**
	 * EventAllocatorView.printAllocations() creates a new GridPane, adds
	 * ListView to it and updates it with current Allocations in the model, then
	 * adds new GridPane as a child to allocationsGridPane
	 */
	public void printAllocations() {
		if (readStatusRegister() == 3) {
			// prepare grid
			GridPane gridPane = cellGrid(400, 300);
			setGridConstraints(gridPane, 2, 1, 400, 150);
			// prepare label and add to grid
			Label eventAllocatorLabel = new Label("Event Allocator");
			setLabelProperties(eventAllocatorLabel, Pos.TOP_CENTER, font,
					(double) 300);
			gridPane.add(eventAllocatorLabel, 0, 0);

			// prepare ListView which will display Allocations as string
			ListView<String> allocationsListView = new ListView<>();
			allocationsListView.setMaxSize(Double.MAX_VALUE,
					Double.MAX_VALUE);
			allocationsListView.setPrefSize(400, 600);
			allocationsListView.setFocusTraversable(false);
			allocationsListView.addEventFilter(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent mouseEvent) {
							mouseEvent.consume();
						}
					});

			// iterate over all allocations
			int index = 1;
			Set<Event> eventSet = model.getAllocation().keySet();
			for (Event event : eventSet) {
				// add allocation as string to listview
				allocationsListView.getItems().add("" + index + ") "
						+ event.toString() + " allocated to "
						+ model.getAllocation().get(event).getName() + "("
						+ model.getAllocation().get(event).getCapacity()
						+ ")");
				// if no traffic print no extra traffic
				if (model.getAllocation().get(event).getTraffic(event)
						.toString().equals("")) {
					allocationsListView.getItems().add("Allocation "
							+ index + ") Generating NO EXTRA Traffic!");
				} else {
					// else print generated traffic
					allocationsListView.getItems().add("Allocation "
							+ index + ") Generating this much traffic:");
					allocationsListView.getItems()
							.add(model.getAllocation().get(event)
									.getTraffic(event).toString());
				}
				// add empty line and increment index
				allocationsListView.getItems().add("");
				index++;

			}
			// add nodes to main node
			gridPane.add(allocationsListView, 0, 1, 1, 2);
			allocationsGridPane.add(gridPane, 1, 0, 1, 2);
		}
	}

	/**
	 * EventAllocatorView.printCapacityTraffic() creates a new GridPane, adds
	 * ListView to it and updates it with current Traffic in the model, then
	 * adds new GridPane as a child to allocationsGridPane
	 */
	public void printCapacityTraffic() {
		if (readStatusRegister() == 3) {
			// prepare grid and label
			GridPane gridPane = cellGrid(400, 300);
			setGridConstraints(gridPane, 2, 1, 400, 150);
			Label capacityTrafficLabel = new Label(
					"Current Traffic of Allocator");
			setLabelProperties(capacityTrafficLabel, Pos.TOP_CENTER,
					smallFont, (double) 300);

			// prepare ListView of Text
			ListView<Text> listView = new ListView<>();
			listView.setPrefSize(400, 300);
			listView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			listView.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);

			// prepare Text, fill text with model.capacityTraffic.toString()
			Text text = new Text(model.getCapacityTraffic().toString());
			text.setFont(smallerFont);
			// add Text to ListView and make ListView unclickable
			listView.getItems().add(text);
			listView.setFocusTraversable(false);
			listView.addEventFilter(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent mouseEvent) {
							mouseEvent.consume();
						}
					});
			// add all widgets to grid
			gridPane.add(capacityTrafficLabel, 0, 0);
			gridPane.add(listView, 0, 1);
			// add grid to main allocations grid
			allocationsGridPane.add(gridPane, 2, 0);
			addInformationText();
		}
	}

	/**
	 * @ensure proper information text added to allocations area
	 */
	private void addInformationText() {
		// information text
		Text informationText = new Text(
				"Select Event and Venue and click allocate,\n"
						+ "or click auto-allocate so computer can allocate,\n"
						+ "or select allocation from top left list and remove it.");
		informationText.setFill(Color.LAWNGREEN);
		informationText.setFont(evenSmallFont);
		allocationsGridPane.add(informationText, 2, 2);
	}

	/**
	 * EventAllocatorView.venuesArea() sets the status 2 meaning we are
	 * displaying Venues initialises the venuesGridPane calls venueAddButton()
	 * and printVenues() adds venuesGridpane as a child to this.gridPane
	 */
	public void venuesArea() {
		// cahnge status 2 > displaying Venues
		statusRegister = 2;
		// prepare grid
		venuesGridPane = cellGrid(1240, 900);
		setGridConstraints(venuesGridPane, 2, 3, 400, 300);
		fillThisGridWithPane(venuesGridPane, 2, 3, 400, 300);
		// display widgets
		venueAddButton();
		printVenues();
		// add venues grid to main grid
		this.gridPane.add(venuesGridPane, 1, 0);
	}

	/**
	 * EventAllocatorView.venueAddButton() creates a new GridPane, adds buttons
	 * and similar widgets related to Venues to new GridPane, then adds new
	 * GridPane as a child to venuesGridPane
	 */
	public void venueAddButton() {

		if (readStatusRegister() == 2) {
			// prepare grid
			GridPane gridPane = cellGrid(400, 300);
			setGridConstraints(gridPane, 6, 2, 200, 50);
			fillThisGridWithPane(gridPane, 6, 2, 200, 50);

			// prepare labels
			Label manageLabel = new Label("Manage Venues");
			Label venueFileNameLabel = new Label("Filename");
			setLabelProperties(manageLabel, Pos.CENTER_RIGHT, font,
					(double) 200);
			setLabelProperties(venueFileNameLabel, Pos.CENTER_LEFT,
					smallerFont, (double) 100);

			// add labels to grid
			gridPane.add(manageLabel, 0, 0);
			gridPane.add(venueFileNameLabel, 0, 1);
			// setup button and textfield add them to grid
			venueFileName.setMinWidth(100);
			SetMenuButtonPro(venueLoad, smallFont);
			SetMenuButtonPro(venueClearAll, smallerFont);
			venueLoad.setPrefSize(100, 50);
			venueClearAll.setTextFill(Color.RED);
			gridPane.add(venueFileName, 1, 1);
			gridPane.add(venueLoad, 0, 2);
			gridPane.add(venueClearAll, 1, 2);

			// add grid to venues grid
			venuesGridPane.add(gridPane, 0, 0);
		}
	}

	/**
	 * EventAllocatorView.printVenues() creates a new GridPane, adds ListView to
	 * it and updates it with current Venues in the model, then adds new
	 * GridPane as a child to venuesGridPane
	 * 
	 * @throws Exception
	 *             can throw FormatException or similar
	 */
	public void printVenues() {
		if (readStatusRegister() == 2) {
			// update venues list view
			updateVenues();

			// prepare grids
			GridPane gridPane = cellGrid(400, 300);
			fillThisGridWithPane(gridPane, 20, 2, 200, 50);
			setGridConstraints(gridPane, 20, 3, 200, 50);
			GridPane midTop = cellGrid(400, 300);
			setGridConstraints(midTop, 6, 1, 400, 100);

			// prepare label
			Label venueList = new Label("Venue List");
			setLabelProperties(venueList, Pos.CENTER_LEFT, smallFont,
					(double) 100);

			// prepare button
			SetMenuButtonPro(venueRemove, smallFont);
			venueRemove.setTextFill(Color.RED);

			// add widgets to grids
			gridPane.add(venueRemove, 1, 0);
			midTop.add(venueList, 0, 0);
			midTop.add(venueListView, 0, 1, 1, 5);
			venuesGridPane.add(gridPane, 1, 1);
			venuesGridPane.add(midTop, 1, 0);

			// information text
			Text informationText = new Text(
					"Load Venues by providing file name,\n"
							+ "or select Venue from Venue List and Remove it.");
			informationText.setFill(Color.LAWNGREEN);
			informationText.setFont(evenSmallFont);
			venuesGridPane.add(informationText, 0, 1);
		}
	}

	/**
	 * EventAllocatorView.setLabelProperties(Label, Pos, Font, Double) takes in
	 * Label as parameter and other attributes also. updates the Label with
	 * these attributes
	 * 
	 * @param label
	 *            Label to be updated
	 * @param position
	 *            Pos position value
	 * @param font
	 *            Font value
	 * @param width
	 *            preffered Width of the Label
	 */
	private void setLabelProperties(Label label, Pos position, Font font,
			Double width) {
		label.setFont(font);
		label.setPrefWidth(width);
		label.setAlignment(position);
		label.setTextFill(Color.LAWNGREEN);
	}

	/**
	 * EventAllocatorView.getEventName() Returns the String value of eventName
	 * TextField
	 * 
	 * @return String value
	 * @require eventName.getText() != ""
	 */
	public String getEventName() {
		if (eventName.getText().equals("")) {
			throw new IllegalArgumentException("Please Enter Event Name!");
		}
		return eventName.getText();
	}

	/**
	 * EventAllocatorView.getEventSize() Returns the Integer value of eventSize
	 * TextField
	 * 
	 * @return int value
	 * @require eventSize.getText() != ""
	 */
	public int getEventSize() {
		if (eventSize.getText().equals("")) {
			throw new IllegalArgumentException("Please Enter Event Size!");
		}
		return Integer.parseInt(eventSize.getText());
	}

	/**
	 * EventAllocatorView.getFileName() Returns the Text value of venueFileName
	 * TextField
	 * 
	 * @return String value
	 * @require venueFileName.getText() != ""
	 */
	public String getFileName() {
		if (venueFileName.getText().equals("")) {
			throw new IllegalArgumentException("Please Enter File Name!");
		}
		return venueFileName.getText();
	}

	/**
	 * EventAllocatorView.getSelectedVenue() Returns the Venue value of
	 * venueListView
	 * 
	 * @return Venue object
	 */
	public Venue getSelectedVenue() {
		return venueListView.getSelectionModel().getSelectedItem();
	}

	/**
	 * EventAllocatorView.getSelectedEvent() Returns the Event value of
	 * eventListView
	 * 
	 * @return Event object
	 */
	public Event getSelectedEvent() {
		return eventListView.getSelectionModel().getSelectedItem();
	}

	/**
	 * EventAllocatorView.getSelectedAllocation() Returns the Map value of
	 * allocationsTable
	 * 
	 * @return Map HashMap where key is Event value is Venue
	 */
	public Map<Event, Venue> getSelectedAllocation() {
		return allocationsTable.getSelectionModel().getSelectedItem();
	}

	/**
	 * EventAllocatorView.menuHomeHandler(EventHandler<ActionEvent>) sets the
	 * menuHome Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void menuHomeHandler(EventHandler<ActionEvent> handler) {
		menuHome.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.menuEventsHandler(EventHandler<ActionEvent>) sets the
	 * menuEvents Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void menuEventsHandler(EventHandler<ActionEvent> handler) {
		menuEvents.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.menuVenuesHandler(EventHandler<ActionEvent>) sets the
	 * menuVenues Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void menuVenuesHandler(EventHandler<ActionEvent> handler) {
		menuVenues.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.menuAllocationsHandler(EventHandler<ActionEvent>) sets
	 * the menuAllocations Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void menuAllocationsHandler(EventHandler<ActionEvent> handler) {
		menuAllocations.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.eventAddHandler(EventHandler<ActionEvent>) sets the
	 * eventAdd Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void eventAddHandler(EventHandler<ActionEvent> handler) {
		eventAdd.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.eventRemoveHandler(EventHandler<ActionEvent>) sets the
	 * eventRemove Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void eventRemoveHandler(EventHandler<ActionEvent> handler) {
		eventRemove.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.eventsClearHandler(EventHandler<ActionEvent>) sets the
	 * eventsClear Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void eventsClearHandler(EventHandler<ActionEvent> handler) {
		eventsClear.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.venueLoadHandler(EventHandler<ActionEvent>) sets the
	 * venueLoad Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void venueLoadHandler(EventHandler<ActionEvent> handler) {
		venueLoad.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.venueClearAllHandler(EventHandler<ActionEvent>) sets
	 * the venueClearAll Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void venueClearAllHandler(EventHandler<ActionEvent> handler) {
		venueClearAll.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.venueRemoveHandler(EventHandler<ActionEvent>) sets the
	 * venueRemove Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void venueRemoveHandler(EventHandler<ActionEvent> handler) {
		venueRemove.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.allocateEventToVenue(EventHandler<ActionEvent>) sets
	 * the allocateEventToVenue Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void allocateEventToVenueHandler(
			EventHandler<ActionEvent> handler) {
		allocateEventToVenue.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.resetAllAllocationsHandler(EventHandler<ActionEvent>)
	 * sets the resetAllAllocations Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void resetAllAllocationsHandler(
			EventHandler<ActionEvent> handler) {
		resetAllAllocations.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.removeSelectedAllocationHandler(EventHandler<ActionEvent>)
	 * sets the removeSelectedAllocation Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void removeSelectedAllocationHandler(
			EventHandler<ActionEvent> handler) {
		removeSelectedAllocation.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.computerAllocateHandler(EventHandler<ActionEvent>)
	 * sets the computerAllocate Buttons setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void computerAllocateHandler(
			EventHandler<ActionEvent> handler) {
		computerAllocate.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.aboutMenuItemHandler(EventHandler<ActionEvent>) sets
	 * the aboutMenuItem MenuItem's setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void aboutMenuItemHandler(EventHandler<ActionEvent> handler) {
		aboutMenuItem.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.saveMenuItemHandler(EventHandler<ActionEvent>) sets
	 * the saveMenuItem MenuItem's setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void saveMenuItemHandler(EventHandler<ActionEvent> handler) {
		saveMenuItem.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.loadMenuItemHandler(EventHandler<ActionEvent>) sets
	 * the loadMenuItem MenuItem's setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void loadMenuItemHandler(EventHandler<ActionEvent> handler) {
		loadMenuItem.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.resetMenuItemHandler(EventHandler<ActionEvent>) sets
	 * the resetMenuItem MenuItem's setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void resetMenuItemHandler(EventHandler<ActionEvent> handler) {
		resetMenuItem.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.quitMenuItemItemHandler(EventHandler<ActionEvent>)
	 * sets the quitMenuItem MenuItem's setOnAction as EventHandler
	 * 
	 * @param handler
	 *            EventHandler<ActionEvent> from EventAllocatorController
	 */
	public void quitMenuItemItemHandler(
			EventHandler<ActionEvent> handler) {
		quitMenuItem.setOnAction(handler);
	}

	/**
	 * EventAllocatorView.fillThisGridWithPane(GridPane, int, int, int, int)
	 * Takes in GridPane and it's rows and cols values and desired size for
	 * Pane's iterates over each cell in the GridPane, Creates new Pane with
	 * given size, adds Pane to cell, return List<Pane> panes, so if needed
	 * those Pane's can be accessible.
	 * 
	 * @param gridPane
	 *            GridPane to be filled with Panes
	 * @param rows
	 *            how many rows in GridPane
	 * @param columns
	 *            how many cols in GridPane
	 * @param prefWidth
	 *            prefered width of Pane
	 * @param prefHeight
	 *            pregered height of Pane
	 * @return List<Pane> list of Panes
	 */
	private List<Pane> fillThisGridWithPane(GridPane gridPane, int rows,
			int columns, int prefWidth, int prefHeight) {
		List<Pane> result = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				Pane pane = new Pane();
				pane.setStyle("-fx-background-fill: black, white"
						+ " ; -fx-background-insets: 0, 1 ;");
				pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				pane.setMinSize(prefWidth, prefHeight);
				pane.setPrefSize(prefWidth, prefHeight);
				gridPane.add(pane, j, i);
				result.add(pane);
			}
		}
		return result;
	}

	/**
	 * EventAllocatorView.prepareCanvas() creates new Canvas with specific hard
	 * coded sizes adds new Canvas s to this.canvass Array
	 */
	private void prepareCanvas() {
		canvass = new Canvas[2];
		canvass[0] = new Canvas(200, 700);
		canvass[1] = new Canvas(1240, 700);
	}

	/**
	 * EventAllocatorView.addAllSectionSkeleton() updates the this.gridPane with
	 * values from this.canvass
	 */
	private void addAllSectionSkeleton() {

		for (int i = 0; i < 2; i++) {
			Canvas canvas = canvass[i];
			GraphicsContext graphicsContext = canvas
					.getGraphicsContext2D();
			graphicsContext.setFill(Color.AZURE);
			graphicsContext.fillRect(0, 0, canvas.getWidth(),
					canvas.getHeight());
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.strokeRect(0, 0, canvas.getWidth(),
					canvas.getHeight());
			gridPane.add(canvas, i, 0);
		}

	}

	/**
	 * EventAllocatorView.aboutMe() prints About Me section of the helpMenu as
	 * Alert information
	 */
	public void aboutMe() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("UQ, Semester I 2017");
		alert.setContentText("CSSE2002 Assignment 3\n" + "by Arda Akgur\n"
				+ "Copyright \u00a9 2017");

		alert.showAndWait();
	}

	/**
	 * EventAllocatorView.loadEvents() executes the model's readEventFile()
	 * method with "events.txt" as parameter updates GUI depending on
	 * statusRegister
	 * 
	 * @throws Exception
	 *             can throw IOException or similar
	 */
	public void loadEvents(File file) throws Exception {

		model.readEventFile(file);
		switch (readStatusRegister()) {
		case 1:
			eventsArea();
			break;
		case 2:
			break;
		case 3:
			allocationsArea();
			break;
		default:
			break;
		}

	}

	/**
	 * EventAllocatorView.resetAll() resets the EventAllocatorModel prints main
	 * page of the GUI
	 */
	public void resetAll() {
		model.resetAll();
		printHome();
	}

	/**
	 * EventAllocatorView.prepareFileMenu() creates and displays FileMenu for
	 * the GUI
	 * 
	 */
	private void prepareFileMenu() {
		// set attributes for borderPane
		borderPane.setPrefSize(1440, 700);
		borderPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		borderPane.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);

		// create menuBar
		MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(borderPane.widthProperty());

		// add menuBar to borderPane
		borderPane.setTop(menuBar);

		// create fileMenu and helpMenu, fill it with menuItem
		Menu fileMenu = new Menu("File");
		fileMenu.getItems().addAll(resetMenuItem, loadMenuItem,
				saveMenuItem, new SeparatorMenuItem(), quitMenuItem);

		Menu helpMenu = new Menu("Help");
		helpMenu.getItems().add(aboutMenuItem);

		// add filemenu and helpmenu to menubar
		menuBar.getMenus().addAll(fileMenu, helpMenu);

	}

	/**
	 * EventAllocatorView.setGridConstraints(GridPane, int, int, int, int) Sets
	 * the row and column Constraints of GridPane depending on the prefSize
	 */
	private void setGridConstraints(GridPane gridPane, int rows,
			int columns, int prefWidth, int prefHeight) {
		// prepare new ColumnConstraint
		ColumnConstraints columnConstraint = new ColumnConstraints();
		columnConstraint.setHgrow(Priority.ALWAYS);
		columnConstraint.setPrefWidth(prefWidth);
		for (int i = 0; i < columns; i++) {
			// set grid col constraint
			gridPane.getColumnConstraints().add(columnConstraint);
		}
		RowConstraints rowConstraint = new RowConstraints();
		rowConstraint.setVgrow(Priority.ALWAYS);
		rowConstraint.setPrefHeight(prefHeight);
		for (int i = 0; i < rows; i++) {
			gridPane.getRowConstraints().add(rowConstraint);
		}
	}

	/**
	 * EventAllocatorView.resetTextFields() Resets the TextField string values
	 */
	public void resetTextFields() {
		eventName.setText("");
		eventSize.setText("");
		venueFileName.setText("venues.txt");
	}
}
