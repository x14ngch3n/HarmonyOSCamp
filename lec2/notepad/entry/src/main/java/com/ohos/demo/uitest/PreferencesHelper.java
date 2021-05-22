package com.ohos.demo.uitest;

import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;

public class PreferencesHelper {
    private static PreferencesHelper sPreferenceHelper;

    public synchronized static PreferencesHelper getInstance() {
        if (sPreferenceHelper == null) {
            sPreferenceHelper = new PreferencesHelper();
        }
        return sPreferenceHelper;
    }

    public Preferences getPreference(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context.getApplicationContext());
        String fileName = "note";
        return databaseHelper.getPreferences(fileName);
    }

}
