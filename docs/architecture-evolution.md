# 架構演化紀錄

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
| 外星人 | 同一分數、只移動 | 不同分數、可挑選射擊者 | 增加遊戲規則與教學上的策略性。 |
| 輸入 | 移動、射擊、R 重開 | 加入 Enter 與 P | 支援開始、重開與暫停。 |
| 繪圖 | 玩家、外星人、玩家子彈、分數 | 加入 lives、level、敵人子彈、防護牆、狀態 overlay | HUD 與狀態提示更完整。 |

## GameEngine 的角色演化

### Version 1

`GameEngine` 幾乎處理所有遊戲規則：

- 玩家移動
- 子彈更新
- 外星人移動
- 碰撞後加分
- 勝利與失敗判斷

這對最小版本是合理的，因為規則少，學生容易追蹤。

### Version 2

規則增加後，`GameEngine` 改成流程協調者：

- 仍負責每一幀的 update 順序。
- 把分數交給 `ScoreManager`。
- 把關卡難度交給 `LevelManager`。
- 把碰撞細節交給 `CollisionManager`。
- 把畫面呈現交給 `GameRenderer`。

這樣 `GameEngine` 還是遊戲核心，但不需要知道所有細節。

## GameState 的演化

Version 1 的 `GameState` 同時放狀態與分數。Version 2 後，分數移出，只留下「遊戲狀態」與「玩家生命」。

這個改動讓 `GameState` 更像真正的狀態模型：

```text
START_SCREEN -> PLAYING -> LEVEL_CLEARED -> PLAYING
                         -> PAUSED -> PLAYING
                         -> GAME_OVER -> PLAYING
```

## CollisionManager 的演化

Version 1：

```text
player bullet vs alien
```

Version 2：

```text
player bullet vs alien
player bullet vs shield
enemy bullet vs player
enemy bullet vs shield
```

V2 的 `CollisionManager` 不直接加分或扣命，而是回傳 `CollisionResult`。這是刻意的分工：

- `CollisionManager` 判斷「撞到了什麼」。
- `GameEngine` 決定「撞到後遊戲規則如何改變」。
- `ScoreManager` 負責「分數怎麼算」。
- `GameState` 負責「生命與狀態怎麼保存」。

## 為什麼 V2 還不建立 GameObject

目前雖然有 `Player`、`Alien`、`Bullet`、`Shield`，但它們的行為差異仍然很大。

現在就抽 `GameObject` 可能會讓初學者先看到抽象，而不是先理解遊戲規則。因此 V2 先保留明確 class。等 V3 若加入更多敵人種類、道具、爆炸效果或場景系統，再考慮抽象共用父類別。

## V2 到 V3 的可能方向

- `Shield` 從單一 health 改成多格破壞模型。
- `Alien` 加入不同類型，可用 `AlienType` 表示。
- `GameRenderer` 開始支援圖片素材，可加入 `SpriteLoader`。
- 加入音效後，可新增 `SoundManager`。
- 開始畫面、遊戲畫面、結束畫面變複雜時，可新增 `Screen` 或 `Scene` 架構。
