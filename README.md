
### Chessonium

v0.1.9: Full quiescence search

## Plans

0.2:
v0.1.7: Add evaluator / configuration for pawn structure cases (doubled pawns, isolated pawns, passed pawns, etc...)
Implement quiesance search configurable on max depth, what constitutes "quiet", etc...
Add sorting based on previous iterations
Can read in configuration from file

## Bugs

Quiescence search isn't really working, try: e4, d5, Nc3, xe4, Qe2 => black should be (at a depth of 2) playing f5 to save the pawn but this move is not recommended

Is this still happening??
Also, started getting out of memory errors (!!!) => this may be due to ide or to the crashes since this happened in conjunction with crashes
