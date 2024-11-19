package chess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import utilities.Bitwise;
import utilities.Logger;

import lombok.Getter;

public class ChessPosition {
  // board is a square of this size
  public static final int BOARD_SIZE = 8;

  // files are columns
  private static long[] files = new long[]{
    0x0101010101010101L,// every 8th bit is 1, starting with the first bit
    0x0101010101010101L << 8,
    0x0101010101010101L << 16,
    0x0101010101010101L << 24,
    0x0101010101010101L << 32,
    0x0101010101010101L << 40,
    0x0101010101010101L << 48,
    0x0101010101010101L << 56
  };
  // files are rows
  private static long[] ranks = new long[]{255, 255 << 8, 255 << 16, 255 << 24, 255 << 32, 255 << 40, 255 << 48, 255 << 56};

  // bitboard representation
  @Getter
  private Map<Integer, Long> pieces;
  // redundant information for classes of pieces
  private long allPieces = 0L;
  private long whitePieces = 0L;
  private long blackPieces = 0L;
  // redundant mailbox representation only used for frontend
  @Getter
  private int[] mailbox;
  // true if white's turn, false if black's turn
  private boolean whiteTurn;
  // bitwise representation of which file(s) the current player's turn can attack en passant
  private byte enPassant;
  // bitwise representation of castling rights -> first bit is white castling kingside, then white castling queenside, etc...
  private byte castlingRights;
  // all valid child positions
  @Getter
  private Map<ChessMove, ChessPosition> children;
  private boolean movesGenerated = false;

  public static ChessPosition createPosition(ChessStartPosition startPosition) {
    ChessPosition position = new ChessPosition();
    position.pieces = new HashMap<Integer, Long>();
    position.mailbox = new int[BOARD_SIZE * BOARD_SIZE];
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

  public static byte coordinatesToByte(int r, int c) {
    return (byte) (r + 8*c);
  }

  private void setupPiecesStandard() {
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
    byte l = coordinatesToByte(r, c);
    long posK = 1L << l;
    this.pieces.put(pk, this.pieces.get(pk) | posK);
    this.allPieces |= posK;
    if (whitePiece) {
      this.whitePieces |= posK;
    } else {
      this.blackPieces |= posK;
    }
    this.mailbox[l] = pk;
  }

  public void generateMoves() {
    if (this.movesGenerated) {
      return;
    }
    this.children = new HashMap<>();
    for (int piece : (this.whiteTurn ? ChessPiece.WHITE_ALL_PIECES : ChessPiece.BLACK_ALL_PIECES)) {
      long bitboard = this.pieces.get(piece);
      int pieceType = piece & 0xFFFF;
      while (bitboard > 0) {
        long lsb = bitboard & -bitboard;
        switch(pieceType) {
          case ChessPieceType.PAWN_VALUE:
            this.generatePawnMoves(piece, lsb);
            break;
          case ChessPieceType.KNIGHT_VALUE:
            break;
          case ChessPieceType.BISHOP_VALUE:
            break;
          default:
            Logger.log("Move generation for piece type not implemented: " + ChessPieceType.values()[pieceType]);
            break;
        }
        bitboard &= ~lsb;
      }
    }
    this.movesGenerated = true;
  }

  private void generatePawnMoves(int type, long p) {
    int dir = this.whiteTurn ? 1 : -1;
    long forward = p << dir;
    if ((this.allPieces & forward) == 0) { // no capture going forward
      this.addPawnMove(new ChessMove(type, p, forward, false, false));
      if ((ranks[this.whiteTurn ? 1 : 6] & p) > 0) { // check if pawn is on starting square
        long forward2 = forward << dir;
        if ((this.allPieces & forward2) == 0) { // no capture going forward
          this.applyMove(new ChessMove(type, p, forward2, false, false));
        }
      }
    }
    long attack1 = p << -7 * dir;
    if (((this.whiteTurn ? this.blackPieces : this.whitePieces) & attack1) > 0) { // must capture going diagonal
      ChessMove mv = new ChessMove(type, p, attack1, false, false);
      this.addPawnMove(mv);
    }
    long attack2 = p << 9 * dir;
    if (((this.whiteTurn ? this.blackPieces : this.whitePieces) & attack2) > 0) { // must capture going diagonal
      ChessMove mv = new ChessMove(type, p, attack2, false, false);
      this.addPawnMove(mv);
    }
    // TODO: check en passant move(s)
  }

  private void addPawnMove(ChessMove mv) {
    // check if it creates en passant
    // check if it's a promotion move
    this.applyMove(mv);
  }

  private ChessPosition applyMove(ChessMove mv) {
    ChessPosition result = new ChessPosition();
    result.pieces = new HashMap<>(this.pieces);
    result.allPieces = this.allPieces;
    result.whitePieces = this.whitePieces;
    result.blackPieces = this.blackPieces;
    result.mailbox = Arrays.copyOf(mailbox, mailbox.length);
    result.whiteTurn = !this.whiteTurn;
    result.enPassant = this.enPassant;
    result.castlingRights = this.castlingRights;
    result.allPieces &= ~mv.start();
    result.allPieces |= mv.end();
    if (this.whiteTurn) {
      result.whitePieces &= ~mv.start();
      result.whitePieces |= mv.end();
      result.blackPieces &= ~mv.end();
    } else {
      result.blackPieces &= ~mv.start();
      result.blackPieces |= mv.end();
      result.whitePieces &= ~mv.end();
    }
    long bitboard = result.pieces.get(mv.piece());
    bitboard &= ~mv.start();
    bitboard |= mv.end();
    result.pieces.put(mv.piece(), bitboard);
    // special rules for casting and en passant
    this.children.put(mv, result);
    return result;
  }
}
