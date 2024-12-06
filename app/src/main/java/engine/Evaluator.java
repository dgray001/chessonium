package engine;

import java.util.HashMap;
import java.util.Map;

import chess.ChessPosition;
import lombok.Getter;
import utilities.Logger;

abstract class Evaluator {
  @Getter
  private HashMap<Long, Float> transpositionTable = new HashMap<Long, Float>();
  @Getter
  private int maxTranspositionTableSize = 100_000;
  private int n = 0;

  public static Evaluator create(String evaluatorName) {
    return Evaluator.create(evaluatorName, new HashMap<String, String>());
  }
  public static Evaluator create(String evaluatorName, Map<String, String> config) {
    Evaluator evaluator;
    switch(evaluatorName.toLowerCase().trim()) {
      case "equal":
        evaluator = new Evaluator_Equal();
        break;
      case "material":
        evaluator = new Evaluator_Material();
        break;
      case "activity":
        evaluator = new Evaluator_Activity();
        break;
      case "pawnstructure":
        evaluator = new Evaluator_PawnStructure();
        break;
      default:
        Logger.err("Unknown evaluator", evaluatorName);
        return null;
    }
    evaluator.setConfig(config);
    return evaluator;
  }

  void setConfig(Map<String, String> config) {
    for (Map.Entry<String, String> entry : config.entrySet()) {
      this._setConfig(entry.getKey().trim(), entry.getValue().trim());
    }
  }

  protected abstract boolean _setConfig(String k, String v);

  public static float configFloat(String v) {
    try {
      return Float.parseFloat(v);
    } catch(Exception e) {
      Logger.err("Error converting config value to float", e);
    }
    return 0;
  }

  public static int configInt(String v) {
    try {
      return Integer.parseInt(v);
    } catch(Exception e) {
      Logger.err("Error converting config value to int", e);
    }
    return 0;
  }

  public static boolean configBool(String v) {
    try {
      return Boolean.parseBoolean(v);
    } catch(Exception e) {
      Logger.err("Error converting config value to boolean", e);
    }
    return false;
  }

  public float evaluate(ChessPosition p) {
    if (this.transpositionTable.containsKey(p.getKey())) {
      this.n++;
      return this.transpositionTable.get(p.getKey());
    }
    float e = p.isEvaluated() ? p.getEvaluation() : this._evaluate(p);
    if (this.transpositionTable.size() < this.maxTranspositionTableSize) {
      this.transpositionTable.put(p.getKey(), e);
    }
    return e;
  }

  public void clearTranspositionTable() {
    Logger.log("Transposition table hits: " + this.n);
    this.transpositionTable.clear();
    this.n = 0;
  }

  protected abstract float _evaluate(ChessPosition p);
}
