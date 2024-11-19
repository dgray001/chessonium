package frontend;

import chess.ChessPiece;
import chess.ChessPosition;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import utilities.Logger;

public class ChessSpace implements Clickable {
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

  public ChessSpace(GridPane grid, int r, int c) {
    this.r = r;
    this.c = c;
    this.pane = new Pane();
    this.refreshBackgroundColor();
    pane.prefWidthProperty().bind(Bindings.min(grid.widthProperty().divide(ChessPosition.BOARD_SIZE), grid.heightProperty().divide(ChessPosition.BOARD_SIZE)));
    pane.prefHeightProperty().bind(Bindings.min(grid.widthProperty().divide(ChessPosition.BOARD_SIZE), grid.heightProperty().divide(ChessPosition.BOARD_SIZE)));
    grid.add(pane, c, r);
    Clickable.instantiate(this);
  }

  public Node getNode() {
    return this.pane;
  }

  public void setImage(ImageView img) {
    img.fitHeightProperty().bind(this.pane.heightProperty());
    img.fitWidthProperty().bind(this.pane.widthProperty());
    this.pane.getChildren().add(img);
  }

  public void hover() {
    this.refreshBackgroundColor();
  }

  public void dehover() {
    this.refreshBackgroundColor();
  }

  public void press() {
    if (piece != null) {
      // TODO: check left vs right click
      this.selected = true;
    }
    this.refreshBackgroundColor();
  }

  public void release() {
    if (this.hovered) {
      if (this.selected) {
        if (this.justSelected) {
          this.selected = false;
        }
        this.justSelected = !this.justSelected;
      }
    } else {
      this.selected = false;
      this.justSelected = false;
    }
    this.refreshBackgroundColor();
  }

  public void clearAnnotations() {
    if (this.selected) {
      this.selected = false;
    } else {
      this.justSelected = false;
    }
    this.refreshBackgroundColor();
  }

  private void refreshBackgroundColor() {
    if (this.selected) {
      pane.setStyle("-fx-background-color:#819669");
      return;
    }
    pane.setStyle(((r + c) % 2 == 0) ? "-fx-background-color:#b58863" : "-fx-background-color:#f0d9b5");
  }
}
