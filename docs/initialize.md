参考[SampleApplication](app/src/main/java/com/quicktvui/sample/SampleApplication.java)代码

``` java
EsManager.get().init(this, InitConfig.getDefault()
                .setChannel("debug")  // 应用渠道，自定义
                .setDebug(BuildConfig.DEBUG)  // true会打印更多log
);
```