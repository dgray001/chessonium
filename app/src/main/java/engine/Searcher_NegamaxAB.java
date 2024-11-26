package engine;

import java.util.Iterator;
import java.util.Map;

import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessResult;
import utilities.Logger;
import utilities.MutableBoolean;

public class Searcher_NegamaxAB extends Searcher {
  private ChessMove[] bestLine;

  public void search(MutableBoolean stop) {
    Logger.log("Initial evaluation at depth 0: " + this.e.evaluate(this.p));
    for (int d = 1; d <= this.depthLimit; d++) {
      Logger.log("Searching at depth", d);
      this.bestLine = this.searchDepthNegamax(d, -Float.MAX_VALUE, Float.MAX_VALUE, stop);
      if (stop.get()) {
        break;
      }
      this.bestMove = this.bestLine[0];
      Logger.log("Finished depth " + d + " (" + this.evaluation + ")", this.bestLine);
    }
  }

  private ChessMove[] searchDepthNegamax(int d, float a, float b, MutableBoolean stop) {
    ChessMove[] line = new ChessMove[d];
    float bestScore = -Float.MAX_VALUE;
    this.p.generateMoves(true);
    this.p.trimCheckMoves(true);
    int color = this.p.isWhiteTurn() ? 1 : -1;
    for (Map.Entry<ChessMove, ChessPosition> entry : this.p.getChildren().entrySet()) {
      ChessMove[] currentLine = new ChessMove[d-1];
      float score = -this.negamax(entry.getValue(), d - 1, -b, -a, -color, currentLine, stop);
      if (score > bestScore) {
        bestScore = score;
        line[0] = entry.getKey();
        System.arraycopy(currentLine, 0, line, 1, d - 1);
      }
      if (stop.get()) {
        return null;
      }
    }
    this.evaluation = color * bestScore;
    return line;
  }

  private float negamax(ChessPosition p, int d, float a, float b, int color, ChessMove[] line, MutableBoolean stop) {
    if (stop.get()) {
      return 0;
    }
    this.n++;
    if (d == 0) {
      return color * this.e.evaluate(p);
    }
    p.generateMoves();
    p.trimCheckMoves();
    ChessResult result = p.getGameResult();
    if (result != ChessResult.NOT_OVER) {
      return color * ChessResult.resultScoreFloat(result);
    }
    float bestScore = -Float.MAX_VALUE;
    Iterator<Map.Entry<ChessMove, ChessPosition>> it = p.getChildren().entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<ChessMove, ChessPosition> entry = it.next();
      ChessMove[] currentLine = new ChessMove[d-1];
      float score = -this.negamax(entry.getValue(), d - 1, -b, -a, -color, currentLine, stop);
      if (score > bestScore) {
        bestScore = score;
        line[0] = entry.getKey();
        System.arraycopy(currentLine, 0, line, 1, d - 1);
      }
      a = Math.max(a, bestScore);
      if (a >= b) {
        p.getChildren().clear();
        break;
      }
      it.remove();
    }
    return bestScore;
  }
}
