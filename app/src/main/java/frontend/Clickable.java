package frontend;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public interface Clickable {
  static public void instantiate(Clickable o) {
    Node node = o.getNode();
    if (node == null) {
      return;
    }
    node.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        o.setHovered(true);
        o.hover();
      }
    });
    node.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        o.setHovered(false);
        o.dehover();
      }
    });
    node.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        // TODO: handle multiple mouse buttons
        o.setPressed(true);
        o.press();
      }
    });
    node.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {
        o.setPressed(false);
        o.release();
      }
    });
  }

  Node getNode();
  void setHovered(boolean b);
  void setPressed(boolean b);
  default void hover() {}
  default void dehover() {}
  default void press() {}
  default void release() {}
}
