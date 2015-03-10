#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>

#include "sensor.h"

/**
 * Reading AINx file in /sys/devices/ocp../helper.../AINx
 */
int get_sensor_value(char *sensor_file)
{
    int value;
	char buf[BUFSIZ];
    FILE *fp;

    if(!(fp = fopen(sensor_file, "r")))
    {
    	printf("Opening ERROR\n");
        return -99;
    }
    else
    {
        fgets(buf, sizeof(buf), fp);
        value = atoi(buf);
        fclose(fp);
    }

  return value;
}

/**
 * Measuring temperature: LM35
 */
int ajtcl_get_temp(char* temp_file)
{
	int temp = -99;
	// Temperature in °C = (Vout in mV) / 10
	temp = (int) (get_sensor_value(temp_file) / 10);
	return temp;
}

/**
 * Measuring light: Photo-resistor
 */
int ajtcl_get_light(char* light_file)
{
	// Just get Vout in mV
	// Bright light: Vout increases
	// Dark light: Vout decreases
    return get_sensor_value(light_file);
}
