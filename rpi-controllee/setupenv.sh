export ALLJOYN_ROOT="/media/sontung/SOURCE/IoT/RPI/rpi-alljoyn"
export ALLJOYN_DIST="$ALLJOYN_ROOT/core/alljoyn/build/linux/arm/debug/dist"
export ALLJOYN_CTPN_DIST="$ALLJOYN_ROOT/services/base/controlpanel/build/linux/arm/debug/dist"

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
		-L$ALLJOYN_CTPN_DIST/services_common/lib \
		-L$ALLJOYN_CTPN_DIST/notification/lib \
		-L$ALLJOYN_CTPN_DIST/controlpanel/lib"

export LD_LIBRARY_PATH="$ALLJOYN_DIST/cpp/lib:$ALLJOYN_DIST/about/lib:$ALLJOYN_CTPN_DIST/services_common/lib:$ALLJOYN_CTPN_DIST/notification/lib:$ALLJOYN_CTPN_DIST/controlpanel/lib"

