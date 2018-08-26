/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.webbluetoothcg.bletestperipheral;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.*;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;


public class Peripherals extends ListActivity {

  private static final String[] PERIPHERALS_NAMES = new String[]{"Battery", "Heart Rate Monitor", "Health Thermometer", "GPS Coordinates"};
  public final static String EXTRA_PERIPHERAL_INDEX = "PERIPHERAL_INDEX";
  protected Location mLastLocation;
  private AddressResultReceiver mResultReceiver;
  private FusedLocationProviderClient mFusedLocationClient;
  Context context = null;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_peripherals_list);
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
        /* layout for the list item */ android.R.layout.simple_list_item_1,
        /* id of the TextView to use */ android.R.id.text1,
        /* values for the list */ PERIPHERALS_NAMES);
    setListAdapter(adapter);
  }
//
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    Intent intent = new Intent(this, Peripheral.class);
    intent.putExtra(EXTRA_PERIPHERAL_INDEX, position);
    startActivity(intent);
  }
//
    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }
//
    class AddressResultReceiver extends ResultReceiver {
        @SuppressLint("RestrictedApi")
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            displayAddressOutput();

            // Show a toast message if an address was found.
            context = getApplicationContext();

            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(context, mAddressOutput , Toast.LENGTH_LONG).show();
            }

        }
    }

    private void displayAddressOutput() {
    }


    public void fetchAddressButtonHander(View view) {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Location mLastKnownLocation = location;

                        // In some rare cases the location returned can be null
                        if (mLastKnownLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(context,
                                    R.string.no_geocoder_available,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new location
                        startIntentService();
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        Toast.makeText(context, "text" , Toast.LENGTH_LONG).show();
    }
}
