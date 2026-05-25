# Version 1 教學筆記

Note: this project is kept compatible with Java 8 so students can run it in older VS Code Java setups.

## JDK 需求

- Minimum: JDK 8
- Recommended: JDK 17 or newer
- VS Code extension: Extension Pack for Java

上課前可以請學生先執行：

```powershell
java -version
javac -version
```

如果 `java` 與 `javac` 版本差異很大，或 VS Code Run 的結果和終端機不同，請先調整 VS Code 的 Java runtime 設定。

這份筆記給教學訓練使用，目標是讓初學者理解一個簡單 Swing 遊戲如何被拆成可維護的 class。

雖然目前程式已進入 Version 3，但這份文件保留 Version 1 的教學視角，方便比較版本演化。

## 學習目標

完成 Version 1 後，學生應該能理解：

- Swing 程式要從 `SwingUtilities.invokeLater` 啟動 UI。
- `JPanel` 可以作為遊戲畫布。
- `Swing Timer` 可以作為簡單 game loop。
- Key Bindings 比 `KeyListener` 更適合 Swing 元件的按鍵控制。
- Java2D 可以用來畫簡單的遊戲圖形。
- 遊戲可以拆成輸入、更新、碰撞、渲染、狀態管理等責任。

## 多 Class 專案的執行方式

這個專案不是單一 Java 檔案，而是由 `Main`、`GameWindow`、`GamePanel`、`GameEngine` 等多個 class 組成。

不要用：

```powershell
java src\Main.java
```

這種方式只會嘗試編譯單一檔案，容易出現：

```text
cannot find symbol: GameWindow
```

請用：

```powershell
javac -d out src\*.java
java -cp out Main
```

或在 VS Code 中使用 Java extension 提供的 **Run**，不要使用 Code Runner 的 **Run Code**。

## 建議教學順序

1. 先用正確方式執行 `Main.java`，讓學生玩一次遊戲。
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

## 和後續版本的銜接

學完 Version 1 後，可以接著看：

- [Version 2 UML Class Model](uml-class-model-v2.md)
- [Version 3 UML Class Model](uml-class-model-v3.md)
- [Version 2 完整規則與設計重點](version2-full-rules.md)
- [Version 3 遊戲體驗設計](version3-game-experience.md)
- [架構演化紀錄](architecture-evolution.md)

重點觀察：

- `GameState` 為什麼不再保存 score。
- `ScoreManager` 與 `LevelManager` 為什麼被拆出來。
- `CollisionManager` 如何從單一碰撞擴充成多種碰撞。
- `SoundManager` 與 `HighScoreManager` 為什麼不應該塞進 `GamePanel`。
