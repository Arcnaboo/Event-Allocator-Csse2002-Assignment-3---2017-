package planner.gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class provides the main method that runs the event allocation program.
 * 
 * INSTRUCTIONS: DO NOT MODIFY THIS CLASS
 */
public class EventAllocator extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		EventAllocatorModel model = new EventAllocatorModel();
		EventAllocatorView view = new EventAllocatorView(model);
		new EventAllocatorController(model, view);

		stage.setScene(view.getScene());
		stage.setTitle("Event Allocator");
		stage.show();
	}
}
