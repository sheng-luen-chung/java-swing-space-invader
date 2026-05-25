# Version 2 完整規則與設計重點

本文件保留 Version 2 的設計紀錄。Version 2 的目標是把 Version 1 的最小可玩版本，擴充成具有完整遊戲規則的 Space Invader-style 小遊戲。

目前專案已進入 Version 3，因此本文件同時標註 V2 與 V3 的差異，方便教學時觀察演化。

## Version 2 新增功能

### 1. 玩家生命數

- 玩家預設有 3 條生命。
- 敵人子彈擊中玩家時，生命數減 1。
- 生命數歸零後，遊戲狀態變成 `GAME_OVER`。
- HUD 顯示 `Lives`。

### 2. 敵人射擊

- 外星人會隨機向下發射子彈。
- 射擊者從目前還活著、且位於前排的外星人中挑選。
- 關卡越高，敵人射擊機率越高。

### 3. 防護牆

- 玩家與外星人之間有 4 個防護牆。
- 玩家子彈與敵人子彈都會破壞防護牆。
- 防護牆有 health，受到多次攻擊後會消失。

### 4. 關卡系統

- 消滅所有外星人後，狀態變成 `LEVEL_CLEARED`。
- Version 2 中，短暫顯示提示後自動進入下一關。
- 下一關會重建外星人與防護牆。
- 關卡越高，外星人速度越快、射擊頻率越高。

### 5. 分數系統

- 不同列的外星人有不同分數。
- 越上排的外星人分數越高。
- HUD 顯示 `Score`、`Level`、`Lives`。

### 6. 暫停與重新開始

- 按 `P` 可以在 `PLAYING` 與 `PAUSED` 間切換。
- 開始畫面按 `ENTER` 開始遊戲。
- Game Over 後按 `ENTER` 重新開始。
- 按 `R` 可以立即重開一局。

## Version 2 狀態機

| 狀態 | 說明 |
| --- | --- |
| `START_SCREEN` | 初始畫面，等待玩家按 Enter。 |
| `PLAYING` | 遊戲進行中。 |
| `PAUSED` | 暫停中，Timer 仍存在，但不更新遊戲規則。 |
| `LEVEL_CLEARED` | 本關敵人全滅，短暫顯示提示後進入下一關。 |
| `GAME_OVER` | 玩家生命歸零，或外星人下降到危險線。 |

## Version 2 Class 分工

| Class | 責任 |
| --- | --- |
| `GameState` | 保存目前狀態與玩家生命數。 |
| `ScoreManager` | 管理分數。 |
| `LevelManager` | 管理關卡、外星人速度、敵人射擊頻率。 |
| `BulletType` | 區分玩家子彈與敵人子彈。 |
| `Shield` | 表示防護牆與耐久度。 |
| `CollisionManager` | 集中處理玩家子彈、敵人子彈、外星人、防護牆、玩家之間的碰撞。 |

## V2 到 V3 的差異

| 主題 | Version 2 | Version 3 |
| --- | --- | --- |
| 開始畫面 | 基本 overlay | 顯示標題、操作方式、high score |
| 過關流程 | 倒數後自動下一關 | 倒數後等待 `ENTER` 進下一關 |
| Game Over | 顯示重新開始提示 | 顯示 final score 與 high score |
| 音效 | 無 | 新增 `SoundManager` 與 wav 音效 |
| 動畫 | 無 | 新增 `ExplosionEffect` 與 `Particle` |
| 玩家受擊 | 只扣生命 | 扣生命並短暫閃爍 |
| 高分 | 無 | 新增 `HighScoreManager` 與 `data/highscore.txt` |
| 設定 | 常數分散在 class | 新增 `GameConfig` |

## Version 2 的設計取捨

- 防護牆使用單一 health，而不是細分成像素或小方塊，讓教學模型保持簡單。
- 敵人射擊使用隨機機率，不建立複雜 AI。
- 子彈使用 `BulletType` 區分玩家與敵人，不另外建立 `PlayerBullet` 與 `EnemyBullet`。
- 關卡切換使用短暫倒數，Version 2 尚未讓玩家按 Enter 控制下一關節奏。

Version 3 的重點不是推翻 Version 2，而是在 Version 2 的規則基礎上補齊展示體驗。
