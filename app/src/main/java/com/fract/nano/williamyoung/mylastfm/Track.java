package com.fract.nano.williamyoung.mylastfm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Track implements Parcelable {
    private String artist;
    private String album;
    private String trackName;
    private int year;
    private int length;
    private String image;
    private String albumCover;
    private String bandUrl;

    public static Track newInstance(String artist, String album, String trackName, int year, int length, String image, String albumCover, String bandUrl) {
        Track track = new Track();

        track.setArtist(artist);
        track.setAlbum(album);
        track.setTrackName(trackName);
        track.setYear(year);
        track.setLength(length);
        track.setImage(image);
        track.setAlbumCover(albumCover);
        track.setBandUrl(bandUrl);

        return track;
    }

    public String getArtist() { return artist; }

    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; }

    public void setAlbum(String album) { this.album = album; }

    public String getTrackName() { return trackName; }

    public void setTrackName(String trackName) { this.trackName = trackName; }

    public int getYear() { return year; }

    public void setYear(int year) { this.year = year; }

    public int getLength() { return length; }

    public void setLength(int length) { this.length = length; }

    public String getFormattedLength() {
        return String.format(Locale.US, "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(length),
            TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length))
        );
    }

    public String getImage() { return image; }

    public void setImage(String imageUrl) { this.image = imageUrl; }

    public String getAlbumCover() { return albumCover; }

    public void setAlbumCover(String albumCover) { this.albumCover = albumCover; }

    public String getBandUrl() { return bandUrl; }

    public void setBandUrl(String bandUrl) { this.bandUrl = bandUrl; }

    public static Parcelable.Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return Track.newInstance(
                source.readString(),
                source.readString(),
                source.readString(),
                source.readInt(),
                source.readInt(),
                source.readString(),
                source.readString(),
                source.readString()
            );
        }

        @Override
        public Track[] newArray(int size) { return new Track[size]; }
    };

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(artist);
        parcel.writeString(album);
        parcel.writeString(trackName);
        parcel.writeInt(year);
        parcel.writeInt(length);
        parcel.writeString(image);
        parcel.writeString(albumCover);
        parcel.writeString(bandUrl);
    }

    @Override
    public boolean equals(Object track) {
        boolean retVal = false;

        if (track instanceof Track) {
            Track ptr = (Track) track;
            retVal = (ptr.artist.equals(this.artist)) &&
                     (ptr.album.equals(this.album)) &&
                     (ptr.trackName.equals(this.trackName));
        }

        return retVal;
    }
}