/******************************************************************************
 * Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ******************************************************************************/

#include "ControlPanelGenerated.h"
#include <alljoyn/controlpanel/LanguageSets.h>
#include <alljoyn/controlpanel/Label.h>
#include "../provided/RPIProvider.h"



using namespace ajn;
using namespace services;

bool ControlPanelGenerated::languageSetsDone = false;
ControlPanelControlleeUnit* ControlPanelGenerated::myDeviceUnit = 0;
HttpControl* ControlPanelGenerated::myDeviceHttpControl = 0;
ControlPanel* ControlPanelGenerated::myDeviceRootContainerControlPanel = 0;
Container* ControlPanelGenerated::myDeviceRootContainer = 0;
MyDeviceLightAction* ControlPanelGenerated::myDeviceLightAction = 0;


#define CHECK(x) if ((status = x) != ER_OK) { return status; }

void ControlPanelGenerated::PrepareLanguageSets()
{
    if (languageSetsDone) {
        return;
    }

    LanguageSet myDeviceEnglish("myDeviceEnglish");
    myDeviceEnglish.addLanguage("en");
    LanguageSets::add(myDeviceEnglish.getLanguageSetName(), myDeviceEnglish);

    languageSetsDone = true;
}

QStatus ControlPanelGenerated::PrepareWidgets(ControlPanelControllee*& controlPanelControllee)
{
    if (controlPanelControllee) {
        return ER_BAD_ARG_1;
    }

    PrepareLanguageSets();

    QStatus status = ER_OK;
    controlPanelControllee = new ControlPanelControllee();

    myDeviceUnit = new ControlPanelControlleeUnit("MyDevice");
    CHECK(controlPanelControllee->addControlPanelUnit(myDeviceUnit));

    myDeviceHttpControl = new HttpControl("http://MyControlPanelUrl.com");
    CHECK(myDeviceUnit->setHttpControl(myDeviceHttpControl));

    myDeviceRootContainerControlPanel = ControlPanel::createControlPanel(LanguageSets::get("myDeviceEnglish"));
    if (!myDeviceRootContainerControlPanel)
        return ER_FAIL;
    CHECK(myDeviceUnit->addControlPanel(myDeviceRootContainerControlPanel));

    myDeviceRootContainer = new Container("rootContainer", NULL);
    CHECK(myDeviceRootContainerControlPanel->setRootWidget(myDeviceRootContainer));

    myDeviceRootContainer->setEnabled(true);
    myDeviceRootContainer->setIsSecured(false);
    myDeviceRootContainer->setBgColor(0x200);

    std::vector<qcc::String> myDeviceRootContainerlabelVec;
    myDeviceRootContainerlabelVec.push_back("Control Panel for turn ON/OFF light");
    myDeviceRootContainer->setLabels(myDeviceRootContainerlabelVec);

    std::vector<uint16_t> myDeviceRootContainerHintsVec;
    myDeviceRootContainerHintsVec.push_back(VERTICAL_LINEAR);
    myDeviceRootContainer->setHints(myDeviceRootContainerHintsVec);

    myDeviceLightAction = new MyDeviceLightAction("lightAction", myDeviceRootContainer);
    CHECK(myDeviceRootContainer->addChildWidget(myDeviceLightAction));

    myDeviceLightAction->setEnabled(true);
    myDeviceLightAction->setIsSecured(false);
    myDeviceLightAction->setBgColor(0x400);

    std::vector<qcc::String> myDeviceLightActionlabelVec;
    myDeviceLightActionlabelVec.push_back("Light ON/OFF");
    myDeviceLightAction->setLabels(myDeviceLightActionlabelVec);

    std::vector<uint16_t> myDeviceLightActionHintsVec;
    myDeviceLightActionHintsVec.push_back(ACTIONBUTTON);
    myDeviceLightAction->setHints(myDeviceLightActionHintsVec);

    return status;
}

void ControlPanelGenerated::Shutdown()
{
    if (myDeviceUnit) {
        delete (myDeviceUnit);
        myDeviceUnit = 0;
    }
    if (myDeviceHttpControl) {
        delete (myDeviceHttpControl);
        myDeviceHttpControl = 0;
    }
    if (myDeviceRootContainerControlPanel) {
        delete (myDeviceRootContainerControlPanel);
        myDeviceRootContainerControlPanel = 0;
    }
    if (myDeviceRootContainer) {
        delete (myDeviceRootContainer);
        myDeviceRootContainer = 0;
    }
    if (myDeviceLightAction) {
        delete (myDeviceLightAction);
        myDeviceLightAction = 0;
    }

}
