package chess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import chess.moves.BishopMoves;
import chess.moves.KingMoves;
import chess.moves.KnightMoves;
import chess.moves.QueenMoves;
import chess.moves.RookMoves;
import utilities.Bitwise;
import utilities.Logger;

import lombok.Getter;

public class ChessPosition {
  // board is a square of this size
  public static final int BOARD_SIZE = 8;

  // files are columns
  private static long[] files = new long[]{255, 255 << 8, 255 << 16, 255 << 24, 255 << 32, 255 << 40, 255 << 48, 255 << 56};
  // files are rows
  private static long[] ranks = new long[]{
    0x0101010101010101L,// every 8th bit is 1, starting with the first bit
    0x0101010101010101L << 1,
    0x0101010101010101L << 2,
    0x0101010101010101L << 3,
    0x0101010101010101L << 4,
    0x0101010101010101L << 5,
    0x0101010101010101L << 6,
    0x0101010101010101L << 7
  };

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
  // bitwise representation of which space the current player's turn can attack en passant
  private long enPassant;
  // bitwise representation of castling rights -> white queenside, white kingside, black queenside, black kingside
  private byte castlingRights;
  private static final byte CASTLING_WHITE_QUEENSIDE = 1;
  private static final byte CASTLING_WHITE_KINGSIDE = 2;
  private static final byte CASTLING_BLACK_QUEENSIDE = 4;
  private static final byte CASTLING_BLACK_KINGSIDE = 8;
  private static final byte CASTLING_WHITE = Bitwise.boolsToByte(new boolean[]{true, true});
  private static final byte CASTLING_BLACK = Bitwise.boolsToByte(new boolean[]{false, false, true, true});
  private static final long WHITE_QUEENSIDE_ROOK_START = 1L << coordinatesToByte(0, 0);
  private static final long WHITE_KINGSIDE_ROOK_START = 1L << coordinatesToByte(0, 7);
  private static final long BLACK_QUEENSIDE_ROOK_START = 1L << coordinatesToByte(7, 0);
  private static final long BLACK_KINGSIDE_ROOK_START = 1L << coordinatesToByte(7, 7);
  private static final long WHITE_QUEENSIDE_ROOK_END = 1L << coordinatesToByte(0, 3);
  private static final long WHITE_KINGSIDE_ROOK_END = 1L << coordinatesToByte(0, 5);
  private static final long BLACK_QUEENSIDE_ROOK_END = 1L << coordinatesToByte(7, 3);
  private static final long BLACK_KINGSIDE_ROOK_END = 1L << coordinatesToByte(7, 5);
  // all valid child positions
  @Getter
  private Map<ChessMove, ChessPosition> children;
  private boolean movesGenerated = false;
  private boolean checkMovesTrimmed = false;
  private long spacesAttacked;

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
    this.spacesAttacked = 0;
    for (int piece : (this.whiteTurn ? ChessPiece.WHITE_ALL_PIECES : ChessPiece.BLACK_ALL_PIECES)) {
      long bitboard = this.pieces.get(piece);
      int pieceType = piece & 0xFFFF;
      while (bitboard != 0) {
        long lsb = bitboard & -bitboard;
        switch(pieceType) {
          case ChessPieceType.PAWN_VALUE:
            this.generatePawnMoves(piece, lsb);
            break;
          case ChessPieceType.KNIGHT_VALUE:
            this.generateKnightMoves(piece, lsb);
            break;
          case ChessPieceType.BISHOP_VALUE:
            this.generateBishopMoves(piece, lsb);
            break;
          case ChessPieceType.ROOK_VALUE:
            this.generateRookMoves(piece, lsb);
            break;
          case ChessPieceType.QUEEN_VALUE:
            this.generateQueenMoves(piece, lsb);
            break;
          case ChessPieceType.KING_VALUE:
            this.generateKingMoves(piece, lsb);
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

  // if true this position is illegal since you cannot move into check
  public boolean kingAttacked() {
    long king = this.whiteTurn ? this.pieces.get(ChessPiece.BLACK_KING) : this.pieces.get(ChessPiece.WHITE_KING);
    return (this.spacesAttacked & king) != 0;
  }

  public static int[] coordinatesFromLong(long l) {
    int i = Long.numberOfTrailingZeros(l);
    return new int[]{(i%8), (i/8)};
  }

  private void generatePawnMoves(int type, long p) {
    long forward = this.whiteTurn ? (p << 1) : (p >>> 1);
    if ((this.allPieces & forward) == 0) { // no capture going forward
      this.addPawnMove(ChessMove.createChessMove(type, p, forward));
      if ((ranks[this.whiteTurn ? 1 : 6] & p) != 0) { // check if pawn is on starting square
        long forward2 = this.whiteTurn ? (forward << 1) : (forward >>> 1);
        if ((this.allPieces & forward2) == 0) { // no capture going forward
          this.addPawnMove(ChessMove.createChessMove(type, p, forward2, forward));
        }
      }
    }
    long attack1 = this.whiteTurn ? (p >>> 7) : (p << 7);
    if (((this.whiteTurn ? this.blackPieces : this.whitePieces) & attack1) != 0) { // must capture going diagonal
      this.addPawnMove(ChessMove.createChessMove(type, p, attack1));
    } else if (((ranks[this.whiteTurn ? 4 : 3] & p) != 0) && ((attack1 & this.enPassant) != 0)) { // en passant
      this.addPawnMove(ChessMove.createChessMove(type, p, attack1, true));
    }
    long attack2 = this.whiteTurn ? (p << 9) : (p >>> 9);
    if (((this.whiteTurn ? this.blackPieces : this.whitePieces) & attack2) != 0) { // must capture going diagonal
      this.addPawnMove(ChessMove.createChessMove(type, p, attack2));
    } else if (((ranks[this.whiteTurn ? 4 : 3] & p) != 0) && ((attack2 & this.enPassant) != 0)) { // en passant
      this.addPawnMove(ChessMove.createChessMove(type, p, attack2, true));
    }
  }

  private void addPawnMove(ChessMove mv) {
    if (this.whiteTurn && (ranks[7] & mv.end()) != 0) {
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.WHITE_KNIGHT));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.WHITE_BISHOP));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.WHITE_ROOK));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.WHITE_QUEEN));
    } else if (!this.whiteTurn && (ranks[0] & mv.end()) != 0) {
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.BLACK_KNIGHT));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.BLACK_BISHOP));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.BLACK_ROOK));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.BLACK_QUEEN));
    } else {
      this.addMove(mv);
    }
  }

  private void generateKnightMoves(int type, long p) {
    for (long mv : KnightMoves.getKnightMoves(p)) {
      if (((this.whiteTurn ? this.whitePieces : this.blackPieces) & mv) != 0) {
        continue;
      }
      this.addMove(ChessMove.createChessMove(type, p, mv));
    }
  }

  private void generateBishopMoves(int type, long p) {
    for (Long[] dir : BishopMoves.getBishopMoves(p)) {
      for (long mv : dir) {
        if (((this.whiteTurn ? this.whitePieces : this.blackPieces) & mv) != 0) {
          break;
        }
        this.addMove(ChessMove.createChessMove(type, p, mv));
        if ((this.allPieces & mv) != 0) {
          break;
        }
      }
    }
  }

  private void generateRookMoves(int type, long p) {
    for (Long[] dir : RookMoves.getRookMoves(p)) {
      for (long mv : dir) {
        if (((this.whiteTurn ? this.whitePieces : this.blackPieces) & mv) != 0) {
          break;
        }
        this.addMove(ChessMove.createChessMove(type, p, mv));
        if ((this.allPieces & mv) != 0) {
          break;
        }
      }
    }
  }

  private void generateQueenMoves(int type, long p) {
    for (Long[] dir : QueenMoves.getQueenMoves(p)) {
      for (long mv : dir) {
        if (((this.whiteTurn ? this.whitePieces : this.blackPieces) & mv) != 0) {
          break;
        }
        this.addMove(ChessMove.createChessMove(type, p, mv));
        if ((this.allPieces & mv) != 0) {
          break;
        }
      }
    }
  }

  private void generateKingMoves(int type, long p) {
    for (long mv : KingMoves.getKingMoves(p)) {
      if (((this.whiteTurn ? this.whitePieces : this.blackPieces) & mv) != 0) {
        continue;
      }
      this.addMove(ChessMove.createChessMove(type, p, mv));
    }
    if ((this.castlingRights & (this.whiteTurn ? CASTLING_WHITE_QUEENSIDE : CASTLING_BLACK_QUEENSIDE)) != 0) {
      if ( ((this.allPieces & (p >>> 8)) == 0) && ((this.allPieces & (p >>> 16)) == 0) && ((this.allPieces & (p >>> 24)) == 0) ) {
        this.addMove(ChessMove.createChessMove(type, p, p >>> 16, this.whiteTurn ? CASTLING_WHITE_QUEENSIDE : CASTLING_BLACK_QUEENSIDE));
      }
    }
    if ((this.castlingRights & (this.whiteTurn ? CASTLING_WHITE_KINGSIDE : CASTLING_BLACK_KINGSIDE)) != 0) {
      if ( ((this.allPieces & (p << 8)) == 0) && ((this.allPieces & (p << 16)) == 0) ) {
        this.addMove(ChessMove.createChessMove(type, p, p << 16, this.whiteTurn ? CASTLING_WHITE_KINGSIDE : CASTLING_BLACK_KINGSIDE));
      }
    }
  }

  private ChessPosition addMove(ChessMove mv) {
    ChessPosition result = new ChessPosition();
    result.pieces = new HashMap<>(this.pieces);
    result.allPieces = this.allPieces;
    result.whitePieces = this.whitePieces;
    result.blackPieces = this.blackPieces;
    result.mailbox = Arrays.copyOf(mailbox, mailbox.length);
    result.whiteTurn = !this.whiteTurn;
    result.enPassant = mv.enPassant();
    result.castlingRights = this.castlingRights;
    result.allPieces &= ~mv.start();
    result.allPieces |= mv.end();
    long captureSquare = mv.end();
    if (mv.isEnPassant()) {
      captureSquare = this.whiteTurn ? (captureSquare >>> 1) : (captureSquare << 1);
      result.allPieces &= ~captureSquare;
      result.mailbox[Long.numberOfTrailingZeros(captureSquare)] = 0;
    } else if (mv.castling() > 0) {
      result.castlingRights &= (this.whiteTurn ? CASTLING_BLACK : CASTLING_WHITE);
      switch(mv.castling()) {
        case CASTLING_WHITE_QUEENSIDE:
          result.movePieceNoCapture(ChessPiece.WHITE_ROOK, WHITE_QUEENSIDE_ROOK_START, WHITE_QUEENSIDE_ROOK_END, true);
          break;
        case CASTLING_WHITE_KINGSIDE:
          result.movePieceNoCapture(ChessPiece.WHITE_ROOK, WHITE_KINGSIDE_ROOK_START, WHITE_KINGSIDE_ROOK_END, true);
          break;
        case CASTLING_BLACK_QUEENSIDE:
          result.movePieceNoCapture(ChessPiece.BLACK_ROOK, BLACK_QUEENSIDE_ROOK_START, BLACK_QUEENSIDE_ROOK_END, false);
          break;
        case CASTLING_BLACK_KINGSIDE:
          result.movePieceNoCapture(ChessPiece.BLACK_ROOK, BLACK_KINGSIDE_ROOK_START, BLACK_KINGSIDE_ROOK_END, false);
          break;
        default:
          Logger.err("Unknown castling location", mv.end());
          break;
      }
    } else if (mv.piece() == ChessPiece.WHITE_KING) {
      result.castlingRights &= CASTLING_BLACK;
    } else if (mv.piece() == ChessPiece.BLACK_KING) {
      result.castlingRights &= CASTLING_WHITE;
    } else if (mv.piece() == ChessPiece.WHITE_ROOK) {
      if (mv.start() == WHITE_QUEENSIDE_ROOK_START) {
        result.castlingRights &= ~CASTLING_WHITE_QUEENSIDE;
      } else if (mv.start() == WHITE_KINGSIDE_ROOK_START) {
        result.castlingRights &= ~CASTLING_WHITE_KINGSIDE;
      }
    } else if (mv.piece() == ChessPiece.BLACK_ROOK) {
      if (mv.start() == BLACK_QUEENSIDE_ROOK_START) {
        result.castlingRights &= ~CASTLING_BLACK_QUEENSIDE;
      } else if (mv.start() == BLACK_KINGSIDE_ROOK_START) {
        result.castlingRights &= ~CASTLING_BLACK_KINGSIDE;
      }
    }
    if (this.whiteTurn) {
      result.whitePieces &= ~mv.start();
      result.whitePieces |= mv.end();
      result.blackPieces &= ~captureSquare;
    } else {
      result.blackPieces &= ~mv.start();
      result.blackPieces |= mv.end();
      result.whitePieces &= ~captureSquare;
    }
    long bitboard = result.pieces.get(mv.piece());
    bitboard &= ~mv.start();
    if (mv.promotionPiece() != 0) {
      long promotionPieceBitboard = result.pieces.get(mv.promotionPiece());
      promotionPieceBitboard |= mv.end();
      result.pieces.put(mv.promotionPiece(), promotionPieceBitboard);
    } else {
      bitboard |= mv.end();
    }
    result.pieces.put(mv.piece(), bitboard);
    int capturedPiece = result.mailbox[Long.numberOfTrailingZeros(captureSquare)];
    if (capturedPiece != 0) {
      long capturedBitboard = result.pieces.get(capturedPiece);
      capturedBitboard &= ~captureSquare;
      result.pieces.put(capturedPiece, capturedBitboard);
    }
    result.mailbox[Long.numberOfTrailingZeros(mv.start())] = 0;
    result.mailbox[Long.numberOfTrailingZeros(mv.end())] = mv.promotionPiece() != 0 ? mv.promotionPiece() : mv.piece();
    this.children.put(mv, result);
    if (mv.castling() == 0) {
      this.spacesAttacked |= mv.end();
    }
    return result;
  }

  private void movePieceNoCapture(int piece, long start, long end, boolean whiteTurn) {
    long bb = this.pieces.get(piece);
    bb &= ~start;
    bb |= end;
    this.pieces.put(piece, bb);
    this.mailbox[Long.numberOfTrailingZeros(start)] = 0;
    this.mailbox[Long.numberOfTrailingZeros(end)] = piece;
    this.allPieces &= ~start;
    this.allPieces |= end;
    if (whiteTurn) {
      this.whitePieces &= ~start;
      this.whitePieces |= end;
    } else {
      this.blackPieces &= ~start;
      this.blackPieces |= end;
    }
  }

  // Trims illegal moves that would put the king in check
  public void trimCheckMoves() {
    if (this.checkMovesTrimmed) {
      return;
    }
    Iterator<Map.Entry<ChessMove, ChessPosition>> it = this.children.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<ChessMove, ChessPosition> entry = it.next();
      entry.getValue().generateMoves();
      if (entry.getValue().kingAttacked()) {
        it.remove();
      }
    }
    this.checkMovesTrimmed = true;
  }
}
