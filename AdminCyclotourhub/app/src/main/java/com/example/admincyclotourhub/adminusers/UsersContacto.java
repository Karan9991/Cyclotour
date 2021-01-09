package com.example.admincyclotourhub.adminusers;

public class UsersContacto {

    private String numero, correo, direccion, id;
    private int img;

    public UsersContacto(String numero, String correo, String direccion, int img) {
        this.numero = numero;
        this.correo = correo;
        this.direccion = direccion;
        this.img = img;
    }
    public UsersContacto(String numero, String correo, String direccion, int img, String id) {
        this.numero = numero;
        this.correo = correo;
        this.direccion = direccion;
        this.img = img;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getNumero() {
        return numero;
    }


    public int getImg() {
        return img;
    }
}
