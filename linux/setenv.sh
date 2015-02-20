#!/bin/bash

ALLJOYN_DIST=alljoyn-dist
export LD_LIBRARY_PATH=${ALLJOYN_DIST}/about/lib:${ALLJOYN_DIST}/cpp/lib:${ALLJOYN_DIST}/controlpanel/lib:${ALLJOYN_DIST}/services_common/lib:${ALLJOYN_DIST}/notification/lib:${ALLJOYN_LIB}/config/lib
