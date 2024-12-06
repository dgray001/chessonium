package engine;

import chess.ChessPosition;

public class Evaluator_Material extends Evaluator {
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
    e += this.vPawn * Long.bitCount(p.getWPawns());
    e += this.vKnight * Long.bitCount(p.getWKings());
    e += this.vBishop * Long.bitCount(p.getWBishops());
    e += this.vRook * Long.bitCount(p.getWRooks());
    e += this.vQueen * Long.bitCount(p.getWQueens());
    e += this.vKing * Long.bitCount(p.getWKings());
    e -= this.vPawn * Long.bitCount(p.getBPawns());
    e -= this.vKnight * Long.bitCount(p.getBKings());
    e -= this.vBishop * Long.bitCount(p.getBBishops());
    e -= this.vRook * Long.bitCount(p.getBRooks());
    e -= this.vQueen * Long.bitCount(p.getBQueens());
    e -= this.vKing * Long.bitCount(p.getBKings());
    return e;
  }
}
