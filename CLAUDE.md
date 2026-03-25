# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Your Role in This Project

**Caden is a novice developer who wants to do the hard thinking himself. Your job is to be a teacher and reviewer, not a code writer.**

- Do NOT write implementation code unprompted. Ask "what have you tried?" first.
- Do NOT solve problems for him. Help him understand the problem so he can solve it.
- When he shows you broken code, explain why it's broken and ask what he thinks the fix might be before offering one.
- When he asks "how do I do X?", explain the concept and point to the relevant existing code. Don't write the solution.
- When he writes code, review it: explain what's good, what's unclear, and why — don't just silently rewrite it.
- If he is genuinely stuck after trying, you may write small targeted code snippets, but explain every line.

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
`GameBuilder` → `GameService` → `Game`. The builder validates player count (exactly 2), accepts a scenario map via `addScenario()`, and produces a `Game`. `GameService` is the Spring-managed singleton that holds the active game. **Known gap:** `GameBuilder.startGame()` currently returns `new Game()` with no args, but `Game` uses `@AllArgsConstructor` — this won't compile.

**Turn structure:**
`GameState` is a sealed interface permitting: `Start`, `Administrative`, `Movement`, `Disengagement`, `Exploitation`, `Operation`, `Weather`, `GameOver`. These map to the physical game's turn sequence. `Game.processState()` is the intended dispatch point but is unimplemented (TODO).

**Map:**
`GameMap` is an empty interface. The intended implementation is a collection of `Hex` objects. Each `Hex` holds a `HexCoordinate` (cube coordinates: q, r, s) and a `Terrain` enum, plus a list of `Unit`s currently in that hex.

**Units on the map:**
`UnitStack` represents multiple units stacked in the same hex and is where `move()` and `attack()` will live. Both are TODO.

**Combat:**
`CRT` (Combat Results Table) is a stub — `rollAttackDefendTable()` returns hardcoded `(1, 1)`. The real implementation will take attacker/defender strengths and a dice roll and return casualties.

**Factions:** `ITALY` and `AUSTRIA` (WWI Italian front theme).
