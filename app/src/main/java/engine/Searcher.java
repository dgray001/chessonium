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
  private int depthLimit;
  private int quiescenceDepth;
  private SearcherType searcherType;
  private boolean abPruning;
  @Getter
  private int n = 0;
  @Getter
  private ChessMove bestMove = null;
  private float evaluation = 0;

  void configure(ChessPosition p, ChessEngineConfiguration config) {
    Logger.log("Configuring search with:\n" + config.toString());
    this.p = p;
    this.depthLimit = config.getDepth();
    this.quiescenceDepth = config.getQuiescenceDepth();
    this.searcherType = config.getSearcherType();
    this.abPruning = config.isAbPruning();
    this.e = Evaluator.create(config.getEvaluatorName(), config.getEvaluatorConfig());
  }

  void setPosition(ChessPosition p) {
    this.p = p;
    this.n = 0;
    this.bestMove = null;
    this.evaluation = this.p.isWhiteTurn() ? -Float.MAX_VALUE : Float.MAX_VALUE;
  }

  public void search(MutableBoolean stop) {
    for (int d = 1; d <= this.depthLimit; d++) {
      Logger.log("Searching at depth", d);
      ChessMove mv = null;
      switch(this.searcherType) {
        case SearcherType.MINIMAX:
          if (this.abPruning) {
            mv = this.searchDepthMinimax(d, -Float.MAX_VALUE, Float.MAX_VALUE, stop);
          } else {
            mv = this.searchDepthMinimax(d, stop);
          }
          break;
        case SearcherType.NEGAMAX:
          mv = this.searchDepthNegamax(d, stop);
          break;
        default:
          Logger.log("Unknown searcher type", this.searcherType);
          break;
      }
      if (stop.get()) {
        break;
      }
      this.bestMove = mv;
      Logger.log("Finshed depth " + d + " (" + this.evaluation + "): " + this.bestMove);
    }
  }

  private ChessMove searchDepthMinimax(int d, MutableBoolean stop) {
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

  private ChessMove searchDepthNegamax(int d, MutableBoolean stop) {
    ChessMove mv = null;
    float bestScore = -Float.MAX_VALUE;
    this.p.generateMoves(true);
    this.p.trimCheckMoves(true);
    for (Map.Entry<ChessMove, ChessPosition> entry : this.p.getChildren().entrySet()) {
      float score = this.negamax(entry.getValue(), d - 1, this.p.isWhiteTurn() ? 1 : -1, stop);
      if (score > bestScore) {
        bestScore = score;
        mv = entry.getKey();
      }
      if (stop.get()) {
        return null;
      }
    }
    this.evaluation = this.p.isWhiteTurn() ? bestScore : -bestScore;
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

  private float negamax(ChessPosition p, int d, int color, MutableBoolean stop) {
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
      return color * ChessResult.resultScoreFloat(result);
    }
    float bestScore = -Float.MAX_VALUE;
    Iterator<ChessPosition> it = p.getChildren().values().iterator();
    while (it.hasNext()) {
      ChessPosition nextP = it.next();
      float score = -this.negamax(nextP, d - 1, -color, stop);
      if (score > bestScore) {
        bestScore = score;
      }
      it.remove();
    }
    return bestScore;
  }
}
