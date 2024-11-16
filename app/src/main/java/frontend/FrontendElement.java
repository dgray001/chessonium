package frontend;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public interface FrontendElement {
  Node drawInitial();
  void draw(Pane pane);
}
