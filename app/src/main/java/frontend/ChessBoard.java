package frontend;

import chess.ChessMove;
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
  private int selected_r = -1;
  private int selected_c = -1;

  public Node _drawInitial() {
    this.node = new GridPane();
    this.node.setAlignment(Pos.CENTER);
    for (int r = 0; r < ChessPosition.BOARD_SIZE; r++) {
      for (int c = 0; c < ChessPosition.BOARD_SIZE; c++) {
        this.spaces[r][c] = new ChessSpace(this, r, c);
      }
    }
    this.node.setBackground(Background.fill(Color.GRAY));
    Clickable.instantiate(this);
    return this.node;
  }

  public void pressFromSpace() {
    this.clearSelectedAnnotations();
  }

  public void clearSelectedAnnotations() {
    this.selected_r = -1;
    this.selected_c = -1;
    for (int r = 0; r < spaces.length; r++) {
      for (int c = 0; c < spaces[r].length; c++) {
        spaces[r][c].clearSelectedAnnotations();
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
        // draw from existant images
    for (int r = 0; r < spaces.length; r++) {
      for (int c = 0; c < spaces[r].length; c++) {
        imgs[r][c] = spaces[r][c].getPieceImage();
      }
    }
        // draw from mailbox
        /*for (int p = 0; p < this.position.getMailbox().length; p++) {
          ChessPiece piece = ChessPiece.fromInt(this.position.getMailbox()[p]);
          if (piece == null) {
            continue;
          }
          Image img = new Image(ClassLoader.getSystemClassLoader().getResource("images/" + piece.imagePath() + "_medium.png").toURI().toString());
          int r = p % 8;
          int c = p / 8;
          imgs[r][c] = new ImageView(img);
        }*/
        // draw from bitboard
        /*for (Map.Entry<Integer, Long> entry : this.position.getPieces().entrySet()) {
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
        }*/
      } catch (Exception e) {
        Logger.err(e.getMessage());
      }
    }
    Platform.runLater(new Runnable() {
      public void run() {
        for (int r = 0; r < spaces.length; r++) {
          for (int c = 0; c < spaces[r].length; c++) {
            spaces[r][c].setPieceImage(imgs[r][c]);
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
  }

  public void spaceSelected(int r, int c) {
    this.selected_r = r;
    this.selected_c = c;
    for (ChessMove mv : this.position.getChildren().keySet()) {
      int start = Long.numberOfTrailingZeros(mv.start());
      int start_r = start % 8;
      int start_c = start / 8;
      if (r != start_r || c != start_c) {
        continue;
      }
      int end = Long.numberOfTrailingZeros(mv.end());
      int end_r = end % 8;
      int end_c = end / 8;
      this.spaces[end_r][end_c].setMoveTarget();
    }
  }

  public void moveTargetPressed(int r, int c) {
    for (ChessMove mv : this.position.getChildren().keySet()) {
      int start = Long.numberOfTrailingZeros(mv.start());
      int start_r = start % 8;
      int start_c = start / 8;
      if (selected_r != start_r || selected_c != start_c) {
        continue;
      }
      int end = Long.numberOfTrailingZeros(mv.end());
      int end_r = end % 8;
      int end_c = end / 8;
      if (r != end_r || c != end_c) {
        continue;
      }
      this.position = this.position.getChildren().get(mv);
      break;
    }
    this.propagatePosition();
    this.clearSelectedAnnotations();
  }

  private void propagatePosition() {
    if (this.position == null) {
      return;
    }
    this.position.generateMoves();
    this.position.trimCheckMoves();
    for (int p = 0; p < this.position.getMailbox().length; p++) {
      int r = p % 8;
      int c = p / 8;
      this.spaces[r][c].setPiece(ChessPiece.fromInt(this.position.getMailbox()[p]));
    }
  }
}
