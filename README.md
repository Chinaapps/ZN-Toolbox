# ℤ𝕟𝕏工具

免 Root 的 Android 容器虚拟机，采用液态玻璃美学设计，容器内自带 Root 权限与 LSPosed (Xposed) 框架。

## 特性

- 🚫 **宿主免 Root**：以容器方式运行，宿主设备无需获取 Root
- 🔓 **容器内自带 Root**：虚拟 Android 系统内默认拥有完整 Root 权限
- 🧩 **内置 LSPosed**：容器内预置 Xposed 框架，模块开箱即用
- 💎 **液态玻璃 UI**：基于 AndroidLiquidGlass 的液态玻璃美学界面
- 📱 **全中文界面**：所有用户可见文字均已汉化
- 🔘 **液态玻璃底部栏**：底部固定玻璃栏 + 醒目的启动按键
- 📱 **双快手链接**：内置快手主页快捷入口
- 🔧 **Profile 管理**：支持多容器 Profile，多系统共存

## 技术栈

- **容器引擎**：Twoyi (MPL-2.0) 及其活跃 fork
- **UI 框架**：Jetpack Compose + Material 3
- **液态玻璃**：Kyant Backdrop (com.kyant.backdrop)
- **构建**：Gradle 9.6 + AGP 9.2.1 + Kotlin 2.x
- **最低 SDK**：Android 8.0 (API 27)
- **架构**：arm64-v8a

## 构建

```bash
# 1. 构建 Rust 引擎 (libtwoyi.so)
cd app/rs
sh build_rs.sh --release

# 2. 构建 APK
cd ../..
./gradlew :app:assembleRelease
```

## 开源声明

本项目基于以下开源项目二次开发：
- [Twoyi](https://github.com/twoyi/twoyi) - MPL-2.0
- [AndroidLiquidGlass](https://github.com/AndroidDeveloper-0/AndroidLiquidGlass) - Apache-2.0

遵循 MPL-2.0 协议开源。

## 版本

v1.0.0-Beta
