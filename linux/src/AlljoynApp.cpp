#include "common.h"
#include "AlljoynApp.h"

using namespace ajn;
using namespace services;

#define SERVICE_PORT 8888

AlljoynApp::AlljoynApp (const char *appName) : busAttachment(NULL),
                                               busListener(NULL),
                                               aboutPropertyStore(NULL) {
    this->appName = strdup(appName);
}

AlljoynApp::~AlljoynApp() {
    if (this->appName) {
        free (this->appName);
    }

    cleanup();
}

/**
 * Fill about description for this Alljoyn interface.
 * @return
 */
QStatus AlljoynApp::fillAboutProperty() {
    QStatus status = ER_OK;

    return status;
}

QStatus AlljoynApp::buildBusObject() {
    QStatus status = ER_OK;

    return status;
}

/**
 * Initialize Alljoyn and application's resource.
 * @return
 */
QStatus AlljoynApp::init() {
    QStatus status = ER_OK;

    // Create message bus
    busAttachment = new BusAttachment(appName, true);
    if (NULL == busAttachment) {
        std::cout << "Could not initialize BusAttachment." << std::endl;
        return ER_OUT_OF_MEMORY;
    }

    // Start the BusAttachment
    status = busAttachment->Start();
    if (ER_OK != status) {
        std::cout << "Failed to start the BusAttachment (" << QCC_StatusText(status) << ")." << std::endl;
        cleanup();
        return status;
    }

    // Connect to the AJ Router
    status = busAttachment->Connect();
    if (ER_OK != status) {
        std::cout << "Failed to connect to AJ Router (" << QCC_StatusText(status) << ")." << std::endl;
        cleanup();
        return status;
    }

    // Create the bus listener
    ajn::SessionPort sp = SERVICE_PORT;
    busListener = new AlljoynBusListenerImpl();
    busListener->setSessionPort(sp);
    busAttachment->RegisterBusListener(*busListener);

    // Bind the session port for client to connect
    SessionOpts opts(SessionOpts::TRAFFIC_MESSAGES, false, SessionOpts::PROXIMITY_ANY, TRANSPORT_ANY);
    status = busAttachment->BindSessionPort(sp, opts, *busListener);
    if (ER_OK != status) {
        std::cout << "BindSessionPort failed (" << QCC_StatusText(status) << ")." << std::endl;
        return status;
    }

    return status;
}

/**
 * Cleanup application's resource.
 */
void AlljoynApp::cleanup() {
    // Clean up
    if (aboutPropertyStore) {
        delete aboutPropertyStore;
        aboutPropertyStore = NULL;
    }
    if (busAttachment && busListener) {
        busAttachment->UnregisterBusListener(*busListener);
        busAttachment->UnbindSessionPort(busListener->getSessionPort());
    }
    if (busListener) {
        delete busListener;
        busListener = NULL;
    }
    if (busAttachment) {
        delete busAttachment;
        busAttachment = NULL;
    }
    AboutServiceApi::DestroyInstance();
}

/**
 * Main application loop.
 */
void AlljoynApp::run() {
    while (isRunning) {
        sleep(100);
    }
}

int main(int argc, char**argv, char**envArg) {

    std::cout << "AllJoyn Library version: " << ajn::GetVersion() << std::endl;
    std::cout << "AllJoyn Library build info: " << ajn::GetBuildInfo() << std::endl;

    AlljoynApp app("MyAllJoynApp");
    app.init();

    //app.run();

    return 0;
}
