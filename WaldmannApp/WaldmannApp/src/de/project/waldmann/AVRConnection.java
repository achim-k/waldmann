package de.project.waldmann;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

// Die Verbindung wurde durch einen TCPSocket realisiert.
// Um sicherzustellen, dass es nur ein Verbindungsobjekt gibt
// wurde es als Singleton implementiert.
public class AVRConnection {

	private static AVRConnection instance = null;

	// TCP Verbindungs Komponenten
	String rtrnMsg;
	static Socket tcpSocket;
	DataOutputStream writeStream;
	BufferedReader readStream;
	InetAddress avrIp;

	private AVRConnection() {
	}

	// dient der einmaligen Instanziierung und wird durch den privaten
	// Konstruktor unterstuetzt
	public static AVRConnection getInstance() {
		if (instance == null) {
			instance = new AVRConnection();
		}
		return instance;
	}

	// verbinden
	// bindet den Socket an eine IP und einen Port fuer die Kommunikation
	public void connect(String avrIpStr, int port) throws UnknownHostException,
			IOException {
		avrIp = InetAddress.getByName(avrIpStr);

		tcpSocket = new Socket(avrIp, port);

		writeStream = new DataOutputStream(tcpSocket.getOutputStream());
		readStream = new BufferedReader(new InputStreamReader(
				tcpSocket.getInputStream()));

	}

	// trennen
	// schliesst alle Streams und den Socket
	public void disconnect() throws IOException {
		if (writeStream != null)
			writeStream.close();
		if (readStream != null)
			readStream.close();
		if (tcpSocket != null)
			tcpSocket.close();
	}

	// senden
	// dient dem Senden und Empfangen von Nachrichten
	public String sendMsg(String msg) throws IOException {

		writeStream.writeBytes(msg + '\n');
		writeStream.flush();

		String cutIt = readStream.readLine();
		rtrnMsg = cutIt.substring(0,
				(cutIt.length() >= 30) ? 30 : cutIt.length());

		return rtrnMsg;

	}
}