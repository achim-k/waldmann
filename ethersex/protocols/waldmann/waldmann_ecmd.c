/*
*
* Copyright (c) 2011 by Achim Krauch, Waldemar Siebert <achim.krauch@gmail.com>
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*
* For more information on the GPL, please go to:
* http://www.gnu.org/copyleft/gpl.html
*/


#include <stdio.h>
#include <avr/pgmspace.h>				//Wegen PSTR
#include "protocols/ecmd/ecmd-base.h"
#include "protocols/syslog/syslog.h"
#include "core/spi.h"

#define cs_low()  PIN_CLEAR(SPI_CS_WALDMANN)
#define cs_high() PIN_SET(SPI_CS_WALDMANN)

#define maxCmdSize 32
#define maxMsgSize 32


void talk_to_waldmann(char *befehl, char *returnArray, uint8_t returnSize)
{
	uint8_t counter = 0;

	cs_low();	 // aquire device (logisch-0-aktiv)

	//Befehl senden:
	while(befehl[counter] != '\0')
	{
		spi_send((uint8_t)befehl[counter]);
		counter++;
	}
	spi_send('\0');

	cs_high();

	//Steuercontroller Zeit zum Verarbeiten des Befehls geben:
	syslog_sendf("Sende '%s' an Steuercontroller\n",befehl);

	cs_low();

	//Antwort des Steuercontrollers einlesen:
	for(counter=0;counter<returnSize;counter++)
	{
		returnArray[counter] = (char)spi_send(0);
		if(returnArray[counter] == '\0')
			break;
	}

	returnArray[returnSize-1] = '\0';	//Stringende am Ende des Arrays setzen

	cs_high();	// release device
}



int16_t parse_cmd_wcmd(char *cmd, char *output, uint16_t len)
{
	uint8_t ret = 0;
	char wcmdArgument[maxCmdSize];	// Festlegen der Array-Größe wichtig, sonst gehts nur bis ca. 5 Zeichen
	char message[maxMsgSize];

	//Einlesen des wcmd-Parameters (als String)
	ret = sscanf_P(cmd, PSTR("%s"), &wcmdArgument);

	syslog_sendf("ECMD 'wcmd %s' erhalten\n", wcmdArgument);

	talk_to_waldmann(wcmdArgument,message,maxMsgSize);

	syslog_sendf("Antwort vom Stuercontroller: '%s'\n", message);

	if (ret == 1)	//Wenn 1 Argument angekommen
		return ECMD_FINAL(snprintf_P(output, len, PSTR("%s"), message));
	else
		return ECMD_ERR_PARSE_ERROR;
}

/*
-- Ethersex META --
block(WALDMANN)
ecmd_feature(wcmd, "wcmd",param1, wcmd-description)
*/

