
Setup environment:

export ALLJOYN_ROOT="/media/sontung/SOURCE/IoT/alljoyn/"
export ALLJOYN_DIST="$ALLJOYN_ROOT/core/alljoyn/build/linux/x86/debug/dist"

export CXXFLAGS="-I$ALLJOYN_DIST/cpp/inc \
		-I$ALLJOYN_DIST/about/inc \
		-I$ALLJOYN_DIST/services_common/inc \
		-I$ALLJOYN_DIST/notification/inc \
		-I$ALLJOYN_DIST/controlpanel/inc \
		-I$ALLJOYN_ROOT/services/base/services_common/cpp/inc/ \
        -I$ALLJOYN_ROOT/services/base/controlpanel/cpp/inc/ \
		-I$ALLJOYN_ROOT/services/sample_apps/cpp/samples_common/"

export LDFLAGS="-L$ALLJOYN_DIST/cpp/lib \
		-L$ALLJOYN_DIST/about/lib \
		-L$ALLJOYN_DIST/services_common/lib \
		-L$ALLJOYN_DIST/notification/lib \
		-L$ALLJOYN_DIST/controlpanel/lib"

export LD_LIBRARY_PATH="$ALLJOYN_DIST/cpp/lib:$ALLJOYN_DIST/about/lib:$ALLJOYN_DIST/services_common/lib:$ALLJOYN_DIST/notification/lib:$ALLJOYN_DIST/controlpanel/lib"


Generate source code from XML:

python CPSAppGenerator/generateCPSApp.py src/RPI_ControlPanel.xml -p src/generated/

Build: (for Linux)

scons WS=off

Run:

./build/rpi-controlee

Tree layout:

├── README.md
├── SConscript
├── SConstruct
└── src
    ├── generated
    │   ├── ControlPanelGenerated.cc
    │   ├── ControlPanelGenerated.h
    │   ├── MyDeviceLightAction.cc
    │   ├── MyDeviceLightAction.h
    │   └── SConscript
    ├── provided
    │   ├── RPIProvider.cc
    │   ├── RPIProvider.h
    │   └── SConscript
    ├── RPIControllee.cc
    ├── RPI_ControlPanel.xml
    └── SConscript
