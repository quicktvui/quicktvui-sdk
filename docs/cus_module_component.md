自定义Moduel和Component和官方文档一致，但是注册方法发生了改变，不在需要调用`EsManager.get().registerModule`方法，而是引入了@ESKitAutoRegister注解器的方式。新的方式可以避免漏注册、多渠道不好管理等问题。  
``` java
// 加入自动注册注解
@ESKitAutoRegister
public class TestModuel implements IEsModule {
}
// 加入自动注册注解
@ESKitAutoRegister
public class TestComponent implements IEsComponent<TestView> {
}
```

module引入自动注册
``` groovy
compileOnly 'com.quicktvui:plugin-annotations:0.2.0'
annotationProcessor 'com.quicktvui:plugin-compiler:0.2.0'
```

参考官网[自定义Moduel和Component](https://quicktvui.com/zh-CN/sdk/cus_module.html)
