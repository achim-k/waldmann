package my.projekt.ws;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

public class ThreeTabs extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources();

		TabHost tabHost = getTabHost();

		// Create an Intent to launch an Activity for the tab
		Intent intent = new Intent().setClass(this, DataActivity.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		TabHost.TabSpec spec1 = tabHost
				.newTabSpec("messwerte")
				.setIndicator("Messwerte",
						res.getDrawable(R.drawable.ic_tab_artists))
				.setContent(intent);
		tabHost.addTab(spec1);

		intent = new Intent().setClass(this, SwitchesActivity.class);
		TabHost.TabSpec spec2 = tabHost
				.newTabSpec("schalter")
				.setIndicator("Schalter",
						res.getDrawable(R.drawable.ic_tab_albums))
				.setContent(intent);
		tabHost.addTab(spec2);

		intent = new Intent().setClass(this, ImpressumActivity.class);
		TabHost.TabSpec spec3 = tabHost
				.newTabSpec("impressum")
				.setIndicator("Impressum",
						res.getDrawable(R.drawable.ic_tab_songs))
				.setContent(intent);
		tabHost.addTab(spec3);

		tabHost.setCurrentTab(0);
	}

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
}
