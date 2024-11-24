
## Stats

### Nodes to search per limit

Limit (half-moves) | Nodes to search from starting position | ~Time with equal evaluator
0: 1      : instant
1: 21     : instant
2: 421    : <1s
3: 9323   : ~3s
4: out of memory error
5:

After a bunch more tests it is somewhere in the range of 2-5kn/s

As far as memory, it uses ~>50MB/100 positions which is a bit much.
This is ~500kb/position which is a lot, although this includes all its children presumably
What I do know is the mailbox is a huge amount which can be eliminated with little change in performance (just makes frontend a bit more annoying)
I can also remove the hashmap of pieces and just have 12 longs. This should be the difference of roughly 100 bytes to 1000 bytes
