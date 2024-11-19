package chess;

public enum ChessPieceType {
  ERROR(0),
  PAWN(1),
  BISHOP(2),
  KNIGHT(3),
  ROOK(4),
  QUEEN(5),
  KING(6),
  ;

  private final short value;

  private ChessPieceType(int value) {
    this.value = (short) value;
  }

  public short getShort() {
    return value;
  }

  @Override
  public String toString() {
    return this.name().toLowerCase();
  }
}
