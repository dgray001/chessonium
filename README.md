
### Chessonium

v0.1.6: Configure ab into negamax search

## Plans

0.2:
v0.1.6: Add evaluator for piece activity (existing move table)
v0.1.7: Add evaluator / configuration for pawn structure cases (doubled pawns, isolated pawns, passed pawns, etc...)
Other evaluator ideas:
  Activity based on which square (not existing move table)
  Activity based on how many squares are attacked
Implement quiesance search configurable on max depth, what constitutes "quiet", etc...
Can read in configuration from file

## Bugs

Negamax not working -> to demonstrate play e4,d5,Ba6,Nxa6 then it evaluates +12 for the move Qg4 (at depth of 4) so it's switching the eval somehow
