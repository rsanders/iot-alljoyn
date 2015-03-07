Build Alljoyn Thin Client library, then copy it to alljoyn-dist folder located under root of this project

Tree as following
hieult@hieult-VirtualBox:/data/workspace/iot/iot-alljoyn/bbb$ tree -L 2
.
|- alljoyn-dist
|-- inc
|-- lib
|- build
|-- alljoyn_app
|-- main.o
|- README
|- SConscript
|- SConstruct
|- src
|-- main.c
|-- SConscript

Set OPENSSL_PATH to directory of OpenSSL i.e. /opt/openssl

Set 
CC=arm-linux-gnueabihf-
PATH point to bin folder of cross toolchain

Build with 
scons CC=${CC}gcc