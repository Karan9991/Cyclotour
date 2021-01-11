package com.example.admincyclotourhub;

public class Users {

    String id;
    String name;
    String username;
    String email;
    String status;
    String email_verified_at;
    String created_at;
    String updated_at;
    String email_verified;
    String email_verification_token;

    public Users() {
    }

    public Users(String id, String name, String username, String email, String status, String email_verified_at, String created_at, String updated_at, String email_verified, String email_verification_token) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.status = status;
        this.email_verified_at = email_verified_at;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.email_verified = email_verified;
        this.email_verification_token = email_verification_token;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail_verified_at() {
        return email_verified_at;
    }

    public void setEmail_verified_at(String email_verified_at) {
        this.email_verified_at = email_verified_at;
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

    public String getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(String email_verified) {
        this.email_verified = email_verified;
    }

    public String getEmail_verification_token() {
        return email_verification_token;
    }

    public void setEmail_verification_token(String email_verification_token) {
        this.email_verification_token = email_verification_token;
    }
}
