#! /bin/bash
set -e

export RUSTFLAGS="-C link-args=-rdynamic -C link-args=-Wl,--export-dynamic"

cargo xdk -t arm64-v8a build $1

BUILD_DIR="debug"
case "$*" in
    *--release*) BUILD_DIR="release" ;;
esac

mkdir -p ../src/main/jniLibs/arm64-v8a
cp target/aarch64-linux-android/${BUILD_DIR}/twoyi ../src/main/jniLibs/arm64-v8a/libtwoyi.so

echo "Built libtwoyi.so in ../src/main/jniLibs/arm64-v8a/"
ls -la ../src/main/jniLibs/arm64-v8a/libtwoyi.so
