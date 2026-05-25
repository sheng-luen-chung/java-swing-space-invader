# Version 1 UML Class Model

本文件描述 Version 1 的 class model。這一版的目標是「最小可玩」，所以模型保留清楚分工，但不過度抽象。

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
        +setMovingLeft(boolean movingLeft) void
        +setMovingRight(boolean movingRight) void
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
        -boolean alive
        +move(int dx, int dy) void
        +getBounds() Rectangle
        +isAlive() boolean
        +destroy() void
        +getX() int
        +getY() int
        +getWidth() int
        +getHeight() int
    }

    class AlienFleet {
        -List~Alien~ aliens
        -int direction
        +reset() void
        +update(int boardWidth) void
        +allAliensDestroyed() boolean
        +hasReachedY(int y) boolean
        +getAliens() List~Alien~
    }

    class Bullet {
        +WIDTH int
        +HEIGHT int
        -int x
        -int y
        -int speed
        +update() void
        +isOffScreen() boolean
        +getBounds() Rectangle
        +getX() int
        +getY() int
    }

    class CollisionManager {
        +handleBulletAlienCollisions(List~Bullet~ bullets, List~Alien~ aliens, Runnable onAlienDestroyed) void
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
    InputHandler --> GameEngine : sends commands
    GameEngine *-- GameState : owns
    GameState --> Status : uses
    GameEngine *-- Player : owns
    GameEngine *-- AlienFleet : owns
    GameEngine *-- CollisionManager : owns
    GameEngine o-- Bullet : manages
    AlienFleet o-- Alien : manages
    CollisionManager ..> Bullet : checks
    CollisionManager ..> Alien : checks
    GameRenderer ..> GameEngine : reads
```

## 責任摘要

| Class | 主要責任 |
| --- | --- |
| `Main` | 程式進入點，使用 `SwingUtilities.invokeLater` 啟動 Swing UI。 |
| `GameWindow` | 建立 `JFrame`，設定視窗屬性，放入 `GamePanel`。 |
| `GamePanel` | Swing 畫布，啟動 `Swing Timer`，每一幀呼叫 `engine.update()` 與 `repaint()`。 |
| `GameEngine` | 遊戲核心流程，更新玩家、子彈、外星人、碰撞、分數與勝敗。 |
| `GameState` | 儲存分數與遊戲狀態。 |
| `Player` | 玩家飛船的位置、尺寸、移動與碰撞範圍。 |
| `Alien` | 單一外星人的位置、尺寸、存活狀態與碰撞範圍。 |
| `AlienFleet` | 管理外星人群體排列、移動方向、碰邊下降與是否全滅。 |
| `Bullet` | 玩家子彈的位置、速度、移動與是否離開畫面。 |
| `CollisionManager` | 判斷子彈與外星人是否相交，並通知分數更新。 |
| `InputHandler` | 使用 Swing Key Bindings 綁定按鍵，不使用 `KeyListener`。 |
| `GameRenderer` | 使用 Java2D 繪製背景、HUD、玩家、外星人、子彈與結束訊息。 |

## Version 1 的設計取捨

- 沒有抽象父類別，例如 `GameObject`，因為目前物件種類少，先保持初學者容易閱讀。
- 子彈限制為畫面上一次一發，讓碰撞與節奏更簡單。
- `GameRenderer` 只讀取 `GameEngine` 狀態，不修改遊戲資料。
- `InputHandler` 不直接移動玩家，而是通知 `GameEngine` 設定移動旗標。
- `AlienFleet` 管理群體邏輯，單一 `Alien` 不知道整隊外星人的移動規則。

## 主要流程

```text
Main
  -> GameWindow
    -> GamePanel
      -> Swing Timer tick
        -> GameEngine.update()
          -> update player
          -> update bullets
          -> update alien fleet
          -> check collisions
          -> check win / game over
        -> GamePanel.repaint()
          -> GameRenderer.render()
```

## 後續版本可能新增的模型

Version 2 或 Version 3 可以考慮加入：

- `EnemyBullet`: 外星人子彈。
- `Lives` 或在 `GameState` 加入玩家生命值。
- `LevelConfig`: 關卡設定，例如外星人速度、排數、分數。
- `SpriteLoader`: 圖片素材載入。
- `SoundManager`: 音效播放。
- `GameObject` 或 `Entity`: 當可移動物件變多時再抽象化共同行為。
