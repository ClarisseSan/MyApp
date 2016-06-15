package com.gerry.myapp.movies.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gerry on 26/5/16.
 */
public class Trailer implements Parcelable {

    String trailer_number;
    String trailer_url;


    public Trailer(String trailer_number, String trailer_url){
        this.trailer_number = trailer_number;
        this.trailer_url = trailer_url;
    }

    protected Trailer(Parcel in) {
        trailer_number = in.readString();
        trailer_number = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailer_number);
        dest.writeString(trailer_url);
    }

    public String getTrailerNumber() {
        return trailer_number;
    }

    public String getTrailerUrl() {
        return trailer_url;
    }

}
