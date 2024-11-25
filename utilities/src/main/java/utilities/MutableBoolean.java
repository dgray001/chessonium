package utilities;

public class MutableBoolean {
  private boolean v;
  public MutableBoolean(boolean v) {
    this.v = v;
  }
  public void set(boolean v) {
    this.v = v;
  }
  public boolean get() {
    return this.v;
  }
}
