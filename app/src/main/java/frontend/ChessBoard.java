package frontend;

import chess.ChessPosition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ChessBoard implements FrontendElement {
  public static final int BOARD_SIZE = 8;

  ChessPosition position;

  public Node drawInitial() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    /*for (int i = 0; i < BOARD_SIZE ; i++){
        RowConstraints rc = new RowConstraints();
        rc.setFillHeight(true);
        rc.setVgrow(Priority.ALWAYS);
        grid.getRowConstraints().add(rc);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setFillWidth(true);
        cc.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(cc);
    }*/
    for (int r = 0; r < BOARD_SIZE; r++) {
      for (int c = 0; c < BOARD_SIZE; c++) {
        //Rectangle rect = new Rectangle();
        Pane rect = new Pane();
        rect.setStyle("-fx-background-color:green");
        rect.prefWidthProperty().bind(Bindings.min(grid.widthProperty().divide(BOARD_SIZE), grid.heightProperty().divide(BOARD_SIZE)));
        rect.prefHeightProperty().bind(Bindings.min(grid.widthProperty().divide(BOARD_SIZE), grid.heightProperty().divide(BOARD_SIZE)));
        //rect.setFill(((r + c) % 2 == 0) ? Color.WHITE : Color.BLACK);
        grid.add(rect, r, c);
      }
    }
    grid.setBackground(Background.fill(Color.RED));
    grid.setGridLinesVisible(true);
    return grid;
  }

  public void draw(Pane pane) {
  }
}
