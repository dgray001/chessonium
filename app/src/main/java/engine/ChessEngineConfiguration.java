package engine;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import utilities.Logger;

public class ChessEngineConfiguration {
  // the evaluator and its configuration
  @Getter
  private String evaluatorName = "equal";
  @Getter
  private Map<String, String> evaluatorConfig;
  // maximum depth for iterative deepening
  @Getter
  private int depth = 1;
  // additional depth for quiescence
  @Getter
  private int quiescenceDepth = 0;
  // which search function to use
  @Getter
  private SearcherType searcherType = SearcherType.MINIMAX;
  // whether the search function should implement alpha beta pruning
  @Getter
  private boolean abPruning = false;

  // TODO: add configurable ab pruning
  // TODO: add definitions for what quiescence can mean
  // TODO: add ways to sort moves (including based on previous depths?)

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Depth: " + this.depth);
    sb.append("\nQuiescence Depth: " + this.quiescenceDepth);
    sb.append("\nSearcher Type: " + this.searcherType.name().toLowerCase());
    sb.append("\nAlpha-Beta Pruning: " + this.abPruning);
    sb.append("\nEvaluator (" + this.evaluatorName + "):");
    for (Map.Entry<String, String> entry : this.evaluatorConfig.entrySet()) {
      sb.append("\n  " + entry.getKey() + ": " + entry.getValue());
    }
    return sb.toString();
  }

  @SuppressWarnings("unchecked")
	public static ChessEngineConfiguration of(Map<String, Object> input) {
    ChessEngineConfiguration config = new ChessEngineConfiguration();
    for (Map.Entry<String, Object> entry : input.entrySet()) {
      try {
        String v = entry.getValue().toString().trim();
        switch(entry.getKey().trim()) {
          case "depth":
            config.depth = Evaluator.configInt(v);
            break;
          case "quiescenceDepth":
            config.quiescenceDepth = Evaluator.configInt(v);
            break;
          case "searcherType":
            if (v.toLowerCase().equals("negamax")) {
              config.searcherType = SearcherType.NEGAMAX;
            } else {
              config.searcherType = SearcherType.MINIMAX;
            }
            break;
          case "abPruning":
            config.abPruning = Evaluator.configBool(v);
            break;
          case "evaluatorName":
            config.evaluatorName = v;
            break;
          case "evaluatorConfig":
            config.evaluatorConfig = (Map<String, String>) entry.getValue();
            break;
          default:
            Logger.err("Unknown chess engine configuration key", entry.getKey());
            break;
        }
      } catch (Exception e) {
        Logger.err("Exception creating engine configuration", e);
      }
    }
    return config;
  }
}
