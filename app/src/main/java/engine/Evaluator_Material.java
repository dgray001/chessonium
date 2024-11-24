package engine;

import java.util.Map;

import chess.ChessPosition;

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
    return e;
  }
}