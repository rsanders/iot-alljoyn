#include "common.h"
#include "AlljoynApp.h"

using namespace ajn;
using namespace services;

int main(int argc, char**argv, char**envArg)
{
    std::cout << "AllJoyn Library version: " << ajn::GetVersion() << std::endl;
    std::cout << "AllJoyn Library build info: " << ajn::GetBuildInfo() << std::endl;

    return 0;
}
