package chess;

public record ChessMove(
  int piece,
  long start,
  long end,
  boolean castling,
  boolean enPassant
) {}
