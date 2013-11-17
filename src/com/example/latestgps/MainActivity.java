package com.example.latestgps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;

public class MainActivity extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private final static long UPDATE_INTERVAL = 5000;
	private final static long FASTEST_UPDATE_INTERVAL = 1000;
	LocationRequest request;
	LocationClient client;
	Location location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		client = new LocationClient(this, this, this);
		request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(UPDATE_INTERVAL);
		request.setFastestInterval(FASTEST_UPDATE_INTERVAL);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (serviceConnected())
			client.connect();
	}

	private boolean serviceConnected() {

		if (ConnectionResult.SUCCESS == GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this)) {
			Log.d("Location Updates", "Google Play services is available.");
			return true;
		} else {
			int errorCode = ConnectionResult.B.getErrorCode();
			Dialog errordialog = GooglePlayServicesUtil.getErrorDialog(
					errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			if (errordialog != null) {

				ShowErrorDialog errFragMentDialog = new ShowErrorDialog();
				errFragMentDialog.setDialog(errordialog);
				errFragMentDialog.show(getSupportFragmentManager(),
						"Location Updates");

			}
			return false;
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {

			try {

				ConnectionResult.B.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);

			} catch (IntentSender.SendIntentException e) {

				e.printStackTrace();
			}

		} else {

			Dialog errordialog = GooglePlayServicesUtil.getErrorDialog(
					ConnectionResult.B.getErrorCode(), this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);
			if (errordialog != null) {

				ShowErrorDialog errFragMentDialog = new ShowErrorDialog();
				errFragMentDialog.setDialog(errordialog);
				errFragMentDialog.show(getSupportFragmentManager(),
						"Location Updates");

			} else {

				Toast.makeText(getApplicationContext(), "Error",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		// location = client.getLastLocation();
		// if (location != null) {
		//
		// Toast.makeText(getApplicationContext(),
		// "Last known: " + location.getLatitude(), Toast.LENGTH_LONG)
		// .show();
		//
		// }

		client.requestLocationUpdates(request, this);

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();

	}

	@SuppressLint("NewApi")
	@Override
	public void onLocationChanged(Location loc) {
		location = loc;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& Geocoder.isPresent()) {

			new GetAddress(this).execute(location);
		}
	}

	@Override
	protected void onStop() {

		if (client.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
			client.removeLocationUpdates(this);
		}
		client.disconnect();
		super.onStop();
	}

	private class GetAddress extends AsyncTask<Location, Void, String> {

		Location loc;
		Context c;

		public GetAddress(Context c) {
			super();
			this.c = c;

		}

		@Override
		protected String doInBackground(Location... param) {
			loc = param[0];
			Geocoder geocoder = new Geocoder(c, Locale.getDefault());
			List<Address> addresses = null;

			try {

				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e1) {
				Log.i("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.i("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}

			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */

				String addressText = String.format(
						"%s, %s, %s, %s, %s, %s, %s, %s, %s",

						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "",

						address.getLocality(),

						address.getCountryName(),

						address.getAdminArea(),

						address.getFeatureName(),

						address.getPremises(),

						address.getSubAdminArea(),

						address.getSubLocality(),

						address.getSubThoroughfare(),

						address.getThoroughfare()

				);
				// Return the text
				return addressText;
			} else {
				return "No address found";
			}

		}

		@Override
		protected void onPostExecute(String address) {
			Log.i("Address", address);
			Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG)
					.show();
		}

	}

}
