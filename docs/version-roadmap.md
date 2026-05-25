# 版本演進規劃

本專案至少規劃三個版本。每一版都保留可執行、可教學、可回顧的狀態。

## Version 1: 最小可玩版本

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

### 對應文件

- [Version 1 UML Class Model](uml-class-model-v1.md)
- [Version 1 教學筆記](teaching-notes-v1.md)

## Version 2: 完整遊戲規則

目前版本。

### 已新增功能

- 玩家生命數 lives。
- 敵人隨機射擊。
- 防護牆 shields。
- 玩家子彈與敵人子彈的類型區分。
- 關卡推進與難度提升。
- 不同敵人的分數差異。
- `START_SCREEN`、`PLAYING`、`PAUSED`、`LEVEL_CLEARED`、`GAME_OVER` 狀態。
- `ENTER` 重新開始與 `P` 暫停。

### 新增或調整的 class

- `BulletType`
- `Shield`
- `ScoreManager`
- `LevelManager`
- `GameState`
- `CollisionManager`
- `GameEngine`
- `GameRenderer`
- `InputHandler`

### 對應文件

- [Version 2 UML Class Model](uml-class-model-v2.md)
- [Version 2 完整規則與設計重點](version2-full-rules.md)
- [架構演化紀錄](architecture-evolution.md)

## Version 3: 視覺與關卡擴充

### 建議新增功能

- 使用圖片素材取代 Java2D 基本圖形。
- 加入音效。
- 多關卡設定。
- 開始畫面與結束畫面拆成更完整的畫面系統。
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

Version 1 先讓遊戲能玩，Version 2 再把完整規則補上。Version 3 如果出現更多畫面、素材與敵人種類，再考慮抽象 `GameObject`、`Entity` 或 Scene 系統。

建議原則：

- 先讓遊戲能玩。
- 再讓程式容易讀。
- 最後才加入抽象與擴充點。
