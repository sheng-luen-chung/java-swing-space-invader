# Version 3 遊戲體驗設計

Version 3 的目標是把 Version 2 的完整規則版，提升成可以展示給學生看的完整小型 Java Swing 遊戲。這一版不使用外部遊戲引擎，也不加入第三方 library，而是在既有 Swing Timer、Key Bindings、Java2D 架構上加入體驗層。

## JDK 需求

- Minimum: JDK 8
- Recommended: JDK 17 or newer
- VS Code extension: Extension Pack for Java

環境檢查：

```powershell
java -version
javac -version
```

Version 3 維持 Java 8 相容，避免使用 `Files.readString`、`Files.writeString` 這類 Java 11-only API。

## 執行方式教學

這個專案是多 class Java 專案，不是單檔程式。

錯誤示範：

```powershell
java src\Main.java
```

正確示範：

```powershell
javac -d out src\*.java
java -cp out Main
```

在 VS Code 中，請使用 Java extension 在 `main` 方法上方提供的 **Run**，不要使用 Code Runner 的 **Run Code**。

## Version 3 新增功能

- 更完整的開始畫面。
- Game Over 顯示 final score 與 high score。
- 過關畫面顯示 `LEVEL CLEARED`，並等待玩家按 `ENTER` 進下一關。
- 使用 `javax.sound.sampled` 播放 wav 音效。
- 外星人被擊中時出現 Java2D 粒子爆炸。
- 玩家被擊中時短暫閃爍。
- 最高分保存到 `data/highscore.txt`。

## V2 到 V3 的差異

| 主題 | Version 2 | Version 3 |
| --- | --- | --- |
| 開始畫面 | 基本 overlay | 標題、操作說明、high score |
| 過關流程 | 倒數後自動下一關 | 倒數後等待 `ENTER` |
| Game Over | 重新開始提示 | final score 與 high score |
| 音效 | 無 | `SoundManager` 播放 wav |
| 動畫 | 無 | `ExplosionEffect` 與 `Particle` |
| 高分紀錄 | 無 | `HighScoreManager` 寫入 `data/highscore.txt` |
| 設定 | 常數分散 | `GameConfig` 集中管理 |

## 音效設計

`SoundManager` 負責播放音效：

```java
soundManager.play(GameConfig.SOUND_SHOOT);
```

音效檔放在：

```text
assets/sounds/
  shoot.wav
  explosion.wav
  player_hit.wav
  level_clear.wav
```

如果音效檔不存在，`SoundManager` 只會印出 warning，不會中止遊戲。

## 動畫設計

外星人被玩家子彈擊中時：

```text
Alien destroyed
  -> ScoreManager add score
  -> create ExplosionEffect
  -> SoundManager play explosion.wav
```

`ExplosionEffect` 由多個 `Particle` 組成。每個 `Particle` 有位置、速度、生命週期與顏色。每一幀由 `GameEngine.updateEffects()` 更新，再由 `GameRenderer` 繪製。

玩家被敵人子彈擊中時，`GameEngine` 會設定 `playerHitFlashTicks`。`GameRenderer` 透過 `engine.isPlayerVisible()` 決定該幀是否畫出玩家。

## 狀態機調整

Version 3 沿用 Version 2 的狀態：

```text
START_SCREEN
PLAYING
PAUSED
LEVEL_CLEARED
GAME_OVER
```

主要差異是 `LEVEL_CLEARED`：

- Version 2：時間到自動進入下一關。
- Version 3：時間到後等待玩家按 `ENTER` 進入下一關。

## 高分紀錄

`HighScoreManager` 負責讀寫：

```text
data/highscore.txt
```

設計行為：

- 程式啟動時讀取 high score。
- 檔案不存在時建立資料夾與檔案，預設分數為 0。
- Game Over 時，如果目前分數高於 high score，就寫回檔案。
- 開始畫面與 Game Over 畫面會顯示 high score。
