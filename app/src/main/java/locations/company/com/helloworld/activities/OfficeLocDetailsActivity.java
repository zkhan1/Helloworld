package locations.company.com.helloworld.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import locations.company.com.helloworld.OfficeDetails;
import locations.company.com.helloworld.R;
import locations.company.com.helloworld.fragments.LocationsFragmentDetails;


public class OfficeLocDetailsActivity extends AppCompatActivity {

    private OfficeDetails officeDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_loc_details);

        // get data from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            officeDetails = (OfficeDetails)getIntent().getSerializableExtra("office");
        }

        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        LocationsFragmentDetails locationsFragmentDetails = (LocationsFragmentDetails) getFragmentManager().findFragmentById(R.id.locationDetailsFragment);
        locationsFragmentDetails.setOfficeDetails(officeDetails);

        GoogleMap googleMap = mf.getMap();
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setAllGesturesEnabled(false);

            Bitmap bitmap = getResizedBitmap(R.drawable.hello_world_logo);

            // add marker to the map
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(officeDetails.getLatitude(), officeDetails.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .title(officeDetails.getName()));


            // move and zoom the map
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
}
