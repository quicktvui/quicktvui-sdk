## 1. 使用我们已经发布的版本
``` groovy
// 配置maven仓库
repositories {
    maven { url 'https://nexus.extscreen.com/repository/maven-public/' }
}

// 引入SDK
dependencies {
    implementation 'com.quicktvui:runtime:0.2.0'

    // 自动注册
    compileOnly 'com.quicktvui:plugin-annotations:0.2.0'
    annotationProcessor 'com.quicktvui:plugin-compiler:0.2.0'
}
```

## 2. 发布到mavenLocal
注意core/sdk-core/maven.txt的配置信息：
``` text
GROUP_ID=com.quicktvui
ARTIFACT_ID=runtime
VERSION=0.2.0
```
在项目根目录执行发布命令
``` bash
./gradlew publishToMavenLocal
```

其它项目依赖
``` groovy
implementation 'com.quicktvui:runtime:0.2.0'
```

## 3. 发布到自定义仓库
需要在local.properties中配置nexus信息:
``` text
nexus.url.release=release仓库地址
nexus.url.snapshots=snapshots仓库地址
nexus.username=用户名
nexus.password=密码
```
在项目根目录执行发布命令
``` bash
./gradlew publishMavenPublicationToNexusRepository
```