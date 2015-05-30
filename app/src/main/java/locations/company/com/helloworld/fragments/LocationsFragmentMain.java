package locations.company.com.helloworld.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import locations.company.com.helloworld.OfficeDetails;
import locations.company.com.helloworld.OfficesListArrayAdapter;
import locations.company.com.helloworld.R;
import locations.company.com.helloworld.activities.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationsFragmentMain extends Fragment {

    private ListView locationsListView;
    private ArrayList<OfficeDetails> officeDetailsArrayList;

    public void setOfficeDetailsArrayList(ArrayList<OfficeDetails> officeDetailsArrayList) {
        this.officeDetailsArrayList = officeDetailsArrayList;

        if (officeDetailsArrayList != null && officeDetailsArrayList.size() > 0) {
            final OfficesListArrayAdapter adapter = new OfficesListArrayAdapter(getActivity(), officeDetailsArrayList);
            locationsListView.setAdapter(adapter);
        }
    }

    public LocationsFragmentMain() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_locations_array, container, false);

        locationsListView = (ListView) rootView.findViewById(R.id.locationsListView);
        locationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity) getActivity()).selectLocation(officeDetailsArrayList.get(i));
            }
        });

        if (officeDetailsArrayList != null && officeDetailsArrayList.size() > 0) {
            final OfficesListArrayAdapter adapter = new OfficesListArrayAdapter(getActivity(), officeDetailsArrayList);
            locationsListView.setAdapter(adapter);
        }

        return rootView;
    }


}
