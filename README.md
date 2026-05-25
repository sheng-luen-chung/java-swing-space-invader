# Java Swing Space Invader

這是一個使用 Java Swing 製作的 Space Invader-style 射擊遊戲教學專案。

目前程式版本是 **Version 2: 完整遊戲規則版**。本版在 Version 1 的最小可玩架構上，加入生命數、敵人射擊、防護牆、關卡推進、暫停與更完整的分數規則。

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
| Enter | 開始遊戲；Game Over 後重新開始 |
| Left / Right | 左右移動玩家飛船 |
| Space | 發射玩家子彈 |
| P | 暫停或繼續 |
| R | 立即重開一局 |

## Version 2 功能

- 玩家有 3 條生命。
- 外星人會隨機向下射擊。
- 玩家被敵人子彈擊中會扣生命。
- 生命歸零時顯示 `GAME OVER`。
- 玩家與外星人之間有防護牆。
- 玩家子彈與敵人子彈都會破壞防護牆。
- 消滅所有外星人後會進入下一關。
- 下一關外星人速度更快，射擊頻率更高。
- 不同列的外星人有不同分數。
- HUD 顯示 score、level、lives。
- 按 `P` 可以暫停或繼續。

## 專案結構

```text
java-swing-space-invader/
  README.md
  docs/
    uml-class-model-v1.md
    uml-class-model-v2.md
    teaching-notes-v1.md
    version2-full-rules.md
    architecture-evolution.md
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
    BulletType.java
    Shield.java
    ScoreManager.java
    LevelManager.java
    CollisionManager.java
    InputHandler.java
    GameRenderer.java
```

## 文件導覽

| 文件 | 用途 |
| --- | --- |
| [Version 1 UML Class Model](docs/uml-class-model-v1.md) | 保留 V1 最小可玩版本的 class model。 |
| [Version 2 UML Class Model](docs/uml-class-model-v2.md) | 描述 V2 加入完整規則後的 class model。 |
| [Version 1 教學筆記](docs/teaching-notes-v1.md) | 適合第一次講解 Swing game loop 與基本分工。 |
| [Version 2 完整規則與設計重點](docs/version2-full-rules.md) | 說明 V2 新增功能、狀態與碰撞規則。 |
| [架構演化紀錄](docs/architecture-evolution.md) | 對照 V1 到 V2 的改良原因。 |
| [版本演進規劃](docs/version-roadmap.md) | 規劃 V1、V2、V3 的功能方向。 |

## 架構重點

本專案刻意避免把所有邏輯塞進一個 `JPanel`：

- `GamePanel` 負責 Swing 畫布與 Timer。
- `GameEngine` 負責遊戲更新流程。
- `GameState` 負責遊戲狀態與生命數。
- `ScoreManager` 負責分數。
- `LevelManager` 負責關卡、外星人速度與射擊頻率。
- `InputHandler` 負責 Key Bindings。
- `GameRenderer` 負責 Java2D 繪圖。
- `CollisionManager` 集中處理所有碰撞。
- `Player`、`Alien`、`AlienFleet`、`Bullet`、`Shield` 負責遊戲物件資料與行為。

## 版本化學習方式

建議教學時不要只看最新版本：

1. 先看 V1 UML，理解最小可玩遊戲如何拆 class。
2. 再看 V2 UML，觀察規則增加後哪些責任被拆出。
3. 最後看架構演化紀錄，討論為什麼要新增 `ScoreManager`、`LevelManager`、`Shield` 與 `BulletType`。
