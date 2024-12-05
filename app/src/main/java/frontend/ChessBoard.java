package frontend;

import java.time.Instant;
import java.util.Map;

import chess.ChessConstants;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessStartPosition;
import engine.ChessEngine;
import engine.ChessEngineConfiguration;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import utilities.Logger;

public class ChessBoard extends FrontendElement implements Clickable {
  private ChessPosition position;
  private ChessEngine engine;
  private ChessSpace[][] spaces = new ChessSpace[ChessConstants.BOARD_SIZE][ChessConstants.BOARD_SIZE];
  @Getter
  private GridPane node;
  @Setter
  private boolean hovered = false;
  @Setter
  private boolean pressed = false;
  private int selected_r = -1;
  private int selected_c = -1;

  private long timeStart = 0L;

  public Node _drawInitial() {
    this.node = new GridPane();
    this.node.setAlignment(Pos.CENTER);
    for (int r = 0; r < ChessConstants.BOARD_SIZE; r++) {
      for (int c = 0; c < ChessConstants.BOARD_SIZE; c++) {
        this.spaces[r][c] = new ChessSpace(this, r, c, 7-r, c);
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
    if (position != null) {
      try {
        // draw from existing images
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
    this.engine = ChessEngine.create(this.position, ChessEngineConfiguration.of(Map.of(
      "turnOn", "true",
      "depth", "12",
      "quiescenceDepth", "6",
      "searcherType", "negamax",
      "abPruning", "true",
      "evaluatorName", "pawnstructure",
      "evaluatorConfig", Map.ofEntries(
        Map.entry("vPawn", "1"),
        Map.entry("vKnight", "2.95"),
        Map.entry("vBishop", "3.05"),
        Map.entry("vRook", "5"),
        Map.entry("vQueen", "9"),
        Map.entry("vKing", "1000"),
        Map.entry("aSpace", "0.02"),
        Map.entry("aEnemySpace", "0.02"),
        Map.entry("aEnemyPiece", "0.02"),
        Map.entry("aKingSpace", "0.02"),
        Map.entry("pEdge", "-0.1"),
        Map.entry("pSide", "-0.02"),
        Map.entry("pCenter", "0.04"),
        Map.entry("pDoubled", "-0.2"),
        Map.entry("pIsolated", "-0.2"),
        Map.entry("pPassed", "0.3")
      )
    )));
    this.timeStart = Instant.now().toEpochMilli();
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
    if (!this.position.isWhiteTurn()) {
      return;
    }
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
      this.engine.playMove(mv);
      break;
    }
    this.propagatePosition();
    this.clearSelectedAnnotations();
    if (!this.position.isWhiteTurn()) {
      long timeS = this.timeStart;
      new java.util.Timer().schedule( 
        new java.util.TimerTask() {
          @Override
          public void run() {
            ChessMove mv = engine.getTopMove();
            position = position.getChildren().get(mv);
            engine.playMove(mv);
            propagatePosition();
            clearSelectedAnnotations();
            timeStart = Instant.now().toEpochMilli();
          }
        }, 
        Instant.now().toEpochMilli() - timeS
      );
    }
  }

  private void propagatePosition() {
    if (this.position == null) {
      return;
    }
    this.position.generateMoves();
    this.position.trimCheckMoves();
    byte[] mailbox = this.position.getMailbox();
    for (int p = 0; p < mailbox.length; p++) {
      int r = p % 8;
      int c = p / 8;
      this.spaces[r][c].setPiece(ChessPiece.fromByte((mailbox[p])));
    }
  }
}
