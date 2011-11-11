/*
 * main.c
 *
 *  Created on: Oct 20, 2011
 *      Author: sac
 */

#include <avr/io.h>
// avr header file for IO ports
int main(void) {
	unsigned char i; // temporary variable
	DDRA = 0x00;
	DDRB = 0xFF;
	PORTB = 0x00;
// set PORTA for input
// set PORTB for output
// turn ON all LEDs initially
	while (1) {
// Read input from PORTA.
// This port will be connected to the 8 switches
		i = PINA;
// Send output to PORTB.
// This port will be connected to the 8 LEDs tttttttttttttttttttttttttttttttttt
		PORTB = i;
	}
	return 1;
}
