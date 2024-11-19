package chess;

public enum ChessStartPosition {
  ERROR(0),
  STANDARD(1),
  ;

  private final short value;

  private ChessStartPosition(int value) {
    this.value = (short) value;
  }

  public short getShort() {
    return value;
  }
}
