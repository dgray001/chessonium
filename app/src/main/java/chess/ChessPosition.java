package chess;

import java.util.Map;

public class ChessPosition {
  // bitboard representation
  private Map<Integer, Long> pieces;
  // true if white's turn, false if black's turn
  private boolean whiteTurn;
  // bitwise representation of which file(s) the current player's turn can attack en passant
  private byte enPassant;
  // bitwise representation of castling rights -> first bit is white castling kingside, then white castling queenside, etc...
  private byte castlingRights;
}
