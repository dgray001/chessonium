package engine;

import java.time.Instant;
import java.util.Map;

import chess.ChessMove;
import chess.ChessPosition;
import utilities.Logger;

public class ChessEngine extends Thread {
  private ChessPosition p;
  private final Object lock = new Object();
  private boolean notified = false;
  private int moves = 1;
  private Searcher s = new Searcher();
  private int maxDepth;

  public static ChessEngine create(ChessPosition position) {
    ChessEngine engine = new ChessEngine();
    engine.p = position;
    engine.s.configure(position, Evaluator.create("material", Map.of(
      "vPawn", "1",
      "vKnight", "3",
      "vBishop", "3",
      "vRook", "5",
      "vQueen", "9",
      "vKing", "1000"
    )));
    engine.maxDepth = 2;
    engine.setDaemon(false);
    engine.start();
    return engine;
  }

  public void playMove(ChessMove mv) {
    ChessPosition newP = this.p.getChildren().get(mv);
    if (newP == null) {
      return;
    }
    this.p = newP;
    synchronized (this.lock) {
      this.moves++;
      this.notified = true;
      this.lock.notify();
    }
  }

  public void run() {
    Logger.log("Chessonium engine starting ...");
    Runtime rt = Runtime.getRuntime();
    try {
      while (true) {
        int moves = this.moves;
        this.s.playMove(this.p);
        long ts = Instant.now().toEpochMilli();
        Logger.log("Starting", moves);
        while(!this.notified) {
          if (this.s.search(this.maxDepth, 1000)) {
            break;
          }
          Logger.log("Calculating", this.s.getN(), rt.totalMemory() / (1024 * 1024) + " MB", rt.maxMemory() / (1024 * 1024) + " MB", rt.freeMemory() / (1024 * 1024) + " MB");
        }
        synchronized (this.lock) {
          long t = Instant.now().toEpochMilli() - ts;
          Logger.log("Finished", moves, this.s.getN(), t, 0.01 * Math.round(100 * (this.s.getN() / ((float)t))) + " kn/s");
          while (!this.notified) {
            this.lock.wait();
          }
          this.notified = false;
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      Logger.err("Chessonium engine interrupted", e);
    }
  }
}
