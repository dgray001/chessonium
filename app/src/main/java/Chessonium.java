
import java.util.Timer;
import java.util.TimerTask;

import frontend.ChessBoard;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utilities.Logger;

public class Chessonium extends Application {
  private static final int FPS = 1;

  @Override
  public void start(Stage stage) {
    BorderPane canvas = new BorderPane();
    ChessBoard board = new ChessBoard();
    canvas.setCenter(board.drawInitial());
    String javaVersion = System.getProperty("java.version");
    String javafxVersion = System.getProperty("javafx.version");
    Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
    canvas.setTop(l);

    Scene scene = new Scene(new StackPane(canvas), 720, 640);
    stage.setScene(scene);
    stage.show();

    new Timer().scheduleAtFixedRate(new TimerTask() {   
      public void run() {
        board.draw();
      }
    }, 10, 1000 / FPS);

    scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        board.setupBoard();
      }
    });
  }

  public static void main(String[] args) {
    Logger.log("Launching ...");
    launch();
  }

}