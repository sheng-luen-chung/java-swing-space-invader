# Version 2 UML Class Model

本文件保留 Version 2 的 class model。Version 2 是「完整規則版」，重點是生命數、敵人射擊、防護牆、關卡、分數與碰撞集中管理。

目前專案已進入 Version 3；如果要看目前最新模型，請看 [Version 3 UML Class Model](uml-class-model-v3.md)。

## Class Diagram

```mermaid
classDiagram
    class Main {
        +main(String[] args) void
    }

    class GameWindow {
        -JFrame frame
        +GameWindow()
        +showWindow() void
    }

    class GamePanel {
        +WIDTH int
        +HEIGHT int
        -GameEngine engine
        -GameRenderer renderer
        +GamePanel()
        #paintComponent(Graphics g) void
    }

    class GameEngine {
        -int boardWidth
        -int boardHeight
        -GameState gameState
        -ScoreManager scoreManager
        -LevelManager levelManager
        -Player player
        -AlienFleet alienFleet
        -List~Bullet~ bullets
        -List~Shield~ shields
        -CollisionManager collisionManager
        -Random random
        -boolean movingLeft
        -boolean movingRight
        -int levelClearedTicks
        +update() void
        +shoot() void
        +restart() void
        +startGame() void
        +togglePause() void
        +getGameState() GameState
        +getScoreManager() ScoreManager
        +getLevelManager() LevelManager
        +getBullets() List~Bullet~
        +getShields() List~Shield~
    }

    class GameState {
        -Status status
        -int lives
        +reset() void
        +isPlaying() boolean
        +isPaused() boolean
        +getStatus() Status
        +setStatus(Status status) void
        +getLives() int
        +loseLife() void
        +hasNoLives() boolean
    }

    class Status {
        <<enumeration>>
        START_SCREEN
        PLAYING
        PAUSED
        LEVEL_CLEARED
        GAME_OVER
    }

    class ScoreManager {
        -int score
        +reset() void
        +addAlienScore(Alien alien) void
        +getScore() int
    }

    class LevelManager {
        -int level
        +reset() void
        +nextLevel() void
        +getLevel() int
        +getAlienSpeed() int
        +getEnemyShotChance() int
    }

    class Bullet {
        -int x
        -int y
        -int speed
        -BulletType type
        +update() void
        +isOffScreen(int boardHeight) boolean
        +getBounds() Rectangle
        +getType() BulletType
    }

    class BulletType {
        <<enumeration>>
        PLAYER_BULLET
        ENEMY_BULLET
    }

    class Shield {
        -int x
        -int y
        -int width
        -int height
        -int health
        +damage() void
        +isDestroyed() boolean
        +getBounds() Rectangle
        +getHealthRatio() double
    }

    class CollisionManager {
        +handleCollisions(List~Bullet~ bullets, List~Alien~ aliens, List~Shield~ shields, Player player) CollisionResult
    }

    class CollisionResult {
        -List~Alien~ destroyedAliens
        -boolean playerHit
        +getDestroyedAliens() List~Alien~
        +isPlayerHit() boolean
    }

    class Player
    class Alien
    class AlienFleet
    class InputHandler
    class GameRenderer

    Main ..> GameWindow : creates
    GameWindow *-- GamePanel : contains
    GamePanel *-- GameEngine : owns
    GamePanel *-- GameRenderer : owns
    GamePanel ..> InputHandler : creates
    InputHandler --> GameEngine : commands
    GameEngine *-- GameState : owns
    GameEngine *-- ScoreManager : owns
    GameEngine *-- LevelManager : owns
    GameEngine *-- Player : owns
    GameEngine *-- AlienFleet : owns
    GameEngine *-- CollisionManager : owns
    GameEngine o-- Bullet : manages
    GameEngine o-- Shield : manages
    GameState --> Status : uses
    Bullet --> BulletType : has
    AlienFleet o-- Alien : manages
    ScoreManager ..> Alien : reads score value
    CollisionManager ..> CollisionResult : returns
    CollisionManager ..> Bullet : checks
    CollisionManager ..> Alien : checks
    CollisionManager ..> Shield : checks
    CollisionManager ..> Player : checks
    GameRenderer ..> GameEngine : reads
```

## Flowchart

```mermaid
flowchart TD
    A[Main.main] --> B[GameWindow]
    B --> C[GamePanel]
    C --> D[Create engine, renderer, input handler]
    D --> E[Start Swing Timer]

    E --> F{Every timer tick}
    F --> G[GameEngine.update]
    G --> H{Current status}
    H -- START_SCREEN / PAUSED / GAME_OVER --> I[Skip simulation]
    H -- LEVEL_CLEARED --> J[Count down levelClearedTicks]
    H -- PLAYING --> K[Move player]
    K --> L[Update player bullets and enemy bullets]
    L --> M[AlienFleet.update]
    M --> N[Maybe create enemy bullet]
    N --> O[CollisionManager.handleCollisions]
    O --> P[ScoreManager adds alien points]
    O --> Q[GameState loses life if player hit]
    P --> R{All aliens destroyed?}
    Q --> S{No lives left or aliens reached player?}
    R -- Yes --> T[Set LEVEL_CLEARED]
    R -- No --> S
    S -- Yes --> U[Set GAME_OVER]
    S -- No --> V[Continue PLAYING]
    I --> W[GamePanel.repaint]
    J --> W
    T --> W
    U --> W
    V --> W
    W --> X[GameRenderer draws board and overlay]

    D --> Y{Key action}
    Y -- Enter --> Z[Start game / next level / restart]
    Y -- P --> AA[Toggle pause]
    Y -- Space --> AB[Create player bullet]
    Y -- Left / Right --> AC[Set movement flags]
```

## Use Case Scenario

### Scenario 1: Start game from start screen

```mermaid
sequenceDiagram
    actor User
    participant Swing as Swing / GamePanel
    participant Input as InputHandler
    participant Engine as GameEngine
    participant State as GameState
    participant Renderer as GameRenderer

    User->>Swing: Press Enter
    Swing->>Input: Key event
    Input->>Engine: startGame()
    Engine->>State: Set PLAYING
    Swing->>Renderer: repaint()
    Renderer->>Engine: Read status
    Renderer-->>User: Draw playable board
```

| Step | User | Swing / GamePanel | InputHandler | GameEngine | GameState | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | Presses Enter | Receives key event | Calls `startGame()` | Accepts start command | Changes from `START_SCREEN` to `PLAYING` | - |
| 2 | Waits for repaint | Calls `repaint()` | - | Exposes current status | Status is `PLAYING` | Draws playable board |

1. User presses Enter on the start screen.
2. `InputHandler` calls `GameEngine.startGame()`.
3. `GameState` changes to `PLAYING`.
4. `GameRenderer` draws the active game board.

### Scenario 2: Fight through a level

```mermaid
sequenceDiagram
    actor User
    participant Swing as Swing / GamePanel
    participant Input as InputHandler
    participant Engine as GameEngine
    participant State as GameState
    participant Managers as LevelManager / ScoreManager
    participant Models as Player / AlienFleet / Bullet / Shield
    participant Collision as CollisionManager
    participant Renderer as GameRenderer

    User->>Swing: Move and shoot
    Input->>Engine: Set movement flags / shoot()
    Engine->>Models: Move player and add PLAYER_BULLET
    Swing->>Engine: Timer tick update()
    Engine->>Managers: Read speed and shot chance
    Engine->>Models: Move bullets and alien fleet
    Engine->>Collision: handleCollisions()
    Collision-->>Engine: Destroyed aliens / player hit
    Engine->>Managers: Add score if alien destroyed
    Engine->>State: Lose life or change status
    Swing->>Renderer: repaint()
    Renderer->>Engine: Read current state
    Renderer-->>User: Draw board
```

| Step | User | Swing / GamePanel | InputHandler | GameEngine | GameState | Level / Score Managers | Model Objects | CollisionManager | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Moves and shoots | Receives key events | Sets movement flags / calls `shoot()` | Moves player and adds bullet | Still `PLAYING` | - | Player and bullet update | - | - |
| 2 | Waits for frames | Timer calls `update()` | - | Updates bullets, fleet, and enemy shots | Still `PLAYING` | Supplies speed and shot chance | Aliens, bullets, and shields update | - | - |
| 3 | Bullet reaches target | - | - | Sends objects to collision check | May stay `PLAYING` | Adds score if needed | Alien, shield, or player changes | Returns collision result | - |
| 4 | Watches result | Calls `repaint()` | - | Exposes current objects | Current status is read | Current score/level are read | Current objects are read | - | Draws updated board |

1. User moves and shoots during `PLAYING`.
2. `InputHandler` updates movement flags and calls `shoot()`.
3. Each timer tick updates bullets, alien movement, and possible enemy shots.
4. `CollisionManager` checks bullets against aliens, shields, and player.
5. `ScoreManager` adds points for destroyed aliens.
6. `GameState` loses lives if the player is hit.
7. `GameRenderer` draws the updated score, level, lives, bullets, shields, and aliens.

### Scenario 3: Pause, level clear, or game over

```mermaid
sequenceDiagram
    actor User
    participant Swing as Swing / GamePanel
    participant Input as InputHandler
    participant Engine as GameEngine
    participant State as GameState
    participant Managers as LevelManager / ScoreManager
    participant Models as Player / AlienFleet / Shield
    participant Renderer as GameRenderer

    alt Pause requested
        User->>Swing: Press P
        Swing->>Input: Key event
        Input->>Engine: togglePause()
        Engine->>State: Toggle PLAYING / PAUSED
    else Level cleared
        Engine->>State: Set LEVEL_CLEARED
        User->>Swing: Press Enter
        Input->>Engine: start next level
        Engine->>Managers: Increase level
        Engine->>Models: Rebuild level objects
    else Game over
        Engine->>State: Set GAME_OVER
        User->>Swing: Press R or Enter
        Input->>Engine: restart()
        Engine->>Managers: Reset level and score
        Engine->>Models: Rebuild starting objects
    end
    Swing->>Renderer: repaint()
    Renderer-->>User: Draw overlay or refreshed board
```

| Step | User | Swing / GamePanel | InputHandler | GameEngine | GameState | Level / Score Managers | Model Objects | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Presses P | Receives key event | Calls `togglePause()` | Pauses or resumes | Toggles `PLAYING` / `PAUSED` | Preserved | Preserved | Draws paused overlay or board |
| 2 | Clears aliens | Timer keeps firing | - | Detects clear condition | Becomes `LEVEL_CLEARED` | Score preserved | Current wave stops | Draws clear overlay |
| 3 | Presses Enter | Receives key event | Calls next-level command | Starts next level | Returns to `PLAYING` | Level advances | Recreates level objects | Draws next level |
| 4 | Loses game | Timer detects end condition | - | Ends run | Becomes `GAME_OVER` | Score remains visible | Current objects stop | Draws game-over overlay |
| 5 | Presses R or Enter | Receives key event | Calls `restart()` | Resets run | Returns to `PLAYING` | Level and score reset | Recreates starting objects | Draws restarted board |

1. User can press P to pause or resume without resetting progress.
2. If all aliens are destroyed, `GameState` changes to `LEVEL_CLEARED`.
3. User presses Enter to advance; `LevelManager` increases the level and objects are rebuilt.
4. If lives run out or aliens reach the player area, `GameState` changes to `GAME_OVER`.
5. User presses R or Enter to restart from the beginning.

## Version 2 設計重點

- `ScoreManager` 從 `GameState` 拆出，讓分數規則獨立。
- `LevelManager` 管理關卡、敵人速度與射擊頻率。
- `BulletType` 讓同一個 `Bullet` 支援玩家與敵人子彈。
- `CollisionManager` 集中處理四種碰撞。
- `Shield` 成為獨立物件，讓防護牆可以逐漸損壞。

## V2 到 V3 的模型差異

Version 3 在此模型上新增：

- `GameConfig`
- `SoundManager`
- `HighScoreManager`
- `ExplosionEffect`
- `Particle`

Version 3 也讓 `GameEngine` 多了體驗層協調責任：播放音效、建立爆炸效果、更新高分、控制玩家受擊閃爍。
