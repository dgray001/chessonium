package engine;

import java.util.ArrayDeque;
import java.util.Queue;

import chess.ChessPosition;
import lombok.Getter;

public class Searcher {
  private ChessPosition p;
  private Evaluator e;
  private Queue<ChessPosition> q = new ArrayDeque<>();
  private int d = 0;
  @Getter
  private int n = 0;

  void configure(ChessPosition p, Evaluator e) {
    this.p = p;
    this.e = e;
  }

  void playMove(ChessPosition p) {
    this.p = p;
    this.q.clear();
    this.q.add(p);
    this.d = 0;
    this.n = 0;
    // TODO: keep positions that are part of this position
  }

  boolean search(int limit, int amount) {
    while(amount > 0) {
      ChessPosition pos = this.q.poll();
      if (pos == null) {
        return true;
      }
      pos.setEvaluation(this.e.evaluate(pos));
      this.d = pos.getDepth() - this.p.getDepth();
      this.n++;
      if (this.d < limit) {
        pos.generateMoves();
        pos.trimCheckMoves();
        for (ChessPosition p : pos.getChildren().values()) {
          this.q.add(p);
        }
      }
      amount--;
    }
    return false;
  }
}
