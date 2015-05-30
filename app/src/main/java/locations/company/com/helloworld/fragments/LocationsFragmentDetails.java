package locations.company.com.helloworld.fragments;


import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.text.DecimalFormat;

import locations.company.com.helloworld.OfficeDetails;
import locations.company.com.helloworld.R;


public class LocationsFragmentDetails extends Fragment {

    private OfficeDetails officeDetails;
    private TextView nameTextView;
    private TextView distanceTextView;
    private TextView addressTextView;
    private Button phoneButton;
    private ImageView imageView;

    public void setOfficeDetails(OfficeDetails officeDetails) {
        this.officeDetails = officeDetails;

        nameTextView.setText(officeDetails.getName());
        if (officeDetails.getDistance() < 0) {
            distanceTextView.setVisibility(View.GONE);
        } else {
            distanceTextView.setVisibility(View.VISIBLE);
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            distanceTextView.setText(formatter.format(officeDetails.getDistance()) + " miles");
        }

        // load image asynchronously
        new DownloadImageTask(imageView).execute(officeDetails.getImageURL());

        addressTextView.setText(officeDetails.getFullAddress());
        phoneButton.setText(officeDetails.getPhone());
    }

    public LocationsFragmentDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_details, container, false);

        nameTextView = (TextView)view.findViewById(R.id.nameTextView);
        distanceTextView = (TextView)view.findViewById(R.id.distanceTextView);
        addressTextView = (TextView)view.findViewById(R.id.addressTextView);
        phoneButton = (Button)view.findViewById(R.id.phoneButton);
        imageView = (ImageView)view.findViewById(R.id.imageView);
        Button mapButton = (Button)view.findViewById(R.id.mapButton);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Get Current Location
        final Location userLocation = locationManager.getLastKnownLocation(provider);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri;

                if (userLocation != null) {
                    uri = Uri.parse("http://maps.google.com/maps?saddr="+userLocation.getLatitude()+","+userLocation.getLongitude()+
                            "&daddr="+ officeDetails.getLatitude()+","+ officeDetails.getLongitude());
                } else {
                    uri = Uri.parse("http://maps.google.com/maps?daddr="+ officeDetails.getLatitude()+","+ officeDetails.getLongitude());
                }
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + officeDetails.getPhone()));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                    Toast.makeText(getActivity(), "This device cannot make phone calls.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
