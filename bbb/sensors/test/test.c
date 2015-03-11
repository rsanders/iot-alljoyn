/*
 * main.c
 *
 *  Created on: Mar 8, 2015
 *      Author: an
 */
#include <stdio.h>

#include "sensor.h"

int main(int argc, char** argv)
{
	printf("TEMP %d LIGHT %d\n", ajtcl_get_temp(argv[1]), ajtcl_get_light(argv[2]));

	return 0;
}



