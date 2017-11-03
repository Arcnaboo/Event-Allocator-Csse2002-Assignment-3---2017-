package planner.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
//from  ww  w .j a  v  a 2s .co m
public class Main extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    ObservableList<String> data = FXCollections.observableArrayList();

    ListView<String> listView = new ListView<String>(data);
    listView.setPrefSize(200, 250);
    listView.setEditable(true);

    data.addAll("A", "B", "C", "D", "E");

    listView.setItems(data);
    listView.setCellFactory((ListView<String> l) -> new ColorRectCell());
    StackPane root = new StackPane();
    root.getChildren().add(listView);
    primaryStage.setScene(new Scene(root, 200, 250));
    primaryStage.show();
  }

  static class ColorRectCell extends ListCell<String> {
    @Override
    public void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);
      Rectangle rect = new Rectangle(100, 20);
      if (item != null) {
        rect.setFill(Color.RED);
        setGraphic(rect);
      }
    }
  }
}