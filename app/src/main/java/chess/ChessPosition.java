package chess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import utilities.Bitwise;
import utilities.Logger;

import lombok.Getter;

public class ChessPosition {
  public static final int BOARD_SIZE = 8;
  // bitboard representation
  @Getter
  private Map<Integer, Long> pieces;
  // redundant mailbox representation
  @Getter
  private int[][] mailbox = new int[BOARD_SIZE][BOARD_SIZE];
  // true if white's turn, false if black's turn
  private boolean whiteTurn;
  // bitwise representation of which file(s) the current player's turn can attack en passant
  private byte enPassant;
  // bitwise representation of castling rights -> first bit is white castling kingside, then white castling queenside, etc...
  private byte castlingRights;

  public static ChessPosition createPosition(ChessStartPosition startPosition) {
    ChessPosition position = new ChessPosition();
    switch(startPosition) {
      case STANDARD:
        position.setupPiecesStandard();
        break;
      default:
        Logger.err("Unknown start position: " + startPosition);
        break;
    }
    return position;
  }

  private void setupPiecesStandard() {
    this.pieces = new HashMap<Integer, Long>();
    this.setPiece(ChessPieceType.ROOK, true, 0, 0);
    this.setPiece(ChessPieceType.KNIGHT, true, 0, 1);
    this.setPiece(ChessPieceType.BISHOP, true, 0, 2);
    this.setPiece(ChessPieceType.QUEEN, true, 0, 3);
    this.setPiece(ChessPieceType.KING, true, 0, 4);
    this.setPiece(ChessPieceType.BISHOP, true, 0, 5);
    this.setPiece(ChessPieceType.KNIGHT, true, 0, 6);
    this.setPiece(ChessPieceType.ROOK, true, 0, 7);
    this.setPiece(ChessPieceType.PAWN, true, 1, 0);
    this.setPiece(ChessPieceType.PAWN, true, 1, 1);
    this.setPiece(ChessPieceType.PAWN, true, 1, 2);
    this.setPiece(ChessPieceType.PAWN, true, 1, 3);
    this.setPiece(ChessPieceType.PAWN, true, 1, 4);
    this.setPiece(ChessPieceType.PAWN, true, 1, 5);
    this.setPiece(ChessPieceType.PAWN, true, 1, 6);
    this.setPiece(ChessPieceType.PAWN, true, 1, 7);
    this.setPiece(ChessPieceType.ROOK, false, 7, 0);
    this.setPiece(ChessPieceType.KNIGHT, false, 7, 1);
    this.setPiece(ChessPieceType.BISHOP, false, 7, 2);
    this.setPiece(ChessPieceType.QUEEN, false, 7, 3);
    this.setPiece(ChessPieceType.KING, false, 7, 4);
    this.setPiece(ChessPieceType.BISHOP, false, 7, 5);
    this.setPiece(ChessPieceType.KNIGHT, false, 7, 6);
    this.setPiece(ChessPieceType.ROOK, false, 7, 7);
    this.setPiece(ChessPieceType.PAWN, false, 6, 0);
    this.setPiece(ChessPieceType.PAWN, false, 6, 1);
    this.setPiece(ChessPieceType.PAWN, false, 6, 2);
    this.setPiece(ChessPieceType.PAWN, false, 6, 3);
    this.setPiece(ChessPieceType.PAWN, false, 6, 4);
    this.setPiece(ChessPieceType.PAWN, false, 6, 5);
    this.setPiece(ChessPieceType.PAWN, false, 6, 6);
    this.setPiece(ChessPieceType.PAWN, false, 6, 7);
    this.whiteTurn = true;
    this.enPassant = 0;
    this.castlingRights = Bitwise.boolsToByte(new boolean[]{true, true, true, true});
  }

  private void setPiece(ChessPieceType type, boolean whitePiece, int r, int c) {
    ChessPiece p = new ChessPiece(type, whitePiece);
    Integer pk = p.hashCode();
    if (!pieces.containsKey(pk)) {
      pieces.put(pk, (long) 0);
    }
    int l = r + 8*c;
    this.pieces.put(pk, this.pieces.get(pk) | 1L << l);
    this.mailbox[r][c] = pk;
  }
}
