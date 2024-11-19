package chess;

import utilities.Bitwise;

public class ChessPiece {
  private ChessPieceType type;
  private boolean color;

  public static int WHITE_PAWN = (new ChessPiece(ChessPieceType.PAWN, true)).hashCode();
  public static int WHITE_KNIGHT = (new ChessPiece(ChessPieceType.KNIGHT, true)).hashCode();
  public static int WHITE_BISHOP = (new ChessPiece(ChessPieceType.BISHOP, true)).hashCode();
  public static int WHITE_ROOK = (new ChessPiece(ChessPieceType.ROOK, true)).hashCode();
  public static int WHITE_QUEEN = (new ChessPiece(ChessPieceType.QUEEN, true)).hashCode();
  public static int WHITE_KING = (new ChessPiece(ChessPieceType.KING, true)).hashCode();
  public static int BLACK_PAWN = (new ChessPiece(ChessPieceType.PAWN, false)).hashCode();
  public static int BLACK_KNIGHT = (new ChessPiece(ChessPieceType.KNIGHT, false)).hashCode();
  public static int BLACK_BISHOP = (new ChessPiece(ChessPieceType.BISHOP, false)).hashCode();
  public static int BLACK_ROOK = (new ChessPiece(ChessPieceType.ROOK, false)).hashCode();
  public static int BLACK_QUEEN = (new ChessPiece(ChessPieceType.QUEEN, false)).hashCode();
  public static int BLACK_KING = (new ChessPiece(ChessPieceType.KING, false)).hashCode();
  public static int[] WHITE_ALL_PIECES = new int[]{WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING};
  public static int[] BLACK_ALL_PIECES = new int[]{BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING};

  public ChessPiece(ChessPieceType type, boolean color) {
    this.type = type;
    this.color = color;
  }

  public static ChessPiece fromInt(int i) {
    short[] s = Bitwise.split(i);
    if (s[1] == 0) {
      return null;
    }
    return new ChessPiece(ChessPieceType.values()[s[1]], s[0] > 0);
  }

  public String imagePath() {
    return this.type.toString() + "_" + (this.color ? "white" : "black");
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    ChessPiece piece = (ChessPiece) obj;
    return this.type == piece.type && this.color == piece.color;
  }

  @Override
  public int hashCode() {
    return Bitwise.combine(this.color, this.type.getByte());
  }

  public static boolean getColor(int piece) {
    return (piece << 16) > 0;
  }
}
