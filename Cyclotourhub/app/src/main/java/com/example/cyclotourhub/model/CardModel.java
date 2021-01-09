package com.example.cyclotourhub.model;

import java.io.Serializable;

/**
 * @author Alhaytham Alfeel on 10/10/2016.
 */

public class CardModel implements Serializable {
    private boolean isSelected;
    private int imageId;
    private int titleId;
    private String imageTitle;
    public String imageURL;

    public String name;
    public String address;
    public String email;
    private String image;
    public String mobile;
    public int trackid;
    public String trackname;
    String id;

    public String slat;
    public String slng;
    public String elat;
    public String elng;

    public CardModel(String imageURL) {
        this.imageURL = imageURL;
    }
    public CardModel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public CardModel(boolean isSelected, String imageTitle) {
        this.isSelected = isSelected;
        this.imageTitle = imageTitle;
    }

    public CardModel() {
    }
    public CardModel(String name, String address, String imageURL) {
        this.name = name;
        this.address = address;
        this.imageURL = imageURL;
    }

    public CardModel(int trackid, String trackname, String name, String address) {
        this.trackid = trackid;
        this.trackname = trackname;
        this.name = name;
        this.address = address;
    }
    public CardModel(String id, String trackname, String name, String address) {
        this.id = id;
        this.trackname = trackname;
        this.name = name;
        this.address = address;
    }

    public CardModel(String name, String address, String imageURL, String email, String mobile) {
        this.name = name;
        this.address = address;
        this.imageURL = imageURL;
        this.email = email;
        this.mobile = mobile;
    }
    public CardModel(String name, String address, String slat, String slng, String elat, String elng) {
        this.name = name;
        this.address = address;
        this.slat = slat;
        this.slng = slng;
        this.elat = elat;
        this.elng = elng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTrackid() {
        return trackid;
    }

    public void setTrackid(int trackid) {
        this.trackid = trackid;
    }

    public String getTrackname() {
        return trackname;
    }

    public void setTrackname(String trackname) {
        this.trackname = trackname;
    }

    public String getSlat() {
        return slat;
    }

    public void setSlat(String slat) {
        this.slat = slat;
    }

    public String getSlng() {
        return slng;
    }

    public void setSlng(String slng) {
        this.slng = slng;
    }

    public String getElat() {
        return elat;
    }

    public void setElat(String elat) {
        this.elat = elat;
    }

    public String getElng() {
        return elng;
    }

    public void setElng(String elng) {
        this.elng = elng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    public CardModel(int imageId, int titleId) {
        this.imageId = imageId;
        this.titleId = titleId;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public int getImageId() {
        return imageId;
    }

    public int getTitle() {
        return titleId;
    }

}
