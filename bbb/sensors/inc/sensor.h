#ifndef SENSOR_H
#define SENSOR_H

/**
 * Input: Locations of AINx file i.e /sys/devices/ocp../helper.../AINx
 */
int ajtcl_get_temp(char* temp_file);
int ajtcl_get_light(char* light_fight);


#endif //SENSOR_H
