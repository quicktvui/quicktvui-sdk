# 版本说明

## ChangeLog

### 2023-07-13 eskit.sdk.support:player-manager:3.1.3-SNAPSHOT

* 修改设置0.8倍速不起作用的问题，去掉固定倍速限制。
* `IPlayer`中`void setPlayRate(PlayRate rate)`
  修改为 `void setPlayRate(float rate)`