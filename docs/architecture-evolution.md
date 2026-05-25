# 架構演化紀錄

JDK baseline: source code remains compatible with JDK 8. Version 3 originally used Java 11 file APIs during high score work, but the implementation was changed back to Java 8-compatible `Files.readAllBytes` and `Files.write`.

本文件記錄各版本之間的設計改良。目的不是只保存最後結果，而是留下「為什麼要改」與「下一版可以怎麼接」。

## V1 到 V2 的改良總覽

| 主題 | Version 1 | Version 2 | 改良原因 |
| --- | --- | --- | --- |
| 遊戲狀態 | `PLAYING`、`YOU_WIN`、`GAME_OVER` | `START_SCREEN`、`PLAYING`、`PAUSED`、`LEVEL_CLEARED`、`GAME_OVER` | 支援開始畫面、暫停、過關轉場與 Game Over 重開。 |
| 分數 | 放在 `GameState` | 移到 `ScoreManager` | 分數規則開始變多，獨立管理較清楚。 |
| 關卡 | 無 | 新增 `LevelManager` | 關卡會影響敵人速度與射擊頻率，需要集中管理。 |
| 子彈 | 只有玩家子彈 | `BulletType.PLAYER_BULLET` 與 `ENEMY_BULLET` | 支援敵人射擊，同一個 `Bullet` 仍可保持簡單。 |
| 碰撞 | 玩家子彈 vs 外星人 | 四種碰撞集中處理 | 防止碰撞規則散落在多個 class。 |
| 防護牆 | 無 | 新增 `Shield` | 加入 Space Invader 典型防守元素。 |

## V2 到 V3 的改良總覽

| 主題 | Version 2 | Version 3 | 改良原因 |
| --- | --- | --- | --- |
| 開始畫面 | 基本 overlay | 標題、操作說明、high score | 更適合課堂展示。 |
| 過關流程 | 倒數後自動下一關 | 顯示後等待 `ENTER` | 讓玩家掌握節奏，也更清楚示範狀態轉換。 |
| Game Over | 顯示重開提示 | 顯示 final score 與 high score | 回饋更完整。 |
| 音效 | 無 | 新增 `SoundManager` | 把體驗層獨立出來，不污染 `GamePanel`。 |
| 動畫 | 無 | 新增 `ExplosionEffect` 與 `Particle` | 用 Java2D 展示簡單動畫設計。 |
| 玩家受擊 | 只扣生命 | 扣生命並閃爍 | 增加可見 feedback。 |
| 高分 | 無 | 新增 `HighScoreManager` | 示範簡單檔案 I/O。 |
| 設定 | 常數分散在 class | 新增 `GameConfig` | 集中調整遊戲參數與路徑。 |
| 執行教學 | 只說明按 Run | 補充多 class 專案的編譯/執行方式 | 避免學生用 `java src\Main.java` 或 Code Runner 執行失敗。 |

## GameEngine 的角色演化

### Version 1

`GameEngine` 幾乎處理所有遊戲規則，包含移動、子彈、外星人、碰撞後加分、勝敗判斷。這對最小版本是合理的，因為規則少，學生容易追蹤。

### Version 2

規則增加後，`GameEngine` 改成流程協調者：

- 把分數交給 `ScoreManager`。
- 把關卡難度交給 `LevelManager`。
- 把碰撞細節交給 `CollisionManager`。
- 把畫面呈現交給 `GameRenderer`。

### Version 3

體驗層增加後，`GameEngine` 仍然不直接處理音效格式或檔案細節，而是協調：

- `SoundManager` 播放音效。
- `HighScoreManager` 讀寫最高分。
- `ExplosionEffect` 與 `Particle` 管理動畫資料。
- `GameRenderer` 負責把動畫與 overlay 畫出來。

## 執行方式的演化

Version 3 後，文件明確提醒這是多 class Java 專案。

錯誤方式：

```powershell
java src\Main.java
```

正確方式：

```powershell
javac -d out src\*.java
java -cp out Main
```

在 VS Code 中，請使用 Java extension 的 **Run**，不要使用 Code Runner 的 **Run Code**。
