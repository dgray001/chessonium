package frontend;

import java.util.Map;

import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessStartPosition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import utilities.Bitwise;
import utilities.Logger;

public class ChessBoard extends FrontendElement implements Clickable {
  private ChessPosition position;
  private ChessSpace[][] spaces = new ChessSpace[ChessPosition.BOARD_SIZE][ChessPosition.BOARD_SIZE];
  @Getter
  private GridPane node;
  @Setter
  private boolean hovered = false;
  @Setter
  private boolean pressed = false;

  public Node _drawInitial() {
    this.node = new GridPane();
    this.node.setAlignment(Pos.CENTER);
    for (int r = 0; r < ChessPosition.BOARD_SIZE; r++) {
      for (int c = 0; c < ChessPosition.BOARD_SIZE; c++) {
        this.spaces[r][c] = new ChessSpace(this.node, r, c);
      }
    }
    this.node.setBackground(Background.fill(Color.GRAY));
    Clickable.instantiate(this);
    return this.node;
  }

  public void press() {
    for (int r = 0; r < spaces.length; r++) {
      for (int c = 0; c < spaces[r].length; c++) {
        spaces[r][c].clearAnnotations();
      }
    }
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
    if (this.position != null) {
      return;
    }
    this.position = ChessPosition.createPosition(ChessStartPosition.STANDARD);
    this.propagatePosition();
    this.position.generateMoves();
  }

  private void propagatePosition() {
    if (this.position == null) {
      return;
    }
    for (int p = 0; p < this.position.getMailbox().length; p++) {
      int r = p % 8;
      int c = p / 8;
      this.spaces[r][c].setPiece(ChessPiece.fromInt(this.position.getMailbox()[p]));
    }
  }
}
