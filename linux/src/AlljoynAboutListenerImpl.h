#ifndef ALLJOYN_ABOUT_LISTENER_H
#define ALLJOYN_ABOUT_LISTENER_H

#include "common.h"

using namespace ajn;

class AlljoynAboutListenerImpl : public AboutListener {
private:
	void Announced(const char* busName, uint16_t version, SessionPort port,
			const MsgArg& objectDescriptionArg, const MsgArg& aboutDataArg) ;

	void printAboutData(AboutData& aboutData, const char* language, int tabNum);

};


#endif
