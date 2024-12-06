package chess;

public enum ChessResult {
  ERROR,
  NOT_OVER,
  DRAW_REPITITION,
  DRAW_50_MOVE_RULE,
  DRAW_WHITE_TIMEOUT,
  DRAW_BLACK_TIMEOUT,
  DRAW_STALEMATE,
  DRAW_INSUFFICIENT_MATERIAL,
  WHITE_CHECKMATE,
  WHITE_RESIGNATION,
  WHITE_TIMEOUT,
  BLACK_CHECKMATE,
  BLACK_RESIGNATION,
  BLACK_TIMEOUT,
  ;

  public static float resultScoreFloat(ChessResult r) {
    switch(r) {
      case WHITE_CHECKMATE:
      case WHITE_RESIGNATION:
      case WHITE_TIMEOUT:
        return Float.MAX_VALUE;
      case BLACK_CHECKMATE:
      case BLACK_RESIGNATION:
      case BLACK_TIMEOUT:
        return -Float.MAX_VALUE;
      default:
        return 0;
    }
  }
}
