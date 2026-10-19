# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Your Role in This Project

**Caden and Claude pair-program.** Caden owns the game rules and design decisions; Claude handles boilerplate, tests, and review.

- Caden pastes the rulebook and decides how rules map to code. Ask when a rule is ambiguous; never guess at game rules.
- Claude may write code (boilerplate, tests, loaders, wiring) but must explain what it wrote and why, since Caden is still learning.
- Keep changes small and reviewable. Describe the design before large changes.
- Caden wants to understand the code, so flag trade-offs instead of silently choosing.

## Commands

```bash
# Build
./mvnw clean install

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=GameApplicationTests

# Play the game in the terminal (hot-seat, type 'help' in game)
./mvnw -q spring-boot:run -Dspring-boot.run.main-class=com.arms.game.cli.GameCli
# Optional scenario name from src/main/resources/scenarios: append  -Dspring-boot.run.arguments=montegrappa

# Run the Spring app (no web endpoints yet)
./mvnw spring-boot:run
```

## Architecture

**Stack:** Java 21, Spring Boot 4.0 (Jackson 3, `tools.jackson`), Lombok, Maven. All state is in-memory. The CLI (`cli/GameCli`) does not use Spring.

**Placeholder data:** rules and numbers that were not in the rulebook photos are marked INFERRED in code and in `montegrappa.json`. Known inferred pieces: the CRT grid (`combat/CRT`), terrain movement costs, unit step counts, most Tactical Coordination modifiers, the Monte Grappa map and setup, the VP threshold. `docs/rules-summary.md` has the rules as read from the photos, with [verify] flags.

**Game lifecycle:** `GameBuilder` (players + scenario) -> `Game`. `GameService` is the Spring singleton holding the active game. Scenario JSON in `src/main/resources/scenarios/` is read into `ScenarioDefinition` DTOs by `MapLoader`, which builds the `GameMap`. Tests use `src/test/resources/scenarios/skirmish.json` so tuning the real scenario does not break them. Optional JSON booleans must be `Boolean` (Jackson 3 rejects missing primitives).

**Turn structure:** `GameState` is a sealed interface; each state implements `advance(Game)` and returns the next state: Start -> Administrative -> Operation -> Weather -> [Movement -> Combat -> Disengagement -> Exploitation] for the initiative player, then the same for the other player -> next Operation / Administrative / GameOver. `Game` holds turn, sequence and phasing faction. Weather, Disengagement and Exploitation actions are not implemented (they pass through; Exploitation runs Marker Adjustment). Overstacking blocks the end of Movement/Combat/Exploitation until fixed with `Game.eliminateUnits`.

**Map and units:** `GameMap` holds `Hex` objects (cube `HexCoordinate`, terrain, trench flag, units), objective hexes, offensive turns, reinforcements and the VP threshold. `Unit` has two-sided stats (`Pair` front/back), morale, steps, and Disorganized/Reorganizing/Exploitation markers. Stacking limits live on `Unit.stackingValue()` and `Hex.isOverstacked()`.

**Rules code:** `rules/MovementRules` (ZoC, MP costs, reachable hexes, move), `rules/Victory`, `combat/CombatResolver` (odds, CRT, Tactical Coordination, retreat, advance), `combat/HitPoints` (HP -> step losses/Disorganisation), `combat/CRT`, `combat/Coordination`. `Game.attack()` enforces Combat-phase rules; `Game.setDice()` makes tests deterministic.

**Not implemented yet:** supply, weather rolls, Disengagement, Exploitation movement/combat, Assault Troops, artillery, tanks, rivers/roads/crest lines, Misunderstood Orders effects, Strategic Victory, Spring/REST layer.

**Factions:** `ENTENTE`/`CENTRAL` (nested `Entente`: FRANCE, ITALY; `Central`: GERMANY, AUSTRIA).
