package utilities;

public class Logger {
  public static void log(String msg) {
    System.out.println(msg);
  }
  public static void log(Object... objs) {
    Logger.log(Logger.objectsToString(objs));
  }

  public static String objectsToString(Object... objs) {
    StringBuilder s = new StringBuilder();
    for (Object o : objs) {
      if (o == null) {
        s.append("null");
      } else if (o.getClass().isArray()) {
        if (o instanceof Object[]) {
          s.append(arrayToString((Object[]) o));
        } else if (o instanceof int[]) {
          s.append(arrayToString((int[]) o));
        } else if (o instanceof double[]) {
          s.append(arrayToString((double[]) o));
        } else if (o instanceof boolean[]) {
          s.append(arrayToString((boolean[]) o));
        } else if (o instanceof char[]) {
          s.append(arrayToString((char[]) o));
        } else {
          s.append("Unsupported array type");
        }
      } else {
        s.append(o);
      }
      s.append(", ");
    }
    if (s.length() > 2) {
        s.setLength(s.length() - 2);
    }
    return s.toString();
  }

  public static <T> String arrayToString(T[] a) {
    StringBuilder s = new StringBuilder("[");
    for (T e : a) {
        s.append(e).append(", ");
    }
    if (s.length() > 2) {
        s.setLength(s.length() - 2);
    }
    s.append("]");
    return s.toString();
  }
  public static String arrayToString(int[] a) {
    StringBuilder s = new StringBuilder("[");
    for (int e : a) {
        s.append(e).append(", ");
    }
    if (s.length() > 2) {
        s.setLength(s.length() - 2);
    }
    s.append("]");
    return s.toString();
  }
  public static String arrayToString(double[] a) {
    StringBuilder s = new StringBuilder("[");
    for (double e : a) {
        s.append(e).append(", ");
    }
    if (s.length() > 2) {
        s.setLength(s.length() - 2);
    }
    s.append("]");
    return s.toString();
  }
  public static String arrayToString(boolean[] a) {
    StringBuilder s = new StringBuilder("[");
    for (boolean e : a) {
        s.append(e).append(", ");
    }
    if (s.length() > 2) {
        s.setLength(s.length() - 2);
    }
    s.append("]");
    return s.toString();
  }
  public static String arrayToString(char[] a) {
    StringBuilder s = new StringBuilder("[");
    for (char e : a) {
        s.append(e).append(", ");
    }
    if (s.length() > 2) {
        s.setLength(s.length() - 2);
    }
    s.append("]");
    return s.toString();
  }

  public static void err(String msg) {
    System.err.println(msg);
  }
}
