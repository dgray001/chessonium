package chess.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.ChessConstants;
import chess.ChessPosition;

public class KingMoves {
  private static KingMoves singleton = new KingMoves();

  private final Map<Long, Long[]> kingMoves = new HashMap<>() {{ put(0L, new Long[0]); }};

  private final Map<Long, Long> allKingMoves = new HashMap<>() {{ put(0L, 0L); }};

  private KingMoves() {
    for (long space : ChessConstants.ALL_SPACES) {
      List<Long> validMoves = new ArrayList<>();
      long allValidMoves = 0L;
      long[] mvs = new long[] {
        space << 1,
        space >>> 1,
        space << 7,
        space >>> 7,
        space << 8,
        space >>> 8,
        space << 9,
        space >>> 9,
      };
      int[] s = ChessPosition.coordinatesFromLong(space);
      for (long mv : mvs) {
        if (mv == 0) {
          continue;
        }
        int[] e = ChessPosition.coordinatesFromLong(mv);
        if (Math.abs(s[0] - e[0]) > 1 || Math.abs(s[1] - e[1]) > 1) {
          continue;
        }
        validMoves.add(mv);
        allValidMoves |= mv;
      }
      this.kingMoves.put(space, validMoves.stream().toArray(Long[]::new));
      this.allKingMoves.put(space, allValidMoves);
    }
  }

  public static Long[] getKingMoves(long p) {
    return KingMoves.singleton.kingMoves.get(p);
  }

  public static long getAllKingMoves(long p) {
    return KingMoves.singleton.allKingMoves.get(p);
  }
}
