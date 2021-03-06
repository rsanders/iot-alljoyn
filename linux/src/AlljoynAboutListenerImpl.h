#ifndef ALLJOYN_ABOUT_LISTENER_H
#define ALLJOYN_ABOUT_LISTENER_H

#include "common.h"

using namespace ajn;

class AlljoynAboutListenerImpl : public AboutListener {
public:
	AlljoynAboutListenerImpl();
	AlljoynAboutListenerImpl(BusAttachment* busAttachment);

	~AlljoynAboutListenerImpl();

	// Todo: Add a method to return a list/tree of AboutData client received.

private:
	void Announced(const char* busName, uint16_t version, SessionPort port,
			const MsgArg& objectDescriptionArg, const MsgArg& aboutDataArg) ;

	void printAboutData(AboutData& aboutData, const char* language, int tabNum);

	BusAttachment* mBusAttachment;
};


#endif
