package de.project.waldmann;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.zip.Inflater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AVRWebserverActivity extends Activity {

	// ActivityManager um eine TabActivity zu machen, ohne TabActivity zu erben
	LocalActivityManager mlam;

	static AVRConnection avrConn;
	static String ipStr;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources();

		// manuell TabActivity erstellen
		mlam = new LocalActivityManager(this, false);

		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

		mlam.dispatchCreate(savedInstanceState);
		tabHost.setup(mlam);

		// die 3 Tabs hinzufuegen zum tabHost
		TabHost.TabSpec spec;
		spec = tabHost
				.newTabSpec("messwerte")
				.setIndicator("Messwerte",
						res.getDrawable(R.drawable.ic_tab_data))
				.setContent(R.id.Tab1);
		tabHost.addTab(spec);

		spec = tabHost
				.newTabSpec("schalter")
				.setIndicator("Schalter",
						res.getDrawable(R.drawable.ic_tab_switches))
				.setContent(R.id.Tab2);
		tabHost.addTab(spec);

		spec = tabHost
				.newTabSpec("impressum")
				.setIndicator("Impressum",
						res.getDrawable(R.drawable.ic_tab_impressum))
				.setContent(R.id.Tab3);
		tabHost.addTab(spec);

		tabHost.setCurrentTabByTag("messwerte");

		// IP Einstellungen beim starten der App
		showAddressDialog();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mlam.dispatchResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mlam.dispatchPause(isFinishing());
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// beim start aus dem Hintergrund, wird die Verbindung wieder aufgebaut
		try {
			avrConn = AVRConnection.getInstance();
			avrConn.connect(ipStr, 2701);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// beim Beenden der Anwendung wird die Verbindung abgebaut
		try {
			avrConn = AVRConnection.getInstance();
			avrConn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	// dieses AlarmFenster erscheint beim Start der Anwendung
	// nach dem Druck auf den Button wird die Verbindung mit dem AVR aufgebaut
	// und dann die Namen der Messquellen, die Messwerte und Einheiten, sowie
	// die Namen der Schalter und deren Zustaende vom AVR geholt.
	void showAddressDialog() {

		// Eingabe der AVR-Serveraddresse
		final EditText inputAddress = new EditText(this);
		inputAddress.setText("192.168.0.174");

		@SuppressWarnings("unused")
		AlertDialog setAVRaddress = new AlertDialog.Builder(this)
				.setTitle(R.string.einstellungen)
				.setMessage(R.string.address)
				.setView(inputAddress)
				.setNegativeButton(R.string.verbinden,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Editable value = inputAddress.getText();
								ipStr = value.toString();
								try {
									avrConn = AVRConnection.getInstance();
									avrConn.connect(ipStr, 2701);

									updateDataAndNames();
									updateSwitches();

									initButtons();

								} catch (UnknownHostException e) {
									e.printStackTrace();
									Toast.makeText(getApplicationContext(),
											e.getMessage(), Toast.LENGTH_SHORT)
											.show();
								} catch (IOException e) {
									e.printStackTrace();
									Toast.makeText(getApplicationContext(),
											e.getMessage(), Toast.LENGTH_SHORT)
											.show();
								}
							}
						}).show();
	}

	// onClickListener auf die Buttons zum aktualisieren gelegt
	void initButtons() {

		Button button1 = (Button) findViewById(R.id.button1);
		Button button2 = (Button) findViewById(R.id.button2);

		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// hier wird das Statusbyte abgefragt
					// je nach Antwort werden Messwerte, Quellnamen,
					// beides, oder garkeine Werte abgefragt
					String rtrnStr = avrConn.sendMsg("wcmd *STB?");
					if (rtrnStr.equals("O")) {
						updateDataAndNames();
						avrConn.sendMsg("wcmd *CLS");
					} else if (rtrnStr.equals("a")) {
						updateNames();
						avrConn.sendMsg("wcmd *CLS");
					} else if (rtrnStr.equals("A")) {
						updateData();
						avrConn.sendMsg("wcmd *CLS");
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.nichtsNeues, Toast.LENGTH_SHORT)
								.show();
					}

				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// beim Aktualisieren Button im Schalter Tab
				// werden die Zustaende der Schalter abgefragt
				updateSwitches();
			}
		});

	}

	// MesswertTab - BEGIN

	// Abfrage von Messquellnamen und Messwerten und Einheiten
	void updateDataAndNames() {
		TextView log;
		try {
			// Abfrage von allen 20 Messquellen
			for (int i = 1; i <= 20; ++i) {
				// holen der TextView durch die Ressource ID
				log = (TextView) findViewById(getResources().getIdentifier(
						"textView" + i + "1", "id", getPackageName()));
				// Text der geholten TextView setzen. Der Inhalt soll die
				// Antwort auf die an den AVR gesendete Nachricht sein.
				log.setText(avrConn.sendMsg("wcmd SOURCE:NAME?(@" + i + ")"));

				log = (TextView) findViewById(getResources().getIdentifier(
						"textView" + i + "2", "id", getPackageName()));
				log.setText(avrConn.sendMsg("wcmd SOURCE:VALUE?(@" + i + ")"));

				log = (TextView) findViewById(getResources().getIdentifier(
						"textView" + i + "3", "id", getPackageName()));
				log.setText(avrConn.sendMsg("wcmd SOURCE:UNIT?(@" + i + ")"));
			}
			Toast.makeText(this, R.string.aktualisiert, Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	// aktualisieren der Messwerte
	private void updateData() {
		TextView log;
		try {
			for (int i = 1; i <= 20; ++i) {
				log = (TextView) findViewById(getResources().getIdentifier(
						"textView" + i + "2", "id", getPackageName()));
				log.setText(avrConn.sendMsg("wcmd SOURCE:VALUE?(@" + i + ")"));
			}
			Toast.makeText(this, R.string.aktualisiert, Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	// aktualisieren der Quellnamen
	private void updateNames() {
		TextView log;
		try {
			for (int i = 1; i <= 20; ++i) {
				log = (TextView) findViewById(getResources().getIdentifier(
						"textView" + i + "1", "id", getPackageName()));
				log.setText(avrConn.sendMsg("wcmd SOURCE:NAME?(@" + i + ")"));
			}
			Toast.makeText(this, R.string.aktualisiert, Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	// MesswertTab - END

	
	// SchalterTab - BEGIN

	// Abfrage aller Schalter
	public void updateSwitches() {
		ToggleButton tb;
		TextView tv;

		try {

			for (int i = 1; i <= 8; ++i) {
				// ToggleButton zum setzen des Status
				tb = (ToggleButton) findViewById(getResources().getIdentifier(
						"toggleButton" + i, "id", getPackageName()));

				// TextVIew zum setzen des ButtonNamens
				tv = (TextView) findViewById(getResources().getIdentifier(
						"textViewB" + i, "id", getPackageName()));

				tv.setText(avrConn.sendMsg("wcmd SWITCH:NAME?(@" + i + ")"));

				if (avrConn.sendMsg("wcmd SWITCH:VALUE?(@" + i + ")").equals(
						"1"))
					// Button auf ON, wenn Rueckgabewert 1 kommt
					tb.setChecked(true);
				else {
					tb.setChecked(false);
				}
			}

			Toast.makeText(this, R.string.aktualisiert, Toast.LENGTH_SHORT)
					.show();

		} catch (Exception e1) {
			e1.printStackTrace();
			Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
		}

	}

	// wird aufgerufen, wenn Schalter1 gedrueckt wird
	public void checkTB01(View v) {
		if (((ToggleButton) v).isChecked()) {
			// Button auf ON
			setSwitchOn(1);
		} else {
			// Button auf OFF
			setSwitchOff(1);
		}
	}

	public void checkTB02(View v) {
		if (((ToggleButton) v).isChecked()) {
			// Button auf ON
			setSwitchOn(2);
		} else {
			// Button auf OFF
			setSwitchOff(2);
		}
	}

	public void checkTB03(View v) {
		if (((ToggleButton) v).isChecked()) {
			// Button auf ON
			setSwitchOn(3);
		} else {
			// Button auf OFF
			setSwitchOff(3);
		}
	}

	public void checkTB04(View v) {
		if (((ToggleButton) v).isChecked()) {
			// Button auf ON
			setSwitchOn(4);
		} else {
			// Button auf OFF
			setSwitchOff(4);
		}
	}

	public void checkTB05(View v) {
		if (((ToggleButton) v).isChecked()) {
			// Button auf ON
			setSwitchOn(5);
		} else {
			// Button auf OFF
			setSwitchOff(5);
		}
	}

	public void checkTB06(View v) {
		if (((ToggleButton) v).isChecked()) {
			// Button auf ON
			setSwitchOn(6);
		} else {
			// Button auf OFF
			setSwitchOff(6);
		}
	}

	public void checkTB07(View v) {
		if (((ToggleButton) v).isChecked()) {
			// Button auf ON
			setSwitchOn(7);
		} else {
			// Button auf OFF
			setSwitchOff(7);
		}
	}

	public void checkTB08(View v) {
		if (((ToggleButton) v).isChecked()) {
			// Button auf ON
			setSwitchOn(8);
		} else {
			// Button auf OFF
			setSwitchOff(8);
		}
	}

	// Schalter auf OFF setzen
	private void setSwitchOff(int i) {
		ToggleButton tb;

		try {
			avrConn.sendMsg("wcmd SWITCH:OFF(@" + i + ")");
			// Abfrag ob Zustand geaendert wurde
			if (avrConn.sendMsg("wcmd SWITCH:VALUE?(@" + i + ")").equals("0")) {
				tb = (ToggleButton) findViewById(getResources().getIdentifier(
						"toggleButton" + i, "id", getPackageName()));
				tb.setChecked(false);
			} else {
				// falls Zustand nicht geaendert wurde
				tb = (ToggleButton) findViewById(getResources().getIdentifier(
						"toggleButton" + i, "id", getPackageName()));
				tb.setChecked(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	// Schalter auf ON setzen
	private void setSwitchOn(int i) {
		ToggleButton tb;

		try {
			avrConn.sendMsg("wcmd SWITCH:ON(@" + i + ")");
			// Abfrag ob Zustand geaendert wurde
			if (avrConn.sendMsg("wcmd SWITCH:VALUE?(@" + i + ")").equals("1")) {
				tb = (ToggleButton) findViewById(getResources().getIdentifier(
						"toggleButton" + i, "id", getPackageName()));
				tb.setChecked(true);
			}
			// falls Zustand nicht geaendert wurde
			else {
				tb = (ToggleButton) findViewById(getResources().getIdentifier(
						"toggleButton" + i, "id", getPackageName()));
				tb.setChecked(false);
			}
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	// SchalterTab - END

	// Menu - BEGIN
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item1:
			showAddressDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Menu - END

}
