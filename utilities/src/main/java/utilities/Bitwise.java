package utilities;

public class Bitwise {
  public static short boolToShort(boolean b) {
    return (short) (b ? 1 : 0);
  }

  public static int combine(short s1, short s2) {
    return (s1 << 16) | (s2 & 0xFFFF);
  }

  public static int combine(boolean b, short s) {
    return combine(boolToShort(b), s);
  }
}
