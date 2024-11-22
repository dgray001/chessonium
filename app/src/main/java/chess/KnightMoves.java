package chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilities.Logger;

public class KnightMoves {
  private static KnightMoves singleton = new KnightMoves();

  private final Map<Long, Long[]> knightMoves = new HashMap<Long, Long[]>();

  private KnightMoves() {
    for (long space : ChessConstants.ALL_SPACES) {
      List<Long> validMoves = new ArrayList<>();
      long[] mvs = new long[] {
        space << 6,
        space >>> 6,
        space << 10,
        space >>> 10,
        space << 15,
        space >>> 15,
        space << 17,
        space >>> 17,
      };
      int[] s = ChessPosition.coordinatesFromLong(space);
      for (long mv : mvs) {
        if (mv == 0) {
          continue;
        }
        int[] e = ChessPosition.coordinatesFromLong(mv);
        if (Math.abs(s[0] - e[0]) > 2 || Math.abs(s[1] - e[1]) > 2) {
          continue;
        }
        validMoves.add(mv);
      }
      this.knightMoves.put(Long.valueOf(space), validMoves.stream().toArray(Long[]::new));
    }
  }

  public static Long[] getKnightMoves(long p) {
    return KnightMoves.singleton.knightMoves.get(p);
  }
}
