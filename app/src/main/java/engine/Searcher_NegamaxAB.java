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
  private ChessMove[] legalMoves;

  public void search(MutableBoolean stop) {
    Logger.log("Initial evaluation at depth 0: " + this.e.evaluate(this.p));
    this.p.generateMoves();
    this.p.trimCheckMoves();
    this.legalMoves = this.p.getChildren().keySet().toArray(new ChessMove[this.p.getChildren().size()]);
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
    float[] scores = new float[this.legalMoves.length];
    for (int i = 0; i < this.legalMoves.length; i++) {
      ChessMove[] currentLine = new ChessMove[d-1];
      scores[i] = -this.negamax(
        this.p.getChildren().get(this.legalMoves[i]),
        d - 1,
        -b,
        -a,
        -color,
        currentLine,
        (this.legalMoves[i].end() & p.getAllPieces()) == 0,
        stop
      );
      if (scores[i] > bestScore) {
        bestScore = scores[i];
        line[0] = this.legalMoves[i];
        System.arraycopy(currentLine, 0, line, 1, d - 1);
      }
      if (stop.get()) {
        return null;
      }
    }
    for (int i = 0; i < this.legalMoves.length - 1; i++) {
      boolean swapped = false;
      for (int j = 0; j < this.legalMoves.length  - i - 1; j++) {
        if (scores[j] < scores[j + 1]) {
          ChessMove t = this.legalMoves[j];
          this.legalMoves[j] = this.legalMoves[j + 1];
          this.legalMoves[j + 1] = t;
          float tf = scores[j];
          scores[j] = scores[j + 1];
          scores[j + 1] = tf;
          swapped = true;
        }
      }
      if (!swapped) {
        break;
      }
    }
    this.evaluation = color * bestScore;
    return line;
  }

  private float negamax(ChessPosition p, int d, float a, float b, int color, ChessMove[] line, boolean quiescence, MutableBoolean stop) {
    if (stop.get()) {
      return 0;
    }
    this.n++;
    if (d < 1) {
      float standPat = color * this.e.evaluate(p);
      if (quiescence || d + this.quiescenceDepth < 1) {
        return standPat;
      }
      return this.quiescenceNegamax(p, d, a, b, color, standPat, stop);
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
      float score = -this.negamax(entry.getValue(), d - 1, -b, -a, -color, currentLine, (entry.getKey().end() & p.getAllPieces()) == 0, stop);
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

  private float quiescenceNegamax(ChessPosition p, int d, float a, float b, int color, float standPat, MutableBoolean stop) {
    if (stop.get()) {
      return 0;
    }
    if (d + this.quiescenceDepth < 1) {
      return standPat;
    }
    this.n++;
    p.generateMoves();
    p.trimCheckMoves();
    ChessResult result = p.getGameResult();
    if (result != ChessResult.NOT_OVER) {
      return ChessResult.resultScoreFloat(result);
    }
    p.trimQuietMoves();
    if (p.getChildren().size() == 0) {
      return standPat;
    }
    float bestScore = standPat;
    Iterator<Map.Entry<ChessMove, ChessPosition>> it = p.getChildren().entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<ChessMove, ChessPosition> entry = it.next();
      float score = -this.quiescenceNegamax(entry.getValue(), d - 1, -b, -a, -color, -color * this.e.evaluate(entry.getValue()), stop);
      if (score > bestScore) {
        bestScore = score;
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
