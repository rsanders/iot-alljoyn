#include "AlljoynAboutListenerImpl.h"
#include <stdio.h>

AlljoynAboutListenerImpl::AlljoynAboutListenerImpl() : mBusAttachment(NULL)
{
}

AlljoynAboutListenerImpl::AlljoynAboutListenerImpl(BusAttachment* busAttachment) : mBusAttachment(busAttachment)
{
}

AlljoynAboutListenerImpl::~AlljoynAboutListenerImpl()
{
}


void AlljoynAboutListenerImpl::Announced(const char* busName, uint16_t version, SessionPort port,
		const MsgArg& objectDescriptionArg, const MsgArg& aboutDataArg)
{
	// Place code here to handle Announce signal.
	printf("*********************************************************************************\n");
	printf("Announce signal discovered\n");
	printf("\tFrom bus %s\n", busName);
	printf("\tAbout version %hu\n", version);
	printf("\tSessionPort %hu\n", port);
	printf("\tObjectDescription:\n");
	AboutObjectDescription objectDescription(objectDescriptionArg);
	size_t path_num = objectDescription.GetPaths(NULL, 0);
	const char** paths = new const char*[path_num];
	objectDescription.GetPaths(paths, path_num);
	for (size_t i = 0; i < path_num; ++i) {
		printf("\t\t%s\n", paths[i]);
		size_t intf_num = objectDescription.GetInterfaces(paths[i], NULL, 0);
		const char** intfs = new const char*[intf_num];
		objectDescription.GetInterfaces(paths[i], intfs, intf_num);
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

	// Request information that is not contained in Announcement
	QStatus status;
	if(mBusAttachment != NULL) {
		// 1. Join the session
		SessionId sessionId;
		SessionOpts opts(SessionOpts::TRAFFIC_MESSAGES, false, SessionOpts::PROXIMITY_ANY, TRANSPORT_ANY);
		mBusAttachment->EnableConcurrentCallbacks();
		status = mBusAttachment->JoinSession(busName, port, NULL, sessionId, opts);

		// 2. Create AboutProxy
		printf("SessionJoined sessionId = %u, status = %s\n", sessionId, QCC_StatusText(status));
		if (ER_OK == status && 0 != sessionId) {
			AboutProxy aboutProxy(*mBusAttachment, busName, sessionId);

			MsgArg objArg;
			aboutProxy.GetObjectDescription(objArg);
			printf("*********************************************************************************\n");
			printf("AboutProxy.GetObjectDescription:\n");
			AboutObjectDescription aod(objArg);
			size_t path_num = aod.GetPaths(NULL, 0);
			const char** paths = new const char*[path_num];
			aod.GetPaths(paths, path_num);
			for (size_t i = 0; i < path_num; ++i) {
				printf("\t%s\n", paths[i]);
				size_t intf_num = aod.GetInterfaces(paths[i], NULL, 0);
				const char** intfs = new const char*[intf_num];
				aod.GetInterfaces(paths[i], intfs, intf_num);
				for (size_t j = 0; j < intf_num; ++j) {
					printf("\t\t%s\n", intfs[j]);
				}
				delete [] intfs;
			}
			delete [] paths;

			MsgArg aArg;
			aboutProxy.GetAboutData("en", aArg);
			printf("*********************************************************************************\n");
			printf("AboutProxy.GetAboutData: (Default Language)\n");
			AboutData aboutData(aArg);
			printAboutData(aboutData, NULL, 1);
			size_t lang_num;
			lang_num = aboutData.GetSupportedLanguages();
			// If the lang_num == 1 we only have a default language
			if (lang_num > 1) {
				const char** langs = new const char*[lang_num];
				aboutData.GetSupportedLanguages(langs, lang_num);
				char* defaultLanguage;
				aboutData.GetDefaultLanguage(&defaultLanguage);
				// print out the AboutData for every language but the
				// default it has already been printed.
				for (size_t i = 0; i < lang_num; ++i) {
					if (strcmp(defaultLanguage, langs[i]) != 0) {
						status = aboutProxy.GetAboutData(langs[i], aArg);
						if (ER_OK == status) {
							aboutData.CreatefromMsgArg(aArg, langs[i]);
							printf("AboutProxy.GetAboutData: (%s)\n", langs[i]);
							printAboutData(aboutData, langs[i], 1);
						}
					}
				}
				delete [] langs;
			}

			uint16_t ver;
			aboutProxy.GetVersion(ver);
			printf("*********************************************************************************\n");
			printf("AboutProxy.GetVersion %hd\n", ver);
			printf("*********************************************************************************\n");
		}
	} else {
        printf("BusAttachment is NULL\n");
	}
}


// Print out the fields found in the AboutData. Only fields with known signatures
// are printed out.  All others will be treated as an unknown field.
void AlljoynAboutListenerImpl::printAboutData(AboutData& aboutData, const char* language, int tabNum)
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
