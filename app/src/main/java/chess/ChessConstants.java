package chess;

import utilities.Bitwise;

public class ChessConstants {
  // board is a square of this size
  public static final int BOARD_SIZE = 8;

  // files are columns
  public static final long[] files = new long[]{255, 255 << 8, 255 << 16, 255 << 24, 255 << 32, 255 << 40, 255 << 48, 255 << 56};
  // files are rows
  public static final long[] ranks = new long[]{
    0x0101010101010101L, // every 8th bit is 1, starting with the first bit
    0x0101010101010101L << 1,
    0x0101010101010101L << 2,
    0x0101010101010101L << 3,
    0x0101010101010101L << 4,
    0x0101010101010101L << 5,
    0x0101010101010101L << 6,
    0x0101010101010101L << 7
  };
  public static final long whiteSide = ranks[0] | ranks[1] | ranks[2] | ranks[3];
  public static final long blackSide = ranks[4] | ranks[5] | ranks[6] | ranks[7];

  public static final long[] ALL_SPACES = {
    1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L, 1024L, 2048L, 4096L, 
    8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 1048576L, 
    2097152L, 4194304L, 8388608L, 16777216L, 33554432L, 67108864L, 134217728L, 
    268435456L, 536870912L, 1073741824L, 2147483648L, 4294967296L, 8589934592L, 
    17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 
    549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 
    17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 
    281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 
    4503599627370496L, 9007199254740992L, 18014398509481984L, 
    36028797018963968L, 72057594037927936L, 144115188075855872L, 
    288230376151711744L, 576460752303423488L, 1152921504606846976L, 
    2305843009213693952L, 4611686018427387904L, -9223372036854775808L
  };

  public static final byte CASTLING_WHITE_QUEENSIDE = 1;
  public static final byte CASTLING_WHITE_KINGSIDE = 2;
  public static final byte CASTLING_BLACK_QUEENSIDE = 4;
  public static final byte CASTLING_BLACK_KINGSIDE = 8;
  public static final byte CASTLING_WHITE = Bitwise.boolsToByte(new boolean[]{true, true});
  public static final byte CASTLING_BLACK = Bitwise.boolsToByte(new boolean[]{false, false, true, true});
  public static final long WHITE_QUEENSIDE_ROOK_START = 1L << ChessPosition.coordinatesToByte(0, 0);
  public static final long WHITE_KINGSIDE_ROOK_START = 1L << ChessPosition.coordinatesToByte(0, 7);
  public static final long BLACK_QUEENSIDE_ROOK_START = 1L << ChessPosition.coordinatesToByte(7, 0);
  public static final long BLACK_KINGSIDE_ROOK_START = 1L << ChessPosition.coordinatesToByte(7, 7);
  public static final long WHITE_QUEENSIDE_ROOK_END = 1L << ChessPosition.coordinatesToByte(0, 3);
  public static final long WHITE_KINGSIDE_ROOK_END = 1L << ChessPosition.coordinatesToByte(0, 5);
  public static final long BLACK_QUEENSIDE_ROOK_END = 1L << ChessPosition.coordinatesToByte(7, 3);
  public static final long BLACK_KINGSIDE_ROOK_END = 1L << ChessPosition.coordinatesToByte(7, 5);
}
