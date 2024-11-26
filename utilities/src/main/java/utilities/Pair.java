package utilities;

import lombok.Getter;
import lombok.Setter;

public class Pair<K, V> {
  @Getter
  @Setter
  private K k;
  @Getter
  @Setter
  private V v;
  public Pair(K k, V v) {
    this.k = k;
    this.v = v;
  }
}
