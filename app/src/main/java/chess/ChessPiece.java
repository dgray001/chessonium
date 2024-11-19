package chess;

import utilities.Bitwise;

public class ChessPiece {
  private ChessPieceType type;
  private boolean color;

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
    return Bitwise.combine(this.color, this.type.getShort());
  }
}
