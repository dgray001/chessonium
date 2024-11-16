package chess;

import utilities.Bitwise;

public class ChessPiece {
  private boolean color;
  private ChessPieceType type;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    ChessPiece piece = (ChessPiece) obj;
    return this.color == piece.color && this.type == piece.type;
  }

  @Override
  public int hashCode() {
    return Bitwise.combine(this.color, this.type.getShort());
  }
}
