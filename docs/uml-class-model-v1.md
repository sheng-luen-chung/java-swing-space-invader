# Version 1 UML Class Model

本文件保留 Version 1 的 class model。Version 1 是「最小可玩版本」，重點是建立 Java Swing 遊戲的基本骨架：視窗、畫布、Timer game loop、Key Bindings、玩家、外星人、子彈、碰撞與分數。

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
        -Player player
        -AlienFleet alienFleet
        -List~Bullet~ bullets
        -CollisionManager collisionManager
        -boolean movingLeft
        -boolean movingRight
        +update() void
        +shoot() void
        +restart() void
        +getGameState() GameState
        +getPlayer() Player
        +getAlienFleet() AlienFleet
        +getBullets() List~Bullet~
    }

    class GameState {
        -Status status
        -int score
        +reset() void
        +isPlaying() boolean
        +getStatus() Status
        +setStatus(Status status) void
        +getScore() int
        +addScore(int amount) void
    }

    class Status {
        <<enumeration>>
        PLAYING
        YOU_WIN
        GAME_OVER
    }

    class Player
    class Alien
    class AlienFleet
    class Bullet
    class CollisionManager
    class InputHandler
    class GameRenderer

    Main ..> GameWindow : creates
    GameWindow *-- GamePanel : contains
    GamePanel *-- GameEngine : owns
    GamePanel *-- GameRenderer : owns
    GamePanel ..> InputHandler : creates
    InputHandler --> GameEngine : commands
    GameEngine *-- GameState : owns
    GameEngine *-- Player : owns
    GameEngine *-- AlienFleet : owns
    GameEngine *-- CollisionManager : owns
    GameEngine o-- Bullet : manages
    GameState --> Status : uses
    AlienFleet o-- Alien : manages
    CollisionManager ..> Bullet : checks
    CollisionManager ..> Alien : checks
    GameRenderer ..> GameEngine : reads
```

## Version 1 責任分工

| Class | 責任 |
| --- | --- |
| `Main` | 程式進入點，啟動 Swing UI。 |
| `GameWindow` | 建立 `JFrame` 並放入 `GamePanel`。 |
| `GamePanel` | 使用 `Swing Timer` 進行 game loop，並觸發 repaint。 |
| `GameEngine` | 協調玩家、子彈、外星人、碰撞、勝敗與分數。 |
| `GameState` | 保存遊戲狀態與分數。 |
| `Player` | 玩家飛船位置、移動與碰撞範圍。 |
| `Alien` | 單一外星人位置、存活狀態與碰撞範圍。 |
| `AlienFleet` | 管理外星人群體移動與下降。 |
| `Bullet` | 玩家子彈。 |
| `CollisionManager` | 處理玩家子彈 vs 外星人。 |
| `InputHandler` | 使用 Key Bindings 處理鍵盤。 |
| `GameRenderer` | 使用 Java2D 繪製畫面。 |

## V1 到 V2/V3 的演化

| 主題 | Version 1 | 後續演化 |
| --- | --- | --- |
| 分數 | 放在 `GameState` | V2 移到 `ScoreManager`。 |
| 子彈 | 只有玩家子彈 | V2 新增 `BulletType` 支援敵人子彈。 |
| 狀態 | `PLAYING`、`YOU_WIN`、`GAME_OVER` | V2/V3 新增開始、暫停、過關狀態。 |
| 防護牆 | 無 | V2 新增 `Shield`。 |
| 音效/動畫/高分 | 無 | V3 新增 `SoundManager`、`ExplosionEffect`、`HighScoreManager`。 |

## 教學用途

Version 1 適合用來教：

- Swing 視窗與 `JPanel`
- `Swing Timer` game loop
- Key Bindings
- Java2D 基本繪圖
- 將遊戲拆成 engine / renderer / input / model
