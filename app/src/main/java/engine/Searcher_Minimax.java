package engine;

import java.util.Iterator;
import java.util.Map;

import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessResult;
import utilities.Logger;
import utilities.MutableBoolean;

public class Searcher_Minimax extends Searcher {
  private ChessMove[] bestLine;

  public void search(MutableBoolean stop) {
    Logger.log("Initial evaluation at depth 0: " + this.e.evaluate(this.p));
    for (int d = 1; d <= this.depthLimit; d++) {
      Logger.log("Searching at depth", d);
      this.bestLine = this.searchDepthMinimax(d, stop);
      if (stop.get()) {
        break;
      }
      this.bestMove = this.bestLine[0];
      Logger.log("Finished depth " + d + " (" + this.evaluation + ")", this.bestLine);
    }
  }

  private ChessMove[] searchDepthMinimax(int d, MutableBoolean stop) {
    ChessMove[] line = new ChessMove[d];
    float bestScore = this.p.isWhiteTurn() ? -Float.MAX_VALUE : Float.MAX_VALUE;
    this.p.generateMoves(true);
    this.p.trimCheckMoves(true);
    for (Map.Entry<ChessMove, ChessPosition> entry : this.p.getChildren().entrySet()) {
      ChessMove[] currentLine = new ChessMove[d-1];
      float score = this.minimax(entry.getValue(), d - 1, currentLine, (entry.getKey().end() & p.getAllPieces()) == 0, stop);
      if ((this.p.isWhiteTurn() && score > bestScore) || (!this.p.isWhiteTurn() && score < bestScore)) {
        bestScore = score;
        line[0] = entry.getKey();
        System.arraycopy(currentLine, 0, line, 1, d - 1);
      }
      if (stop.get()) {
        return null;
      }
    }
    this.evaluation = bestScore;
    return line;
  }

  private float minimax(ChessPosition p, int d, ChessMove[] line, boolean quiescence, MutableBoolean stop) {
    if (stop.get()) {
      return 0;
    }
    this.n++;
    if (d < 1) {
      float standPat = this.e.evaluate(p);
      if (quiescence || d + this.quiescenceDepth < 1) {
        return standPat;
      }
      return this.quiescenceMinimax(p, d, standPat, stop);
    }
    p.generateMoves();
    p.trimCheckMoves();
    ChessResult result = p.getGameResult();
    if (result != ChessResult.NOT_OVER) {
      return ChessResult.resultScoreFloat(result);
    }
    float bestScore = p.isWhiteTurn() ? -Float.MAX_VALUE : Float.MAX_VALUE;
    Iterator<Map.Entry<ChessMove, ChessPosition>> it = p.getChildren().entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<ChessMove, ChessPosition> entry = it.next();
      ChessMove[] currentLine = new ChessMove[d-1];
      float score = this.minimax(entry.getValue(), d - 1, currentLine, (entry.getKey().end() & p.getAllPieces()) == 0, stop);
      if ((p.isWhiteTurn() && score > bestScore) || (!p.isWhiteTurn() && score < bestScore)) {
        bestScore = score;
        line[0] = entry.getKey();
        System.arraycopy(currentLine, 0, line, 1, d - 1);
      }
      it.remove();
    }
    return bestScore;
  }

  private float quiescenceMinimax(ChessPosition p, int d, float standPat, MutableBoolean stop) {
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
    float bestScore = p.isWhiteTurn() ? -Float.MAX_VALUE : Float.MAX_VALUE;
    Iterator<Map.Entry<ChessMove, ChessPosition>> it = p.getChildren().entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<ChessMove, ChessPosition> entry = it.next();
      float score = this.quiescenceMinimax(entry.getValue(), d - 1, this.e.evaluate(entry.getValue()), stop);
      if ((p.isWhiteTurn() && score > bestScore) || (!p.isWhiteTurn() && score < bestScore)) {
        bestScore = score;
      }
      it.remove();
    }
    return bestScore;
  }
}
