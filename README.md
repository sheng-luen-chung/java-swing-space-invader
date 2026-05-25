# Java Swing Space Invader

這是一個使用 Java Swing 製作的 Space Invader-style 射擊遊戲教學專案。

目前版本是 **Version 1: 最小可玩版本**，重點是建立清楚的 class 分工、Swing Timer game loop、Key Bindings 鍵盤控制，以及基本遊戲規則。

## 如何執行

1. 使用 VS Code 開啟本專案資料夾。
2. 如果尚未安裝 Java 開發工具，請安裝 VS Code 的 **Extension Pack for Java**。
3. 開啟 `src/Main.java`。
4. 點擊 `main` 方法上方的 **Run**。

也可以用終端機執行：

```powershell
javac -d out src\*.java
java -cp out Main
```

## 操作方式

| 按鍵 | 功能 |
| --- | --- |
| Left / Right | 左右移動玩家飛船 |
| Space | 發射子彈 |
| R | 重新開始 |

## Version 1 功能

- 玩家飛船可以左右移動。
- 玩家可以按 Space 發射子彈。
- 畫面上方有多排外星人。
- 外星人群體會左右移動，碰到邊界後下降。
- 子彈擊中外星人後，外星人消失，分數增加。
- 全部外星人被消滅時顯示 `YOU WIN`。
- 外星人下降到玩家附近或底部時顯示 `GAME OVER`。

## 專案結構

```text
java-swing-space-invader/
  README.md
  docs/
    uml-class-model-v1.md
    teaching-notes-v1.md
    version-roadmap.md
  src/
    Main.java
    GameWindow.java
    GamePanel.java
    GameEngine.java
    GameState.java
    Player.java
    Alien.java
    AlienFleet.java
    Bullet.java
    CollisionManager.java
    InputHandler.java
    GameRenderer.java
```

## 主要文件

- [Version 1 UML Class Model](docs/uml-class-model-v1.md)
- [Version 1 教學筆記](docs/teaching-notes-v1.md)
- [版本演進規劃](docs/version-roadmap.md)

## 架構重點

本專案刻意避免把所有邏輯塞進一個 `JPanel`，而是把責任拆開：

- `GamePanel` 負責 Swing 畫布與 Timer。
- `GameEngine` 負責遊戲規則與狀態更新。
- `InputHandler` 負責 Key Bindings。
- `GameRenderer` 負責 Java2D 繪圖。
- `CollisionManager` 負責碰撞判斷。
- `Player`、`Alien`、`AlienFleet`、`Bullet` 負責遊戲物件資料與行為。

這樣的分工適合後續逐步加入敵人子彈、生命值、關卡、圖片素材與音效。
