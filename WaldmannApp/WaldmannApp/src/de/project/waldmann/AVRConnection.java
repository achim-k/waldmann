package de.project.waldmann;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class AVRConnection {
	
	private static AVRConnection instance = null;

	// TCP Verbindungskomponenten
	
	// String message;
	String rtrnMsg;
	static Socket tcpSocket;
	DataOutputStream writeStream;
	BufferedReader readStream;
	InetAddress avrIp;
	
	private AVRConnection() {
		// TODO Auto-generated constructor stub
	}
	
	public static AVRConnection getInstance() {
		if(instance == null) {
			instance = new AVRConnection();
		}
		return instance;
	}

	// verbinden
	public void connect(String avrIpStr, int port) throws UnknownHostException, IOException {
		avrIp = InetAddress.getByName(avrIpStr);

		tcpSocket = new Socket(avrIp, port);
		

		writeStream = new DataOutputStream(tcpSocket.getOutputStream());
		readStream = new BufferedReader(new InputStreamReader(
				tcpSocket.getInputStream()));


	}

	// trennen
	public void disconnect() throws IOException {
		
		writeStream.close();
		readStream.close();

		tcpSocket.close();
	}

	public String sendMsg(String msg) throws IOException {

		writeStream.writeBytes(msg + '\n');
		writeStream.flush();

		rtrnMsg = readStream.readLine();
		
		//rtrnMsg = cutString().toString();
		
		return rtrnMsg;
	
	}

/*
 *  //Stringverarbeitung, um 30x20 Feld zu erstellen
	private StringBuffer cutString() {
		// Abgrenzen von 20 Zeichen - BEGIN
		StringBuffer feldStr = new StringBuffer("");

		// wenn die Antwort 30 oder mehr Zeichen hat wird ein newline nach 30
		// zeichen eingefuegt
		if (rtrnMsg.length() >= 30) {
			int i = 1;
			for (; ((30 * i) <= rtrnMsg.length() && i <= 20); ++i) {
				feldStr.append(rtrnMsg.substring(30 * (i - 1), 30 * i)).append(
						"\n");
			}
			if (i <= 20)
				// wenn die Anzahl der Zeichen kein Teiler von 30 ist
				feldStr.append(rtrnMsg.substring(30 * (i - 1), rtrnMsg.length()));
		} else {
			// falls die Anzahl von Zeichen unter 30 ist
			feldStr.append(rtrnMsg);
		}

		return feldStr;
		// Abgrenzen von 20 Zeichen - END
	}
*/

}
