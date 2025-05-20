## 📦 项目说明

`quicktvui-sdk`是[quicktvui](https://github.com/quicktvui/quicktvui)框架中的快应用`Runtime 运行环境`，它依赖`hippy-tv`， 添加了TV瀑布流式UI、
lottie动画、音频、视频、图片加载、下载器、应用等功能模块。支持运行由[quicktvui](https://github.com/quicktvui/quicktvui)框架开发的快应用程序。  

该项目支持五种启动方式:  
1.本地调试  
2.从本地资源加载  
3.从文件路径加载  
4.从网页加载  
5.从仓库加载  

```mermaid
graph LR
A[quicktvui] -- run on --> B[quicktvui-sdk]
B -- depends on --> C[hippy-tv]
```

> ℹ️ [quicktvui](https://github.com/quicktvui/quicktvui)由三部分组成：[quicktvui-sdk](https://github.com/quicktvui/quicktvui-sdk)（安卓RuntimeSDK）、[hippy-tv](https://github.com/quicktvui/hippy-tv)（基于hippy的tv扩展）、[quicktvui](https://github.com/quicktvui/quicktvui)（Vue 快应用框架）。 本仓库为运行环境仓库，若需查看完整安卓端运行效果和实际应用示例，请前往 [quicktvui-sdk](https://github.com/quicktvui/quicktvui-sdk) 项目。

> ⚠️ 目前仅支持Android >= 17 版本设备

## 🧱 项目结构

```text
quicktvui-sdk/
├── app/                   # 示例应用或集成测试模块
├── base/                  # 基础模块（如 UI 基础实现）
│   ├── sdk-base/
│   └── ui/                # UI 组件库
├── core/                  # 核心模块（如业务框架、数据结构等）
│   └── sdk-core/
├── gradle/                # 自定义 Gradle 脚本
├── plugin/                # 插件模块
│   ├── annotations/       # 注解定义
│   └── compiler/          # 注解处理器 (APT)
├── support/               # 支持模块（扩展功能库）
│   ├── audio-record/      # 音频录制相关功能
│   ├── baseui/            # 基础 UI 元件
│   ├── border-drawable/   # 边框绘制相关
│   ├── brightness/        # 亮度调节支持
│   ├── canvas/            # 自定义画布支持
│   ├── card/              # 卡片 UI 控件
│   ├── chart/             # 图表支持
│   ├── core/              # 支持核心模块（通用工具类等）
│   ├── data-group/        # 数据分组支持
│   ├── data-shared/       # 数据共享/缓存支持
│   ├── device-info/       # 设备信息获取
│   ├── download/          # 下载器模块
│   ├── gif-view/          # GIF 播放支持
│   ├── ijk-base/          # 播放器基础库
│   ├── image-loader/      # 图片加载支持
│   ├── install-manager/   # 安装器模块
│   ├── log/               # 基础日志模块
│   ├── long-image/        # 长图组件模块
│   ├── lottie-view/       # lottie动画组件模块
│   ├── network-speed/     # 网络测速模块
│   ├── player-async/      # 基于原生AsyncPlayer音频播放模块
│   ├── player-audio-android/   # 音频播放模块
│   ├── player-audio-ijk/   # 基于ijk的音频播放模块
│   ├── player-audio-soundpool/   # 系统音效播放模块
│   ├── player-ijk/        # ijk视频播放模块
│   ├── player-manager/    # 播放管理模块
│   ├── small-player/      # 小窗播放模块
│   ├── socket-io/         # 长链接模块
│   ├── subtitle-converter/  # 字幕解析模块
│   ├── rippleview/        # 水波纹效果1
│   ├── swiper-view/       # 水波纹效果2
│   └── ui/                # 瀑布流相关组件
│   └── upload/            # 上传模块
│   └── websocket/         # websocket模块
│   └── webview/           # webview组件
│   └── x5webview/         # 基于x5引擎的网页组件
│   └── template/          # demo示例结构
│   └── ...                # 其他支持模块可自行拓展
```

### 🧩 模块说明  
|  模块 | 描述 |
| ------------- | --------------------------------------- |
|base/sdk-base|提供 SDK 最基础的功能接口  
|base/ui|UI 控件基础封装  
|core/sdk-core|核心功能，如路由、生命周期、资源管理等  
|plugin/annotations|自定义注解  
|plugin/compiler|对应注解处理器（APT）  
|support|主要的扩展模块，按功能划分，例如图表、下载、GIF、亮度调节等  

## 📘 文档  

[SDK集成](docs/publish_sdk.md)  
[初始化](docs/initialize.md)   
[自定义扩展](docs/cus_module_component.md)  

## 📄 版本说明

本项目采用**三段式版本命名规范**（即 `主版本号.次版本号.修订号`），遵循[语义化版本](https://semver.org/)原则：

- **`主版本号 (MAJOR)`**：重大变更或不兼容的API修改
- **`次版本号 (MINOR)`**：新增功能且向下兼容
- **`修订号 (PATCH)`**：问题修复或微小改进，完全兼容

### 版本兼容性指南
| 版本范围        | 兼容性说明                     |
|-----------------|------------------------------|
| `1.x.x`         | 同一主版本内保证API兼容性      |
| `x.2.x` → `x.3.x` | 可安全升级，包含新功能        |
| `x.x.1` → `x.x.2` | 强烈建议升级，仅含错误修复    |


## 🤝 贡献

> ℹ️ 本项目已成功应用于多个实际 TV 应用中，但整体仍处于快速演进阶段，使用时请注意版本变化。

> ⚠️ 欢迎你 Fork 本项目，自由修改并根据自身需求进行定制开发。

如你有问题或建议，欢迎通过 Issue 与我们沟通反馈。

---

## ⚖ License

该项目使用[Apache-2.0协议](https://www.apache.org/licenses/LICENSE-2.0.txt)

## 🔗 Links

[官网首页](https://quicktvui.com/zh-CN/)

[QuickTVUI核心框架](https://github.com/quicktvui/quicktvui)

[hippy-tv基于Hippy架构的TV端项目](https://github.com/quicktvui/hippy-tv)

[基于sdk支持模块的api演示项目](https://github.com/quicktvui/quicktvui-api-demo-vue3)