## How to build
* Install required library for Alljoyn development: scons,...
* Build the Alljoyn library with
  * Services: about, notification, controlpanel, config, onboarding
  * Binding: cpp
  * Built-in router: on
```
scons BINDINGS=cpp WS=off BR=on ICE=off SERVICES="about,notification,controlpanel,config,onboarding"
```

* The output will be in somewhere like $ALLJOYN_ROOT/build/linux/x86/debug/dist
* Set environment ALLJOYN_DIST to the above folder or copy content of above folder into alljoyn-dist folder inside root directory of this project

* The directory layout is something like this
```
linux
|-- alljoyn-dist
|   |-- about
|   |-- config
|   |-- controlpanel
|   |-- cpp
|   |-- notification
|   |-- onboarding
|   |-- README.txt
|   |-- ReleaseNotes.txt
|   \-- services_common
|-- build
|-- README
|-- SConscript
|-- SConstruct
\-- src
```
    
* Inside root of this project type scons to build


## How to run
* Start the alljoyn-daemon in the background (located under build/cpp/bin/)
* Setup LD_LIBRARY_PATH
* Run alljoyn_app