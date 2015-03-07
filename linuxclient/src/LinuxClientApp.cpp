#include "LinuxClientApp.h"
#include <qcc/String.h>
#include <qcc/StringUtil.h>
#include <stdio.h>

static const char* INTERFACE_NAME = "com.*";
static const char* APP_NAME = "LinuxAlljoynBus";


// Print out the fields found in the AboutData. Only fields with known signatures
// are printed out.  All others will be treated as an unknown field.
void MyAboutListener::printAboutData(AboutData& aboutData, const char* language, int tabNum)
{
    size_t count = aboutData.GetFields();

    const char** fields = new const char*[count];
    aboutData.GetFields(fields, count);

    for (size_t i = 0; i < count; ++i) {
        for (int j = 0; j < tabNum; ++j) {
            printf("\t");
        }
        printf("Key: %s", fields[i]);

        MsgArg* tmp;
        aboutData.GetField(fields[i], tmp, language);
        printf("\t");
        if (tmp->Signature() == "s") {
            const char* tmp_s;
            tmp->Get("s", &tmp_s);
            printf("%s", tmp_s);
        } else if (tmp->Signature() == "as") {
            size_t las;
            MsgArg* as_arg;
            tmp->Get("as", &las, &as_arg);
            for (size_t j = 0; j < las; ++j) {
                const char* tmp_s;
                as_arg[j].Get("s", &tmp_s);
                printf("%s ", tmp_s);
            }
        } else if (tmp->Signature() == "ay") {
            size_t lay;
            uint8_t* pay;
            tmp->Get("ay", &lay, &pay);
            for (size_t j = 0; j < lay; ++j) {
                printf("%02x ", pay[j]);
            }
        } else {
            printf("User Defined Value\tSignature: %s", tmp->Signature().c_str());
        }
        printf("\n");
    }
    delete [] fields;
}


void MyAboutListener::Announced(const char* busName, uint16_t version, SessionPort port, 
                               const MsgArg& objectDescriptionArg, const MsgArg& aboutDataArg) 
{
        // Place code here to handle Announce signal.
        printf("Announce signal discovered\n");

        AboutObjectDescription objectDescription;
        objectDescription.CreateFromMsgArg(objectDescriptionArg);
        printf("*********************************************************************************\n");
        printf("Announce signal discovered\n");
        printf("\tFrom bus %s\n", busName);
        printf("\tAbout version %hu\n", version);
        printf("\tSessionPort %hu\n", port);
        printf("\tObjectDescription:\n");
        AboutObjectDescription aod(objectDescriptionArg);
        size_t path_num = aod.GetPaths(NULL, 0);
        const char** paths = new const char*[path_num];
        aod.GetPaths(paths, path_num);
        for (size_t i = 0; i < path_num; ++i) {
            printf("\t\t%s\n", paths[i]);
            size_t intf_num = aod.GetInterfaces(paths[i], NULL, 0);
            const char** intfs = new const char*[intf_num];
            aod.GetInterfaces(paths[i], intfs, intf_num);
            for (size_t j = 0; j < intf_num; ++j) {
                printf("\t\t\t%s\n", intfs[j]);
            }
            delete [] intfs;
        }
        delete [] paths;
        printf("\tAboutData:\n");
        AboutData aboutData(aboutDataArg);
        printAboutData(aboutData, NULL, 2);
        printf("*********************************************************************************\n");
}


LinuxClientApp::LinuxClientApp() : mBusAttachment(NULL),
									mAboutListener(NULL),
									mNotificationService(NULL),
									mNotificationReceiver(NULL)
{
// Construct here
}

LinuxClientApp::~LinuxClientApp() 
{
// Decontruct here
   cleanup();
}

QStatus LinuxClientApp::prepareBusAttachment()
{
    QStatus status = ER_OK;

    if (!mBusAttachment) {
        mBusAttachment = new BusAttachment(APP_NAME, true);
        if (NULL == mBusAttachment) {
            std::cout << "Could not initialize BusAttachment." << std::endl;
            return ER_OUT_OF_MEMORY;
        }
        /* Start the msg bus */
        status = mBusAttachment->Start();
        if (ER_OK != status) {
            std::cout << "Failed to start the BusAttachment (" << QCC_StatusText(status) << ")." << std::endl;
            cleanup();
            return status;
        }
        /* Connect to the daemon */
        status = mBusAttachment->Connect();
        if (ER_OK != status) {
            std::cout << "BusAttachment Connect failed." << std::endl;
            return status;
        }
        
        std::cout << "Created BusAttachmeant and connected.\n" ;
 	    mAboutListener = new MyAboutListener();
        mBusAttachment->RegisterAboutListener(*mAboutListener);

        std::cout << "Register About Listener.\n";

        const char* interfaces[] = { INTERFACE_NAME };
        status = mBusAttachment->WhoImplements(interfaces, sizeof(interfaces) / sizeof(interfaces[0]));
       
        // Passing NULL into WhoImplements will listen for all About announcements
        // Note: specifying NULL for the interface parameter could have significant impact 
        // on network performance and should be avoided unless all announcements are needed.
         //status = mBusAttachment->WhoImplements(NULL);

        if (ER_OK == status) {
            std::cout << "WhoImplements called.\n";
        } else {
            std::cout << "WhoImplements call FAILED with status (" << QCC_StatusText(status) << ")" <<std::endl;
            
        } 

		
    }
                return status;

}

QStatus LinuxClientApp::init() 
{
	    QStatus status = ER_OK;

	// Initialize Service object and send it Notification Receiver object
	if (!mNotificationService)
		mNotificationService = NotificationService::getInstance();
		
	status = prepareBusAttachment();
	if (ER_OK != status)
	{
		std::cout << "Failed to prepare BusAttachment (" << QCC_StatusText(status) << ")." << std::endl;
        cleanup();
        return status;
	}

/* 
 * Have problem when running with Notification 
 * 
    mNotificationReceiver = new NotificationReceiverImpl();
    
    status = mNotificationService->initReceive(mBusAttachment, mNotificationReceiver);
    if (status != ER_OK) {
		std::cout << "Failed to init Notification Service (" << QCC_StatusText(status) << ")." << std::endl;
        cleanup();
        return status;
    }

*/ 


}

void LinuxClientApp::cleanup() 
{
	if (mNotificationService) {
        std::cout << "cleanup() - Notification Service" << std::endl;
        mNotificationService->shutdown();
        mNotificationService = NULL;
    }
    if (mNotificationReceiver) {
        std::cout << "cleanup() - Notification Receiver" << std::endl;
        delete mNotificationReceiver;
        mNotificationReceiver = NULL;
    }
    
	if (mBusAttachment) {
        delete mBusAttachment;
        mBusAttachment = NULL;
    }
    
    if (mAboutListener) {
		delete mAboutListener;
		mAboutListener = NULL;
	}
}


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


int main(int argc, char**argv) {

    std::cout << "AllJoyn Demo" << std::endl;

    LinuxClientApp app;
    app.init();
    const int bufSize = 1024;
    char buf[bufSize];

        // An input loop, to allow for easy extension of the sample that takes in input
    while (get_line(buf, bufSize, stdin)) {
        qcc::String line(buf);
        qcc::String cmd = NextToken(line);
        if (cmd == "help" || cmd == "?") {
            printHelp();
        } else if (cmd == "quit") {
            return 0;
        } else {
            printHelp();	
        }
    }
    
    return 0;
        
}
