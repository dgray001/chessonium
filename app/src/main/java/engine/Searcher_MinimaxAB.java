package engine;

import java.util.Iterator;
import java.util.Map;

import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessResult;
import utilities.Logger;
import utilities.MutableBoolean;

public class Searcher_MinimaxAB extends Searcher {

  public void search(MutableBoolean stop) {
    for (int d = 1; d <= this.depthLimit; d++) {
      Logger.log("Searching at depth", d);
      ChessMove mv = this.searchDepthMinimax(d, -Float.MAX_VALUE, Float.MAX_VALUE, stop);
      if (stop.get()) {
        break;
      }
      this.bestMove = mv;
      Logger.log("Finshed depth " + d + " (" + this.evaluation + "): " + this.bestMove);
    }
  }

  private ChessMove searchDepthMinimax(int d, float a, float b, MutableBoolean stop) {
    ChessMove mv = null;
    float bestScore = this.p.isWhiteTurn() ? -Float.MAX_VALUE : Float.MAX_VALUE;
    this.p.generateMoves(true);
    this.p.trimCheckMoves(true);
    for (Map.Entry<ChessMove, ChessPosition> entry : this.p.getChildren().entrySet()) {
      float score = this.minimax(entry.getValue(), a, b, d - 1, stop);
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

  private float minimax(ChessPosition p, float a, float b, int d, MutableBoolean stop) {
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
      float score = this.minimax(nextP, a, b, d - 1, stop);
      if ((p.isWhiteTurn() && score > bestScore) || (!p.isWhiteTurn() && score < bestScore)) {
        bestScore = score;
      }
      if (p.isWhiteTurn()) {
        a = Math.max(a, score);
        if (b <= a) {
          p.getChildren().clear();
          break;
        }
      } else {
        b = Math.min(b, score);
        if (b <= a) {
          p.getChildren().clear();
          break;
        }
      }
      it.remove();
    }
    return bestScore;
  }
}
