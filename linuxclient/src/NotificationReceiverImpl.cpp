#include "NotificationReceiverImpl.h"
#include <iostream>

using namespace ajn::services;

NotificationReceiverImpl::NotificationReceiverImpl() 
{
}

NotificationReceiverImpl::~NotificationReceiverImpl() 
{
}


void NotificationReceiverImpl::Receive(ajn::services::Notification const& notification) 
{

    qcc::String appName = notification.getAppName();
    // If applications list is empty or the name exists in the filter list then print the notification
        std::cout << "******************** Begin New Message Received ********************" << std::endl;
        std::cout << "Message Id: " << notification.getMessageId() << std::endl;
        std::cout << "Device Id: " << notification.getDeviceId() << std::endl;
        std::cout << "Device Name: " << notification.getDeviceName() << std::endl;
        std::cout << "App Id: " << notification.getAppId() << std::endl;
        std::cout << "App Name: " << notification.getAppName() << std::endl;
        std::cout << "Sender BusName: " << notification.getSenderBusName() << std::endl;
        std::cout << "Message Type " << notification.getMessageType()  << std::endl;
        std::cout << "Notification version: " << notification.getVersion() << std::endl;


        // get vector of text messages and iterate through it
        std::vector<NotificationText> vecMessages = notification.getText();

        for (std::vector<NotificationText>::const_iterator vecMessage_it = vecMessages.begin(); vecMessage_it != vecMessages.end(); ++vecMessage_it) {
            std::cout << "Language: " << vecMessage_it->getLanguage().c_str() << "  Message: " << vecMessage_it->getText().c_str() << std::endl;
        }

        // Print out any other parameters sent in
        std::cout << "Other parameters included:" << std::endl;
        std::map<qcc::String, qcc::String> customAttributes = notification.getCustomAttributes();

        for (std::map<qcc::String, qcc::String>::const_iterator customAttributes_it = customAttributes.begin(); customAttributes_it != customAttributes.end(); ++customAttributes_it) {
            std::cout << "Custom Attribute Key: " << customAttributes_it->first.c_str() << "  Custom Attribute Value: " << customAttributes_it->second.c_str() << std::endl;
        }

        if (notification.getRichIconUrl()) {
            std::cout << "Rich Content Icon Url: " << notification.getRichIconUrl() << std::endl;
        }

        // get vector of audio content and iterate through it
        std::vector<RichAudioUrl> richAudioUrl = notification.getRichAudioUrl();

        if (!richAudioUrl.empty()) {
            std::cout << "******************** Begin Rich Audio Content ********************" << std::endl;
            for (std::vector<RichAudioUrl>::const_iterator vecAudio_it = richAudioUrl.begin(); vecAudio_it != richAudioUrl.end(); ++vecAudio_it) {
                std::cout << "Language: " << vecAudio_it->getLanguage().c_str() << "  Audio Url: " << vecAudio_it->getUrl().c_str() << std::endl;
            }
            std::cout << "******************** End Rich Audio Content ********************" << std::endl;

        }

        if (notification.getRichIconObjectPath()) {
            std::cout << "Rich Content Icon Object Path: " << notification.getRichIconObjectPath() << std::endl;
        }

        if (notification.getRichAudioObjectPath()) {
            std::cout << "Rich Content Audio Object Path: " << notification.getRichAudioObjectPath() << std::endl;
        }

        if (notification.getControlPanelServiceObjectPath()) {
            std::cout << "ControlPanelService object path: " << notification.getControlPanelServiceObjectPath() << std::endl;
        }

        if (notification.getOriginalSender()) {
            std::cout << "OriginalSender: " << notification.getOriginalSender() << std::endl;
        }


        std::cout << "******************** End New Message Received ********************" << std::endl << std::endl;
}


void NotificationReceiverImpl::Dismiss(const int32_t msgId, const qcc::String appId)
{
    std::cout << "Got NotificationReceiverTestImpl::Dismiss with msgId=" << msgId << " appId=" << appId.c_str() << std::endl;
}
