package planner.gui;

import java.io.File;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import planner.*;

/**
 * The controller for the event allocator program.
 */
public class EventAllocatorController {

	// the model of the event allocator
	private EventAllocatorModel model;
	// the view of the event allocator
	private EventAllocatorView view;

	/**
	 * Initialises the controller for the event allocator program.
	 * 
	 * @param model
	 *            the model of the event allocator
	 * @param view
	 *            the view of the event allocator
	 */
	public EventAllocatorController(EventAllocatorModel model,
			EventAllocatorView view) {
		this.model = model;
		this.view = view;

		// initialise Action Handlers
		view.eventAddHandler(new EventAddActionHandler());
		view.eventsClearHandler(new EventsClearActionHandler());
		view.eventRemoveHandler(new EventRemoveActionHandler());
		view.venueLoadHandler(new VenueLoadActionHandler());
		view.venueRemoveHandler(new VenueRemoveActionHandler());
		view.venueClearAllHandler(new VenueClearAllActionHandler());
		view.menuEventsHandler(new MenuEventsActionHandler());
		view.menuHomeHandler(new MenuHomeActionHandler());
		view.menuVenuesHandler(new MenuVenueActionHandler());
		view.menuAllocationsHandler(new MenuAllocationsActionHandler());
		view.allocateEventToVenueHandler(
				new AllocateEventToVenueActionHandler());
		view.removeSelectedAllocationHandler(
				new RemoveSelectedAllocationActionHandler());
		view.computerAllocateHandler(new ComputerAllocateActionHandler());
		view.resetAllAllocationsHandler(
				new ResetAllAllocationsActionHandler());
		view.resetMenuItemHandler(new ResetMenuItemActionHandler());
		view.saveMenuItemHandler(new SaveMenuItemActionHandler());
		view.loadMenuItemHandler(new LoadMenuItemActionHandler());
		view.quitMenuItemItemHandler(actionEvent -> Platform.exit());
		view.aboutMenuItemHandler(new AboutMenuItemActionHandler());
	}

	/**
	 * AboutMenuItemHandler an EventHandler class that handles AboutMenuItem
	 * MenuItem of the View
	 * 
	 * @author arda
	 *
	 */
	private class AboutMenuItemActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			view.aboutMe();
		}
	}

	/**
	 * LoadMenuItemHandler an EventHandler class that handles LoadMenuItem
	 * MenuItem of the View
	 * 
	 * @author arda
	 *
	 */
	private class LoadMenuItemActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			// open file chooser dialog
			FileChooser fileChooser = new FileChooser();
			// filter to only .arc files
			fileChooser.getExtensionFilters().add(
					new FileChooser.ExtensionFilter("arc file", "*.arc"));
			// open file
			File file = fileChooser.showOpenDialog(new Stage());
			try {
				// reset current model
				model.resetAll();
				// load the file
				model.loadEventAllocator(file);
				// print home page
				view.printHome();
			} catch (NullPointerException nullPointerException) {
				// no need to rethrow anything
			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * SaveMenuItemHandler an EventHandler class that handles SaveMenuItem
	 * MenuItem of the View
	 * 
	 * @author arda
	 *
	 */
	private class SaveMenuItemActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			// open file chooser dialog box
			FileChooser fileChooser = new FileChooser();
			// set extension filter to .arc file
			fileChooser.getExtensionFilters().add(
					new FileChooser.ExtensionFilter("arc file", "*.arc"));
			// create new File
			File file = fileChooser.showSaveDialog(new Stage());
			try {
				// try to save the model to file
				if (file != null)
					model.saveEventAllocator(file);
			} catch (Exception exception) {
				// display error if anything goes wrong
				view.displayError(exception);
			}
		}
	}

	/**
	 * ResetMenuItemHandler an EventHandler class that handles ResetMenuItem
	 * MenuItem of the View
	 * 
	 * @author arda
	 *
	 */
	private class ResetMenuItemActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			// confirm with user and reset model
			if (view.areYouSure("Reset everything!")) {
				view.resetAll();
				// then print home page
				view.printHome();
			}
		}
	}

	/**
	 * ResetAllAllocationsActionHandler an EventHandler class that handles
	 * resetAllAllocations Button of the View
	 * 
	 * @author arda
	 *
	 */
	private class ResetAllAllocationsActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			// simple try catch statement to display error if error occurs
			try {
				// boolean confimation pop-up call
				if (view.areYouSure("remove all current allocations!!")) {
					// if true execute model.resetAllAllocations
					model.resetAllAllocations();
					// update the GUI so changes can be seen
					view.printAllocations();
					view.printCapacityTraffic();
					view.allocationAddButton();
				}
			} catch (Exception exception) {
				view.displayError(exception);
			}

		}

	}

	/**
	 * ComputerAllocateActionHandler an EventHandler class that handles
	 * omputerAllocate Button of the View
	 * 
	 * @author arda
	 *
	 */
	private class ComputerAllocateActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// check if user want to continue
				if (view.areYouSure("remove all current allocations!!\n"
						+ "Then Computer will allocate!")) {
					// call model's runAllocation() method
					model.runAllocation();
					// update GUI
					view.printAllocations();
					view.printCapacityTraffic();
					view.allocationAddButton();
				}
			} catch (NullPointerException exception) {
				view.printAllocations();
				view.printCapacityTraffic();
				view.allocationAddButton();
			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * 
	 * @author arda
	 *
	 */
	private class RemoveSelectedAllocationActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// check first if user even selected anything
				if (view.getSelectedAllocation() == null) {
					view.displayError(
							new Exception("Please Select an Allocation"
									+ " from the Allocations List\n"
									+ "(Top-Left List)"));
				} else {
					// check user confirmation
					if (view.areYouSure("remove"
							+ view.getSelectedAllocation().toString())) {

						// selected allocation is Map<Event,Venue>
						// but single key and single value in this map
						// we iterate over it's keyset so we can get the key
						// iteration only happens once
						for (Event event : view.getSelectedAllocation()
								.keySet()) {
							// we call model's removeFromAllocation(Event,
							// Venue)
							// method
							model.removeFromAllocation(event, view
									.getSelectedAllocation().get(event));
						}
						// update GUI
						view.printAllocations();
						view.printCapacityTraffic();
						view.allocationAddButton();
					}
				}

			} catch (Exception exception) {
				view.displayError(exception);
			}

		}
	}

	/**
	 * AllocateEventToVenueActionHandler EventHandler class that handles
	 * AllocateEventToVenue Button of View
	 * 
	 * @author arda
	 *
	 */
	private class AllocateEventToVenueActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				if (view.getSelectedEvent() == null
						|| view.getSelectedVenue() == null) {
					view.displayError(
							new Exception("Please Select an Event and/or"
									+ " Venue from the lists\n"
									+ "(towards right --> of the Allocate button)"));
				} else {
					// call model's addToAllocation(Event, VEnue) method
					model.addToAllocation(view.getSelectedEvent(),
							view.getSelectedVenue());
					// if succesfull then update GUI
					view.printAllocations();
					view.printCapacityTraffic();
					view.allocationAddButton();
				}
			} catch (Exception exception) {
				// if any error thrown by model, then display error in gui
				view.displayError(exception);
			}

		}
	}

	/**
	 * MenuAllocationsActionHandler EventHandler that handles menuAllcoations
	 * Button of View
	 * 
	 * @author arda
	 *
	 */
	private class MenuAllocationsActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// change View to allocationsArea
				view.allocationsArea();
			} catch (Exception exception) {
				// display error if anything goes wrong
				view.displayError(exception);
			}
		}
	}

	/**
	 * MenuVenueActionHandler EventHandler that handles menuVenue Button of View
	 * 
	 * @author arda
	 *
	 */
	private class MenuVenueActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// change view to venues page
				view.venuesArea();
			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * MenuHomeActionHandler EventHandler that handles menuHome Button of View
	 * 
	 * @author arda
	 *
	 */
	private class MenuHomeActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// change view to home page
				view.printHome();
			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * MenuEventsActionHandler EventHandler that handles MenuEvents Button of
	 * View
	 * 
	 * @author arda
	 *
	 */
	private class MenuEventsActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// change view to Events page
				view.eventsArea();
			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * VenueRemoveActionHandler EventHandler that handles VenueRemove Button of
	 * View
	 * 
	 * @author arda
	 * @require view.selectedVenue != null
	 * @ensure removal of a Venue
	 */
	private class VenueRemoveActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				if (view.getSelectedVenue() == null) {
					view.displayError(new NullPointerException(
							"Please select a Venue from the list!"));
				} else {
					// confirm with user then remove the Venue
					if (view.areYouSure("remove "
							+ view.getSelectedVenue().getName() + "!..")) {
						model.removeVenue(view.getSelectedVenue());
						// reset view
						view.printVenues();
					}
				}

			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * VenueLoadActionHandler EventHandler that handles VenueLoad Button of View
	 * 
	 * @author arda
	 *
	 */
	private class VenueLoadActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// try to load venues with given file name
				model.loadVenues(view.getFileName());

				// reset view
				view.printVenues();
				view.resetTextFields();
			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * EventRemoveActionHandler EventHandler that handles EventRemove Button of
	 * View
	 * 
	 * @author arda
	 *
	 */
	private class EventRemoveActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// check if user selected an Event from list
				if (view.getSelectedEvent() == null) {
					view.displayError(new Exception(
							"Please select an Event from the List"));
				} else {
					// confirm user action
					if (view.areYouSure("remove "
							+ view.getSelectedEvent().getName() + "!!")) {
						// remove event from model
						model.removeEvent(view.getSelectedEvent());
						// reset view
						view.printEvents();
					}
				}
			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * VenueClearAllActionHandler EventHandler that handles VenueClearAll Button
	 * of View
	 * 
	 * @author arda
	 *
	 */
	private class VenueClearAllActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// check Venues list size in model and
				// confirm with user again
				if (model.getVenues().size() > 0
						&& view.areYouSure("delete all Venues!!")) {
					// reset venues list in model
					model.clearVenues();
					// reset view
					view.printVenues();
				}
			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * EventsClearActionHandler EventHandler that handles EventsClear Button of
	 * View
	 * 
	 * @author arda
	 *
	 */
	private class EventsClearActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {

			try {
				// confirm action with user
				if (view.areYouSure("delete all Events!!")) {
					// reset Events list at model
					model.clearEvents();
					// reset view
					view.printEvents();
				}
			} catch (Exception exception) {
				view.displayError(exception);
			}
		}
	}

	/**
	 * EventAddActionHandler EventHandler that handles EventAdd Button of View
	 * 
	 * @author arda
	 *
	 */
	private class EventAddActionHandler
			implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent actionEvent) {
			try {
				// create Event in model
				model.createEvent(view.getEventName(),
						view.getEventSize());

				// reset view
				view.printEvents();
				view.resetTextFields();

			} catch (NumberFormatException numberFormatException) {
				// if problem with Event size
				view.displayError(new NumberFormatException(
						"Please enter valid Event Size.."));
			} catch (Exception exception) {
				// any other Error just display exception message
				view.displayError(exception);
			}
		}
	}
}
