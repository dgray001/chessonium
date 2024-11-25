package engine;

import java.util.Iterator;
import java.util.Map;

import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessResult;
import lombok.Getter;
import utilities.Logger;
import utilities.MutableBoolean;

public class Searcher {
  private ChessPosition p;
  private Evaluator e;
  @Getter
  private int n = 0;
  @Getter
  private ChessMove bestMove = null;
  private float evaluation = 0;

  void configure(ChessPosition p, Evaluator e) {
    this.p = p;
    this.e = e;
  }

  void setPosition(ChessPosition p) {
    this.p = p;
    this.n = 0;
    this.bestMove = null;
    this.evaluation = this.p.isWhiteTurn() ? -Float.MAX_VALUE : Float.MAX_VALUE;
  }

  public boolean search(int limit, MutableBoolean stop) {
    for (int d = 1; d <= limit; d++) {
      Logger.log("Searching at depth", d);
      ChessMove mv = this.searchDepth(d, stop);
      if (stop.get()) {
        break;
      }
      this.bestMove = mv;
      Logger.log("Finshed depth " + d + " (" + this.evaluation + "): " + this.bestMove);
    }
    return false;
  }

  private ChessMove searchDepth(int d, MutableBoolean stop) {
    ChessMove mv = null;
    float bestScore = this.p.isWhiteTurn() ? -Float.MAX_VALUE : Float.MAX_VALUE;
    this.p.generateMoves(true);
    this.p.trimCheckMoves(true);
    for (Map.Entry<ChessMove, ChessPosition> entry : this.p.getChildren().entrySet()) {
      float score = this.minimax(entry.getValue(), d - 1, stop);
      if ((this.p.isWhiteTurn() && score > bestScore) || (!this.p.isWhiteTurn() && score < bestScore)) {
        bestScore = score;
        mv = entry.getKey();
      }
      if (stop.get()) {
        return null;
      }
    }
    this.evaluation = bestScore;
    return mv;
  }

  private float minimax(ChessPosition p, int d, MutableBoolean stop) {
    if (stop.get()) {
      return 0;
    }
    this.n++;
    if (d == 0) {
      return this.e._evaluate(p);
    }
    p.generateMoves();
    p.trimCheckMoves();
    ChessResult result = p.getGameResult();
    if (result != ChessResult.NOT_OVER) {
      return ChessResult.resultScoreFloat(result);
    }
    float bestScore = p.isWhiteTurn() ? -Float.MAX_VALUE : Float.MAX_VALUE;
    Iterator<ChessPosition> it = p.getChildren().values().iterator();
    while (it.hasNext()) {
      ChessPosition nextP = it.next();
      float score = this.minimax(nextP, d - 1, stop);
      if ((p.isWhiteTurn() && score > bestScore) || (!p.isWhiteTurn() && score < bestScore)) {
        bestScore = score;
      }
      it.remove();
    }
    return bestScore;
  }
}
