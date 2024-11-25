package engine;

import java.util.HashMap;
import java.util.Map;

import chess.ChessPosition;
import utilities.Logger;

public interface Evaluator {
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
      default:
        Logger.err("Unknown evaluator", evaluatorName);
        return null;
    }
    evaluator.setConfig(config);
    return evaluator;
  }

  default void setConfig(Map<String, String> config) {
    for (Map.Entry<String, String> entry : config.entrySet()) {
      this._setConfig(entry.getKey().trim(), entry.getValue().trim());
    }
  }

  boolean _setConfig(String k, String v);

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

  default float evaluate(ChessPosition p) {
    if (p.isEvaluated()) {
      return p.getEvaluation();
    }
    return this._evaluate(p);
  }

  float _evaluate(ChessPosition p);
}
