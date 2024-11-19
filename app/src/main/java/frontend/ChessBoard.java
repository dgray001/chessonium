package frontend;

import java.util.Map;

import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessStartPosition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import utilities.Bitwise;
import utilities.Logger;

public class ChessBoard extends FrontendElement {
  private ChessPosition position;
  private ChessSpace[][] spaces = new ChessSpace[ChessPosition.BOARD_SIZE][ChessPosition.BOARD_SIZE];

  public Node _drawInitial() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    for (int r = 0; r < ChessPosition.BOARD_SIZE; r++) {
      for (int c = 0; c < ChessPosition.BOARD_SIZE; c++) {
        this.spaces[r][c] = new ChessSpace(grid, r, c);
      }
    }
    grid.setBackground(Background.fill(Color.GRAY));

    grid.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        for (int r = 0; r < spaces.length; r++) {
          for (int c = 0; c < spaces[r].length; c++) {
            spaces[r][c].clearAnnotations();
          }
        }
      }
    });

    grid.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
      }
    });

    return grid;
  }

  public void draw() {
    ImageView[][] imgs = new ImageView[8][8];
    if (position == null) {
      try {
        Image img = new Image(ClassLoader.getSystemClassLoader().getResource("images/bishop_white_medium.png").toURI().toString());
        ImageView imgV = new ImageView(img);
        Platform.runLater(new Runnable() {
          public void run() {
            imgs[1][1] = imgV;
          }
        });
      } catch (Exception e) {
        Logger.err(e.getMessage());
      }
    } else {
      try {
        for (Map.Entry<Integer, Long> entry : this.position.getPieces().entrySet()) {
          ChessPiece piece = ChessPiece.fromInt(entry.getKey());
          if (entry.getValue() == 0) {
            continue;
          }
          boolean[] pieceSpaces = Bitwise.longToBools(entry.getValue());
          Image img = new Image(ClassLoader.getSystemClassLoader().getResource("images/" + piece.imagePath() + "_medium.png").toURI().toString());
          for (int i = 0; i < pieceSpaces.length; i++) {
            if (!pieceSpaces[i]) {
              continue;
            }
            int r = i % 8;
            int c = i / 8;
            imgs[r][c] = new ImageView(img);
          }
        }
      } catch (Exception e) {
        Logger.err(e.getMessage());
      }
    }
    Platform.runLater(new Runnable() {
      public void run() {
        for (int r = 0; r < spaces.length; r++) {
          for (int c = 0; c < spaces[r].length; c++) {
            spaces[r][c].getPane().getChildren().clear();
            if (imgs[r][c] != null) {
              spaces[r][c].setImage(imgs[r][c]);
            }
          }
        }
      }
    });
  }

  public void setupBoard() {
    this.position = ChessPosition.createPosition(ChessStartPosition.STANDARD);
    this.propagatePosition();
  }

  private void propagatePosition() {
    if (this.position == null) {
      return;
    }
    for (int r = 0; r < this.position.getMailbox().length; r++) {
      for (int c = 0; c < this.position.getMailbox()[r].length; c++) {
        this.spaces[r][c].setPiece(ChessPiece.fromInt(this.position.getMailbox()[r][c]));
      }
    }
  }
}
