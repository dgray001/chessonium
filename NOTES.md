
## Stats

### Initial nodes to search per limit

Limit (half-moves) | Nodes to search from starting position | ~Time with equal evaluator
0: 1      : instant
1: 21     : instant
2: 421    : <1s
3: 9323   : ~3s
4: out of memory error
5:

After a bunch more tests it is somewhere in the range of 2-5kn/s

As far as memory, it uses ~60-80 MB/100 positions which is a bit much.
This is ~600-800 kb/position which is a lot, although this includes all its children presumably
What I do know is the mailbox is a huge amount which can be eliminated with little change in performance (just makes frontend a bit more annoying)
I can also remove the hashmap of pieces and just have 12 longs. This should be the difference of roughly 100 bytes to 1000 bytes

After these optimizations, the memory used is ~100-150 kb/position which is much much less

### Further optimizations

I also was generating moves for all the leaf nodes which is unnecessary, so I stopped doing that and went to 50-150 kn/s

Next big optimization needs to be not storing every position in memory (only the move chain?)
Also related to that I can optimize the move chain to only store 16 bits for a move

### Example Code

#### Search / Evaluation
    // Minimax algorithm with recursion and no excessive board storage
    public int minimax(int depth, boolean maximizingPlayer) {
        if (depth == 0 || board.isGameOver()) {
            return board.evaluate(); // Evaluate the current board position
        }

        int bestScore = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        MoveGenerator moveGenerator = new MoveGenerator(board); // Generate moves dynamically
        Move move;
        while ((move = moveGenerator.nextMove()) != null) {
            board.makeMove(move); // Make the move
            int score = minimax(depth - 1, !maximizingPlayer); // Recursive call
            board.undoMove(move); // Undo the move

            if (maximizingPlayer) {
                bestScore = Math.max(bestScore, score);
            } else {
                bestScore = Math.min(bestScore, score);
            }
        }

        return bestScore;
    }

    // Find the best move using minimax
    public Move findBestMove(int depth) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        MoveGenerator moveGenerator = new MoveGenerator(board);
        Move move;
        while ((move = moveGenerator.nextMove()) != null) {
            board.makeMove(move); // Make the move
            int score = minimax(depth - 1, false); // Evaluate with minimax
            board.undoMove(move); // Undo the move

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }
