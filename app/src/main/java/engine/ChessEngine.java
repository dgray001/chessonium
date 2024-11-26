package engine;

import java.time.Instant;

import chess.ChessMove;
import chess.ChessPosition;
import utilities.Logger;
import utilities.MutableBoolean;

public class ChessEngine extends Thread {
  private ChessPosition p;
  private final Object lock = new Object();
  private MutableBoolean notified = new MutableBoolean(false);
  private int moves = 1;
  private Searcher s;

  public static ChessEngine create(ChessPosition position, ChessEngineConfiguration config) {
    if (!config.isTurnOn()) {
      return new ChessEngine();
    }
    Logger.log("Configuring chess engine with:\n" + config.toString());
    ChessEngine engine = new ChessEngine();
    engine.p = position;
    engine.s = Searcher.create(position, config);
    engine.setDaemon(false);
    engine.start();
    return engine;
  }

  public void playMove(ChessMove mv) {
    if (!this.isAlive()) {
      return;
    }
    ChessPosition newP = this.p.getChildren().get(mv);
    if (newP == null) {
      return;
    }
    this.p = newP;
    synchronized (this.lock) {
      this.moves++;
      this.notified.set(true);
      this.lock.notify();
    }
  }

  public void run() {
    Logger.log("Chessonium engine starting ...");
    try {
      while (true) {
        int moves = this.moves;
        this.s.setPosition(this.p);
        long ts = Instant.now().toEpochMilli();
        Logger.log("Starting move: " + moves);
        this.s.search(this.notified);
        synchronized (this.lock) {
          long t = Instant.now().toEpochMilli() - ts;
          Logger.log("Finished move " + moves, this.s.getN(), t, 0.01 * Math.round(100 * (this.s.getN() / ((float)t))) + " kn/s");
          while (!this.notified.get()) {
            this.lock.wait();
          }
          this.notified.set(false);
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      Logger.err("Chessonium engine interrupted", e);
    }
  }
}
