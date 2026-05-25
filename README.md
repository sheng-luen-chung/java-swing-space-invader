# Java Swing Space Invader

這是一個使用 Java Swing 製作的 Space Invader-style 射擊遊戲教學專案。

目前版本是 **Version 3: 遊戲體驗展示版**。Version 3 在 Version 2 的完整遊戲規則上，加入開始畫面、音效、高分紀錄、爆炸動畫、玩家受擊閃爍，以及更完整的過關與 Game Over 畫面。

## JDK 需求

- Minimum: JDK 8
- Recommended: JDK 17 or newer
- VS Code extension: Extension Pack for Java

檢查環境：

```powershell
java -version
javac -version
```

本專案維持 Java 8 相容，避免使用 `Files.readString`、`Files.writeString` 這類 Java 11 才有的 API。

## 如何執行

### VS Code

1. 使用 VS Code 開啟本專案資料夾。
2. 安裝 **Extension Pack for Java**。
3. 開啟 `src/Main.java`。
4. 點擊 `main` 方法上方由 Java extension 提供的 **Run**。

不要使用 Code Runner 的 **Run Code**，也不要直接執行：

```powershell
java src\Main.java
```

這是多 class 專案，單獨執行 `Main.java` 會找不到 `GameWindow`、`GamePanel` 等 class。

### 終端機

```powershell
javac -d out src\*.java
java -cp out Main
```

## 操作方式

| 按鍵 | 功能 |
| --- | --- |
| Enter | 開始遊戲；過關後進入下一關；Game Over 後重新開始 |
| Left / Right | 左右移動玩家飛船 |
| Space | 發射玩家子彈 |
| P | 暫停或繼續 |
| R | 立即重開一局 |

## Version 3 功能

- 開始畫面顯示標題、操作方式與 high score。
- 玩家有 3 條生命。
- 外星人會隨機向下射擊。
- 防護牆會被玩家與敵人子彈破壞。
- 消滅所有外星人後顯示 `LEVEL CLEARED`，再按 `ENTER` 進下一關。
- Game Over 顯示 final score 與 high score。
- 高分紀錄保存到 `data/highscore.txt`。
- 外星人被擊中時有 Java2D 粒子爆炸。
- 玩家被擊中時會短暫閃爍。
- 支援 wav 音效。

## Version 2 到 Version 3 的差異

| 主題 | Version 2 | Version 3 |
| --- | --- | --- |
| 開始畫面 | 基本 overlay | 顯示標題、操作方式、high score |
| 過關流程 | 倒數後自動下一關 | 倒數後等待 `ENTER` 進下一關 |
| Game Over | 顯示重新開始提示 | 顯示 final score 與 high score |
| 音效 | 無 | 新增 `SoundManager` 與 wav 音效 |
| 動畫 | 無 | 新增 `ExplosionEffect` 與 `Particle` |
| 玩家受擊 | 只扣生命 | 扣生命並短暫閃爍 |
| 高分 | 無 | 新增 `HighScoreManager` 與 `data/highscore.txt` |
| 設定 | 常數分散 | 新增 `GameConfig` |

## 音效檔

教學用音效檔放在：

```text
assets/sounds/
  shoot.wav
  explosion.wav
  player_hit.wav
  level_clear.wav
```

如果音效檔不存在，遊戲仍會正常執行，`SoundManager` 只會印出 warning。

## 文件導覽

| 文件 | 用途 |
| --- | --- |
| [Version 1 UML Class Model](docs/uml-class-model-v1.md) | 保留 V1 最小可玩版本的 class model。 |
| [Version 2 UML Class Model](docs/uml-class-model-v2.md) | 保留 V2 完整規則版的 class model。 |
| [Version 3 UML Class Model](docs/uml-class-model-v3.md) | 說明目前 V3 的 class model。 |
| [Version 1 教學筆記](docs/teaching-notes-v1.md) | 講解 Swing game loop、Key Bindings 與基本分工。 |
| [Version 2 完整規則與設計重點](docs/version2-full-rules.md) | 說明 V2 規則，並標註 V3 的差異。 |
| [Version 3 遊戲體驗設計](docs/version3-game-experience.md) | 說明音效、動畫、狀態機、高分紀錄與執行方式。 |
| [架構演化紀錄](docs/architecture-evolution.md) | 對照 V1、V2、V3 的改良原因。 |
| [版本演進規劃](docs/version-roadmap.md) | 規劃各版本的功能方向。 |

## 架構重點

- `GamePanel` 負責 Swing 畫布與 Timer。
- `GameEngine` 負責遊戲更新流程。
- `GameState` 負責遊戲狀態與生命數。
- `GameConfig` 集中常數與路徑。
- `ScoreManager` 負責分數。
- `LevelManager` 負責關卡、外星人速度與射擊頻率。
- `HighScoreManager` 負責最高分檔案。
- `SoundManager` 負責 wav 音效。
- `InputHandler` 負責 Key Bindings。
- `GameRenderer` 負責 Java2D 繪圖。
- `CollisionManager` 集中處理所有碰撞。
- `ExplosionEffect` 與 `Particle` 負責爆炸動畫。
