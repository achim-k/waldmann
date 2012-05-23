package my.projekt.ws;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DataActivity extends Activity {

	Button button1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act1_data);

		init();
	}

	void init() {

		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				update();
				// TODO Auto-generated method stub
			}
		});
	}

	String message;
	String rtrnStr;
	Socket tcpSocket;

	DataOutputStream writeStream;
	BufferedReader readStream;

	void update() {
		TextView log = (TextView) findViewById(R.id.textView2);
		log.setText("connecting to AVR...");

		try {
			tcpSocket = new Socket("192.168.0.174", 2701);

			writeStream = new DataOutputStream(tcpSocket.getOutputStream());
			readStream = new BufferedReader(new InputStreamReader(
					tcpSocket.getInputStream()));

			// SCPI BEFEHL!!! - BEGIN
			message = "hostname";
			// SCPI BEFEHL!!! - END

			writeStream.writeBytes(message + '\n');
			writeStream.flush();

			rtrnStr = readStream.readLine();

			// Stringverarbeitung um 30x20 Feld zu erstellen
			log.setText(cutString().toString());
			Toast.makeText(this, "aktualisiert", Toast.LENGTH_LONG).show();

			tcpSocket.close();

		} catch (Exception e1) {
			e1.printStackTrace();
			Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	StringBuffer cutString() {
		// Abgrenzen von 20 Zeichen - BEGIN
		StringBuffer feldStr = new StringBuffer("");

		// wenn die Antwort 30 oder mehr Zeichen hat wird ein newline nach 30 zeichen eingefuegt
		if (rtrnStr.length() >= 30) {
			int i = 1;
			for (; ((30 * i) <= rtrnStr.length() && i <= 20); ++i) {
				feldStr.append(rtrnStr.substring(30 * (i - 1), 30 * i))
						.append("\n");
			}
			if (i <= 20)
				// wenn die Anzahl der Zeichen kein Teiler von 30 ist
				feldStr.append(rtrnStr.substring(30 * (i - 1),
						rtrnStr.length()));
		} else {
			// falls die Anzahl von Zeichen unter 30 ist
			feldStr.append(rtrnStr);
		}
		
		return feldStr;
		// Abgrenzen von 20 Zeichen - END
	}
}
