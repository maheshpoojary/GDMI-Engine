# GDMI-Engine

General Decision & Mathematical Intelligence Engine.

## Reverse Edge Engine

GDMI-Engine contains a Live Cricket Over/Under analysis engine based on
the Reverse Edge methodology.

### Inputs

- Current score
- Balls completed
- Wickets fallen
- Recent over runs
- Market line
- Over odds
- Under odds

### Core Analysis

The engine combines:

1. Current Run Rate (CRR)
2. Recent momentum
3. Wicket factor
4. Expected Over Runs (EOR)
5. Market line
6. Market edge
7. Over/Under odds

### Decision

The engine produces one of three decisions:

- `OVER`
- `UNDER`
- `NO BET`

### Reverse Edge Logic

Expected Over Runs are compared against the bookmaker's market line.

```text
Edge = Expected Over Runs - Market Line
