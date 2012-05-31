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

		// logik des messwerteTab
		init();

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
			avrConn.connect(ipStr, 2701);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			avrConn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.item1:
			// TCPSocket einstellungen
			showAddressDialog();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
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
								} catch (UnknownHostException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}).show();
	}

	// MesswertTab - BEGIN
	void init() {

		Button button1 = (Button) findViewById(R.id.button1);
		Button button2 = (Button) findViewById(R.id.button2);

		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateData();
			}
		});

		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				updateSwitches();
			}
		});

	}

	void updateData() {
		TextView log;

		try {
			// 10.0.2.2 ist der Localhost des PC auf dem das virt.Device laeuft
			// avrConn.connect("10.0.2.2", 8001);

			// spaeter:
			// log.setText(avrConn.sendMsg("wcmd SOURCE:NAME?(@1)"));

			for (int i = 1; i <= 20; ++i) {
				String rtrStr = "";

				log = (TextView) findViewById(getResources().getIdentifier(
						"textView" + i, "id", getPackageName()));
				rtrStr = rtrStr + avrConn.sendMsg("wcmd SOURCE:NAME?(@" + i + ")");
				rtrStr = rtrStr + avrConn.sendMsg("wcmd SOURCE:VALUE?(@" + i + ")");
				rtrStr = rtrStr + avrConn.sendMsg("wcmd SOURCE:UNIT?(@" + i + ")");
				log.setText(rtrStr);
			}

			Toast.makeText(this, "Achim stink!", Toast.LENGTH_SHORT)
					.show();

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	// MesswertTab - END

	// SchalterTab - BEGIN

	public void updateSwitches() {
		ToggleButton tb;

		try {

			for (int i = 1; i <= 8; ++i) {

				tb = (ToggleButton) findViewById(getResources().getIdentifier(
						"toggleButton" + i, "id", getPackageName()));
				// tb.setText(avrConn.sendMsg("hostname"));
				if (avrConn.sendMsg("wcmd SWITCH:VALUE?(@" + i + ")").equals("1"))
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
			try {
				// Button auf ON und zustand auf ON
				 avrConn.sendMsg("wcmd SWITCH:ON(@1)");
				// Abfrag ob Zustand geaendert wurde
				
				if (avrConn.sendMsg("wcmd SWITCH:VALUE?(@1)").equals("1")) {
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// Button auf OFF
			try {
				avrConn.sendMsg("wcmd SWITCH:OFF(@1)");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if (avrConn.sendMsg("wcmd SWITCH:VALUE?(@1)").equals("0")) {
					ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton1);
					tb.setChecked(false);
					Toast.makeText(this, "OFF-OK", Toast.LENGTH_SHORT).show();
				} else {
					ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton1);
					tb.setChecked(true);
					Toast.makeText(this, "OFF-FAIL", Toast.LENGTH_SHORT).show();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// SCPI BEFEHL!!! - BEGIN

		// SCPI BEFEHL!!! - END

		// writeStream.writeBytes(message + '\n');
		// writeStream.flush();

		// rtrnStr = readStream.readLine();
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

	// SchalterTab - END

}