package engine;

import chess.ChessPosition;

public class Evaluator_Equal extends Evaluator {
  public boolean _setConfig(String k, String v) {
    return false;
  }

  public float _evaluate(ChessPosition p) {
    return 0;
  }
}
