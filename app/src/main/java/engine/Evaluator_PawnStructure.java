package engine;

import chess.ChessConstants;
import chess.ChessPosition;
import utilities.Logger;

public class Evaluator_PawnStructure extends Evaluator_Activity {
  protected float pEdge;
  protected float pSide;
  protected float pCenter;
  protected float pDoubled;
  protected float pIsolated;
  protected float pPassed;

  @Override
  public boolean _setConfig(String k, String v) {
    if (super._setConfig(k, v)) {
      return true;
    }
    switch(k) {
      case "pEdge":
        this.pEdge = Evaluator.configFloat(v);
        break;
      case "pSide":
        this.pSide = Evaluator.configFloat(v);
        break;
      case "pCenter":
        this.pCenter = Evaluator.configFloat(v);
        break;
      case "pDoubled":
        this.pDoubled = Evaluator.configFloat(v);
        break;
      case "pIsolated":
        this.pIsolated = Evaluator.configFloat(v);
        break;
      case "pPassed":
        this.pPassed = Evaluator.configFloat(v);
        break;
      default:
        return false;
    }
    return true;
  }

  @Override
  public float _evaluate(ChessPosition p) {
    float e = super._evaluate(p);
    boolean last1_w = false; // last w's file had pawns
    boolean last2_w = false; // w's file before had pawns
    int lastCount_w = 0; // w's number last file
    boolean last1_b = false; // last b's file had pawns
    boolean last2_b = false; // b's file before had pawns
    int lastCount_b = 0; // b's number last file
    for (int i = 0; i < ChessConstants.BOARD_SIZE; i++) {
      long wf = ChessConstants.files[i] & p.getWPawns();
      long bf = ChessConstants.files[i] & p.getBPawns();
      int wbc = Long.bitCount(wf);
      int bbc = Long.bitCount(bf);
      // pawn files
      if (i == 0 || i == 7) {
        e += this.pEdge * wbc;
        e -= this.pEdge * bbc;
      } else if (i == 1 || i == 6) {
        e += this.pSide * wbc;
        e -= this.pSide * bbc;
      } else if (i == 3 || i == 4) {
        e += this.pCenter * wbc;
        e -= this.pCenter * bbc;
      }
      // doubled pawns
      if (wbc > 1) {
        e += this.pDoubled * (wbc - 1);
      }
      if (bbc > 1) {
        e -= this.pDoubled * (bbc - 1);
      }
      // isolated pawns
      boolean this_w = wf != 0;
      boolean this_b = bf != 0;
      if (!last2_w && last1_w && !this_w) {
        e += this.pIsolated * lastCount_w;
      }
      if (!last2_b && last1_b && !this_b) {
        e -= this.pIsolated * lastCount_b;
      }
      // passed pawns
      if (last1_w && !last2_b && !last1_b && !this_b) {
        e += this.pPassed * lastCount_w;
      }
      if (last1_b && !last2_w && !last1_w && !this_w) {
        e -= this.pPassed * lastCount_b;
      }
      // set for next file
      last2_w = last1_w;
      last2_b = last1_b;
      last1_w = this_w;
      last1_b = this_b;
      lastCount_w = wbc;
      lastCount_b = bbc;
    }
    // check for last file
    if (!last2_w && last1_w) {
      e += this.pIsolated * lastCount_w;
    }
    if (!last2_b && last1_b) {
      e -= this.pIsolated * lastCount_b;
    }
    if (last1_w && !last2_b && !last1_b) {
      e += this.pPassed * lastCount_w;
    }
    if (last1_b && !last2_w && !last1_w) {
      e -= this.pPassed * lastCount_b;
    }
    return e;
  }
}
