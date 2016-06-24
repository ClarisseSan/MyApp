package com.gerry.myapp.movies.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by gerry on 19/5/16.
 */
public class Movie implements Parcelable {
    long movie_id;
    String movie_name;
    String movie_image;
    String movie_overview;
    String movie_date;
    String movie_vote;
    String movie_duration;
    List<Trailer> trailerList;
    List<Reviews> reviewsList;


    public Movie(long movie_id, String movie_name, String movie_image, String movie_overview, String movie_date, String movie_vote, String movie_duration, List<Trailer> trailerList, List<Reviews> reviewsList) {
        this.movie_id = movie_id;
        this.movie_name = movie_name;
        this.movie_image = movie_image;
        this.movie_overview = movie_overview;
        this.movie_date = movie_date;
        this.movie_vote = movie_vote;
        this.movie_duration = movie_duration;
        this.trailerList = trailerList;
        this.reviewsList = reviewsList;
    }

    protected Movie(Parcel in) {
        movie_id = in.readLong();
        movie_name = in.readString();
        movie_image = in.readString();
        movie_overview = in.readString();
        movie_date = in.readString();
        movie_duration = in.readString();
        trailerList = in.createTypedArrayList(Trailer.CREATOR);
        reviewsList = in.createTypedArrayList(Reviews.CREATOR);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void setMovie_id(long movie_id) {
        this.movie_id = movie_id;
    }

    public static Creator<Movie> getCREATOR() {
        return CREATOR;
    }



    public void setTrailerList(List<Trailer> trailerList) {

        this.trailerList = trailerList;
    }

    public List<Trailer> getTrailerList() {
        return trailerList;
    }

    public List<Reviews> getReviewsList() {
        return reviewsList;
    }

    public void setReviewsList(List<Reviews> reviewsList) {
        this.reviewsList = reviewsList;
    }

    public void setMovie_duration(String movie_duration) {

        this.movie_duration = movie_duration;
    }

    public void setMovie_vote(String movie_vote) {

        this.movie_vote = movie_vote;
    }

    public void setMovie_date(String movie_date) {

        this.movie_date = movie_date;
    }

    public void setMovie_overview(String movie_overview) {

        this.movie_overview = movie_overview;
    }

    public void setMovie_image(String movie_image) {

        this.movie_image = movie_image;
    }

    public void setMovie_name(String movie_name) {

        this.movie_name = movie_name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movie_id);
        dest.writeString(movie_name);
        dest.writeString(movie_image);
        dest.writeString(movie_overview);

        dest.writeString(movie_date);
        dest.writeString(movie_duration);
        dest.writeTypedList(trailerList);
    }

    public String getMovie_name() {
        return movie_name;
    }

    public String getMovie_date() {
        return movie_date;
    }

    public String getMovie_overview() {
        return movie_overview;
    }

    public long getMovie_id() {
        return movie_id;
    }

    public String getMovie_vote() {
        return movie_vote;
    }

    public String getMovie_image() {
        return movie_image;
    }

    public String getMovie_duration() {
        return movie_duration;
    }

    public List<Trailer> getMovie_trailerList(){
        return trailerList;
    }
}
