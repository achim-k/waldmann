#include <avr/io.h>

#define SPI_PORT PORTB
#define SPI_DDR  DDRB

unsigned char SPI_WriteRead(unsigned char dataout)
{
	// Put Slave Data On SPDR
	SPDR=dataout;
	// Wait for transmission complete
	while(!(SPSR & (1<<SPIF)));
	// Return Serial In Value (MISO)
	return SPDR;
}

int main(void)
{	
	unsigned char datain,dataout,slavereg;

	DDRC = 0xFF;	//PORTC als Output definieren

	
	// Set MISO (PB6) as output, others as input
	SPI_DDR |= (1<<PB6);
	// Enable SPI as Slave
	SPCR = (1<<SPE);
	
	PORTC = 0xFF;
	while(1)
	{
		datain = SPI_WriteRead(0x41);
		PORTC = datain;				//Empfangene SPI-Daten auf PORTC setzen
	}

	return 1;
}
