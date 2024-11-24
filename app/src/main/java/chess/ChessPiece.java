package chess;

public class ChessPiece {
  private ChessPieceType type;
  private boolean color;

  public static final byte WHITE_BIT = 16;
  public static final byte WHITE_PAWN = ChessPieceType.PAWN_VALUE | WHITE_BIT;
  public static final byte WHITE_KNIGHT = ChessPieceType.KNIGHT_VALUE | WHITE_BIT;
  public static final byte WHITE_BISHOP = ChessPieceType.BISHOP_VALUE | WHITE_BIT;
  public static final byte WHITE_ROOK = ChessPieceType.ROOK_VALUE | WHITE_BIT;
  public static final byte WHITE_QUEEN = ChessPieceType.QUEEN_VALUE | WHITE_BIT;
  public static final byte WHITE_KING = ChessPieceType.KING_VALUE | WHITE_BIT;
  public static final byte BLACK_PAWN = ChessPieceType.PAWN_VALUE;
  public static final byte BLACK_KNIGHT = ChessPieceType.KNIGHT_VALUE;
  public static final byte BLACK_BISHOP = ChessPieceType.BISHOP_VALUE;
  public static final byte BLACK_ROOK = ChessPieceType.ROOK_VALUE;
  public static final byte BLACK_QUEEN = ChessPieceType.QUEEN_VALUE;
  public static final byte BLACK_KING = ChessPieceType.KING_VALUE;
  public static final byte[] WHITE_ALL_PIECES = new byte[]{WHITE_PAWN, WHITE_KNIGHT, WHITE_BISHOP, WHITE_ROOK, WHITE_QUEEN, WHITE_KING};
  public static final byte[] BLACK_ALL_PIECES = new byte[]{BLACK_PAWN, BLACK_KNIGHT, BLACK_BISHOP, BLACK_ROOK, BLACK_QUEEN, BLACK_KING};

  public ChessPiece(ChessPieceType type, boolean color) {
    this.type = type;
    this.color = color;
  }

  public static ChessPiece fromByte(byte i) {
    if (i == 0) {
      return null;
    }
    boolean w = (i & WHITE_BIT) != 0;
    i &= ~WHITE_BIT;
    return new ChessPiece(ChessPieceType.values()[i], w);
  }

  public String imagePath() {
    return this.imagePath("medium");
  }
  public String imagePath(String size) {
    return this.type.toString() + "_" + (this.color ? "white" : "black") + "_" + size;
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

  public byte getByte() {
    if (this.color) {
      return (byte) ((this.type.getByte() << 1) | 1);
    }
    return (byte) (this.type.getByte() << 1);
  }

  public static boolean getColor(byte piece) {
    return (piece & WHITE_BIT) != 0;
  }

  @Override
  public String toString() {
    String s = this.color ? "white " : "black ";
    s += this.type.toString();
    return s;
  }
}
