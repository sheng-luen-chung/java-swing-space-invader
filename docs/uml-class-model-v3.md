# Version 3 UML Class Model

本文件描述目前最新的 Version 3 class model。Version 3 在 Version 2 的完整規則上加入體驗層：音效、高分紀錄、爆炸動畫、玩家受擊閃爍，以及更完整的開始 / 過關 / Game Over 畫面。

JDK baseline: Version 3 remains compatible with JDK 8. `SoundManager` uses `javax.sound.sampled`, and `HighScoreManager` uses Java 8-compatible NIO file APIs.

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

    class GameConfig {
        <<final>>
        +BOARD_WIDTH int
        +BOARD_HEIGHT int
        +FRAME_DELAY_MS int
        +INITIAL_LIVES int
        +SOUND_DIR Path
        +HIGH_SCORE_FILE Path
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
        -List~ExplosionEffect~ explosions
        -CollisionManager collisionManager
        -SoundManager soundManager
        -HighScoreManager highScoreManager
        -Random random
        -boolean movingLeft
        -boolean movingRight
        -int levelClearedTicks
        -int playerHitFlashTicks
        +update() void
        +setMovingLeft(boolean movingLeft) void
        +setMovingRight(boolean movingRight) void
        +shoot() void
        +restart() void
        +startGame() void
        +startNextLevel() void
        +togglePause() void
        +getGameState() GameState
        +getScoreManager() ScoreManager
        +getLevelManager() LevelManager
        +getPlayer() Player
        +getAlienFleet() AlienFleet
        +getBullets() List~Bullet~
        +getShields() List~Shield~
        +getExplosions() List~ExplosionEffect~
        +getHighScoreManager() HighScoreManager
        +isPlayerVisible() boolean
        +canAdvanceLevel() boolean
    }

    class GameState {
        -Status status
        -int lives
        -int finalScore
        +reset() void
        +isPlaying() boolean
        +isPaused() boolean
        +loseLife() void
        +hasNoLives() boolean
        +getFinalScore() int
        +setFinalScore(int finalScore) void
    }

    class Status {
        <<enumeration>>
        START_SCREEN
        PLAYING
        PAUSED
        LEVEL_CLEARED
        GAME_OVER
    }

    class SoundManager {
        -Set~String~ warnedSounds
        +play(String soundName) void
    }

    class HighScoreManager {
        -Path highScoreFile
        -int highScore
        +getHighScore() int
        +updateHighScore(int score) void
    }

    class ExplosionEffect {
        -List~Particle~ particles
        +update() void
        +render(Graphics2D g) void
        +isFinished() boolean
    }

    class Particle {
        -double x
        -double y
        -double vx
        -double vy
        -int life
        +update() void
        +render(Graphics2D g) void
        +isAlive() boolean
    }

    class BulletType {
        <<enumeration>>
        PLAYER_BULLET
        ENEMY_BULLET
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

    class CollisionManager {
        +handleCollisions(List~Bullet~ bullets, List~Alien~ aliens, List~Shield~ shields, Player player) CollisionResult
    }

    class CollisionResult {
        -List~Alien~ destroyedAliens
        -boolean playerHit
        +addDestroyedAlien(Alien alien) void
        +getDestroyedAliens() List~Alien~
        +isPlayerHit() boolean
        +setPlayerHit(boolean playerHit) void
    }

    class Player
    class Alien
    class AlienFleet
    class Bullet
    class Shield
    class InputHandler
    class GameRenderer

    Main ..> GameWindow : creates
    GameWindow *-- GamePanel : contains
    GamePanel *-- GameEngine : owns
    GamePanel *-- GameRenderer : owns
    GamePanel ..> InputHandler : creates
    GamePanel ..> GameConfig : reads constants
    InputHandler --> GameEngine : commands
    GameEngine *-- GameState : owns
    GameEngine *-- ScoreManager : owns
    GameEngine *-- LevelManager : owns
    GameEngine *-- Player : owns
    GameEngine *-- AlienFleet : owns
    GameEngine *-- CollisionManager : owns
    GameEngine *-- SoundManager : owns
    GameEngine *-- HighScoreManager : owns
    GameEngine o-- Bullet : manages
    GameEngine o-- Shield : manages
    GameEngine o-- ExplosionEffect : manages
    GameEngine ..> GameConfig : reads constants
    GameState --> Status : uses
    ExplosionEffect o-- Particle : owns
    AlienFleet o-- Alien : manages
    Bullet --> BulletType : has
    CollisionManager ..> CollisionResult : returns
    CollisionManager ..> Bullet : checks
    CollisionManager ..> Alien : checks
    CollisionManager ..> Shield : checks
    CollisionManager ..> Player : checks
    ScoreManager ..> Alien : reads score value
    GameRenderer ..> GameEngine : reads
    SoundManager ..> GameConfig : reads sound path
    HighScoreManager ..> GameConfig : reads data path
```

## Flowchart - Application Lifecycle

```mermaid
flowchart TD
    A[Main.main] --> B[Create GameWindow]
    B --> C[Create GamePanel]
    C --> D[Create GameEngine]
    C --> E[Create GameRenderer]
    C --> F[Register InputHandler key bindings]
    C --> G[Start Swing Timer]

    D --> H[Create GameState, managers, player, fleet, shields]
    D --> I[Load high score through HighScoreManager]

    G --> J{Every 16 ms}
    J --> K[GameEngine.update]
    K --> L[GamePanel.repaint]
    L --> M[GameRenderer.render]
    M --> N[Draw background, HUD, shields, player, aliens, bullets, explosions]
    N --> O{Status overlay needed?}
    O -- START_SCREEN --> P[Draw title, controls, high score]
    O -- PAUSED --> Q[Draw pause message]
    O -- LEVEL_CLEARED --> R[Draw level cleared and next-level hint]
    O -- GAME_OVER --> S[Draw final score and high score]
    O -- PLAYING --> T[No overlay]

    F --> U{Key action}
    U -- Left / Right pressed or released --> V[Set movement flags]
    U -- Space --> W[shoot if playing and bullet limit allows]
    U -- P --> X[Toggle PLAYING / PAUSED]
    U -- R --> Y[Restart from level 1]
    U -- Enter on START_SCREEN --> Z[Set PLAYING]
    U -- Enter on LEVEL_CLEARED --> AA[Advance level after countdown]
    U -- Enter on GAME_OVER --> Y
```

## Flowchart - Version 3 Update Loop

```mermaid
flowchart TD
    A[GameEngine.update] --> B[Update visual effects]
    B --> C{Status is LEVEL_CLEARED?}
    C -- Yes --> D[Decrease levelClearedTicks if needed]
    D --> E[Return and wait for Enter]
    C -- No --> F{Status is PLAYING?}
    F -- No --> G[Return without simulation]
    F -- Yes --> H[Move player from left/right flags]
    H --> I[Update bullets and remove off-screen bullets]
    I --> J[AlienFleet.update using LevelManager speed]
    J --> K{Enemy shot roll succeeds?}
    K -- Yes --> L[Pick front alien and add ENEMY_BULLET]
    K -- No --> M[No enemy shot this frame]
    L --> N[CollisionManager.handleCollisions]
    M --> N

    N --> O{Destroyed aliens?}
    O -- Yes --> P[Add score, create ExplosionEffect, play explosion sound]
    O -- No --> Q[No score change]
    P --> R{Player hit?}
    Q --> R
    R -- Yes --> S[Lose life, flash player, play hit sound]
    R -- No --> T[Keep lives unchanged]
    S --> U{Lives are 0?}
    U -- Yes --> V[Set finalScore, update high score, set GAME_OVER]
    U -- No --> W[Continue playing]
    T --> W

    W --> X{All aliens destroyed?}
    X -- Yes --> Y[Set LEVEL_CLEARED, start countdown, play clear sound]
    X -- No --> Z{Aliens reached danger line or bottom?}
    Z -- Yes --> V
    Z -- No --> AA[Stay PLAYING]
```

## Use Case Scenario

### Scenario 1: Start game from title screen

```mermaid
sequenceDiagram
    actor User
    participant Swing as Swing / GamePanel
    participant Input as InputHandler
    participant Engine as GameEngine
    participant State as GameState
    participant Services as SoundManager / HighScoreManager
    participant Renderer as GameRenderer

    User->>Swing: Launch game
    Swing->>Engine: Create GameEngine
    Engine->>Services: Load high score
    Swing->>Input: Register key bindings
    Swing->>Renderer: Draw title screen

    User->>Swing: Press Enter
    Swing->>Input: Key event
    Input->>Engine: startGame()
    Engine->>State: Set PLAYING
    Swing->>Renderer: repaint()
    Renderer->>Engine: Read status and high score
    Renderer-->>User: Draw active board
```

| Step | User | Swing / GamePanel | InputHandler | GameEngine | GameState | Sound / High Score | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Launches game | Creates `GamePanel`; starts Timer | Registers key bindings | Creates game services and objects | Starts at `START_SCREEN` | Loads saved high score | Draws title screen |
| 2 | Presses Enter | Receives key event | Calls `startGame()` | Accepts start command | Changes to `PLAYING` | High score remains available | Draws active board |

1. User launches the game.
2. `GamePanel` creates `GameEngine`, `InputHandler`, and `GameRenderer`.
3. `HighScoreManager` loads the saved high score.
4. User presses Enter.
5. `InputHandler` calls `GameEngine.startGame()`.
6. `GameState` changes from `START_SCREEN` to `PLAYING`.
7. `GameRenderer` draws the active board.

### Scenario 2: Move and shoot

```mermaid
sequenceDiagram
    actor User
    participant Swing as Swing / GamePanel
    participant Input as InputHandler
    participant Engine as GameEngine
    participant Models as Player / Bullet / AlienFleet
    participant Services as SoundManager
    participant Renderer as GameRenderer

    User->>Swing: Press Left / Right
    Swing->>Input: Key event
    Input->>Engine: setMovingLeft() / setMovingRight()
    Swing->>Engine: Timer tick update()
    Engine->>Models: Move player and alien fleet
    User->>Swing: Press Space
    Swing->>Input: Key event
    Input->>Engine: shoot()
    Engine->>Models: Add PLAYER_BULLET
    Engine->>Services: Play shoot sound
    Swing->>Renderer: repaint()
    Renderer->>Engine: Read player, bullets, aliens
    Renderer-->>User: Draw movement and bullet
```

| Step | User | Swing / GamePanel | InputHandler | GameEngine | Model Objects | Sound / High Score | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Presses Left or Right | Receives key event | Calls movement setter | Stores movement flag | Player waits for next update | - | - |
| 2 | Waits for frame | Timer calls `update()` | - | Moves player and alien fleet | Player and aliens move | - | - |
| 3 | Presses Space | Receives key event | Calls `shoot()` | Checks bullet limit and creates bullet | Adds `PLAYER_BULLET` | Plays shoot sound | - |
| 4 | Watches result | Calls `repaint()` | - | Exposes current objects | Player, bullet, and aliens are read | - | Draws updated board |

1. User presses Left or Right.
2. `InputHandler` sets movement flags on `GameEngine`.
3. On the next timer tick, `GameEngine.update()` moves the player and alien fleet.
4. User presses Space.
5. `GameEngine.shoot()` adds a `PLAYER_BULLET` if the bullet limit allows it.
6. `SoundManager` plays the shoot sound.
7. `GameRenderer` draws the new positions and bullet.

### Scenario 3: Player bullet destroys an alien

```mermaid
sequenceDiagram
    participant Swing as Swing / GamePanel
    participant Engine as GameEngine
    participant Managers as LevelManager / ScoreManager
    participant Models as Bullet / AlienFleet / Shield
    participant Collision as CollisionManager
    participant Effects as ExplosionEffect / Particle
    participant Services as SoundManager
    participant Renderer as GameRenderer

    Swing->>Engine: Next timer tick update()
    Engine->>Managers: Read alien speed and shot chance
    Engine->>Models: Update bullets and alien fleet
    Engine->>Collision: handleCollisions()
    Collision-->>Engine: Destroyed alien
    Engine->>Managers: Add score
    Engine->>Effects: Create explosion particles
    Engine->>Services: Play explosion sound
    Swing->>Renderer: repaint()
    Renderer->>Engine: Read score, aliens, effects
    Renderer-->>Swing: Draw score change and particles
```

| Step | Swing / GamePanel | GameEngine | Level / Score Managers | Model Objects | Collision / Effects | Sound / High Score | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Timer calls `update()` | Updates bullets and fleet | Supplies alien speed | Bullets and aliens move | - | - | - |
| 2 | - | Sends objects to collision check | - | Bullet intersects alien | Reports destroyed alien | - | - |
| 3 | - | Applies result | Adds alien score | Alien is destroyed; bullet removed | Creates explosion particles | Plays explosion sound | - |
| 4 | Calls `repaint()` | Exposes current objects | Score is read | Aliens are read | Effects are read | - | Draws score and particles |

1. Swing Timer calls `GameEngine.update()`.
2. Bullets and alien fleet move.
3. `CollisionManager` checks player bullets against aliens and shields.
4. A destroyed alien is returned to `GameEngine`.
5. `ScoreManager` adds points.
6. `ExplosionEffect` particles are created.
7. `SoundManager` plays the explosion sound.
8. `GameRenderer` draws the updated score and explosion.

### Scenario 4: Enemy bullet hits shield or player

```mermaid
sequenceDiagram
    participant Swing as Swing / GamePanel
    participant Engine as GameEngine
    participant State as GameState
    participant Managers as LevelManager
    participant Models as AlienFleet / Bullet / Shield / Player
    participant Collision as CollisionManager
    participant Services as SoundManager
    participant Renderer as GameRenderer

    Swing->>Engine: Timer tick update()
    Engine->>Managers: Read enemy shot chance
    Engine->>Models: Pick front alien and add ENEMY_BULLET
    Engine->>Collision: handleCollisions()
    alt Shield hit
        Collision-->>Engine: Shield damaged
        Engine->>Models: Remove bullet
    else Player hit
        Collision-->>Engine: Player hit
        Engine->>State: loseLife()
        Engine->>Services: Play player-hit sound
    end
    Swing->>Renderer: repaint()
    Renderer->>Engine: Read shields and player visibility
    Renderer-->>Swing: Draw shield damage or blinking player
```

| Step | Swing / GamePanel | GameEngine | GameState | Level Manager | Model Objects | CollisionManager | Sound / High Score | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Timer calls `update()` | Rolls enemy shot | Still `PLAYING` | Supplies shot chance | Front alien may create `ENEMY_BULLET` | - | - | - |
| 2 | - | Sends bullets, shields, player to collision check | Still `PLAYING` | - | Enemy bullet reaches target | Reports shield or player hit | - | - |
| 3 | - | Applies result | Loses life if player hit | - | Shield health drops or player flashes | - | Plays hit sound if player hit | - |
| 4 | Calls `repaint()` | Exposes state | Lives are read | - | Shields/player are read | - | - | Draws damage or blink |

1. `GameEngine` may create an enemy bullet from a front alien.
2. `CollisionManager` checks enemy bullets against shields first, then the player.
3. If a shield is hit, shield health decreases and the bullet is removed.
4. If the player is hit, `GameState.loseLife()` runs.
5. `SoundManager` plays the player-hit sound.
6. `GameRenderer` draws shield damage or a blinking player.

### Scenario 5: Pause and resume

```mermaid
sequenceDiagram
    actor User
    participant Swing as Swing / GamePanel
    participant Input as InputHandler
    participant Engine as GameEngine
    participant State as GameState
    participant Effects as ExplosionEffect / Particle
    participant Renderer as GameRenderer

    User->>Swing: Press P
    Swing->>Input: Key event
    Input->>Engine: togglePause()
    Engine->>State: Toggle PLAYING / PAUSED
    Swing->>Engine: Timer tick update()
    Engine->>Effects: Update visual effects
    Engine->>State: Return before gameplay simulation if PAUSED
    Swing->>Renderer: repaint()
    Renderer-->>User: Draw paused overlay
```

| Step | User | Swing / GamePanel | InputHandler | GameEngine | GameState | Collision / Effects | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Presses P | Receives key event | Calls `togglePause()` | Switches pause state | Toggles `PLAYING` / `PAUSED` | - | - |
| 2 | Waits while paused | Timer still calls `update()` | - | Returns before gameplay simulation | Remains `PAUSED` | Effects can still age | - |
| 3 | Watches overlay | Calls `repaint()` | - | Exposes paused status | Status is read | Effects are read | Draws paused overlay |

1. User presses P.
2. `InputHandler` calls `GameEngine.togglePause()`.
3. `GameState` toggles between `PLAYING` and `PAUSED`.
4. While paused, `update()` skips gameplay simulation after effect updates.
5. `GameRenderer` draws the paused overlay.

### Scenario 6: Clear level and advance

```mermaid
sequenceDiagram
    actor User
    participant Swing as Swing / GamePanel
    participant Input as InputHandler
    participant Engine as GameEngine
    participant State as GameState
    participant Managers as LevelManager / ScoreManager
    participant Models as Player / AlienFleet / Bullet / Shield
    participant Services as SoundManager
    participant Renderer as GameRenderer

    Swing->>Engine: Timer tick update()
    Engine->>Models: Check all aliens destroyed
    Engine->>State: Set LEVEL_CLEARED
    Engine->>Services: Play level-clear sound
    Swing->>Renderer: repaint()
    Renderer-->>User: Draw level-cleared overlay
    User->>Swing: Press Enter after countdown
    Swing->>Input: Key event
    Input->>Engine: startNextLevel()
    Engine->>Managers: nextLevel()
    Engine->>Models: Reset player, fleet, bullets, shields, effects
    Engine->>State: Set PLAYING
```

| Step | User | Swing / GamePanel | InputHandler | GameEngine | GameState | Level / Score Managers | Model Objects | Sound / High Score | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Clears final alien | Timer calls `update()` | - | Detects empty fleet | Changes to `LEVEL_CLEARED` | Score preserved | Current wave stops | Plays level-clear sound | - |
| 2 | Waits for countdown | Calls `repaint()` | - | Decrements `levelClearedTicks` | Remains `LEVEL_CLEARED` | Preserved | Preserved | - | Draws clear overlay |
| 3 | Presses Enter | Receives key event | Calls `startNextLevel()` | Starts next wave | Returns to `PLAYING` | Level increases | Rebuilds player/fleet/bullets/shields/effects | - | Draws next level |

1. `GameEngine` detects that all aliens are destroyed.
2. `GameState` changes to `LEVEL_CLEARED`.
3. `SoundManager` plays the level-clear sound.
4. `GameRenderer` draws the level-cleared overlay.
5. After the countdown, user presses Enter.
6. `LevelManager.nextLevel()` increases difficulty.
7. `GameEngine` rebuilds current-level objects and returns to `PLAYING`.

### Scenario 7: Game over and restart

```mermaid
sequenceDiagram
    actor User
    participant Swing as Swing / GamePanel
    participant Input as InputHandler
    participant Engine as GameEngine
    participant State as GameState
    participant Managers as LevelManager / ScoreManager
    participant Models as Player / AlienFleet / Bullet / Shield
    participant Services as HighScoreManager
    participant Renderer as GameRenderer

    Swing->>Engine: Timer tick update()
    Engine->>State: Store final score and set GAME_OVER
    Engine->>Services: Update high score if needed
    Swing->>Renderer: repaint()
    Renderer-->>User: Draw final score and high score
    User->>Swing: Press Enter or R
    Swing->>Input: Key event
    Input->>Engine: restart()
    Engine->>State: Reset and set PLAYING
    Engine->>Managers: Reset score and level
    Engine->>Models: Rebuild starting objects
    Swing->>Renderer: repaint()
    Renderer-->>User: Draw restarted board
```

| Step | User | Swing / GamePanel | InputHandler | GameEngine | GameState | Level / Score Managers | Model Objects | Sound / High Score | GameRenderer |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Loses final life or aliens reach danger line | Timer calls `update()` | - | Runs end-game behavior | Stores final score; becomes `GAME_OVER` | Supplies final score | Stops gameplay objects | Updates high score if needed | - |
| 2 | Watches result | Calls `repaint()` | - | Exposes final data | `GAME_OVER` is read | Score is read | Objects are read | High score is read | Draws final score and high score |
| 3 | Presses Enter or R | Receives key event | Calls `restart()` | Resets session | Returns to `PLAYING` | Score and level reset | Recreates starting objects | High score remains loaded | Draws restarted board |

1. `GameEngine` detects no lives or alien danger-line contact.
2. Final score is stored in `GameState`.
3. `HighScoreManager` updates `data/highscore.txt` if the new score is higher.
4. `GameState` changes to `GAME_OVER`.
5. `GameRenderer` draws final score and high score.
6. User presses Enter or R.
7. `GameEngine.restart()` resets state, score, level, and current objects.
8. `GameRenderer` draws the restarted board.

## Version 3 新增責任

| Class | 責任 |
| --- | --- |
| `GameConfig` | 集中畫面尺寸、速度、音效檔名與 high score 檔案路徑。 |
| `SoundManager` | 播放 wav 音效；缺檔或載入失敗只印 warning。 |
| `HighScoreManager` | 讀寫 `data/highscore.txt`，並在 Game Over 時更新最高分。 |
| `ExplosionEffect` | 管理一組爆炸粒子。 |
| `Particle` | 單一粒子的移動、生命週期與繪製。 |
| `GameEngine` | 協調音效、高分、爆炸效果、玩家受擊閃爍與 V3 狀態轉換。 |
| `GameRenderer` | 繪製開始畫面、過關畫面、Game Over final score / high score 與粒子動畫。 |

## V2 到 V3 的模型差異

Version 2 已經有完整規則；Version 3 沒有推翻它，而是新增體驗層：

- `SoundManager`：音效。
- `HighScoreManager`：高分檔案。
- `ExplosionEffect`、`Particle`：動畫。
- `GameConfig`：集中設定。
- `GameRenderer`：新增開始畫面、Game Over 分數、高分與粒子繪製。
- `GameEngine`：協調音效、動畫、高分與新的過關流程。
