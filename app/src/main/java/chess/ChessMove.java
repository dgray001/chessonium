package chess;

public record ChessMove(
  byte piece,
  long start,
  long end,
  byte castling,
  boolean isEnPassant,
  long enPassant,
  byte promotionPiece
) {
  public static ChessMove createChessMove(byte piece, long start, long end) {
    return new ChessMove(piece, start, end, (byte) 0, false, 0, (byte) 0);
  }
  public static ChessMove createChessMove(byte piece, long start, long end, boolean isEnPassant) {
    return new ChessMove(piece, start, end, (byte) 0, isEnPassant, 0, (byte) 0);
  }
  public static ChessMove createChessMove(byte piece, long start, long end, long enPassant) {
    return new ChessMove(piece, start, end, (byte) 0, false, enPassant, (byte) 0);
  }
  public static ChessMove createChessMove(byte piece, long start, long end, byte castling, byte promotionPiece) {
    return new ChessMove(piece, start, end, castling, false, 0, promotionPiece);
  }
  public static ChessMove createChessMove(byte piece, long start, long end, byte castling, boolean isEnPassant, long enPassant, byte promotionPiece) {
    return new ChessMove(piece, start, end, castling, isEnPassant, enPassant, promotionPiece);
  }
  public static ChessMove createChessMove(ChessMove mv, byte promotionPiece) {
    return new ChessMove(mv.piece(), mv.start(), mv.end(), mv.castling(), mv.isEnPassant(), mv.enPassant(), promotionPiece);
  }
}
