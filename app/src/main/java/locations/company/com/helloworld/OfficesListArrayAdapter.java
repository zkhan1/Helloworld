package locations.company.com.helloworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Zeeshan on 5/28/15.
 *
 */
public class OfficesListArrayAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<OfficeDetails> officeDetailsArrayList;

    public OfficesListArrayAdapter(Context context, ArrayList<OfficeDetails> officeDetailsArrayList) {
        this.context = context;
        this.officeDetailsArrayList = officeDetailsArrayList;
    }

    @Override
    public int getCount() {
        return officeDetailsArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return officeDetailsArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.location_list_item, viewGroup, false);
        }

        TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        TextView distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);

        OfficeDetails officeDetails = officeDetailsArrayList.get(i);
        nameTextView.setText(officeDetails.getName());

        addressTextView.setText(officeDetails.getFullAddress());

        if (officeDetails.getDistance() < 0) {
            distanceTextView.setText("N/A");
        } else {
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            distanceTextView.setText(formatter.format(officeDetails.getDistance()));
        }


        return view;
    }
}
