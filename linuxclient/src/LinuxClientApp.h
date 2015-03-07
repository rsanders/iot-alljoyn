/*
 * LinuxClientDemo.h
 * 	Created on : Feb 03, 2015
 * 	Authors: lyhh
 */

#ifndef LINUXCLIENTAPP_H
#define LINUXCLIENTAPP_H

#include "common.h"
#include "NotificationReceiverImpl.h"


using namespace ajn;
using namespace services;

class MyAboutListener : public AboutListener {

	void Announced(const char* busName, uint16_t version, SessionPort port, 
                               const MsgArg& objectDescriptionArg, const MsgArg& aboutDataArg) ;
    void printAboutData(AboutData& aboutData, const char* language, int tabNum);

};

class LinuxClientApp 
{	
		
    public:
        LinuxClientApp();
        
        ~LinuxClientApp();
          
       /**
        * Setup AllJoyn, creating the objects needed and registering listeners.
        *
        * @param appName  This value is provided to the BusAttachment constructor to name the application
        */

		QStatus init();
        
	    void cleanup();

    private:
		QStatus prepareBusAttachment();
    
    
        BusAttachment* mBusAttachment;
        MyAboutListener* mAboutListener;
        
        NotificationService* mNotificationService;
        NotificationReceiver* mNotificationReceiver;
	
};

#endif // LinuxClientApp.h
