package htw_berlin.de.mapmanager.prefs;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import htw_berlin.de.mapmanager.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);
    }
}
