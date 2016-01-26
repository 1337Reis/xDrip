/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 * Additions by jamorham to facilitate tasker plugin interface for xDrip
 *
 */

package com.eveningoutpost.dexdrip.localeTasker.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.eveningoutpost.dexdrip.localeTasker.Constants;
import com.eveningoutpost.dexdrip.localeTasker.bundle.BundleScrubber;
import com.eveningoutpost.dexdrip.localeTasker.bundle.PluginBundleManager;
import com.eveningoutpost.dexdrip.localeTasker.ui.EditActivity;

import java.util.Locale;


/**
 * This is the "fire" BroadcastReceiver for a Locale Plug-in setting.
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class FireReceiver extends BroadcastReceiver {

    /**
     * @param context {@inheritDoc}.
     * @param intent the incoming {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING} Intent. This
     * should contain the {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} that was saved by
     * {@link EditActivity} and later broadcast by Locale.
     */
    private final String TAG = FireReceiver.class.getSimpleName();

//    private SharedPreferences getMultiProcessSharedPreferences(Context context)
//    {
//        return context.getSharedPreferences(Preferences.getMultiProcessSharedPreferencesName(), Context.MODE_MULTI_PROCESS);
//
//    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        /*
         * Always be strict on input parameters! A malicious third-party app could send a malformed Intent.
         */

        if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
            if (Constants.IS_LOGGABLE) {
                Log.e(Constants.LOG_TAG,
                        String.format(Locale.US, "Received unexpected Intent action %s", intent.getAction())); //$NON-NLS-1$
            }
            return;
        }

        BundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(bundle);

        if (PluginBundleManager.isBundleValid(bundle)) {
            // We receive a space delimited string message from Tasker in the format
            // COMMAND PARAM1 PARAM2 etc..

            final String message = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE);

            if (!message.isEmpty()) {
                String[] message_array = message.toString().split("\\s+"); // split by space


                // Commands recognised:
                //

                // CAL: BG: -> Calibrate from a historical blood glucose reading

                switch (message_array[0]) {

                    case "CAL":
                    case "BG":
                    case "CAL:":
                    case "BG:":

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        // format = BG BGAGE
                        // bg in whatever format the app is using mmol/l or mg/dl
                        // needs sanity check and potential standardizing

                        // bgage is positive age ago of bg test reading in seconds (minus applied later)

                        // We push the values to the Calibration Activity
                        Intent calintent = new Intent();
                        calintent.setClassName("com.eveningoutpost.dexdrip", "com.eveningoutpost.dexdrip.AddCalibration");
                        calintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        calintent.putExtra("bg_string", message_array[1]);
                        calintent.putExtra("bg_age", message_array[2]);
                        calintent.putExtra("from_external","true");
                        context.startActivity(calintent);

                        break;


                    case "ALERT":
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                        // Modify Alerts

                        break;

                    case "SNOOZE":

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                        // Snooze Alerts


//                    case "PREFS":
//
//                        SharedPreferences prefs = getMultiProcessSharedPreferences(context);
//                        if (prefs==null) {
//
//                            Toast.makeText(context, "xDrip tasker - Cannot get access to Preferences", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//
//
//                        switch (message_array[1]) {
//
//                            case "B":
//                                // needs boolean type handler
//                                break;
//                            case "S":
//                                // string type handler
//                                if ((message_array[2]==null) || (message_array[3] == null)) {
//                                    Toast.makeText(context, "xDrip tasker - Blank parameter passed to string preferences", Toast.LENGTH_LONG).show();
//                                    return;
//                                }
//                                // Toast.makeText(context, "SET PREF STRING: " + message_array[2] + " to " + message_array[3], Toast.LENGTH_SHORT).show();
//                                Log.e(TAG,"firereceiver - About to write prefs");
//                                prefs.edit().putString(message_array[2], message_array[3]).commit();
//                                // If preferences screen is actually open right now then shut it down to avoid cache errors
//                                Intent fintent = new Intent(context.getString(R.string.finish_preferences_activity));
//                                context.sendBroadcast(fintent);
//                                prefs.edit().putString(message_array[2],message_array[3]).commit(); // save again
//                                Log.e(TAG,"firereceiver about to restart collector");
//                                // blanket restart
//                                try {
//                                    CollectionServiceStarter.restartCollectionService(context.getApplicationContext());
//                                } catch (Exception e) {
//
//                                    Log.e(TAG,"fireReceiver: error restarting collectionservice "+e.toString());
//                                }
//
//                                break;
//                            default:
//                                Toast.makeText(context, "Unknown xDrip Tasker prefs type "+message_array[1], Toast.LENGTH_LONG).show();
//
//                                break;
//                        }
//
//                        break;
                    default:
                        Toast.makeText(context, "Unknown xDrip first Tasker parameter " + message_array[0], Toast.LENGTH_LONG).show();
                        break;
                }

            }

        }
    }


}