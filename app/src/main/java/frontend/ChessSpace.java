package frontend;

import java.net.URISyntaxException;

import chess.ChessPiece;
import chess.ChessPosition;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;
import utilities.Logger;

public class ChessSpace implements Clickable {
  private ChessBoard board;
  @Getter
  private Pane pane;
  @Setter
  private ChessPiece piece;
  private int r;
  private int c;
  @Setter
  private boolean hovered = false;
  @Setter
  private boolean pressed = false;
  private boolean selected = false;
  private boolean justSelected = false;
  private boolean moveTarget = false;
  private Node moveTargetImage;
  private ImageView pieceImage;

  public ChessSpace(ChessBoard board, int r, int c) {
    this.board = board;
    this.r = r;
    this.c = c;
    this.pane = new StackPane();
    this.refreshBackgroundColor();
    pane.prefWidthProperty().bind(Bindings.min(board.getNode().widthProperty().divide(ChessPosition.BOARD_SIZE), board.getNode().heightProperty().divide(ChessPosition.BOARD_SIZE)));
    pane.prefHeightProperty().bind(Bindings.min(board.getNode().widthProperty().divide(ChessPosition.BOARD_SIZE), board.getNode().heightProperty().divide(ChessPosition.BOARD_SIZE)));
    board.getNode().add(pane, c, r);
    Clickable.instantiate(this);
  }

  public Node getNode() {
    return this.pane;
  }

  public ImageView getPieceImage() throws URISyntaxException {
    if (this.piece == null) {
      return null;
    }
    // TODO: get img from image service which caches images
    Image img = new Image(ClassLoader.getSystemClassLoader().getResource("images/" + piece.imagePath() + "_medium.png").toURI().toString());
    return new ImageView(img);
  }

  public void setPieceImage(ImageView pieceImage) {
    if (this.pieceImage != null) {
      this.pane.getChildren().remove(this.pieceImage);
    }
    this.pieceImage = pieceImage;
    if (pieceImage != null) {
      pieceImage.fitHeightProperty().bind(this.pane.heightProperty());
      pieceImage.fitWidthProperty().bind(this.pane.widthProperty());
      this.pane.getChildren().addFirst(pieceImage);
    }
  }

  public void hover() {
    this.refreshBackgroundColor();
  }

  public void dehover() {
    this.refreshBackgroundColor();
  }

  public void press() {
    if (this.moveTarget) {
      this.board.moveTargetPressed(this.r, this.c);
      return;
    }
    this.board.pressFromSpace();
    if (this.piece != null) {
      // TODO: check left vs right click
      this.selected = true;
      this.board.spaceSelected(this.r, this.c);
    }
    this.refreshBackgroundColor();
  }

  public void release() {
    if (this.hovered) {
      if (this.selected) {
        if (this.justSelected) {
          this.selected = false;
          this.board.clearSelectedAnnotations();
        }
        this.justSelected = !this.justSelected;
      }
    } else {
      this.selected = false;
      this.justSelected = false;
      this.board.clearSelectedAnnotations();
    }
    this.refreshBackgroundColor();
  }

  public void setMoveTarget() {
    this.moveTarget = true;
    if (this.piece == null) {
      Circle circ = new Circle();
      circ.setFill(Color.web(((this.r + this.c) % 2 == 0) ? "#646f41" : "#829769"));
      circ.radiusProperty().bind(this.pane.widthProperty().multiply(0.15));
      this.moveTargetImage = circ;
      this.pane.getChildren().add(circ);
    } else {
      // TODO: implement
    }
  }

  public void clearSelectedAnnotations() {
    if (this.selected) {
      this.selected = false;
    } else {
      this.justSelected = false;
    }
    if (this.moveTarget) {
      this.moveTarget = false;
      this.pane.getChildren().remove(this.moveTargetImage);
      this.moveTargetImage = null;
    }
    this.refreshBackgroundColor();
  }

  private void refreshBackgroundColor() {
    if (this.hovered && this.moveTarget) {
      this.pane.setStyle(((this.r + this.c) % 2 == 0) ? "-fx-background-color:#646f41" : "-fx-background-color:#829769");
    } else if (this.selected) {
      this.pane.setStyle(((this.r + this.c) % 2 == 0) ? "-fx-background-color:#646d40" : "-fx-background-color:#819669");
    } else {
      this.pane.setStyle(((this.r + this.c) % 2 == 0) ? "-fx-background-color:#b58863" : "-fx-background-color:#f0d9b5");
    }
  }
}
