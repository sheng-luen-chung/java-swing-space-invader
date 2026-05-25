# 版本演進規劃

本專案至少規劃三個版本。每一版都保留可執行、可教學、可回顧的狀態。

## Version 1: 最小可玩版本

目前版本。

### 目標

- 建立 Java Swing 遊戲基本架構。
- 使用 `Swing Timer` 作為 game loop。
- 使用 Key Bindings 處理鍵盤。
- 用 Java2D 畫出簡單圖形。
- 完成最基本勝敗條件。

### 已有功能

- 玩家左右移動。
- 玩家發射子彈。
- 多排外星人左右移動並下降。
- 子彈擊中外星人後消滅外星人。
- 分數顯示。
- `YOU WIN` 與 `GAME OVER`。
- 按 `R` 重新開始。

## Version 2: 遊戲性強化

### 建議新增功能

- 外星人可以發射子彈。
- 玩家有生命值。
- 玩家被擊中後扣血或短暫無敵。
- 分數、生命值、關卡資訊放入更完整的 HUD。
- 外星人速度隨時間或剩餘數量提高。
- 加入暫停功能。

### 可能新增或調整的 class

- `EnemyBullet`
- `HealthSystem` 或在 `GameState` 增加 lives 欄位
- `LevelConfig`
- `GameConfig`

## Version 3: 視覺與關卡擴充

### 建議新增功能

- 使用圖片素材取代 Java2D 基本圖形。
- 加入音效。
- 多關卡設定。
- 開始畫面與結束畫面。
- 儲存最高分。
- 加入不同種類外星人。

### 可能新增或調整的 class

- `SpriteLoader`
- `SoundManager`
- `Scene` 或 `Screen`
- `MenuScreen`
- `GameScreen`
- `HighScoreManager`
- `AlienType`

## 架構演進提醒

Version 1 先不急著建立大型抽象，例如 `GameObject` 或 `Entity`。等到 Version 2 或 Version 3 出現更多可移動物件時，再抽象共用欄位與方法會更自然。

建議原則：

- 先讓遊戲能玩。
- 再讓程式容易讀。
- 最後才加入抽象與擴充點。
