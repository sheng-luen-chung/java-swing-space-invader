# 版本演進規劃

JDK baseline: this project keeps source compatibility with JDK 8. JDK 17 or newer is recommended for development, but classroom machines using JDK 8 should still be able to compile and run the project.

本專案至少規劃三個版本。每一版都保留可執行、可教學、可回顧的狀態。

## Version 1: 最小可玩版本

### 目標

- 建立 Java Swing 遊戲基本架構。
- 使用 `Swing Timer` 作為 game loop。
- 使用 Key Bindings 處理鍵盤。
- 用 Java2D 畫出簡單圖形。
- 完成最基本勝敗條件。

### 對應文件

- [Version 1 UML Class Model](uml-class-model-v1.md)
- [Version 1 教學筆記](teaching-notes-v1.md)

## Version 2: 完整遊戲規則

### 已新增功能

- 玩家生命數 lives。
- 敵人隨機射擊。
- 防護牆 shields。
- 玩家子彈與敵人子彈的類型區分。
- 關卡推進與難度提升。
- 不同敵人的分數差異。
- `START_SCREEN`、`PLAYING`、`PAUSED`、`LEVEL_CLEARED`、`GAME_OVER` 狀態。

### 對應文件

- [Version 2 UML Class Model](uml-class-model-v2.md)
- [Version 2 完整規則與設計重點](version2-full-rules.md)
- [架構演化紀錄](architecture-evolution.md)

## Version 3: 遊戲體驗展示版

目前版本。

### 已新增功能

- 更完整的開始畫面。
- Game Over 顯示 final score 與 high score。
- 過關後顯示 `LEVEL CLEARED`，並等待 `ENTER` 進下一關。
- 使用 `javax.sound.sampled` 播放 wav 音效。
- 外星人被擊中時顯示粒子爆炸。
- 玩家被擊中時短暫閃爍。
- 最高分保存到 `data/highscore.txt`。
- 補上多 class 專案的正確執行方式教學。

### 新增或調整的 class

- `GameConfig`
- `SoundManager`
- `HighScoreManager`
- `ExplosionEffect`
- `Particle`
- `GameEngine`
- `GameRenderer`
- `InputHandler`

### 對應文件

- [Version 3 UML Class Model](uml-class-model-v3.md)
- [Version 3 遊戲體驗設計](version3-game-experience.md)

## Version 4 可能方向

- 使用圖片素材取代 Java2D 基本圖形。
- 加入音效開關與設定畫面。
- 高分榜改成前 5 名。
- 開始畫面加入難度選擇。
- 將畫面拆成 `Screen` 或 `Scene` 架構。

## 架構演進提醒

Version 1 先讓遊戲能玩，Version 2 補上完整規則，Version 3 補上遊戲體驗。後續如果出現更多畫面、素材與敵人種類，再考慮抽象 `GameObject`、`Entity` 或 Scene 系統。
