
import frontend.ChessBoard;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import utilities.Logger;

public class Chessonium extends Application {

  @Override
  public void start(Stage stage) {
    BorderPane canvas = new BorderPane();
    ChessBoard board = new ChessBoard();
    canvas.setCenter(board.drawInitial());

    String javaVersion = System.getProperty("java.version");
    String javafxVersion = System.getProperty("javafx.version");
    Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
    Scene scene = new Scene(new StackPane(canvas, l), 640, 480);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    Logger.log("Launching ...");
    launch();
  }

}