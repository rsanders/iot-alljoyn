# iot-alljoyn
IoT research on Alljoyn framework

# To build Alljoyn
* Checkout the source
```
mkdir WORKING_DIRECTORY
cd WORKING_DIRECTORY

git clone https://git.allseenalliance.org/gerrit/core/alljoyn.git core/alljoyn
git clone https://git.allseenalliance.org/gerrit/core/ajtcl.git core/ajtcl
git clone https://git.allseenalliance.org/gerrit/services/base.git services/base
git clone https://git.allseenalliance.org/gerrit/services/base_tcl.git services/base_tcl
git clone https://git.allseenalliance.org/gerrit/data/datadriven_api.git data/datadriven_api
git clone https://git.allseenalliance.org/gerrit/devtools/codegen.git devtools/codegen
```

* Then 
```
cd $AJ_ROOT/core/alljoyn
scons BINDINGS=cpp WS=off BR=off ICE=off SERVICES="about,notification,controlpanel,config,onboarding"
```
* Some scons options
```
    WS=off - This turns off the whitespace checker. The whitespace checker is for developers to ensure that the code they write conforms to our coding standard is not necessary for customers to use.
    BR=off - This disables the bundled AllJoynrouter functionality. If you intend to use a stand-alone AllJoynRouter, this should be set to off.Setting it to “on” will include the AllJoynRouter - note that the default is “on”.
    OS=linux - This is for building AllJoyn for desktop Linux (and is the default).
    CPU=x86_64 - This is for building 64-bit binaries. If you are running on a 32-bit Linux machine then set this to “x86”.
    BINDINGS=cpp - This controls what to build. By specifying just “cpp”, only the AllJoynRouter, C++ libraries, and C++ sample/test code is built. Other bindings that can be built are C, Java, and a browser plugin for a JavaScript binding.
    SERVICES=“notification,controlpanel,config,onboarding” - This specifies the Service Frameworks to build. “About” is always built by default and doesn’t need to be specified. The standard service framework options include “notification, controlpanel, config, onboarding”.
    VARIANT=release - This builds AllJoyn in release mode. If there is a need to get verbose debug messages and run AllJoyn code in a debugger, then this can be set to “debug”.
```
