# 📊 Modelado UML y Diseño Visual (RA5, RA6)

Este documento contiene la representación visual de la arquitectura y el comportamiento del sistema Sudoku Elite mediante diagramas Mermaid.

## 1. Diagrama de Clases (Estructura)
Representa la jerarquía de clases y las relaciones entre componentes.

```mermaid
classDiagram
    class Sudoku {
        -int[][] board
        -int[][] solutionBoard
        -boolean[][] fixedCells
        +isValidMovement(row, col, val) bool
        +placeNumber(row, col, val) bool
        +isResolved() bool
    }
    class SudokuGenerator {
        -Sudoku sudoku
        +generate(difficulty)
        -fillBoard() bool
        -removeNumbers(difficulty)
    }
    class CommandManager {
        -Stack undoStack
        -Stack redoStack
        +executeCommand(command)
        +undo()
        +redo()
    }
    class MoveCommand {
        -Sudoku sudoku
        -int row, col, oldVal, newVal
        +execute()
        +undo()
    }
    class GameDAO {
        +saveGame(userId, board, diff, score)
        +getTopRankings() List
    }
    class MainFrame {
        -Sudoku sudoku
        -CommandManager commandManager
        -SudokuBoardPanel boardPanel
        +main(args)
    }

    MainFrame --> Sudoku
    MainFrame --> CommandManager
    MainFrame --> SudokuGenerator
    MainFrame --> GameDAO
    CommandManager o-- MoveCommand
    MoveCommand --> Sudoku
    SudokuGenerator --> Sudoku
    GameDAO ..> DatabaseConnection
```

## 2. Diagrama de Actividad (Flujo de Juego)
Modela el proceso desde que el usuario inicia el programa hasta que resuelve el puzzle.

```mermaid
graph TD
    Start([Inicio]) --> SelectDiff[Seleccionar Dificultad]
    SelectDiff --> GenBoard[Generar Tablero - Backtracking]
    GenBoard --> UserMove{¿Movimiento del Usuario?}
    UserMove -->|Número| Validate[Validar Movimiento]
    UserMove -->|Pista| GetHint[Obtener Pista]
    UserMove -->|Undo/Redo| StackAction[Ejecutar Comando]
    
    Validate --> UpdateUI[Actualizar Interfaz]
    GetHint --> UpdateUI
    StackAction --> UpdateUI
    
    UpdateUI --> CheckWin{¿Resuelto?}
    CheckWin -->|No| UserMove
    CheckWin -->|Sí| Win([Victoria y Guardado])
```

## 3. Diagrama de Estados (Celda de Sudoku)
Representa los estados posibles por los que pasa una celda durante la ejecución.

```mermaid
stateDiagram-v2
    [*] --> Vacia
    Vacia --> Fija: Generación inicial
    Vacia --> Editada: Entrada usuario
    Editada --> Vacia: Borrado
    Editada --> Error: Incumple reglas
    Error --> Editada: Cambio valor
    Error --> Vacia: Borrado
    Fija --> [*]
    Editada --> [*]: Juego terminado
```

---
*Diagramas actualizados automáticamente reflejando la ingeniería inversa del código Java.*
