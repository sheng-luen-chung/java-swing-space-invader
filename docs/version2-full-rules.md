# Version 2 完整規則與設計重點

Version 2 的目標是把 Version 1 的最小可玩遊戲，擴充成更接近完整 Space Invader-style 規則的版本，同時維持教學用的 class 分工。

## 新增功能

### 1. 玩家生命數

- 玩家預設有 3 條生命。
- 敵人子彈擊中玩家時，生命數減 1。
- 生命數歸零後，遊戲狀態變成 `GAME_OVER`。
- HUD 會顯示 `Lives`。

相關 class：

- `GameState`
- `GameEngine`
- `CollisionManager`

### 2. 敵人射擊

- 外星人會隨機向下發射子彈。
- 射擊者會從目前還活著、且位於前排的外星人中挑選。
- 關卡越高，敵人射擊機率越高。

相關 class：

- `AlienFleet`
- `Bullet`
- `BulletType`
- `LevelManager`
- `GameEngine`

### 3. 防護牆 Shields

- 玩家與外星人之間有 4 個防護牆。
- 玩家子彈與敵人子彈都會破壞防護牆。
- 每個防護牆有 health，受到多次攻擊後會消失。
- 畫面會根據剩餘 health 改變防護牆高度。

相關 class：

- `Shield`
- `CollisionManager`
- `GameRenderer`

### 4. 關卡系統

- 消滅所有外星人後，狀態變成 `LEVEL_CLEARED`。
- 短暫顯示關卡完成訊息後，進入下一關。
- 下一關會重建外星人與防護牆。
- 關卡越高，外星人速度越快、射擊頻率越高。

相關 class：

- `LevelManager`
- `GameEngine`
- `AlienFleet`

### 5. 分數系統

- 不同列的外星人有不同分數。
- 越上排的外星人分數越高。
- HUD 會顯示 `Score`、`Level`、`Lives`。

相關 class：

- `Alien`
- `ScoreManager`
- `GameRenderer`

### 6. 暫停與重新開始

- 按 `P` 可以在 `PLAYING` 與 `PAUSED` 間切換。
- 開始畫面按 `ENTER` 開始遊戲。
- Game Over 後按 `ENTER` 重新開始。
- 按 `R` 可以立即重開一局。

相關 class：

- `InputHandler`
- `GameEngine`
- `GameState`

## GameState 狀態

Version 2 的 `GameState.Status` 包含：

| 狀態 | 說明 |
| --- | --- |
| `START_SCREEN` | 初始畫面，等待玩家按 Enter。 |
| `PLAYING` | 遊戲進行中。 |
| `PAUSED` | 暫停中，Timer 仍存在，但不更新遊戲規則。 |
| `LEVEL_CLEARED` | 本關敵人全滅，短暫顯示提示後進入下一關。 |
| `GAME_OVER` | 玩家生命歸零，或外星人下降到危險線。 |

## CollisionManager 的責任

Version 2 將碰撞集中在 `CollisionManager`：

- 玩家子彈 vs 外星人
- 玩家子彈 vs 防護牆
- 敵人子彈 vs 玩家
- 敵人子彈 vs 防護牆

`CollisionManager` 回傳 `CollisionResult`，再由 `GameEngine` 根據結果更新分數、生命與遊戲狀態。

這樣可以避免碰撞規則散落在 `GameEngine`、`Bullet`、`Alien` 等多個 class 中。

## 為什麼新增 ScoreManager 與 LevelManager

Version 2 的規則比 Version 1 多很多。如果全部放進 `GameEngine`，它會很快變成難以閱讀的大型 class。

因此本版拆出：

- `ScoreManager`: 管理分數計算與查詢。
- `LevelManager`: 管理目前關卡、外星人速度、敵人射擊機率。

`GameEngine` 仍然是流程協調者，但不再負責所有細節。

## 和 V1 的模型差異

更完整的 class 對照請看 [架構演化紀錄](architecture-evolution.md)。

| 項目 | V1 | V2 |
| --- | --- | --- |
| 狀態 | `PLAYING`、`YOU_WIN`、`GAME_OVER` | 新增開始、暫停、過關狀態。 |
| 子彈 | 只有玩家子彈 | 使用 `BulletType` 區分玩家與敵人。 |
| 分數 | 放在 `GameState` | 移到 `ScoreManager`。 |
| 關卡 | 無 | 新增 `LevelManager`。 |
| 防護牆 | 無 | 新增 `Shield`。 |
| 碰撞 | 單一碰撞 | 集中處理四種碰撞。 |

## Version 2 的設計取捨

- 防護牆使用單一 health，而不是細分成像素或小方塊，讓教學模型保持簡單。
- 敵人射擊使用隨機機率，不建立複雜 AI。
- 子彈使用 `BulletType` 區分玩家與敵人，不另外建立 `PlayerBullet` 與 `EnemyBullet`。
- 關卡切換使用短暫倒數，不新增 Scene 系統。

這些取捨讓 Version 2 仍然適合初學者閱讀，也保留 Version 3 加入圖片、音效與更多畫面的空間。
