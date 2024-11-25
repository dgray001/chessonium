
### Chessonium

v0.1.4: Refactor to abstract different searches

## Plans

0.2:
v0.1.5: Configure ab into negamax search
v0.1.6: Add evaluator for piece activity (existing move table)
v0.1.7: Add evaluator / configuration for pawn structure cases (doubled pawns, isolated pawns, passed pawns, etc...)
Other evaluator ideas:
  Activity based on which square (not existing move table)
  Activity based on how many squares are attacked
Implement quiesance search configurable on max depth, what constitutes "quiet", etc...
Can read in configuration from file

## Bugs

King in check from queen and could move toward it diagonally
