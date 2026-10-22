# Flight Reservation – Strategy Pattern (Assignment 5)

Domain: **flight reservation**. Two families of interchangeable algorithms are implemented with the Strategy pattern:

| Family | What varies | Strategies |
|---|---|---|
| **Fare** | how a ticket price is calculated | Distance, Demand, LastMinute, International |
| **Seat** | how seats are chosen | Window, FrontRowsFirst, GroupTogether, EmptyRow |

Java 17, JUnit 5, Maven.

## How to run

```bash
mvn test                      # all tests
mvn compile exec:java         # console demo (flight.Main)
```

Without Maven (IntelliJ / VS Code): open the folder as a Maven project, run `flight.Main`, run tests from `src/test/java`.

## Class responsibilities

One sentence each (max 25 words, none of the words if / and / or / but).

| Class | Responsibility |
|---|---|
| `FlightRequest` | Holds route, distance, days to departure, occupancy percent, international flag for one fare calculation. |
| `ExchangeRateProvider` | Declares how currency rates are obtained from any source. |
| `SimulatedExchangeRateProvider` | Returns fixed currency rates from a built-in table. |
| `Prices` | Provides base rate, tax constants, rounding for money values. |
| `FareStrategy` | Declares one method that turns a flight request into a ticket price. |
| `DistanceFareStrategy` | Prices a ticket as distance times base rate plus tax. |
| `DemandFareStrategy` | Multiplies the base price by a factor that grows with flight occupancy. |
| `LastMinuteFareStrategy` | Multiplies the base price by a factor that grows as departure approaches. |
| `InternationalFareStrategy` | Prices a ticket in US dollars then converts it with the current exchange rate. |
| `FareCalculator` (context) | Holds the current fare strategy, delegates every price calculation to it. |
| `FareStrategySelector` | Chooses the fare strategy matching the current request situation. |
| `Seat` | Represents one seat by row number, letter. |
| `Flight` | Stores the seat map with occupied seats for one flight. |
| `SeatRequest` | Holds group size, seat preference of one booking. |
| `SeatPreference` | Lists the seat wishes a passenger may express. |
| `SeatAssignmentStrategy` | Declares one method that picks seats for a request. |
| `WindowSeatStrategy` | Picks the first free window seats in row order. |
| `FrontRowsFirstStrategy` | Picks the first free seats starting from the front row. |
| `GroupTogetherStrategy` | Picks neighbouring free seats in one row for a group. |
| `EmptyRowStrategy` | Picks seats in the row with the most free seats. |
| `SeatAssigner` (context) | Holds the current seat strategy, delegates every assignment to it. |
| `SeatStrategySelector` | Chooses the seat strategy matching group size, passenger preference. |
| `Main` | Demonstrates both families, both selectors, runtime switching in the console. |

### Selector rules (first match wins)

**FareStrategySelector**

1. departure in 2 days or less → `LastMinuteFareStrategy`
2. flight at least 80 % full → `DemandFareStrategy`
3. international flight → `InternationalFareStrategy`
4. otherwise → `DistanceFareStrategy`

**SeatStrategySelector**

1. more than one passenger → `GroupTogetherStrategy`
2. preference `WINDOW` → `WindowSeatStrategy`
3. preference `MORE_SPACE` → `EmptyRowStrategy`
4. otherwise → `FrontRowsFirstStrategy`

The Selectors are the only classes that contain `if` / `switch` on the situation. The contexts (`FareCalculator`, `SeatAssigner`) contain none.

## Why Strategy (and not State or Template Method)

The fare and seat algorithms are **interchangeable ways of doing the same job**. The client (or a Selector) picks one from outside, from facts about the request, then hands it to a context. A strategy does not know about the other strategies and does not decide when it is replaced.

**Not State.** In State the object changes its own behaviour because its internal state changes, and the states usually know the next state and trigger transitions. Here nothing is a life-cycle: `LastMinuteFareStrategy` never turns into `DemandFareStrategy` by itself. The change is decided externally by the Selector or the caller.

**Not Template Method.** Template Method fixes the skeleton of an algorithm in a base class and lets subclasses override single steps, so variation is by inheritance and is chosen at compile time. Our variants share no skeleton: a distance fare and a currency-converted fare have different structure, not only different steps. Strategy uses composition, so a variant can be swapped at runtime (`setStrategy`) and a new variant is a new class with no change to existing ones.

## Refactoring with Git (fare family)

| Step | Git tag | Content |
|---|---|---|
| Before | `before-refactoring` | `FareCalculator` with one `switch` on `FareType` and `if/else` chains |
| After | `after-refactoring` | `FareStrategy` + 4 classes + `FareCalculator` context without `if/switch` |

```bash
git show before-refactoring     # the "bad" version
git show after-refactoring      # the refactored version
git diff before-refactoring after-refactoring
```

Links (replace after pushing to GitHub):

- Before: `https://github.com/<user>/<repo>/tree/before-refactoring`
- After: `https://github.com/<user>/<repo>/tree/after-refactoring`

The test class `FareBehaviourTest` is **identical** on both commits and passes on both. Only the tiny adapter `FareTestSupport` differs, because the constructor of `FareCalculator` changed.

## Isolation from change (Clean Code, Chapter 10)

`InternationalFareStrategy` needs a currency rate, which is data from outside the system. The strategy depends only on our own interface `ExchangeRateProvider`, never on a concrete source.

- Production / demo: `SimulatedExchangeRateProvider` (built-in table).
- Tests: `FakeExchangeRateProvider` (fixed rate, e.g. 1 USD = 500 KZT), so tests are deterministic.
- A real web API can be added later as a new `ExchangeRateProvider` with no change to the strategy.

## Tests (JUnit 5, 96 test cases)

| Test class | What it covers |
|---|---|
| `FareBehaviourTest` | exact prices of every fare variant (runs on both refactoring commits) |
| `FareStrategyTest` | each fare strategy, boundary values |
| `FareStrategyParameterizedTest` | `@ParameterizedTest` on all fare strategies: never negative, longer flight never cheaper |
| `FareCalculatorTest` | context: delegation, `setStrategy`, null check |
| `FareStrategySelectorTest` | every Selector rule, rule order, boundaries |
| `SeatStrategyTest` | each seat strategy |
| `SeatStrategyParameterizedTest` | `@ParameterizedTest` on all seat strategies: free, distinct, correct count, nothing booked |
| `SeatAssignerTest` | context: delegation, `setStrategy`, null check |
| `SeatStrategySelectorTest` | every Selector rule, rule order |
| `FlightTest` | seat map and booking |

## Project structure

```
src/main/java/flight/
  Main.java
  model/   FlightRequest, Seat, SeatRequest, SeatPreference, Flight
  data/    ExchangeRateProvider, SimulatedExchangeRateProvider
  fare/    FareStrategy, 4 strategies, FareCalculator, FareStrategySelector, Prices
  seat/    SeatAssignmentStrategy, 4 strategies, SeatAssigner, SeatStrategySelector
src/test/java/flight/ ...
```
