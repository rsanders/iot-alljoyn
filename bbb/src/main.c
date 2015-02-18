#include <stdio.h>
#include <stdlib.h>
//#include <aj_debug.h>
#include <alljoyn.h>

int AJ_Main(void)
{
    AJ_Status status = AJ_OK;
    AJ_BusAttachment bus;
    uint8_t connected = FALSE;
    uint8_t done = FALSE;
    uint32_t sessionId = 0;

    /*
     * One time initialization before calling any other AllJoyn APIs
     */
    AJ_Initialize();

    return status;
}

#ifdef AJ_MAIN
int main()
{
    return AJ_Main();
}
#endif
