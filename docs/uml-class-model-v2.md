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
