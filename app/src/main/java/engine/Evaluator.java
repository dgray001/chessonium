package engine;

import chess.ChessPosition;

public interface Evaluator {
  default float evaluate(ChessPosition p) {
    if (p.isEvaluated()) {
      return p.getEvaluation();
    }
    return this._evaluate(p);
  }
  float _evaluate(ChessPosition p);
}

class Evaluator_Equal implements Evaluator {
  public float _evaluate(ChessPosition p) {
    return 0;
  }
}

class Evaluator_ implements Evaluator {
  public float _evaluate(ChessPosition p) {
    return 0;
  }
}
