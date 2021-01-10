package com.example.cyclotourhub.model;

public class AddTracks {

    int id;
    String trackName;
    String source;
    String dest;

    public AddTracks(int id, String trackName, String source, String dest) {
        this.id = id;
        this.trackName = trackName;
        this.source = source;
        this.dest = dest;
    }

    public AddTracks() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }
}
