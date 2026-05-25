# Version 1 教學筆記

這份筆記給教學訓練使用，目標是讓初學者理解一個簡單 Swing 遊戲如何被拆成可維護的 class。

雖然目前程式已進入 Version 2，但這份文件刻意保留 Version 1 的教學視角，方便比較版本演化。

## 學習目標

完成 Version 1 後，學生應該能理解：

- Swing 程式要從 `SwingUtilities.invokeLater` 啟動 UI。
- `JPanel` 可以作為遊戲畫布。
- `Swing Timer` 可以作為簡單 game loop。
- Key Bindings 比 `KeyListener` 更適合 Swing 元件的按鍵控制。
- Java2D 可以用來畫簡單的遊戲圖形。
- 遊戲可以拆成輸入、更新、碰撞、渲染、狀態管理等責任。

## 建議教學順序

1. 先執行 `Main.java`，讓學生玩一次遊戲。
2. 看 `GamePanel`，理解 Timer 每 16ms 執行一次。
3. 看 `InputHandler`，理解按鍵如何轉成 `GameEngine` 的指令。
4. 看 `GameEngine.update()`，理解每一幀的遊戲更新順序。
5. 看 `AlienFleet.update()`，理解群體碰邊下降的規則。
6. 看 `CollisionManager`，理解 `Rectangle.intersects` 的碰撞判斷。
7. 看 `GameRenderer`，理解畫面只是目前狀態的呈現。

## Game Loop 觀念

Version 1 的 game loop 在 `GamePanel` 裡：

```java
Timer timer = new Timer(FRAME_DELAY_MS, event -> {
    engine.update();
    repaint();
});
timer.start();
```

這段程式代表：

- `engine.update()` 更新遊戲資料。
- `repaint()` 要求 Swing 重新繪製畫面。
- 真正的繪圖會發生在 `paintComponent`。

## 為什麼不用 KeyListener

Swing 的 `KeyListener` 常常會遇到焦點問題。Key Bindings 可以用：

```java
JComponent.WHEN_IN_FOCUSED_WINDOW
```

讓按鍵在視窗取得焦點時就能運作，比較適合 Swing 遊戲或工具程式。

## 為什麼分成 GameEngine 與 GameRenderer

`GameEngine` 負責規則，例如：

- 玩家怎麼移動。
- 子彈怎麼飛。
- 外星人何時下降。
- 何時加分。
- 何時勝利或失敗。

`GameRenderer` 負責畫面，例如：

- 玩家飛船畫成三角形。
- 外星人畫成圓角矩形。
- 子彈畫成小長條。
- 顯示分數與結束訊息。

這樣做的好處是：之後改美術，不容易動到規則；之後改規則，也不容易弄壞繪圖。

## 和 Version 2 的銜接

學完 Version 1 後，可以接著看：

- [Version 2 UML Class Model](uml-class-model-v2.md)
- [Version 2 完整規則與設計重點](version2-full-rules.md)
- [架構演化紀錄](architecture-evolution.md)

重點觀察：

- `GameState` 為什麼不再保存 score。
- `ScoreManager` 與 `LevelManager` 為什麼被拆出來。
- `CollisionManager` 如何從單一碰撞擴充成多種碰撞。
- `BulletType` 如何讓同一個 `Bullet` 支援玩家與敵人。

## 可安排的練習題

- 修改玩家移動速度。
- 修改外星人排數與欄數。
- 修改每個外星人的分數。
- 允許畫面上同時存在更多玩家子彈。
- 讓外星人越來越快。
- 在 `GAME_OVER` 時顯示最後分數。
- 把玩家飛船改成不同 Java2D 圖形。
