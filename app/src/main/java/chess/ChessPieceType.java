package chess;

public enum ChessPieceType {
  ERROR(0),
  PAWN(1),
  KNIGHT(2),
  BISHOP(3),
  ROOK(4),
  QUEEN(5),
  KING(6),
  ;

  public static final byte PAWN_VALUE = 1;
  public static final byte KNIGHT_VALUE = 2;
  public static final byte BISHOP_VALUE = 3;
  public static final byte ROOK_VALUE = 4;
  public static final byte QUEEN_VALUE = 5;
  public static final byte KING_VALUE = 6;

  private final byte value;

  private ChessPieceType(int value) {
    this.value = (byte) value;
  }

  public byte getByte() {
    return value;
  }

  @Override
  public String toString() {
    return this.name().toLowerCase();
  }
}
