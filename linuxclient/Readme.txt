Setup environment:
=======================================
export AJ_ROOT=<path>/source

export TARGET_CPU=x86_64 (or x86)

export ALLJOYN_DIST=$AJ_ROOT/core/alljoyn/build/linux/$TARGET_CPU/debug/dist/

export LD_LIBRARY_PATH=$AJ_ROOT/core/alljoyn/build/linux/$TARGET_CPU/debug/dist/cpp/lib:$AJ_ROOT/core/alljoyn/build/linux/x86_64/debug/dist/controlpanel/lib/:$AJ_ROOT/core/alljoyn/build/linux/x86_64/debug/dist/notification/lib:$AJ_ROOT/core/alljoyn/build/linux/$TARGET_CPU/debug/dist/services_common/lib:$LD_LIBRARY_PATH

