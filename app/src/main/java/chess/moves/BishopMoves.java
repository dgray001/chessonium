package chess.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.ChessConstants;
import chess.ChessPosition;

public class BishopMoves {
  private static BishopMoves singleton = new BishopMoves();

  private final Map<Long, Long[][]> bishopMoves = new HashMap<Long, Long[][]>() {{ put(0L, new Long[0][0]); }};

  private BishopMoves() {
    for (long space : ChessConstants.ALL_SPACES) {
      int[] dirs = new int[]{7, -7, 9, -9};
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
      this.bishopMoves.put(space, validMoves);
    }
  }

  public static Long[][] getBishopMoves(long p) {
    return BishopMoves.singleton.bishopMoves.get(p);
  }
}
