/*
 * AlljoynApp.h
 *
 *  Created on: Feb 14, 2015
 *      Author: hieult
 */

#ifndef ALLJOYNAPP_H_
#define ALLJOYNAPP_H_

#include "AlljoynBusObject.h"

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

    // For About services
    ajn::services::AboutPropertyStoreImpl*  aboutPropertyStore;

    // For Control Panel services
    ajn::services::ControlPanelService*     controlPanelService;
    ajn::services::ControlPanelControllee*  controlPanelControllee;

    ajn::services::NotificationService*     notificationService;

    QStatus fillAboutProperty();
    QStatus buildBusObject();

  public:
    AlljoynApp ();
    virtual ~AlljoynApp();

    void init();
    void shutdown();

};

#endif /* ALLJOYNAPP_H_ */
