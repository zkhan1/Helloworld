package locations.company.com.helloworld;

import java.io.Serializable;

/**
 * Created by Zeeshan on 5/28/15.
 */
public class OfficeDetails implements Comparable<OfficeDetails>, Serializable {
    private String mofficeName;
    private String mofficeAddress1;
    private String mofficeAddress2;
    private String mofficeCity;
    private String mofficeState;
    private String mofficeZip;
    private String mofficePhone;
    private double mofficeDistance;
    private float mOfficeLatitude;
    private float mofficeLongitude;
    private String mofficeImageURL;

    public String getName() {
        return mofficeName;
    }

    public void setName(String name) {
        this.mofficeName = name;
    }

    public String getAddress1() {
        return mofficeAddress1;
    }

    public void setAddress1(String address1) {
        this.mofficeAddress1 = address1;
    }

    public String getAddress2() {
        return mofficeAddress2;
    }

    public void setAddress2(String address2) {
        this.mofficeAddress2 = address2;
    }

    public String getCity() {
        return mofficeCity;
    }

    public void setCity(String city) {
        this.mofficeCity = city;
    }

    public String getState() {
        return mofficeState;
    }

    public void setState(String state) {
        this.mofficeState = state;
    }

    public String getZip() {
        return mofficeZip;
    }

    public void setZip(String zip) {
        this.mofficeZip = zip;
    }

    public String getPhone() {
        return mofficePhone;
    }

    public void setPhone(String phone) {
        this.mofficePhone = phone;
    }

    public double getDistance() {
        return mofficeDistance;
    }

    public void setDistance(double mofficeDistance) {
        this.mofficeDistance = mofficeDistance;
    }

    public float getLatitude() {
        return mOfficeLatitude;
    }

    public void setLatitude(float mOfficeLatitude) {
        this.mOfficeLatitude = mOfficeLatitude;
    }

    public float getLongitude() {
        return mofficeLongitude;
    }

    public void setLongitude(float mofficeLongitude) {
        this.mofficeLongitude = mofficeLongitude;
    }

    public String getImageURL() {
        return mofficeImageURL;
    }

    public void setImageURL(String imageURL) {
        this.mofficeImageURL = imageURL;
    }

    public String getFullAddress() {
        // build address string
        String fullAddress = mofficeAddress1;
        if (mofficeAddress2 != null && mofficeAddress2.length() > 0) {
            fullAddress += "\n" + mofficeAddress2;
        }
        fullAddress += "\n" + mofficeCity + ", " + mofficeState + "  " + mofficeZip;

        return fullAddress;
    }

    public int compareTo(OfficeDetails compareOfficeDetails) {
        float lastKnownDistance = (float) this.mofficeDistance;
        float otherLastKnownDistance = (float) ((OfficeDetails) compareOfficeDetails).getDistance();
        if (lastKnownDistance > otherLastKnownDistance) {
            return 1;
        } else if (lastKnownDistance < otherLastKnownDistance) {
            return -1;
        } else {

            return 0;
        }



    }





}
