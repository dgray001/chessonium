package frontend;

import javafx.scene.Node;
import utilities.Logger;

public abstract class FrontendElement {
  boolean _drawInitialCalled = false;
  public Node drawInitial() {
    if (_drawInitialCalled) {
      Logger.err("drawInitial already called");
      return null;
    }
    this._drawInitialCalled = true;
    return _drawInitial();
  }
  abstract Node _drawInitial();

  abstract void draw();
}
