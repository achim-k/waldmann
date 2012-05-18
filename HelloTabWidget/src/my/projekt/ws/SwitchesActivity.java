package my.projekt.ws;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SwitchesActivity extends Activity {

	ToggleButton buttonOnOff01;
	ToggleButton buttonOnOff02;
	ToggleButton buttonOnOff03;
	ToggleButton buttonOnOff04;
	ToggleButton buttonOnOff05;
	ToggleButton buttonOnOff06;
	ToggleButton buttonOnOff07;
	ToggleButton buttonOnOff08;

	String message;
	String rtrnStr;
	Socket tcpSocket;

	DataOutputStream writeStream;
	BufferedReader readStream;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act2_switches);
		
		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
/*
		try {
	        log.setText("connecting to AVR...");

			tcpSocket = new Socket("192.168.0.174", 2701);

			writeStream = new DataOutputStream(tcpSocket.getOutputStream());
			readStream = new BufferedReader(new InputStreamReader(
					tcpSocket.getInputStream()));
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		*/

		// buttonOnOff01.setChecked(true); // Wenn SCHALTER01 an sein soll.
	}
	
	public void onPause(Bundle saveInstanceState) {
		Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
		/*
		try {
			tcpSocket.close();
		} catch (IOException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		*/
	}
	
	public void onResume(Bundle saveInstanceState) {
		Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
		/*
		try {
			tcpSocket = new Socket("192.168.0.174", 2701);
		} catch (UnknownHostException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		*/
	}
	

	public void checkTB01(View v) {

		if (((ToggleButton) v).isChecked()) {
			Toast.makeText(this, "TB1 on", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TB1 off", Toast.LENGTH_SHORT).show();
		}
		// SCPI BEFEHL!!! - BEGIN
		message = "hostname";
		// SCPI BEFEHL!!! - END

		//writeStream.writeBytes(message + '\n');
		//writeStream.flush();

//		rtrnStr = readStream.readLine();
	}

	public void checkTB02(View v) {

		if (((ToggleButton) v).isChecked()) {
			Toast.makeText(this, "TB2 on", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TB2 off", Toast.LENGTH_SHORT).show();
		}
	}

	public void checkTB03(View v) {

		if (((ToggleButton) v).isChecked()) {
			Toast.makeText(this, "TB3 on", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TB3 off", Toast.LENGTH_SHORT).show();
		}
	}

	public void checkTB04(View v) {

		if (((ToggleButton) v).isChecked()) {
			Toast.makeText(this, "TB4 on", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TB4 off", Toast.LENGTH_SHORT).show();
		}
	}

	public void checkTB05(View v) {

		if (((ToggleButton) v).isChecked()) {
			Toast.makeText(this, "TB5 on", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TB5 off", Toast.LENGTH_SHORT).show();
		}
	}

	public void checkTB06(View v) {

		if (((ToggleButton) v).isChecked()) {
			Toast.makeText(this, "TB6 on", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TB6 off", Toast.LENGTH_SHORT).show();
		}
	}

	public void checkTB07(View v) {

		if (((ToggleButton) v).isChecked()) {
			Toast.makeText(this, "TB7 on", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TB7 off", Toast.LENGTH_SHORT).show();
		}
	}

	public void checkTB08(View v) {

		if (((ToggleButton) v).isChecked()) {
			Toast.makeText(this, "TB8 on", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TB8 off", Toast.LENGTH_SHORT).show();
		}
	}

}