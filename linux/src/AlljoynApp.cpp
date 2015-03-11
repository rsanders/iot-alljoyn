#include "common.h"
#include "AlljoynApp.h"

#include <alljoyn/PasswordManager.h>

#include <qcc/Debug.h>
#include <qcc/String.h>
#include <qcc/StringUtil.h>

using namespace ajn;
using namespace services;
using namespace qcc;

#define SERVICE_PORT 8888

#define QCC_MODULE "SAMPLE_DAEMON"

const char* DefaultDaemonBusName = "org.alljoyn.BusNode.TestingPurposesOnly";

const char* ThinClientAuthMechanism = "ALLJOYN_PIN_KEYX";
const char* ThinClientDefaultBusPwd = "1234";

AlljoynApp::AlljoynApp (const char *appName) : busAttachment(NULL),
                                               busListener(NULL),
                                               aboutPropertyStore(NULL),
                                               isRunning(false),
                                               notificationService(NULL),
                                               busObj(NULL) ,
                                               controlPanelControllee(NULL),
                                               controlPanelService(NULL) {
    if (appName != NULL) {
        this->appName = strdup(appName);
    } else {
        this->appName = NULL;
    }
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

    // TODO Ly fill in

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
    String nameToAdvertise = DefaultDaemonBusName;

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

    // Set the credential that thin clients have to offer to connect
    // to this SampleDaemon in a trusted manner.
    PasswordManager::SetCredentials(ThinClientAuthMechanism, ThinClientDefaultBusPwd);

    // Connect to the bundled AJ Router
    status = busAttachment->Connect("null:");
    if (ER_OK != status) {
        std::cout << "Failed to connect to AJ Router (" << QCC_StatusText(status) << ")." << std::endl;
        cleanup();
        return status;
    }

//    // Create the bus listener
//    ajn::SessionPort sp = SESSION_PORT_ANY;
//    busListener = new AlljoynBusListenerImpl();
//    busListener->setSessionPort(sp);
//    busAttachment->RegisterBusListener(*busListener);

    if (ER_OK == status) {
        // 'Quiet'ly advertise the name to be discovered by thin clients.
        // Also, given that thin clients are in the same network as the
        // SampleDaemon, advertise the name ONLY over TCP Transport.
        nameToAdvertise = "quiet@" + nameToAdvertise;
        status = busAttachment->AdvertiseName(nameToAdvertise.c_str(), TRANSPORT_TCP);
        if (ER_OK != status) {
            QCC_LogError(status, ("Unable to quietly advertise the name %s", nameToAdvertise.c_str()));
            cleanup();
            return status;
        }
    }

//    // Bind the session port for client to connect
//    SessionOpts opts(SessionOpts::TRAFFIC_MESSAGES, false, SessionOpts::PROXIMITY_ANY, TRANSPORT_ANY);
//    status = busAttachment->BindSessionPort(sp, opts, *busListener);
//    if (ER_OK != status) {
//        std::cout << "BindSessionPort failed (" << QCC_StatusText(status) << ")." << std::endl;
//        return status;
//    }

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
    isRunning = true;

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
