package engine;

import chess.ChessConstants;
import chess.ChessPosition;
import chess.moves.BishopMoves;
import chess.moves.KingMoves;
import chess.moves.KnightMoves;
import chess.moves.QueenMoves;
import chess.moves.RookMoves;

public class Evaluator_Activity implements Evaluator {
  protected float vPawn;
  protected float vKnight;
  protected float vBishop;
  protected float vRook;
  protected float vQueen;
  protected float vKing;
  protected float aSpace;
  protected float aEnemySpace;
  protected float aEnemyPiece;
  protected float aKingSpace;

  public boolean _setConfig(String k, String v) {
    switch(k) {
      case "vPawn":
        this.vPawn = Evaluator.configFloat(v);
        break;
      case "vKnight":
        this.vKnight = Evaluator.configFloat(v);
        break;
      case "vBishop":
        this.vBishop = Evaluator.configFloat(v);
        break;
      case "vRook":
        this.vRook = Evaluator.configFloat(v);
        break;
      case "vQueen":
        this.vQueen = Evaluator.configFloat(v);
        break;
      case "vKing":
        this.vKing = Evaluator.configFloat(v);
        break;
      case "aSpace":
        this.aSpace = Evaluator.configFloat(v);
        break;
      case "aEnemySpace":
        this.aEnemySpace = Evaluator.configFloat(v);
        break;
      case "aEnemyPiece":
        this.aEnemyPiece = Evaluator.configFloat(v);
        break;
      case "aKingSpace":
        this.aKingSpace = Evaluator.configFloat(v);
        break;
      default:
        return false;
    }
    return true;
  }

  private float activityScore(long attack, long enemySide, long enemyPieces, long enemyKing) {
    float e = this.aSpace;
    if ((attack & enemySide) != 0) {
      e += this.aEnemySpace;
    }
    if ((attack & enemyPieces) != 0) {
      e += this.aEnemyPiece;
    }
    if ((attack & enemyKing) != 0) {
      e += this.aKingSpace;
    }
    return e;
  }

  public float _evaluate(ChessPosition p) {
    float e = 0;
    long wKing = KingMoves.getAllKingMoves(p.getWKings());
    long bKing = KingMoves.getAllKingMoves(p.getBKings());
    long bb = p.getWPawns();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vPawn;
      e += this.activityScore(lsb >>> 7, ChessConstants.blackSide, p.getBlackPieces(), bKing);
      e += this.activityScore(lsb << 9, ChessConstants.blackSide, p.getBlackPieces(), bKing);
      bb &= ~lsb;
    }
    bb = p.getWKnights();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vKnight;
      for (long mv : KnightMoves.getKnightMoves(lsb)) {
        e += this.activityScore(mv, ChessConstants.blackSide, p.getBlackPieces(), bKing);
      }
      bb &= ~lsb;
    }
    bb = p.getWBishops();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vBishop;
      for (Long[] dir : BishopMoves.getBishopMoves(lsb)) {
        for (long mv : dir) {
          e += this.activityScore(mv, ChessConstants.blackSide, p.getBlackPieces(), bKing);
          if ((p.getAllPieces() & mv) != 0) {
            break;
          }
        }
      }
      bb &= ~lsb;
    }
    bb = p.getWRooks();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vRook;
      for (Long[] dir : RookMoves.getRookMoves(lsb)) {
        for (long mv : dir) {
          e += this.activityScore(mv, ChessConstants.blackSide, p.getBlackPieces(), bKing);
          if ((p.getAllPieces() & mv) != 0) {
            break;
          }
        }
      }
      bb &= ~lsb;
    }
    bb = p.getWQueens();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vQueen;
      for (Long[] dir : QueenMoves.getQueenMoves(lsb)) {
        for (long mv : dir) {
          e += this.activityScore(mv, ChessConstants.blackSide, p.getBlackPieces(), bKing);
          if ((p.getAllPieces() & mv) != 0) {
            break;
          }
        }
      }
      bb &= ~lsb;
    }
    bb = p.getWKings();
    while (bb != 0) {
      long lsb = bb & -bb;
      e += this.vKing;
      for (long mv : KingMoves.getKingMoves(lsb)) {
        e += this.activityScore(mv, ChessConstants.blackSide, p.getBlackPieces(), bKing);
      }
      bb &= ~lsb;
    }
    bb = p.getBPawns();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vPawn;
      e -= this.activityScore(lsb << 7, ChessConstants.whiteSide, p.getWhitePieces(), wKing);
      e -= this.activityScore(lsb >>> 9, ChessConstants.whiteSide, p.getWhitePieces(), wKing);
      bb &= ~lsb;
    }
    bb = p.getBKnights();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vKnight;
      for (long mv : KnightMoves.getKnightMoves(lsb)) {
        e -= this.activityScore(mv, ChessConstants.whiteSide, p.getWhitePieces(), wKing);
      }
      bb &= ~lsb;
    }
    bb = p.getBBishops();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vBishop;
      for (Long[] dir : BishopMoves.getBishopMoves(lsb)) {
        for (long mv : dir) {
          e -= this.activityScore(mv, ChessConstants.whiteSide, p.getWhitePieces(), wKing);
          if ((p.getAllPieces() & mv) != 0) {
            break;
          }
        }
      }
      bb &= ~lsb;
    }
    bb = p.getBRooks();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vRook;
      for (Long[] dir : RookMoves.getRookMoves(lsb)) {
        for (long mv : dir) {
          e -= this.activityScore(mv, ChessConstants.whiteSide, p.getWhitePieces(), wKing);
          if ((p.getAllPieces() & mv) != 0) {
            break;
          }
        }
      }
      bb &= ~lsb;
    }
    bb = p.getBQueens();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vQueen;
      for (Long[] dir : QueenMoves.getQueenMoves(lsb)) {
        for (long mv : dir) {
          e -= this.activityScore(mv, ChessConstants.whiteSide, p.getWhitePieces(), wKing);
          if ((p.getAllPieces() & mv) != 0) {
            break;
          }
        }
      }
      bb &= ~lsb;
    }
    bb = p.getBKings();
    while (bb != 0) {
      long lsb = bb & -bb;
      e -= this.vKing;
      for (long mv : KingMoves.getKingMoves(lsb)) {
        e -= this.activityScore(mv, ChessConstants.whiteSide, p.getWhitePieces(), wKing);
      }
      bb &= ~lsb;
    }
    return e;
  }
}
