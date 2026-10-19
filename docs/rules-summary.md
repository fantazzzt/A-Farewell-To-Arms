# Italia 1917-1918 / A Farewell to Arms: Rules Summary

Source: photos of the rulebook (pp. 2-19: Basic Rules + scenario list). Pages 2-11 were photographed sideways with small text. Items marked **[verify]** are things I was not fully sure I read correctly. Red text in the book = Italia 18-specific rules; blue = advanced/optional.

## Not in the photos (needed before we can finish)

- **The CRT itself** (odds columns x 2d6 result grid). The text only describes how to use it (see 5.8).
- **Terrain Effects Chart (TEC)**: MP costs per terrain, hexside effects.
- **Monte Grappa setup player aid**: unit list, positions, which units start reduced/Disorganized.
- **The Monte Grappa map** (hex IDs, terrain, trenches, rivers, supply sources, HQ locations; hexes 2024 and 2026).
- Book pages 1 and 16, and the Basic-game "Doctrine Track" player aid.

---

## 1. Basics

- Two players: **Central Powers (CP)** (Austria-Hungary + Germany) vs **Entente** (Italy, France, UK, US). Faction model: `ENTENTE`/`CENTRAL`.
- 1 hex ≈ 3 km. 1 game turn = 3 days.
- Hexes are identified by number (e.g. 2024). Terrain is whatever fills the hex centre.
- Dice: 4 six-sided dice. **2 white** = combat resolution. **Blue** = Entente Tactical Coordination, **green** = CP Tactical Coordination. Other rolls use any single die.
- **Initiative player** (phasing first) vs **reacting player**. The scenario sets who starts with initiative; it can change.
- **Phasing player** = the player whose phase it is.

## 2. Counters

- Each combat counter has **front (full strength)** and **back (reduced)** sides, each with Attack - Defence - Movement factors (e.g. `9-11-4` / `4-5-4`). Also: unit ID, size symbol (detachment `no symbol`, `I` battalion, `III` regiment, `X` brigade, `XX` division), type (infantry, cavalry, mountain, assault...), and a **morale** number (2-6).
- **Combat units**: divisions, brigades, regiments, infantry detachments, tank units.
- **Support units**: HQs, Army/Corps Artillery, Assault Troops (Stosstruppen / Sturmbataillone / Arditi), Air supports, Supply Depots, Trucks. Support units never get Disorganized and do not exert ZoC.
- **Elite**: a regiment, brigade or infantry detachment with morale 5 or 6. (Italy 18 note: do not assume non-elite just because a unit is a regiment; check the morale.)
- Markers: Pinned, Disorganized (DG), Reorganizing (RG), Exploitation, Reserves, Disengagement, Out of Supply (OOS), Step Loss, Pontoon Bridge, Destroyed Bridge, Retreat mode.
- Detachments (Bersaglieri, Fanteria for Italy; Gebirgstruppen for AH) are small, 1 step. A detachment moving outside an enemy ZoC may flip to its other side [verify].

## 3. Stacking (sec 4)

- Max **4 combat units** in a hex.
- Full-strength **division = 3 units**; reduced division = 2; brigade/regiment/tank = 1; reduced? regiments/brigades still 1 [verify].
- Infantry detachments **don't count** against the limit, but never more than **2 detachments** per hex at end of phase.
- Support units don't count. Assault Troops are limited to the number of eligible combat units in the hex.
- Checked **at the end of a game phase**; excess units are eliminated (owner's choice). Temporary overstacking during movement is allowed.
- Stack units from the same division together where possible.

## 4. Steps and Hit Points

- Units lose "steps". Regiment/brigade/detachment: 1-2 steps; divisions have 2 or 3 steps depending on morale (morale 2-3 -> 2 steps; morale 4 -> 3 steps, with a Step Loss marker on the reduced side for the 3rd) **[verify]**.
- A step loss flips the counter to its reduced side. Losing the last step eliminates it.
- Combat/bombardment results are expressed in **Hit Points (HP)**, converted to step losses at the end of combat (see 5.10).

## 5. Sequence of Play (sec 7)

One **game turn**:

1. **Administrative Sequence**
   - Declaring Offensives (initiative player only) (sec 8)
   - Supply Phase (sec 9)
   - Air Superiority Phase (sec 17, **not used in the introductory scenario**)
2. **1st Operational Sequence**
   - Weather (sec 10, initiative player rolls)
   - Initiative player: **Movement -> Combat -> Disengagement -> Exploitation**
   - Reacting player: the same 4 phases
3. **2nd and 3rd Operational Sequences** only **if an Offensive is underway** (same structure, no Weather roll or new weather per scenario).
4. **Victory Point Determination & End of Turn** (sec 16): VPs recorded; advance the Turn marker. If last turn, determine winner.

### 5.1 Declaring Offensives (sec 8)
- Initiative player may declare one (scenario may force it). It gives the declarer 2 extra Operational Sequences (3 total that turn) and lasts 3 sequences.
- During an Offensive: Army Artillery supports in **Offensive mode** may take part in Bombardment/Combat; an **Offensive modifier** (a DRM-like shift set by the scenario, e.g. +2/+1/0 for the 1st/2nd/3rd sequence) applies to Bombardment.

### 5.2 Supply (sec 9)
- Combat units need a **supply line** traced from the unit's hex to a **supply source** (an HQ, itself in supply via its Command Radius, or a map-edge source / Supply Depot).
- Line traces through hexes; cannot cross enemy units or enemy ZoC (unless a friendly unit occupies the hex); bridges needed over major rivers; rough terrain hexes (mountain, high mountain, marsh) cost 2 hexes of range [verify].
- Unsupplied unit gets an **OOS marker**: it cannot attack, and suffers a defence penalty; it must try to retreat toward supply. Unsupplied support units are eliminated if they can't trace supply.
- HQ Command Radius (CR) counts hexes (not MP); Supply Depot radius up to 20 hexes [verify].
- Supply sources by scenario (map edges, Venice, etc).

### 5.3 Weather (sec 10)
- Initiative player rolls **2d6**: 2-4 Fair; 5-6 Poor; 7 Morning Fog (then re-roll 1d6: even = Fair, odd = Poor); 8-9 Fair; 10-12 Poor.
- Poor Weather: minor roads (dotted) unusable, no air ops; Fair has no effect on air ops.
- **Monte Grappa**: no weather roll; fixed (see sec 11).

### 5.4 Movement (sec 11)
- Each unit has a movement factor (MPs), chosen side by reduced/full strength. Units move individually or as a stack. MPs can't be saved or transferred.
- **Terrain costs from the TEC** (not in photos). Costs are cumulative with hexside costs; **exception**: trench/crater cost don't stack.
- **Minimum move**: a unit may always move at least 1 hex if it isn't going into an impassable hex, a hex occupied by an enemy unit, or leaving/entering an eZoC without being relieved.
- **Road movement**: moving along road hex-to-hex costs 1 MP per hex regardless of terrain. Minor roads only in Fair weather.
- **Rail** is only for Army Artillery supports in Offensive mode and the supply line; no rail movement in the basic game.
- **Enemy ZoC (eZoC)**: a unit entering an eZoC **must stop**. Moving from one eZoC hex directly to another is not allowed. Leaving an eZoC costs no extra MPs. A friendly unit in the hex does not negate the eZoC for movement purposes (it does for supply / retreat).
- **Relief**: a unit that starts in an eZoC can move out only if another friendly unit moves into its hex (relief) or it ends up able to stay; a unit can't leave an eZoC unless relieved by a friendly unit moving in, with the relieved unit allowed to move away up to its full MP. A unit that begins movement alone in an eZoC hex can't leave it unless another friendly unit relieves it (see 11.2.2).
- **Rivers**: major river needs a bridge (road/rail) to cross without extra cost (+1 MP); without a bridge, the unit must make a Pontoon attempt or can't cross. Minor rivers cost +1 MP (no cost via bridge).
- **Pontoon bridges** (CP and Entente): built during Movement/Exploitation from a hex adjacent to the river, within CR of an HQ. 1d6: 1-4 success, with DRMs (+1 enemy within 2 hexes, +1 major river, +1 poor weather/fog, -2 if friendly unit is on the far side, etc.). Costs 1 MP. Limited attempts per phase.
- **Destroying/rebuilding bridges**: destroying requires being within CR of an HQ and an enemy within 3 hexes; 1d6 1-4 destroyed (DRMs) [verify].
- **Forced march**: a non-Disorganized combat unit doubles MPs if the entire move is along roads, it doesn't end within 2 hexes of an enemy, and it isn't in an Offensive-related support...; at the end it becomes Disorganized. Not in Exploitation. Tanks excluded.
- **Reserves markers** (11.2.8): at the end of Movement, a player may place Reserves markers on full-strength, non-Disorganized, non-moved units not in an eZoC and not in range of a friendly Army Artillery. Limited number per side (by scenario). They allow movement during Exploitation Phase (+2 MP).
- **Disengagement markers** (11.2.7): at the end of Movement, place on units that will try to disengage (they're in eZoC).
- **Reinforcements** arrive at scenario-listed HQs at the start of the Movement Phase, stacked/adjacent to the HQ; they can move normally.
- **Support units move**: Army Artillery (road-bound, very limited), Corps Artillery, HQ (like combat units but cannot enter eZoC, can't enter high mountain), Supply Depots don't move in the Basic game, Trucks (15 MPs, road only).
- **Retreat mode** (advanced scenarios), **Variant for experienced players**: not needed.

### 5.5 Combat (sec 12)
Combat is **voluntary**. Phasing player is the attacker. Units attack adjacent enemy hexes. Each hex can be attacked **once** per Combat Phase; each unit attacks **once**. A stack may split to attack different hexes. A unit in an enemy ZoC isn't forced to attack, but all defenders in the hex defend together.

**10-step Combat Sequence** (per combat, resolved one at a time in the attacker's order):
1. Attacker designates target hex and attacking units.
2. Attacker designates Artillery for Bombardment/Counter-battery.
3. Defender designates Artillery.
4. Attacker states Counter-battery.
5. Counter-battery fire resolved (attacker first).
6. Each side designates its **lead unit** (the unit whose morale will be used).
7. Compute the **odds ratio**.
8. **Combat resolution**: roll 2 white dice vs CRT -> HPs for each side.
9. **Tactical Coordination**: each player rolls own colored die.
10. Apply results & convert HPs to step losses / Disorganisation.

*(Steps 2-5 only matter if Artillery is in play.)*

**Designating the lead unit**: the highest-morale unit (owner's choice among ties) among the participating units; must have morale ≥ 4 to be allowed to use Bombardment for the attack [verify]. The lead unit suffers the 1st step loss.

**Odds ratio (step 7)**:
- Sum attackers' Attack factors (front/back side as it is); sum defenders' Defence factors.
- Attack factor / Defence factor -> ratio; round **in favour of the defender** (fractions don't round up for the attacker).
- Then apply column shifts (cumulative), net shift left (defender-favouring) or right (attacker-favouring). The CRT range limits apply; below 1/2 = auto "attacker loses 3 steps" [verify]. Cap at the table edge: extra shifts beyond the table's last column are lost [verify].

**Terrain / situational modifiers** [verify details]:
| Situation | Effect |
|---|---|
| Defender in Forest, Marsh, Town, City | 1 column shift left (City: also see Bombardment) |
| Defender in Mountain / High Mountain | 1 column shift left |
| Defender in Trenches | Defence factor x3 (x2 for the Entente under Evolving or Cadorna/Foch doctrine, per Monte Grappa rules) |
| Attack across minor river | 1 column shift left (if **all** attackers attack across it) |
| Attack across major river | Attack factor x1/2 for units attacking across |
| Attacking up a crest line hexside | Attack factor x1/2 |
| Concentric attack (6 hexes) | 1 column right (see Other modifiers) |
| Assault Troops (Stoss/SturmB/Arditi) attacking | 1 column shift right (once, regardless of number) |
| Tank unit in the attack | Coordination bonus column shifts [verify] |
| Lead unit morale difference | Column shift per point of morale difference [verify] |
| Defender Out of Supply | 1 column shift right |
| Defender in the Offensive modifier / Bombardment | per Bombardment Table |

**Combat results (step 8)**: roll 2 white d6, cross-reference with the odds column of the CRT. Results are like `A#`/`D#` = number of Hit Points inflicted on Attacker/Defender; `*` = obligatory loss of an Assault Troops support if used (step loss if one was used for the column shift).

**Tactical Coordination (step 9)**: attacker and defender each roll **their own coloured die**, apply DRMs, and consult:
- Natural 6 or modified **6+** = **Perfect**
- **3-5** = **Partial**
- **1-2** (or natural 1) = **No Coordination**
- **0 or less** (including natural 1 with negative modifiers) = **Misunderstood Orders** (also No Coordination, plus roll another d6 on the Misunderstood Orders table).

DRMs listed (attacker/defender; the exact breakdown needs checking) include: lead unit elite (+1), morale of the lead unit 2/3/5/6, trench (-2 for defender? or +), friendly Air support, Disorganized lead (-1), inter-allied cooperation penalty (-1 for units of different nationalities, Entente only), Cadorna/Foch doctrine (-1), Diaz doctrine, Offensive modifier **[verify the whole table; I'll need a clearer photo]**.

**Perfect Tactical Coordination** (defender): may retreat up to 3 hexes, deducts 3 HP per hex; may advance... Attacker: may advance after combat up to 2 hexes if the defender is eliminated/retreated, place up to 2 units into Exploitation mode.
**Partial**: defender may retreat up to 2 hexes (3 HP deducted per hex); may move 1 friendly unit adjacent to... Attacker: may advance into the defender's hex, may place 1 unit in Exploitation.
**None**: no benefit; and the *other* side may remove a Reserves/Exploitation marker from a unit within 3 hexes.
**Misunderstood Orders**: roll another d6 -> table (defender: sacrifice/panic/suicidal counter-attack/rout/indecision (Pinned)/misdirected reserves; attacker: bitter fighting/confusion/stubborn attack/panic/half-hearted/counterorders).

**Converting HP -> losses (step 10)** (cost in HP):
- Step loss: regiment, detachment **2 HP**; brigade (AH or Italian) **3 HP**; division **3 HP**; British brigade 2 HP; US regiment 3; Tank = its protection rating; Assault Troops support **2 HP**.
- **Disorganise** a unit: **1 HP**.
- Order: 1st step loss to the **lead unit** (if enough HP); then distribute remaining HP into step losses on other units at owner's choice (aiming to eliminate whole units first, including the lead unit's elimination); then leftover HP Disorganise units (lead unit first if it survived); extra HP are lost.
- Exceptions: Stosstruppen/SturmB/Arditi support take specified losses if used; Tanks attacking always take the first loss if HPs ≥ protection rating.
- Italian "display" divisions (23 DF, 52 DF, 80 DF) take losses from their component units rather than as step losses.

### 5.6 Retreat and Advance (12.3, 12.4)
- **Retreat**: defender chooses path, ignores terrain costs; all units in the hex withdraw. Move toward supply/friendly HQ, avoiding eZoCs where possible. Each eZoC passed through (not friendly-occupied) costs a step loss. Retreating through friendly stacks overstacks/Disorganises them. A 2-3 hex retreat -> Disorganized. Crossing an unbridged major river hex: must stop and become Disorganized. Support units don't retreat apart from Assault Troops accompanying units.
- **Advance after combat**: full-strength, non-Disorganized attackers may advance after the defenders are eliminated/retreat, depending on the Tactical Coordination result (Partial: into the defender's hex; Perfect: as many hexes as the defender retreated, or up to 2 hexes if the defender was eliminated). Ignores MP costs. First hex must be the attacked hex; no entering eZoC to eZoC after the 1st hex (except CP Stoss/SturmB). Unbridged major river stops the advance in the 1st hex. Crest line hexside crossed in the 2nd/3rd hex = Disorganized.

### 5.7 Disorganized / Reorganizing (12.5)
- DG: movement allowance halved (round down), can't attack (but can disengage), no advance/Exploitation.
- DG markers flip to RG in the Marker Adjustment segment (14.3); RG units still treated as DG for penalties; RG units in an eZoC cannot have the marker removed.
- A DG unit suffering another DG result is unaffected. Support units can't be DG. HPs Disorganise **units, not stacks**; a **forced march or retreat result** Disorganises the whole stack.

### 5.8 Disengagement (sec 13)
- After all Combats in an Operational Sequence, units that received a Disengagement marker in the preceding Movement may try to disengage from eZoCs (not support units).
- Roll 1d6 per stack (using the morale of a chosen unit for DRMs), consult the table:
  | Roll | Result |
  |---|---|
  | <0 | `*r1` (a step loss, then retreat 1) |
  | 1 | R3 (retreat 3, opponent chooses path, Disorganized) |
  | 2 | R3 |
  | 3-4 | r2 / r2 |
  | 5 | r3 |
  | 6+ | r3 |
  | >7 | r4* |
  DRMs: morale factor 2 = -2; 3 = -1; 5 = +1; 6 = +2; cavalry +2; exit trench +3; exit city +1; surrounded by enemy units/eZoCs -3 (adjacent friendly unit cancels); stack contains DG/RG unit -2; Retreat mode +1. [verify table]
- Retreat follows 12.3.

### 5.9 Exploitation (sec 14)
- Phase has 3 segments: **(1) Exploitation Combat** (units with Exploitation markers adjacent to enemies may attack; optional; no new Exploitation markers; only Counter-battery for artillery), **(2) Exploitation Movement** (units with Exploitation markers move up to half their MP; Reserves units move with +2 MP; forced march not allowed), **(3) Marker Adjustment**.
- Only full-strength, non-DG units that took part in combat and got an Exploitation marker are eligible; a division's/units' markers: 1 marker covers all regiments/brigades of one division stacked together if they're the only combat units in the hex.
- **Marker Adjustment order**: (1) remove Reorganizing markers on units not in an eZoC, (2) flip Disorganized -> Reorganizing, (3) remove Exploitation & Reserves, (4) remove Disengagement, (5) remove Pinned. (Adjusts **both sides'** units.)

### 5.10 Artillery, Supports, Tanks (sec 15) [Basic outline only]
- **Corps Artillery**: Bombardment mode (supports one combat per phase, if the hex is in range) or Counter-battery mode. **Army Artillery**: Bombardment mode (1 combat) or Offensive mode (up to 4 combats, only in a declared Offensive).
- **Bombardment Table**: roll 1d6 on a column determined by Bombardment points (attacker's minus defender's Counter-battery points); gives HPs immediately.
- **Assault Troops**: Stosstruppen (CP, German regiments), Sturmbataillone (CP, AH brigades morale 5+ or divisions morale 4), Arditi (Italian regiment/brigade/division of morale 4). Provide +1 column shift (attack), certain special movement (move from eZoC to eZoC on advance), +1 DRM for Stoss (Stoss only). If the CRT result has an `*`, the Assault Troops must be eliminated. 
- **Trucks** (Entente): Move 15 MPs along roads; can carry units (see 11.3.4).
- **Supply Depots**: static; supply source; captured by a friendly unit moving in alone (gives VPs). Destruction needs same conditions as bridges (see 11.2.5/15.5.1).
- **Tanks** (Entente basic; German/Italian tanks are a campaign random event): count as 1 unit for stacking, 1 step; **protection rating** = HPs needed to eliminate; attacks: "coordination bonus" column shifts; tank must take the first loss if HP ≥ protection rating.

### 5.11 Victory Points & End of Turn (sec 16)
- Each player records the VPs gained this turn on the VP track; the scenario states how the winner is determined.

---

## 6. Monte Grappa: The Sacred Mountain (Introductory Scenario)

**Historical:** the last Austro-German offensive of December 1917, trying to take Monte Grappa. About one hour of play.

- **Length**: 2 turns, 11-16 December 1917 (**Turns 17 and 18**). 4 Operational Sequences total (3 in Turn 17 because the CP declares an Offensive; 1 in Turn 18).
- **Map**: own map (*Monte Grappa: The Sacred Mountain*); play area bounded: no hexes west of the River Brenta, none east of the River Piave. **Units that are forced out of the play area are eliminated.**
- **Initial setup**: see Monte Grappa setup player aid (**not photographed**). Some CP units start reduced and/or Disorganized.
- **Initiative**: CP has it for the whole scenario (matches `"initiative": "GERMANY"` in `montegrappa.json`).
- **Turn 17**: CP **must declare an Offensive**: offensive modifiers **+2 / +1 / 0** (1st/2nd/3rd sequence); **1** Army Artillery support in Offensive mode.
- **Turn 18**: no Offensive, only one Operational Sequence.
- **Weather**: "Morning Fog, then Poor Weather" on Turn 17's 1st Operational Sequence, then **Poor Weather** for the rest. So: no weather rolls, no Air supports, **minor roads unusable**.
- **Entente doctrines**: Italians **Evolving**, French **Cadorna/Foch**. Effects: all Entente units have **defence values doubled when entrenched**; inter-allied cooperation allowed with a **-1 DRM penalty** ("units of different nationalities") on Tactical Coordination (even if the only cooperation is artillery support from another nationality). The **lead unit** determines which doctrine applies per combat. Austro-Hungarian and German units count as one nationality (no penalty).
- **Reinforcements** (CP only): Turn 17, start of the CP Movement Phase of the **2nd Operational Sequence**: **50 ID AH** arrives stacked with or adjacent to **XIV Armee HQ**. It can move and fight normally from arrival.
- **Victory conditions** (CP):
  - **Strategic Victory**: at end of scenario at least 1 CP combat unit occupies a hex in **clear terrain south of the Entente trench line** and can trace supply to XIV Armee HQ.
  - **Operational Victory**: occupies hexes **2024 and 2026** with combat units that can trace supply to XIV Armee HQ.
  - Otherwise count VPs: 1 VP per Entente trench hex occupied/controlled by CP, plus the value of any objective hexes (Iron Cross symbols = VPs). **> 5 VPs = Tactical Victory (CP)**; **= 5 = draw**; **< 5 = Entente wins**.
  - CP victory level is lowered one level if at the end the Entente occupies at least one CP trench hex with a combat unit that can trace supply to **IV Armata HQ**.

---

## 7. Mapping to the current code (preview)

| Rule | Where in code | Status |
|---|---|---|
| Two-sided counters (A-D-M x front/back) | `Unit`, `AttackUnit` | Exists; no morale, no size, no steps/step loss model, no unit name/ID |
| Stacking limits (4 units, division = 3, etc.) | none | Missing |
| Hex map, terrain, trenches, rivers, roads | `Hex`, `GameMap`, `HexCoordinate` | Only `GRASSLAND`; no hexsides, no trench/road/river |
| Movement with MP, ZoC, terrain cost | `UnitStack.move` | Distance-only; needs TEC + ZoC |
| Phases/turn sequence | `GameState` sealed interface | Empty states; no dispatch |
| Odds ratio, shifts, CRT, Tactical Coordination, HP conversion | `CRT` | Placeholder; needs the real tables |
| Supply, weather, VPs | none | Missing (weather fixed in Monte Grappa) |
| Scenario setup/loading | `MapLoader`, `montegrappa.json` | Loader exists; JSON has no content |

## 8. Suggested scope for the first playable Monte Grappa

Required: map, setup, stacking, movement (with ZoC and roads/terrain), combat (odds, CRT, Tactical Coordination, HP conversion, retreat, advance), Disorganized/Reorganizing, Disengagement, Exploitation, Marker Adjustment, supply (simplified), Offensive declaration, one reinforcement, victory check.
Can defer: Air support, weather rolls, bridges/pontoons (if the map has no major rivers inside the play area), Trucks, Supply Depot capture, tanks, rail, Reserves markers (if setup uses none), Advanced Rules.
