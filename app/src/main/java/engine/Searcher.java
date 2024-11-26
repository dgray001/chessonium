package engine;

import chess.ChessMove;
import chess.ChessPosition;
import lombok.Getter;
import utilities.Logger;
import utilities.MutableBoolean;

public abstract class Searcher {
  protected ChessPosition p;
  protected Evaluator e;
  protected int depthLimit;
  protected int quiescenceDepth;
  @Getter
  protected int n = 0;
  @Getter
  protected ChessMove bestMove = null;
  protected float evaluation = 0;

  public static Searcher create(ChessPosition p, ChessEngineConfiguration c) {
    Searcher s = null;
    switch(c.getSearcherType()) {
      case MINIMAX:
        if (c.isAbPruning()) {
          s = new Searcher_MinimaxAB();
        } else {
          s = new Searcher_Minimax();
        }
        break;
      case NEGAMAX:
        if (c.isAbPruning()) {
          s = new Searcher_NegamaxAB();
        } else {
          s = new Searcher_Negamax();
        }
        break;
      default:
        Logger.err("Unknown searcher type", c.getSearcherType());
        return null;
    }
    s.p = p.copyPosition();
    s.depthLimit = c.getDepth();
    s.quiescenceDepth = c.getQuiescenceDepth();
    s.e = Evaluator.create(c.getEvaluatorName(), c.getEvaluatorConfig());
    return s;
  }

  public void setPosition(ChessPosition p) {
    this.p = p.copyPosition();
    this.n = 0;
    this.bestMove = null;
    this.evaluation = this.p.isWhiteTurn() ? -Float.MAX_VALUE : Float.MAX_VALUE;
  }

  public abstract void search(MutableBoolean stop);
}
