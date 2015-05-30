package locations.company.com.helloworld.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import locations.company.com.helloworld.JSONResponse;
import locations.company.com.helloworld.OfficeDetails;
import locations.company.com.helloworld.R;
import locations.company.com.helloworld.fragments.LocationsFragmentMain;






public class MainActivity extends AppCompatActivity implements JSONResponse {

    private ArrayList<OfficeDetails> officeDetailsArrayList = new ArrayList<>();
    private LocationsFragmentMain locationsFragment;
    private JSONResponse jsonResponse = (JSONResponse)this;
    private Location userLocation = null;
    private GoogleMap googleMap;
    private ProgressDialog progressDialog;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        locationsFragment = (LocationsFragmentMain)
                getFragmentManager().findFragmentById(R.id.locationsFragment);

        if (savedInstanceState == null) {
            googleMap = mf.getMap();
            if (googleMap != null) {
                //googleMap.setMyLocationEnabled(true);

                // Get LocationManager object from System Service LOCATION_SERVICE
                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);


                // Get the name of the best provider
                String provider = LocationManager.NETWORK_PROVIDER;


                // Get Current Location
                userLocation = locationManager.getLastKnownLocation(provider);


                googleMap.setOnMyLocationChangeListener
                        (new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        userLocation = arg0;
                    }
                });
            }
            new RequestAsynkJasonTask().execute("http://www.helloworld.com/helloworld_locations.json");
        }
    }


    public class RequestAsynkJasonTask extends AsyncTask<String, String, String> {
        private boolean isSuccess;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Downloading Locations");
            progressDialog.setMessage("Please wait while office locations are downloaded");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... uri) {
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpResponse response;
            String responseString;

            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    isSuccess = true;
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    isSuccess = false;
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                isSuccess = false;
                responseString = "Please ensure that you have internet connectivity and retry.";
            } catch (IOException e) {
                isSuccess = false;
                responseString = "Please ensure that you have internet connectivity and retry.";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (isSuccess) {
                jsonResponse.SuccessfulJsonResponse(result);
            } else {
                jsonResponse.FailedJsonResponse(result);
            }
        }
    }


    @Override
    public void SuccessfulJsonResponse(String responseString) {

        // Save data in case of no internet/wifi or location connectivity
        SharedPreferences prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        // save the user list to pref
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LocationsList", responseString);
        editor.apply();
       this.parseJsonString(responseString);

        if (userLocation != null) {
            // sort by distance
            Collections.sort(officeDetailsArrayList);
        }

        this.locationsFragment.setOfficeDetailsArrayList(officeDetailsArrayList);
        this.updateGoogleMap();
        this.dismissDialog();
    }

    @Override
    public void FailedJsonResponse(String responseString) {
        this.dismissDialog();

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Sorry");
        alertDialog.setMessage(responseString);
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        SharedPreferences prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String jsonString = prefs.getString("LocationsList", null);
        this.parseJsonString(jsonString);
        this.locationsFragment.setOfficeDetailsArrayList(officeDetailsArrayList);
        this.updateGoogleMap();

    }


    private void parseJsonString(String jsonString) {
        try {
            JSONObject jObject = new JSONObject(jsonString);
            JSONArray jsonArray = jObject.getJSONArray("locations");

            for (int i=0; i < jsonArray.length(); i++)
            {
                JSONObject locationObject = jsonArray.getJSONObject(i);
                OfficeDetails officeDetails = new OfficeDetails();
                officeDetails.setAddress1(locationObject.getString("address"));
                officeDetails.setAddress2(locationObject.getString("address2"));
                officeDetails.setCity(locationObject.getString("city"));
                officeDetails.setState(locationObject.getString("state"));
                officeDetails.setZip(locationObject.getString("zip_postal_code"));
                officeDetails.setImageURL(locationObject.getString("office_image"));
                officeDetails.setLatitude((float)locationObject.getDouble("latitude"));
                officeDetails.setLongitude((float) locationObject.getDouble("longitude"));
                officeDetails.setName(locationObject.getString("name"));
                officeDetails.setPhone(locationObject.getString("phone"));

                if (userLocation != null) {
                    Location officeLocation = new Location("");
                    officeLocation.setLatitude(officeDetails.getLatitude());
                    officeLocation.setLongitude(officeDetails.getLongitude());
                    officeDetails.setDistance((float)(userLocation.distanceTo(officeLocation)/1609.344));
                }

                officeDetailsArrayList.add(officeDetails);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void updateGoogleMap() {
        if (googleMap != null) {
            ArrayList<Marker> markerOptionsArrayList = new ArrayList<>();

            for (OfficeDetails officeDetails : officeDetailsArrayList) {
                Bitmap bitmap = getResizedBitmap(R.drawable.hello_world_logo);

                // add markers to the map
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(officeDetails.getLatitude(), officeDetails.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .title(officeDetails.getName()));

                markerOptionsArrayList.add(marker);
            }

            // calculate bounds of all markers
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markerOptionsArrayList) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            // move the map
            int padding = 10; // offset from edges of the map in pixels
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.moveCamera(cameraUpdate);

            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    selectLocation(marker.getPosition());
                }
            });
        }
    }

    private Bitmap getResizedBitmap (int id) {
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), id);
        float targetWidth = 100;
        float scale = targetWidth/(float)originalBitmap.getWidth();

        int newWidth = Math.round((float)originalBitmap.getWidth() * scale);
        int newHeight = Math.round((float)originalBitmap.getHeight() * scale);
        return Bitmap.createScaledBitmap(originalBitmap,newWidth, newHeight, true);
    }

    public void selectLocation(LatLng position) {
        OfficeDetails selectedOfficeDetails = null;

        // get office object from LatLng
        for (OfficeDetails officeDetails : officeDetailsArrayList) {
            if (officeDetails.getLatitude() == position.latitude && officeDetails.getLongitude() == position.longitude) {
                selectedOfficeDetails = officeDetails;
            }
        }

        this.selectLocation(selectedOfficeDetails);
    }

    public void selectLocation(OfficeDetails officeDetails) {
        Intent intent = new Intent(this, OfficeLocDetailsActivity.class);
        intent.putExtra("office", officeDetails);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_locations, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dismissDialog() {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }



}
