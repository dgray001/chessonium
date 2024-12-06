package chess;

import java.util.concurrent.ThreadLocalRandom;

public class ZobristHasher {
  private static ZobristHasher singleton = new ZobristHasher();
  
  private static final int BOARD_SIZE = ChessConstants.BOARD_SIZE * ChessConstants.BOARD_SIZE;

  private final long[][][] pieceKeys = new long[2][6][BOARD_SIZE];
  private final long[] turnKey = new long[2];
  private final long[] castlingRightsKey = new long[256];
  private final long[] enPassantKeys = new long[BOARD_SIZE + 1];

  private final ThreadLocalRandom rand;

  private ZobristHasher() {
    this.rand = ThreadLocalRandom.current();
    generateKeys();
  }

  private void generateKeys() {
    for (int color = 0; color < 2; color++) {
      for (int piece = 0; piece < 6; piece++) {
        for (int square = 0; square < BOARD_SIZE; square++) {
          pieceKeys[color][piece][square] = rand.nextLong();
        }
      }
    }
    turnKey[0] = rand.nextLong();
    turnKey[1] = rand.nextLong();
    for (int i = 0; i < 256; i++) {
      castlingRightsKey[i] = rand.nextLong();
    }
    for (int i = 0; i < BOARD_SIZE + 1; i++) {
      enPassantKeys[i] = rand.nextLong();
    }
  }

  public static long generateZobristHash(ChessPosition p) {
    long hash = 0L;
    for (int i = 0; i < BOARD_SIZE; i++) {
      if ((p.getWPawns() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[0][0][i];
      }
      if ((p.getWKnights() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[0][1][i];
      }
      if ((p.getWBishops() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[0][2][i];
      }
      if ((p.getWRooks() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[0][3][i];
      }
      if ((p.getWQueens() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[0][4][i];
      }
      if ((p.getWKings() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[0][5][i];
      }
      if ((p.getBPawns() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[1][0][i];
      }
      if ((p.getBKnights() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[1][1][i];
      }
      if ((p.getBBishops() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[1][2][i];
      }
      if ((p.getBRooks() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[1][3][i];
      }
      if ((p.getBQueens() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[1][4][i];
      }
      if ((p.getBKings() & (1L << i)) != 0) {
        hash ^= ZobristHasher.singleton.pieceKeys[1][5][i];
      }
    }
    hash ^= ZobristHasher.singleton.turnKey[p.isWhiteTurn() ? 0 : 1];
    hash ^= ZobristHasher.singleton.castlingRightsKey[p.getCastlingRights()];
    int enPassantSpace = Long.numberOfTrailingZeros(p.getEnPassant());
    if (enPassantSpace >= 0 && enPassantSpace < BOARD_SIZE) {
      hash ^= ZobristHasher.singleton.enPassantKeys[enPassantSpace];
    }
    return hash;
  }

  public static long[][][] pieceKeys() {
    return ZobristHasher.singleton.pieceKeys;
  }

  public static long[] turnKey() {
    return ZobristHasher.singleton.turnKey;
  }

  public static long[] castlingRightsKey() {
    return ZobristHasher.singleton.castlingRightsKey;
  }

  public static long[] enPassantKeys() {
    return ZobristHasher.singleton.enPassantKeys;
  }
}
