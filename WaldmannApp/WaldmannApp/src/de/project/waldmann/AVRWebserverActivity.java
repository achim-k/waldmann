package de.project.waldmann;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AVRWebserverActivity extends Activity {

	// ActivityMngr fuer TabActivity
	LocalActivityManager mlam;

	// cut String
	String rtrnStr;

	// ToggleButtons des SwitchTab
	ToggleButton buttonOnOff01;
	ToggleButton buttonOnOff02;
	ToggleButton buttonOnOff03;
	ToggleButton buttonOnOff04;
	ToggleButton buttonOnOff05;
	ToggleButton buttonOnOff06;
	ToggleButton buttonOnOff07;
	ToggleButton buttonOnOff08;

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
				.setContent(R.id.layoutTab1);
		tabHost.addTab(spec);

		spec = tabHost
				.newTabSpec("schalter")
				.setIndicator("Schalter",
						res.getDrawable(R.drawable.ic_tab_switches))
				.setContent(R.id.layoutTab2);
		tabHost.addTab(spec);

		spec = tabHost
				.newTabSpec("impressum")
				.setIndicator("Impressum",
						res.getDrawable(R.drawable.ic_tab_impressum))
				.setContent(R.id.layoutTab3);
		tabHost.addTab(spec);

		tabHost.setCurrentTabByTag("messwerte");

		// logik des messwerteTab

		init();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mlam.dispatchResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mlam.dispatchPause(isFinishing());
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
		TextView log = (TextView) findViewById(R.id.textView1);
		log.setText("connecting to AVR...");

		try {

			AVRConnection avrConn = new AVRConnection();

			// avrConn.connect("192.168.0.174", 2701);
			// 10.0.2.2 ist der Localhost des PC auf dem das virt.Device laeuft
			avrConn.connect("10.0.2.2", 8001);

			// SCPI BEFEHL!!! - BEGIN
			rtrnStr = avrConn.sendMsg("hostname");
			// SCPI BEFEHL!!! - END

			log.setText(rtrnStr);
			Toast.makeText(this, "aktualisiert", Toast.LENGTH_LONG).show();

			avrConn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	// MesswertTab - END

	// SchalterTab - BEGIN

	public void updateSwitches() {

		try {
			// AVRConnection avrConn = new AVRConnection();

			// avrConn.connect("192.168.0.174", 2701);
			// 10.0.2.2 ist der Localhost des PC auf dem das virt.Device laeuft
			// avrConn.connect("10.0.2.2", 8001);

			// SCPI BEFEHL!!! - BEGIN
			// rtrnStr = avrConn.sendMsg("switches?");
			// SCPI BEFEHL!!! - END

			/*
			 * Boolean[] switches = new Boolean[8];
			 * 
			 * switches[0] = true; switches[1] = false; switches[2] = true;
			 * switches[3] = false; switches[4] = false; switches[5] = false;
			 * switches[6] = true; switches[7] = true;
			 * 
			 * 
			 * buttonOnOff01.setChecked( switches[0] );
			 * buttonOnOff02.setChecked( switches[1] );
			 * buttonOnOff03.setChecked( switches[2] );
			 * buttonOnOff04.setChecked( switches[3] );
			 * buttonOnOff05.setChecked( switches[4] );
			 * buttonOnOff06.setChecked( switches[5] );
			 * buttonOnOff07.setChecked( switches[6] );
			 * buttonOnOff08.setChecked( switches[7] );
			 */
			buttonOnOff01 = (ToggleButton) findViewById(R.id.toggleButton01);
			buttonOnOff01.setChecked(true);

			// Stringverarbeitung um 30x20 Feld zu erstellen

			Toast.makeText(this, "aktualisiert", Toast.LENGTH_LONG).show();

			// avrConn.disconnect();

		} catch (Exception e1) {
			e1.printStackTrace();
			Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	public void checkTB01(View v) {
		

		if (((ToggleButton) v).isChecked()) {
			Toast.makeText(this, "TB1 on", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TB1 off", Toast.LENGTH_SHORT).show();
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

	// Menu - BEGIN
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		Toast.makeText(this, "Menü", Toast.LENGTH_LONG).show();

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.menu.menu:
			// TCPSocket einstellungen

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Menu - END
}