package chess;

public record ChessMove(
  int piece,
  long start,
  long end,
  boolean castling,
  boolean isEnPassant,
  long enPassant
) {
  public static ChessMove createChessMove(int piece, long start, long end) {
    return new ChessMove(piece, start, end, false, false, 0);
  }
  public static ChessMove createChessMove(int piece, long start, long end, boolean castling, boolean isEnPassant) {
    return new ChessMove(piece, start, end, castling, isEnPassant, 0);
  }
  public static ChessMove createChessMove(int piece, long start, long end, long enPassant) {
    return new ChessMove(piece, start, end, false, false, enPassant);
  }
  public static ChessMove createChessMove(int piece, long start, long end, boolean castling, boolean isEnPassant, long enPassant) {
    return new ChessMove(piece, start, end, castling, isEnPassant, enPassant);
  }
}
