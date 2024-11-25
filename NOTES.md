
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

I implemented iterative deepening and measured nodes/second as number of times a position is hit by the search, which brought my nodes/second to ~475 kn/s
Of course, this is with a simple evaluator, but it does show that my program is plenty fast enough
Also, since I just use minimax with no pruning or other search optimizations, this speed is only getting me to depth 4-5 in a few seconds

### Example Code

#### Search / Evaluation
