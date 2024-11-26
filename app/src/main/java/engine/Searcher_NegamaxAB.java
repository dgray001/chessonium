package engine;

import utilities.MutableBoolean;

public class Searcher_NegamaxAB extends Searcher {

  public void search(MutableBoolean stop) {}
  

  /*public void search(MutableBoolean stop) {
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
  }*/
}
