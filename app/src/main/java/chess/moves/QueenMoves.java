package chess.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.ChessConstants;
import chess.ChessPosition;

public class QueenMoves {
  private static QueenMoves singleton = new QueenMoves();

  private final Map<Long, Long[][]> queenMoves = new HashMap<Long, Long[][]>();

  private QueenMoves() {
    for (long space : ChessConstants.ALL_SPACES) {
      int[] dirs = new int[]{1, -1, 7, -7, 8, -8, 9, -9};
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
      this.queenMoves.put(Long.valueOf(space), validMoves);
    }
  }

  public static Long[][] getQueenMoves(long p) {
    return QueenMoves.singleton.queenMoves.get(p);
  }
}
