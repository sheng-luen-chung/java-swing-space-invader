# Version 2 UML Class Model

Version 2 在 Version 1 的架構上加入完整遊戲規則：生命數、敵人射擊、防護牆、關卡推進、分數管理、暫停與重新開始。這一版的設計目標是「規則變多，但 class 責任仍然清楚」。

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
        +setMovingLeft(boolean movingLeft) void
        +setMovingRight(boolean movingRight) void
        +shoot() void
        +restart() void
        +startGame() void
        +togglePause() void
        +getGameState() GameState
        +getScoreManager() ScoreManager
        +getLevelManager() LevelManager
        +getPlayer() Player
        +getAlienFleet() AlienFleet
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

    class Player {
        -int x
        -int y
        -int width
        -int height
        +moveLeft(int speed) void
        +moveRight(int speed, int boardWidth) void
        +getBounds() Rectangle
        +getCenterX() int
        +getX() int
        +setX(int x) void
        +getY() int
        +getWidth() int
        +getHeight() int
    }

    class Alien {
        -int x
        -int y
        -int width
        -int height
        -int scoreValue
        -boolean alive
        +move(int dx, int dy) void
        +getBounds() Rectangle
        +isAlive() boolean
        +destroy() void
        +getScoreValue() int
    }

    class AlienFleet {
        -List~Alien~ aliens
        -int direction
        +reset() void
        +update(int boardWidth, int horizontalSpeed) void
        +allAliensDestroyed() boolean
        +hasReachedY(int y) boolean
        +getAliens() List~Alien~
        +getRandomShooter(Random random) Alien
    }

    class Bullet {
        +WIDTH int
        +HEIGHT int
        -int x
        -int y
        -int speed
        -BulletType type
        +update() void
        +isOffScreen(int boardHeight) boolean
        +getBounds() Rectangle
        +getX() int
        +getY() int
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
        +addDestroyedAlien(Alien alien) void
        +getDestroyedAliens() List~Alien~
        +isPlayerHit() boolean
        +setPlayerHit(boolean playerHit) void
    }

    class InputHandler {
        -JComponent component
        -GameEngine engine
        +registerKeyBindings() void
    }

    class GameRenderer {
        +render(Graphics2D g, GameEngine engine) void
    }

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
    AlienFleet o-- Alien : manages
    Bullet --> BulletType : has
    ScoreManager ..> Alien : reads score value
    CollisionManager ..> CollisionResult : returns
    CollisionManager ..> Bullet : checks
    CollisionManager ..> Alien : checks
    CollisionManager ..> Shield : checks
    CollisionManager ..> Player : checks
    GameRenderer ..> GameEngine : reads
```

## Class 責任摘要

| Class | Version 2 責任 |
| --- | --- |
| `GameEngine` | 遊戲流程協調者，負責 update 順序、狀態切換、射擊、關卡切換。 |
| `GameState` | 保存目前狀態與玩家生命數。 |
| `ScoreManager` | 管理分數，依照被擊中的 `Alien` 加分。 |
| `LevelManager` | 管理關卡、外星人速度、敵人射擊頻率。 |
| `BulletType` | 區分玩家子彈與敵人子彈。 |
| `Bullet` | 根據 `BulletType` 決定往上或往下移動。 |
| `Shield` | 防護牆，保存耐久度並提供碰撞範圍。 |
| `CollisionManager` | 集中處理玩家子彈、敵人子彈、外星人、防護牆、玩家之間的碰撞。 |
| `CollisionResult` | 把碰撞結果回傳給 `GameEngine`，讓流程層決定加分與扣命。 |
| `AlienFleet` | 管理外星人群體移動，也負責挑選可射擊的前排外星人。 |
| `GameRenderer` | 根據 V2 狀態繪製 HUD、子彈類型、防護牆、暫停與過關畫面。 |
| `InputHandler` | 加入 `Enter` 開始/重開與 `P` 暫停。 |

## Version 2 流程

```text
Swing Timer tick
  -> GameEngine.update()
    -> if LEVEL_CLEARED, run level transition
    -> if not PLAYING, skip update
    -> update player
    -> update bullets
    -> update alien fleet by level speed
    -> maybe enemy shoots
    -> CollisionManager.handleCollisions()
      -> player bullet vs shield
      -> player bullet vs alien
      -> enemy bullet vs shield
      -> enemy bullet vs player
    -> ScoreManager adds score
    -> GameState loses life if player hit
    -> check level cleared / game over
  -> repaint()
```

## V2 的主要設計改進

- `score` 從 `GameState` 移到 `ScoreManager`，避免狀態 class 同時負責計分規則。
- `level` 與難度公式放進 `LevelManager`，避免塞進 `GameEngine`。
- `Bullet` 用 `BulletType` 支援雙向子彈，不急著拆成兩個子類別。
- `CollisionManager` 回傳結果，不直接修改分數或生命，讓碰撞判斷與遊戲規則更新分離。
- `Shield` 成為獨立物件，未來可進一步演化成多格破壞模型。
