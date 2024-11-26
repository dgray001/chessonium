package frontend;

import java.net.URISyntaxException;

import chess.ChessConstants;
import chess.ChessPiece;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import services.Images;

public class ChessSpace implements Clickable {
  private ChessBoard board;
  @Getter
  private Pane pane;
  @Setter
  private ChessPiece piece;
  private int r;
  private int c;
  private boolean lightSpace;
  @Setter
  private boolean hovered = false;
  @Setter
  private boolean pressed = false;
  private boolean selected = false;
  private boolean justSelected = false;
  private boolean moveTarget = false;
  private Node moveTargetImage;
  private ImageView pieceImage;

  public ChessSpace(ChessBoard board, int r, int c, int r_draw, int c_draw) {
    this.board = board;
    this.r = r;
    this.c = c;
    this.lightSpace = ((this.r + this.c) % 2) != 0;
    this.pane = new StackPane();
    this.refreshBackgroundColor();
    pane.prefWidthProperty().bind(Bindings.min(board.getNode().widthProperty().divide(ChessConstants.BOARD_SIZE), board.getNode().heightProperty().divide(ChessConstants.BOARD_SIZE)));
    pane.prefHeightProperty().bind(Bindings.min(board.getNode().widthProperty().divide(ChessConstants.BOARD_SIZE), board.getNode().heightProperty().divide(ChessConstants.BOARD_SIZE)));
    board.getNode().add(pane, c_draw, r_draw);
    if (r_draw == 7) {
      Text t = new Text();
      t.setFont(new Font("Montserrat", 12));
      t.setText(Character.toString((char) ('A' + c)));
      t.setFill(Color.web(this.lightSpace ? "#987455" : "#f0d9b5"));
      t.setStyle("-fx-font-weight: bold");
      VBox box = new VBox();
      box.setAlignment(Pos.BOTTOM_LEFT);
      box.setPadding(new Insets(0, 0, 0, 2));
      box.getChildren().add(t);
      pane.getChildren().add(box);
    }
    if (c_draw == 7) {
      Text t = new Text();
      t.setFont(new Font("Montserrat", 12));
      t.setText(Integer.toString(r + 1));
      t.setFill(Color.web(this.lightSpace ? "#987455" : "#f0d9b5"));
      t.setStyle("-fx-font-weight: bold");
      VBox box = new VBox();
      box.setAlignment(Pos.TOP_RIGHT);
      box.setPadding(new Insets(0, 2, 0, 0));
      box.getChildren().add(t);
      pane.getChildren().add(box);
    }
    Clickable.instantiate(this);
  }

  public Node getNode() {
    return this.pane;
  }

  public ImageView getPieceImage() throws URISyntaxException {
    if (this.piece == null) {
      return null;
    }
    return new ImageView(Images.get(piece.imagePath()));
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
      circ.setFill(Color.web(this.lightSpace ? "#829769" : "#646f41"));
      circ.radiusProperty().bind(this.pane.widthProperty().multiply(0.15));
      this.moveTargetImage = circ;
      this.pane.getChildren().add(circ);
    } else {
      ImageView imgV = new ImageView(Images.get("attack_" + (this.lightSpace ? "light" : "dark")));
      imgV.fitHeightProperty().bind(this.pane.heightProperty());
      imgV.fitWidthProperty().bind(this.pane.widthProperty());
      this.moveTargetImage = imgV;
      this.pane.getChildren().add(imgV);
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
      this.pane.setStyle(this.lightSpace ? "-fx-background-color:#adb17e" : "-fx-background-color:#847945");
      if (this.moveTargetImage != null) {
        this.moveTargetImage.setVisible(false);
      }
    } else {
      if (this.moveTarget && this.moveTargetImage != null) {
        this.moveTargetImage.setVisible(true);
      }
      if (this.selected) {
        this.pane.setStyle(this.lightSpace ? "-fx-background-color:#819669" : "-fx-background-color:#646d40");
      } else {
        this.pane.setStyle(this.lightSpace ? "-fx-background-color:#f0d9b5" : "-fx-background-color:#b58863");
      }
    }
  }
}
