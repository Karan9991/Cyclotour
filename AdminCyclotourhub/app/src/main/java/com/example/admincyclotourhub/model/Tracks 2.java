package com.example.admincyclotourhub.model;

public class Tracks {

    String id;
    String name;
    String starting_lat;
    String starting_lng;
    String from;
    String ending_lat;
    String ending_lng;
    String distance;
    String kml_file;
    String status;
    String created_at;
    String updated_at;

    public Tracks() {
    }

    public Tracks(String id, String name, String starting_lat, String starting_lng, String from, String ending_lat, String ending_lng, String distance, String kml_file, String status, String created_at, String updated_at) {
        this.id = id;
        this.name = name;
        this.starting_lat = starting_lat;
        this.starting_lng = starting_lng;
        this.from = from;
        this.ending_lat = ending_lat;
        this.ending_lng = ending_lng;
        this.distance = distance;
        this.kml_file = kml_file;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStarting_lat() {
        return starting_lat;
    }

    public void setStarting_lat(String starting_lat) {
        this.starting_lat = starting_lat;
    }

    public String getStarting_lng() {
        return starting_lng;
    }

    public void setStarting_lng(String starting_lng) {
        this.starting_lng = starting_lng;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getEnding_lat() {
        return ending_lat;
    }

    public void setEnding_lat(String ending_lat) {
        this.ending_lat = ending_lat;
    }

    public String getEnding_lng() {
        return ending_lng;
    }

    public void setEnding_lng(String ending_lng) {
        this.ending_lng = ending_lng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getKml_file() {
        return kml_file;
    }

    public void setKml_file(String kml_file) {
        this.kml_file = kml_file;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
