#include "common.h"
#include "AlljoynApp.h"
#include "NotificationReceiverImpl.h"

#include <alljoyn/PasswordManager.h>

#include <qcc/Debug.h>
#include <qcc/String.h>
#include <qcc/StringUtil.h>

using namespace ajn;
using namespace services;
using namespace qcc;

#define SERVICE_PORT 8888

#define QCC_MODULE "SAMPLE_DAEMON"

static const char* INTERFACE_NAME = "com.*";

const char* DefaultDaemonBusName = "org.alljoyn.BusNode.TestingPurposesOnly";

const char* ThinClientAuthMechanism = "ALLJOYN_PIN_KEYX";
const char* ThinClientDefaultBusPwd = "1234";

AlljoynApp::AlljoynApp (const char *appName) : busAttachment(NULL),
                                               busListener(NULL),
                                               aboutListener(NULL),
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

    std::cout << "Created BusAttachment and connected.\n" ;

    // Register About listener
    aboutListener = new AlljoynAboutListenerImpl(busAttachment);
    busAttachment->RegisterAboutListener(*aboutListener);

    const char* interfaces[] = { INTERFACE_NAME };
    status = busAttachment->WhoImplements(interfaces, sizeof(interfaces) / sizeof(interfaces[0]));

    // Passing NULL into WhoImplements will listen for all About announcements
    // Note: specifying NULL for the interface parameter could have significant impact
    // on network performance and should be avoided unless all announcements are needed.
    //status = busAttachment->WhoImplements(NULL);

    if (ER_OK != status) {
    	std::cout << "WhoImplements call FAILED with status (" << QCC_StatusText(status) << ")" <<std::endl;
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

    // Initialize Notification Consumer
	// Initialize Service object and send it Notification Receiver object
    // Todo: Debug error "Failed to init Notification Service (ER_BUS_OBJ_ALREADY_EXISTS)."

//    if (!notificationService)
//    	notificationService = NotificationService::getInstance();
//    notificationReceiver = new NotificationReceiverImpl();
//
//    status = notificationService->initReceive(busAttachment, notificationReceiver);
//    if (status != ER_OK) {
//    	std::cout << "Failed to init Notification Service (" << QCC_StatusText(status) << ")." << std::endl;
//    	cleanup();
//    	return status;
//    }


    return status;
}

/**
 * Cleanup application's resource.
 */
void AlljoynApp::cleanup() {
    // Clean up
	if (aboutListener) {
		delete aboutListener;
		aboutListener = NULL;
	}
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
 * Helper methods to receive command from console
 */

/*
 * get a line of input from the the file pointer (most likely stdin).
 * This will capture the the num-1 characters or till a newline character is
 * entered.
 *
 * @param[out] str a pointer to a character array that will hold the user input
 * @param[in]  num the size of the character array 'str'
 * @param[in]  fp  the file pointer the sting will be read from. (most likely stdin)
 *
 * @return returns the same string as 'str' if there has been a read error a null
 *                 pointer will be returned and 'str' will remain unchanged.
 */
char* get_line(char*str, size_t num, FILE*fp)
{
    char*p = fgets(str, num, fp);

    // fgets will capture the '\n' character if the string entered is shorter than
    // num. Remove the '\n' from the end of the line and replace it with nul '\0'.
    if (p != NULL) {
        size_t last = strlen(str) - 1;
        if (str[last] == '\n') {
            str[last] = '\0';
        }
    }
    return p;
}

static qcc::String NextToken(qcc::String& inStr)
{
    qcc::String ret;
    size_t off = inStr.find_first_of(' ');
    if (off == qcc::String::npos) {
        ret = inStr;
        inStr.clear();
    } else {
        ret = inStr.substr(0, off);
        inStr = Trim(inStr.substr(off));
    }
    return Trim(ret);
}

void printHelp() {
    printf("\n");
    printf("quit                             - Shutdown the application and exit\n");
    printf("\n");
}


/**
 * Main application loop.
 */
void AlljoynApp::run() {
//    isRunning = true;
//
//    while (isRunning) {
//        sleep(100);
//    }
    const int bufSize = 1024;
    char buf[bufSize];

        // An input loop, to allow for easy extension of the sample that takes in input
    // ToDo: Add more commands here
    while (get_line(buf, bufSize, stdin)) {
        qcc::String line(buf);
        qcc::String cmd = NextToken(line);
        if (cmd == "help" || cmd == "?") {
            printHelp();
        } else if (cmd == "quit") {
            return;
        } else {
            printHelp();
        }
    }


}

int main(int argc, char**argv, char**envArg) {

    std::cout << "AllJoyn Library version: " << ajn::GetVersion() << std::endl;
    std::cout << "AllJoyn Library build info: " << ajn::GetBuildInfo() << std::endl;

    AlljoynApp app("MyAllJoynApp");
    app.init();

    app.run();

    return 0;
}
