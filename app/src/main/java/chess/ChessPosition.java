package chess;

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
  // position key set deterministically by this class
  @Getter
  private int key;
  private static int nextKey = 1;
  // bitboard representation
  @Getter
  private long wPawns = 0L;
  @Getter
  private long wKnights = 0L;
  @Getter
  private long wBishops = 0L;
  @Getter
  private long wRooks = 0L;
  @Getter
  private long wQueens = 0L;
  @Getter
  private long wKings = 0L;
  @Getter
  private long bPawns = 0L;
  @Getter
  private long bKnights = 0L;
  @Getter
  private long bBishops = 0L;
  @Getter
  private long bRooks = 0L;
  @Getter
  private long bQueens = 0L;
  @Getter
  private long bKings = 0L;
  // redundant information for classes of pieces
  @Getter
  private long allPieces = 0L;
  @Getter
  private long whitePieces = 0L;
  @Getter
  private long blackPieces = 0L;
  // true if white's turn, false if black's turn
  @Getter
  private boolean whiteTurn;
  // bitwise representation of which space the current player's turn can attack en passant
  private long enPassant;
  // bitwise representation of castling rights -> white queenside, white kingside, black queenside, black kingside
  private byte castlingRights;
  // all valid child positions
  @Getter
  private Map<ChessMove, ChessPosition> children;
  private boolean movesGenerated = false;
  private boolean checkMovesTrimmed = false;
  private long spacesEnemyAttacking;
  private long spacesAttacked;
  // the following are only used by the chess engine
  @Getter
  private float evaluation;
  @Getter
  private boolean evaluated = false;

  public static ChessPosition createPosition(ChessStartPosition startPosition) {
    ChessPosition position = new ChessPosition();
    ChessPosition.setKey(position);
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

  public byte[] getMailbox() {
    byte[] mb = new byte[64];
    long bb = this.wPawns;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.WHITE_PAWN;
      bb &= ~lsb;
    }
    bb = this.wKnights;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.WHITE_KNIGHT;
      bb &= ~lsb;
    }
    bb = this.wBishops;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.WHITE_BISHOP;
      bb &= ~lsb;
    }
    bb = this.wRooks;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.WHITE_ROOK;
      bb &= ~lsb;
    }
    bb = this.wQueens;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.WHITE_QUEEN;
      bb &= ~lsb;
    }
    bb = this.wKings;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.WHITE_KING;
      bb &= ~lsb;
    }
    bb = this.bPawns;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.BLACK_PAWN;
      bb &= ~lsb;
    }
    bb = this.bKnights;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.BLACK_KNIGHT;
      bb &= ~lsb;
    }
    bb = this.bBishops;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.BLACK_BISHOP;
      bb &= ~lsb;
    }
    bb = this.bRooks;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.BLACK_ROOK;
      bb &= ~lsb;
    }
    bb = this.bQueens;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.BLACK_QUEEN;
      bb &= ~lsb;
    }
    bb = this.bKings;
    while (bb != 0) {
      long lsb = bb & -bb;
      mb[Long.numberOfTrailingZeros(lsb)] = ChessPiece.BLACK_KING;
      bb &= ~lsb;
    }
    return mb;
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
    byte l = coordinatesToByte(r, c);
    long posK = 1L << l;
    switch(type) {
      case PAWN:
        if (whitePiece) {
          this.wPawns |= posK;
        } else {
          this.bPawns |= posK;
        }
        break;
      case KNIGHT:
        if (whitePiece) {
          this.wKnights |= posK;
        } else {
          this.bKnights |= posK;
        }
        break;
      case BISHOP:
        if (whitePiece) {
          this.wBishops |= posK;
        } else {
          this.bBishops |= posK;
        }
        break;
      case ROOK:
        if (whitePiece) {
          this.wRooks |= posK;
        } else {
          this.bRooks |= posK;
        }
        break;
      case QUEEN:
        if (whitePiece) {
          this.wQueens |= posK;
        } else {
          this.bQueens |= posK;
        }
        break;
      case KING:
        if (whitePiece) {
          this.wKings |= posK;
        } else {
          this.bKings |= posK;
        }
        break;
      default:
        break;
    }
    this.allPieces |= posK;
    if (whitePiece) {
      this.whitePieces |= posK;
    } else {
      this.blackPieces |= posK;
    }
  }

  public void setEvaluation(float e) {
    this.evaluation = e;
    this.evaluated = true;
  }

  public void generateMoves() {
    this.generateMoves(false);
  }
  public void generateMoves(boolean force) {
    if (this.movesGenerated && !force) {
      return;
    }
    this.children = new HashMap<chess.ChessMove,chess.ChessPosition>();
    this.whiteTurn = !this.whiteTurn;
    this._generateMoves(); // TODO: optimize to just get squares the enemy attacks
    this.spacesEnemyAttacking = this.spacesAttacked;
    this.whiteTurn = !this.whiteTurn;
    this._generateMoves();
    this.movesGenerated = true;
  }

  private void _generateMoves() {
    this.children = new HashMap<>();
    this.spacesAttacked = 0;
    if (this.whiteTurn) {
      long bb = this.wPawns;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generatePawnMoves(ChessPiece.WHITE_PAWN, lsb);
        bb &= ~lsb;
      }
      bb = this.wKnights;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateKnightMoves(ChessPiece.WHITE_KNIGHT, lsb);
        bb &= ~lsb;
      }
      bb = this.wBishops;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateBishopMoves(ChessPiece.WHITE_BISHOP, lsb);
        bb &= ~lsb;
      }
      bb = this.wRooks;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateRookMoves(ChessPiece.WHITE_ROOK, lsb);
        bb &= ~lsb;
      }
      bb = this.wQueens;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateQueenMoves(ChessPiece.WHITE_QUEEN, lsb);
        bb &= ~lsb;
      }
      bb = this.wKings;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateKingMoves(ChessPiece.WHITE_KING, lsb);
        bb &= ~lsb;
      }
    } else {
      long bb = this.bPawns;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generatePawnMoves(ChessPiece.BLACK_PAWN, lsb);
        bb &= ~lsb;
      }
      bb = this.bKnights;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateKnightMoves(ChessPiece.BLACK_KNIGHT, lsb);
        bb &= ~lsb;
      }
      bb = this.bBishops;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateBishopMoves(ChessPiece.BLACK_BISHOP, lsb);
        bb &= ~lsb;
      }
      bb = this.bRooks;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateRookMoves(ChessPiece.BLACK_ROOK, lsb);
        bb &= ~lsb;
      }
      bb = this.bQueens;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateQueenMoves(ChessPiece.BLACK_QUEEN, lsb);
        bb &= ~lsb;
      }
      bb = this.bKings;
      while (bb != 0) {
        long lsb = bb & -bb;
        this.generateKingMoves(ChessPiece.BLACK_KING, lsb);
        bb &= ~lsb;
      }
    }
  }

  // if true this position is illegal since opponent cannot move into check
  public boolean kingAttacked() {
    long king = this.whiteTurn ? this.bKings : this.wKings;
    return (this.spacesAttacked & king) != 0;
  }

  public boolean inCheck() {
    long king = this.whiteTurn ? this.wKings : this.bKings;
    return (this.spacesEnemyAttacking & king) != 0;
  }

  public static int[] coordinatesFromLong(long l) {
    int i = Long.numberOfTrailingZeros(l);
    return new int[]{(i%8), (i/8)};
  }

  public static String spaceFromLong(long l) {
    int[] c = coordinatesFromLong(l);
    return Character.toString((char)('A' + c[1])) + Integer.toString((char)(c[0] + 1));
  }

  public ChessResult getGameResult() {
    if (this.children.size() > 0) {
      // TODO: check insufficient material, draw by repitition, and 50 move rule
      return ChessResult.NOT_OVER;
    }
    if (!this.inCheck()) {
      return ChessResult.DRAW_STALEMATE;
    }
    if (this.whiteTurn) {
      return ChessResult.BLACK_CHECKMATE;
    }
    return ChessResult.WHITE_CHECKMATE;
  }

  private synchronized void generatePawnMoves(byte type, long p) {
    long forward = this.whiteTurn ? (p << 1) : (p >>> 1);
    if ((this.allPieces & forward) == 0) { // no capture going forward
      this.addPawnMove(ChessMove.createChessMove(type, p, forward));
      if ((ChessConstants.ranks[this.whiteTurn ? 1 : 6] & p) != 0) { // check if pawn is on starting square
        long forward2 = this.whiteTurn ? (forward << 1) : (forward >>> 1);
        if ((this.allPieces & forward2) == 0) { // no capture going forward
          this.addPawnMove(ChessMove.createChessMove(type, p, forward2, forward));
        }
      }
    }
    long attack1 = this.whiteTurn ? (p >>> 7) : (p << 7);
    this.spacesAttacked |= attack1;
    if (((this.whiteTurn ? this.blackPieces : this.whitePieces) & attack1) != 0) { // must capture going diagonal
      this.addPawnMove(ChessMove.createChessMove(type, p, attack1));
    } else if (((ChessConstants.ranks[this.whiteTurn ? 4 : 3] & p) != 0) && ((attack1 & this.enPassant) != 0)) { // en passant
      this.addPawnMove(ChessMove.createChessMove(type, p, attack1, true));
    }
    long attack2 = this.whiteTurn ? (p << 9) : (p >>> 9);
    this.spacesAttacked |= attack2;
    if (((this.whiteTurn ? this.blackPieces : this.whitePieces) & attack2) != 0) { // must capture going diagonal
      this.addPawnMove(ChessMove.createChessMove(type, p, attack2));
    } else if (((ChessConstants.ranks[this.whiteTurn ? 4 : 3] & p) != 0) && ((attack2 & this.enPassant) != 0)) { // en passant
      this.addPawnMove(ChessMove.createChessMove(type, p, attack2, true));
    }
  }

  private void addPawnMove(ChessMove mv) {
    if (this.whiteTurn && (ChessConstants.ranks[7] & mv.end()) != 0) {
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.WHITE_KNIGHT));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.WHITE_BISHOP));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.WHITE_ROOK));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.WHITE_QUEEN));
    } else if (!this.whiteTurn && (ChessConstants.ranks[0] & mv.end()) != 0) {
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.BLACK_KNIGHT));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.BLACK_BISHOP));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.BLACK_ROOK));
      this.addMove(ChessMove.createChessMove(mv, ChessPiece.BLACK_QUEEN));
    } else {
      this.addMove(mv);
    }
  }

  private void generateKnightMoves(byte type, long p) {
    for (long mv : KnightMoves.getKnightMoves(p)) {
      this.spacesAttacked |= mv;
      if (((this.whiteTurn ? this.whitePieces : this.blackPieces) & mv) != 0) {
        continue;
      }
      this.addMove(ChessMove.createChessMove(type, p, mv));
    }
  }

  private void generateBishopMoves(byte type, long p) {
    for (Long[] dir : BishopMoves.getBishopMoves(p)) {
      for (long mv : dir) {
        this.spacesAttacked |= mv;
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

  private void generateRookMoves(byte type, long p) {
    for (Long[] dir : RookMoves.getRookMoves(p)) {
      for (long mv : dir) {
        this.spacesAttacked |= mv;
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

  private void generateQueenMoves(byte type, long p) {
    for (Long[] dir : QueenMoves.getQueenMoves(p)) {
      for (long mv : dir) {
        this.spacesAttacked |= mv;
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

  private void generateKingMoves(byte type, long p) {
    if (this.whiteTurn) {
      for (long mv : KingMoves.getKingMoves(p)) {
        this.spacesAttacked |= mv;
        if ((this.whitePieces & mv) != 0) {
          continue;
        }
        this.addMove(ChessMove.createChessMove(type, p, mv));
      }
      if ((this.spacesEnemyAttacking & p) != 0) {
        return;
      }
      if (
        ((this.castlingRights & ChessConstants.CASTLING_WHITE_QUEENSIDE) != 0) &&
        ((this.allPieces & (p >>> 8)) == 0) &&
        ((this.allPieces & (p >>> 16)) == 0) &&
        ((this.allPieces & (p >>> 24)) == 0) &&
        (this.spacesEnemyAttacking & (p >>> 8)) == 0 &&
        (this.spacesEnemyAttacking & (p >>> 16)) == 0
      ) {
        this.addMove(ChessMove.createChessMove(type, p, p >>> 16, ChessConstants.CASTLING_WHITE_QUEENSIDE, (byte) 0));
      }
      if (
        ((this.castlingRights & ChessConstants.CASTLING_WHITE_KINGSIDE) != 0) &&
        ((this.allPieces & (p << 8)) == 0) &&
        ((this.allPieces & (p << 16)) == 0) &&
        (this.spacesEnemyAttacking & (p << 8)) == 0
      ) {
        this.addMove(ChessMove.createChessMove(type, p, p << 16, ChessConstants.CASTLING_WHITE_KINGSIDE, (byte) 0));
      }
    } else {
      for (long mv : KingMoves.getKingMoves(p)) {
        this.spacesAttacked |= mv;
        if ((this.blackPieces & mv) != 0) {
          continue;
        }
        this.addMove(ChessMove.createChessMove(type, p, mv));
      }
      if ((this.spacesEnemyAttacking & p) != 0) {
        return;
      }
      if (
        ((this.castlingRights & ChessConstants.CASTLING_BLACK_QUEENSIDE) != 0) &&
        ((this.allPieces & (p >>> 8)) == 0) &&
        ((this.allPieces & (p >>> 16)) == 0) &&
        ((this.allPieces & (p >>> 24)) == 0) &&
        (this.spacesEnemyAttacking & (p >>> 8)) == 0 &&
        (this.spacesEnemyAttacking & (p >>> 16)) == 0
      ) {
        this.addMove(ChessMove.createChessMove(type, p, p >>> 16, ChessConstants.CASTLING_BLACK_QUEENSIDE, (byte) 0));
      }
      if (
        ((this.castlingRights & ChessConstants.CASTLING_BLACK_KINGSIDE) != 0) &&
        ((this.allPieces & (p << 8)) == 0) &&
        ((this.allPieces & (p << 16)) == 0) &&
        (this.spacesEnemyAttacking & (p << 8)) == 0
      ) {
        this.addMove(ChessMove.createChessMove(type, p, p << 16, ChessConstants.CASTLING_BLACK_KINGSIDE, (byte) 0));
      }
    }
  }

  public ChessPosition copyPosition() {
    ChessPosition result = new ChessPosition();
    result.wPawns = this.wPawns;
    result.wKnights = this.wKnights;
    result.wBishops = this.wBishops;
    result.wRooks = this.wRooks;
    result.wQueens = this.wQueens;
    result.wKings = this.wKings;
    result.bPawns = this.bPawns;
    result.bKnights = this.bKnights;
    result.bBishops = this.bBishops;
    result.bRooks = this.bRooks;
    result.bQueens = this.bQueens;
    result.bKings = this.bKings;
    result.allPieces = this.allPieces;
    result.whitePieces = this.whitePieces;
    result.blackPieces = this.blackPieces;
    result.whiteTurn = this.whiteTurn;
    result.enPassant = this.enPassant;
    result.castlingRights = this.castlingRights;
    return result;
  }

  private ChessPosition addMove(ChessMove mv) {
    ChessPosition result = this.copyPosition();
    result.whiteTurn = !this.whiteTurn;
    result.enPassant = mv.enPassant();
    long captureSquare = mv.end();
    if (mv.isEnPassant()) {
      captureSquare = this.whiteTurn ? (captureSquare >>> 1) : (captureSquare << 1);
      result.allPieces &= ~captureSquare;
    } else if (mv.castling() > 0) {
      result.castlingRights &= (this.whiteTurn ? ChessConstants.CASTLING_BLACK : ChessConstants.CASTLING_WHITE);
      switch(mv.castling()) {
        case ChessConstants.CASTLING_WHITE_QUEENSIDE:
          result.moveWhitePiece(ChessPiece.WHITE_ROOK, ChessConstants.WHITE_QUEENSIDE_ROOK_START, ChessConstants.WHITE_QUEENSIDE_ROOK_END);
          break;
        case ChessConstants.CASTLING_WHITE_KINGSIDE:
          result.moveWhitePiece(ChessPiece.WHITE_ROOK, ChessConstants.WHITE_KINGSIDE_ROOK_START, ChessConstants.WHITE_KINGSIDE_ROOK_END);
          break;
        case ChessConstants.CASTLING_BLACK_QUEENSIDE:
          result.moveBlackPiece(ChessPiece.BLACK_ROOK, ChessConstants.BLACK_QUEENSIDE_ROOK_START, ChessConstants.BLACK_QUEENSIDE_ROOK_END);
          break;
        case ChessConstants.CASTLING_BLACK_KINGSIDE:
          result.moveBlackPiece(ChessPiece.BLACK_ROOK, ChessConstants.BLACK_KINGSIDE_ROOK_START, ChessConstants.BLACK_KINGSIDE_ROOK_END);
          break;
        default:
          Logger.err("Unknown castling location", mv.end());
          break;
      }
    } else if (mv.piece() == ChessPiece.WHITE_KING) {
      result.castlingRights &= ChessConstants.CASTLING_BLACK;
    } else if (mv.piece() == ChessPiece.BLACK_KING) {
      result.castlingRights &= ChessConstants.CASTLING_WHITE;
    } else if (mv.piece() == ChessPiece.WHITE_ROOK) {
      if (mv.start() == ChessConstants.WHITE_QUEENSIDE_ROOK_START) {
        result.castlingRights &= ~ChessConstants.CASTLING_WHITE_QUEENSIDE;
      } else if (mv.start() == ChessConstants.WHITE_KINGSIDE_ROOK_START) {
        result.castlingRights &= ~ChessConstants.CASTLING_WHITE_KINGSIDE;
      }
    } else if (mv.piece() == ChessPiece.BLACK_ROOK) {
      if (mv.start() == ChessConstants.BLACK_QUEENSIDE_ROOK_START) {
        result.castlingRights &= ~ChessConstants.CASTLING_BLACK_QUEENSIDE;
      } else if (mv.start() == ChessConstants.BLACK_KINGSIDE_ROOK_START) {
        result.castlingRights &= ~ChessConstants.CASTLING_BLACK_KINGSIDE;
      }
    }
    if (this.whiteTurn) {
      result.captureBlackPiece(captureSquare);
      if (mv.promotionPiece() == 0) {
        result.moveWhitePiece(mv.piece(), mv.start(), mv.end());
      } else {
        result.moveAndPromoteWhitePiece(mv.piece(), mv.start(), mv.end(), mv.promotionPiece());
      }
    } else {
      result.captureWhitePiece(captureSquare);
      if (mv.promotionPiece() == 0) {
        result.moveBlackPiece(mv.piece(), mv.start(), mv.end());
      } else {
        result.moveAndPromoteBlackPiece(mv.piece(), mv.start(), mv.end(), mv.promotionPiece());
      }
    }
    this.children.put(mv, result);
    ChessPosition.setKey(result);
    return result;
  }

  private static synchronized void setKey(ChessPosition child) {
    child.key = ChessPosition.nextKey;
    ChessPosition.nextKey++;
  }

  private void moveWhitePiece(byte piece, long start, long end) {
    this.allPieces &= ~start;
    this.allPieces |= end;
    this.whitePieces &= ~start;
    this.whitePieces |= end;
    switch(piece) {
      case ChessPiece.WHITE_PAWN:
        this.wPawns &= ~start;
        this.wPawns |= end;
        break;
      case ChessPiece.WHITE_KNIGHT:
        this.wKnights &= ~start;
        this.wKnights |= end;
        break;
      case ChessPiece.WHITE_BISHOP:
        this.wBishops &= ~start;
        this.wBishops |= end;
        break;
      case ChessPiece.WHITE_ROOK:
        this.wRooks &= ~start;
        this.wRooks |= end;
        break;
      case ChessPiece.WHITE_QUEEN:
        this.wQueens &= ~start;
        this.wQueens |= end;
        break;
      case ChessPiece.WHITE_KING:
        this.wKings &= ~start;
        this.wKings |= end;
        break;
    }
  }

  private void moveAndPromoteWhitePiece(byte piece, long start, long end, byte promotionPiece) {
    this.allPieces &= ~start;
    this.allPieces |= end;
    this.whitePieces &= ~start;
    this.whitePieces |= end;
    switch(piece) {
      case ChessPiece.WHITE_PAWN:
        this.wPawns &= ~start;
        break;
      case ChessPiece.WHITE_KNIGHT:
        this.wKnights &= ~start;
        break;
      case ChessPiece.WHITE_BISHOP:
        this.wBishops &= ~start;
        break;
      case ChessPiece.WHITE_ROOK:
        this.wRooks &= ~start;
        break;
      case ChessPiece.WHITE_QUEEN:
        this.wQueens &= ~start;
        break;
      case ChessPiece.WHITE_KING:
        this.wKings &= ~start;
        break;
    }
    switch(promotionPiece) {
      case ChessPiece.WHITE_PAWN:
        this.wPawns &= ~end;
        break;
      case ChessPiece.WHITE_KNIGHT:
        this.wKnights &= ~end;
        break;
      case ChessPiece.WHITE_BISHOP:
        this.wBishops &= ~end;
        break;
      case ChessPiece.WHITE_ROOK:
        this.wRooks &= ~end;
        break;
      case ChessPiece.WHITE_QUEEN:
        this.wQueens &= ~end;
        break;
      case ChessPiece.WHITE_KING:
        this.wKings &= ~end;
        break;
    }
  }

  private void moveBlackPiece(byte piece, long start, long end) {
    this.allPieces &= ~start;
    this.allPieces |= end;
    this.blackPieces &= ~start;
    this.blackPieces |= end;
    switch(piece) {
      case ChessPiece.BLACK_PAWN:
        this.bPawns &= ~start;
        this.bPawns |= end;
        break;
      case ChessPiece.BLACK_KNIGHT:
        this.bKnights &= ~start;
        this.bKnights |= end;
        break;
      case ChessPiece.BLACK_BISHOP:
        this.bBishops &= ~start;
        this.bBishops |= end;
        break;
      case ChessPiece.BLACK_ROOK:
        this.bRooks &= ~start;
        this.bRooks |= end;
        break;
      case ChessPiece.BLACK_QUEEN:
        this.bQueens &= ~start;
        this.bQueens |= end;
        break;
      case ChessPiece.BLACK_KING:
        this.bKings &= ~start;
        this.bKings |= end;
        break;
    }
  }

  private void moveAndPromoteBlackPiece(byte piece, long start, long end, byte promotionPiece) {
    this.allPieces &= ~start;
    this.allPieces |= end;
    this.blackPieces &= ~start;
    this.blackPieces |= end;
    switch(piece) {
      case ChessPiece.BLACK_PAWN:
        this.bPawns &= ~start;
        break;
      case ChessPiece.BLACK_KNIGHT:
        this.bKnights &= ~start;
        break;
      case ChessPiece.BLACK_BISHOP:
        this.bBishops &= ~start;
        break;
      case ChessPiece.BLACK_ROOK:
        this.bRooks &= ~start;
        break;
      case ChessPiece.BLACK_QUEEN:
        this.bQueens &= ~start;
        break;
      case ChessPiece.BLACK_KING:
        this.bKings &= ~start;
        break;
    }
    switch(promotionPiece) {
      case ChessPiece.BLACK_PAWN:
        this.bPawns |= end;
        break;
      case ChessPiece.BLACK_KNIGHT:
        this.bKnights |= end;
        break;
      case ChessPiece.BLACK_BISHOP:
        this.bBishops |= end;
        break;
      case ChessPiece.BLACK_ROOK:
        this.bRooks |= end;
        break;
      case ChessPiece.BLACK_QUEEN:
        this.bQueens |= end;
        break;
      case ChessPiece.BLACK_KING:
        this.bKings |= end;
        break;
    }
  }

  private void captureWhitePiece(long captureSquare) {
    this.whitePieces &= ~captureSquare;
    this.wPawns &= ~captureSquare;
    this.wKnights &= ~captureSquare;
    this.wBishops &= ~captureSquare;
    this.wRooks &= ~captureSquare;
    this.wQueens &= ~captureSquare;
    this.wKings &= ~captureSquare;
  }

  private void captureBlackPiece(long captureSquare) {
    this.blackPieces &= ~captureSquare;
    this.bPawns &= ~captureSquare;
    this.bKnights &= ~captureSquare;
    this.bBishops &= ~captureSquare;
    this.bRooks &= ~captureSquare;
    this.bQueens &= ~captureSquare;
    this.bKings &= ~captureSquare;
  }

  // Trims illegal moves that would put the king in check
  public synchronized void trimCheckMoves() {
    this.trimCheckMoves(false);
  }
  public synchronized void trimCheckMoves(boolean force) {
    if (!force && (this.checkMovesTrimmed || !this.movesGenerated)) {
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
  public synchronized void trimQuietMoves() {
    Iterator<Map.Entry<ChessMove, ChessPosition>> it = this.children.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<ChessMove, ChessPosition> entry = it.next();
      if ((entry.getKey().end() & this.allPieces) == 0) {
        it.remove();
      }
    }
    this.checkMovesTrimmed = true;
  }
}
