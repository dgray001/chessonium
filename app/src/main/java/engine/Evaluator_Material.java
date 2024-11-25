package engine;

import chess.ChessPosition;
import utilities.Logger;

public class Evaluator_Material implements Evaluator {
  protected float vPawn;
  protected float vKnight;
  protected float vBishop;
  protected float vRook;
  protected float vQueen;
  protected float vKing;

  public boolean _setConfig(String k, String v) {
    switch(k) {
      case "vPawn":
        this.vPawn = Evaluator.configFloat(v);
        break;
      case "vKnight":
        this.vKnight = Evaluator.configFloat(v);
        break;
      case "vBishop":
        this.vBishop = Evaluator.configFloat(v);
        break;
      case "vRook":
        this.vRook = Evaluator.configFloat(v);
        break;
      case "vQueen":
        this.vQueen = Evaluator.configFloat(v);
        break;
      case "vKing":
        this.vKing = Evaluator.configFloat(v);
        break;
      default:
        return false;
    }
    return true;
  }

  public float _evaluate(ChessPosition p) {
    float e = 0;
    long bb = p.getWPawns();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vPawn;
      bb &= ~lsb;
    }
    bb = p.getWKnights();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vKnight;
      bb &= ~lsb;
    }
    bb = p.getWBishops();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vBishop;
      bb &= ~lsb;
    }
    bb = p.getWRooks();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vRook;
      bb &= ~lsb;
    }
    bb = p.getWQueens();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vQueen;
      bb &= ~lsb;
    }
    bb = p.getWKings();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vKing;
      bb &= ~lsb;
    }
    bb = p.getBPawns();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vPawn;
      bb &= ~lsb;
    }
    bb = p.getBKnights();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vKnight;
      bb &= ~lsb;
    }
    bb = p.getBBishops();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vBishop;
      bb &= ~lsb;
    }
    bb = p.getBRooks();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vRook;
      bb &= ~lsb;
    }
    bb = p.getBQueens();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vQueen;
      bb &= ~lsb;
    }
    bb = p.getBKings();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vKing;
      bb &= ~lsb;
    }
    return e;
  }
}
