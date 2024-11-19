package utilities;

public class Bitwise {
  public static byte boolToByte(boolean b) {
    return (byte) (b ? 1 : 0);
  }

  public static short boolToShort(boolean b) {
    return (short) (b ? 1 : 0);
  }

  public static int boolToInt(boolean b) {
    return (b ? 1 : 0);
  }

  public static long boolToLong(boolean b) {
    return (b ? 1 : 0);
  }

  public static byte boolsToByte(boolean[] bs) {
    byte r = 0;
    for (int i = 0; i < bs.length && i < 8; i++) {
      if (bs[i]) {
        r |= 1 << i;
      }
    }
    return r;
  }

  public static short boolsToShort(boolean[] bs) {
    short r = 0;
    for (int i = 0; i < bs.length && i < 16; i++) {
      if (bs[i]) {
        r |= 1 << i;
      }
    }
    return r;
  }

  public static int boolsToInt(boolean[] bs) {
    int r = 0;
    for (int i = 0; i < bs.length && i < 32; i++) {
      if (bs[i]) {
        r |= 1 << i;
      }
    }
    return r;
  }

  public static long boolsToLong(boolean[] bs) {
    long r = 0;
    for (int i = 0; i < bs.length && i < 64; i++) {
      if (bs[i]) {
        r |= 1 << i;
      }
    }
    return r;
  }

  public static boolean[] byteToBools(byte n) {
    boolean[] r = new boolean[8];
    for (int i = 0; i < r.length; i++) {
      r[i] = (n & (1 << i)) != 0;
    }
    return r;
  }

  public static boolean[] shortToBools(short n) {
    boolean[] r = new boolean[16];
    for (int i = 0; i < r.length; i++) {
      r[i] = (n & (1 << i)) != 0;
    }
    return r;
  }

  public static boolean[] intToBools(int n) {
    boolean[] r = new boolean[32];
    for (int i = 0; i < r.length; i++) {
      r[i] = (n & (1 << i)) != 0;
    }
    return r;
  }

  public static boolean[] longToBools(long n) {
    boolean[] r = new boolean[64];
    for (int i = 0; i < r.length; i++) {
      r[i] = (n & (1L << i)) != 0;
    }
    return r;
  }

  public static int combine(short s1, short s2) {
    return (s1 << 16) | (s2 & 0xFFFF);
  }

  public static int combine(boolean b, short s) {
    return combine(boolToShort(b), s);
  }

  public static short[] split(int i) {
    short s1 = (short) (i >> 16);
    short s2 = (short) (i & 0xFFFF);
    return new short[]{s1, s2};
  }
}
