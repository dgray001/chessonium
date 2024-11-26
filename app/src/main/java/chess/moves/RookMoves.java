package chess.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.ChessConstants;
import chess.ChessPosition;

public class RookMoves {
  private static RookMoves singleton = new RookMoves();

  private final Map<Long, Long[][]> rookMoves = new HashMap<Long, Long[][]>() {{ put(0L, new Long[0][0]); }};

  private RookMoves() {
    for (long space : ChessConstants.ALL_SPACES) {
      int[] dirs = new int[]{1, -1, 8, -8};
      Long[][] validMoves = new Long[dirs.length][];
      for (int i = 0; i < dirs.length; i++) {
        int dir = dirs[i];
        long mv = space;
        List<Long> mvs = new ArrayList<>();
        while (true) {
          int[] s = ChessPosition.coordinatesFromLong(mv);
          mv = dir > 0 ? (mv << dir) : (mv >>> (-dir));
          if (mv == 0) {
            break;
          }
          int[] e = ChessPosition.coordinatesFromLong(mv);
          if (Math.abs(s[0] - e[0]) > 1 || Math.abs(s[1] - e[1]) > 1) {
            break;
          }
          mvs.add(mv);
        }
        validMoves[i] = mvs.stream().toArray(Long[]::new);
      }
      this.rookMoves.put(space, validMoves);
    }
  }

  public static Long[][] getRookMoves(long p) {
    return RookMoves.singleton.rookMoves.get(p);
  }
}
