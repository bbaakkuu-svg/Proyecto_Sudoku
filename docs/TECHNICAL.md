# Technical Documentation - Sudoku Elite
## Industrial Architecture & UML Vivo

This document provides a deep dive into the architectural design of Sudoku Elite, adhering to the requirements of "Entornos de Desarrollo".

### 1. Class Diagram (Mermaid)
The following diagram illustrates the relationship between the domain, persistence, and UI layers.

```mermaid
classDiagram
    class Sudoku {
        -int[][] board
        -boolean[][] fixedCells
        +isValidMovement(row, col, val) bool
        +placeNumber(row, col, val) bool
        +isResolved() bool
    }
    class SudokuGenerator {
        -Sudoku sudoku
        +generate(difficulty)
        -fillBoard() bool
    }
    class DatabaseConnection {
        <<Singleton>>
        -HikariDataSource dataSource
        +getConnection() Connection
    }
    class UserDAO {
        +createUser(user, pass)
        +login(user, pass)
    }
    class GameDAO {
        +saveGame(userId, board, diff, score)
        +getTopRankings() List
    }
    class MainFrame {
        -Sudoku sudoku
        -SudokuBoardPanel boardPanel
        +main(args)
    }
    class SudokuBoardPanel {
        -JTextField[][] cells
        +updateBoard()
    }

    MainFrame --> Sudoku
    MainFrame --> SudokuBoardPanel
    MainFrame --> SudokuGenerator
    MainFrame --> GameDAO
    SudokuBoardPanel --> Sudoku
    SudokuGenerator --> Sudoku
    GameDAO ..> DatabaseConnection
    UserDAO ..> DatabaseConnection
```

### 2. Sequence Diagram: New Game Generation
How the system reacts when a user requests a new game.

```mermaid
sequenceDiagram
    participant User
    participant UI as MainFrame
    participant Gen as SudokuGenerator
    participant Core as Sudoku
    participant DB as GameDAO

    User->>UI: Click "NEW GAME"
    UI->>Gen: generate("medium")
    Gen->>Core: clear()
    Gen->>Gen: fillBoard() (Backtracking)
    Gen->>Core: placeNumber()
    Gen->>UI: updateBoard()
    UI->>User: Display new puzzle
```

### 3. State Diagram: Board Logic
States of a single cell in the Sudoku grid.

```mermaid
stateDiagram-v2
    [*] --> Empty
    Empty --> Fixed: Generator (Initial Clue)
    Empty --> UserEntry: User Input
    UserEntry --> Empty: Clear
    UserEntry --> UserEntry: Update Value
    UserEntry --> Error: Invalid Move (Rule violation)
    Error --> UserEntry: Correct Value
    Fixed --> [*]
```

### 4. Technical Stack Summary
- **Language**: Java 17
- **Architecture**: MVC (Model-View-Controller) / DAO Pattern
- **Persistence**: MySQL 8.0 with HikariCP Pooling
- **Testing**: JUnit 5 + JaCoCo (Quality Gate 50-80%)
- **CI/CD**: GitHub Actions (Build, Test, Javadoc, Coverage)
