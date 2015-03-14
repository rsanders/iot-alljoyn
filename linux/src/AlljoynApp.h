/*
 * AlljoynApp.h
 *
 *  Created on: Feb 14, 2015
 *      Author: hieult
 */

#ifndef ALLJOYNAPP_H_
#define ALLJOYNAPP_H_

#include "AlljoynBusObject.h"
#include "AlljoynBusListenerImpl.h"
#include "AlljoynAboutListenerImpl.h"

#include <alljoyn/BusAttachment.h>
#include <alljoyn/BusObject.h>

#include <alljoyn/about/AboutServiceApi.h>
#include <alljoyn/about/AboutPropertyStoreImpl.h>

#include <alljoyn/controlpanel/ControlPanelService.h>
#include <alljoyn/controlpanel/ControlPanelControllee.h>

#include <alljoyn/notification/Notification.h>
#include <alljoyn/notification/NotificationReceiver.h>
#include <alljoyn/notification/NotificationService.h>
#include <alljoyn/notification/NotificationText.h>

/**
 * The main entry of our app.
 */
class AlljoynApp
{
  private:
    ajn::BusAttachment*                     busAttachment;

    AlljoynBusObject*                       busObj; // Our bus object
    AlljoynBusListenerImpl*                 busListener;

    // For About
    AlljoynAboutListenerImpl* aboutListener;

    // For About service
    ajn::services::AboutPropertyStoreImpl*  aboutPropertyStore;

    // For Control Panel service
    ajn::services::ControlPanelService*     controlPanelService;
    ajn::services::ControlPanelControllee*  controlPanelControllee;

    // For Notification service
    ajn::services::NotificationService*     notificationService;
    ajn::services::NotificationReceiver*    notificationReceiver;

    bool isRunning;
    char *appName;

    QStatus fillAboutProperty();

    QStatus buildBusObject();

  public:
    AlljoynApp (const char * appName);
    virtual ~AlljoynApp();

    QStatus init();
    void cleanup();
    void run();

};

#endif /* ALLJOYNAPP_H_ */
