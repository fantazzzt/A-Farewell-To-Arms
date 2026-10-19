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

# Run the app
./mvnw spring-boot:run
```

## Architecture

**Stack:** Java 21, Spring Boot 4.0, Lombok, Maven. Currently no persistence layer — all state is in-memory.

**Domain model — the two-sided token:**
Units (`Unit.java`) model physical cardboard counters that have a front side (full strength) and a back side (step loss). This is why attack/defense/movement are all `Pair<Integer, Integer>` — index 0 is full strength, index 1 is reduced. `AttackUnit` is the one concrete subclass so far.

**Game lifecycle:**
`GameBuilder` → `GameService` → `Game`. The builder validates player count (exactly 2), accepts a scenario map via `addScenario()`, and produces a `Game`. `GameService` is the Spring-managed singleton that holds the active game. `startGame()` builds the `Game` from the loaded map (it NPEs if no scenario was added).

**Turn structure:**
`GameState` is a sealed interface permitting: `Start`, `Administrative`, `Movement`, `Disengagement`, `Exploitation`, `Operation`, `Weather`, `GameOver`. These map to the physical game's turn sequence. `Game.processState()` is the intended dispatch point but is unimplemented (TODO).

**Map:**
`GameMap` is a Jackson-loaded class (name, grid size, initiative faction, start date, `hexMap`). Scenario JSON currently lives in `src/main/java/.../scenarios` (should move to resources) and is mostly empty. Each `Hex` holds a `HexCoordinate` (cube coordinates: q, r, s) and a `Terrain` enum, plus a list of `Unit`s currently in that hex.

**Units on the map:**
`UnitStack` represents multiple units stacked in the same hex and is where `move()` and `attack()` will live. `move()` only checks distance; `attack()` is a TODO.

**Combat:**
`CRT` (Combat Results Table) is a stub — the table is placeholder data and the index math is wrong (2d6 sum vs 11 rows, integer division on ratios). Needs the real table from the rules.

**Factions:** `ENTENTE`/`CENTRAL`, with nested `Entente` (FRANCE, ITALY) and `Central` (GERMANY, AUSTRIA) enums (WWI Italian front).
