package de.project.waldmann;

import java.io.IOException;
import java.net.UnknownHostException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AVRWebserverActivity extends Activity {

	// ActivityMngr fuer TabActivity
	LocalActivityManager mlam;

	// cut String
	String rtrnStr;
	static AVRConnection avrConn;
	static String ipStr;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// manuell TabActivity erstellen
		Resources res = getResources();

		mlam = new LocalActivityManager(this, false);

		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

		mlam.dispatchCreate(savedInstanceState);
		tabHost.setup(mlam);

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

		// TCPSocket einstellungen
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
		try {
			avrConn = AVRConnection.getInstance();
			avrConn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	// MesswertTab - BEGIN
	void initButtons() {

		Button button1 = (Button) findViewById(R.id.button1);
		Button button2 = (Button) findViewById(R.id.button2);

		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
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
				updateSwitches();
			}
		});

	}

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

									// Logik des messwerteTab
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

	void updateDataAndNames() {
		TextView log;
		try {
			// 10.0.2.2 ist der Localhost des PC auf dem das virt.Device laeuft
			// avrConn.connect("10.0.2.2", 8001);
			for (int i = 1; i <= 20; ++i) {

				log = (TextView) findViewById(getResources().getIdentifier(
						"textView" + i + "1", "id", getPackageName()));
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
				// Button zum setzen des Status
				tb = (ToggleButton) findViewById(getResources().getIdentifier(
						"toggleButton" + i, "id", getPackageName()));

				// ButtonText zum setzen des ButtonNamens
				tv = (TextView) findViewById(getResources().getIdentifier(
						"textView4" + i, "id", getPackageName()));

				tv.setText(avrConn.sendMsg("wcmd SWITCH:NAME?(@" + i + ")"));

				if (avrConn.sendMsg("wcmd SWITCH:VALUE?(@" + i + ")").equals(
						"1"))
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

	private void setSwitchOff(int i) {
		try {
			avrConn.sendMsg("wcmd SWITCH:OFF(@" + i + ")");
			// Abfrag ob Zustand geaendert wurde
			if (avrConn.sendMsg("wcmd SWITCH:VALUE?(@" + i + ")").equals("0")) {
				ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton1);
				tb.setChecked(false);
				Toast.makeText(this, "OFF-OK", Toast.LENGTH_SHORT).show();
			} else {
				// falls Zustand nicht geaendert wurde
				ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton1);
				tb.setChecked(true);
				Toast.makeText(this, "OFF-FAIL", Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void setSwitchOn(int i) {
		try {
			avrConn.sendMsg("wcmd SWITCH:ON(@" + i + ")");
			// Abfrag ob Zustand geaendert wurde
			if (avrConn.sendMsg("wcmd SWITCH:VALUE?(@" + i + ")").equals("1")) {
				ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton1);
				tb.setChecked(true);
				Toast.makeText(this, "ON-OK", Toast.LENGTH_SHORT).show();
			}
			// falls Zustand nicht geaendert wurde
			else {
				ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton1);
				tb.setChecked(false);
				Toast.makeText(this, "ON-FAIL", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	// SchalterTab - END
}