package com.example.admincyclotourhub;

public class Contacto {

    private String numero, correo, direccion, extra, bextra;
    private int img;

    public Contacto(String numero, String correo, String direccion, int img) {
        this.numero = numero;
        this.correo = correo;
        this.direccion = direccion;
        this.img = img;
    }
    public Contacto(String numero, String correo, String direccion, int img, String extra, String bextra) {
        this.numero = numero;
        this.correo = correo;
        this.direccion = direccion;
        this.img = img;
        this.extra = extra;
        this.bextra = bextra;

    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getBextra() {
        return bextra;
    }

    public void setBextra(String bextra) {
        this.bextra = bextra;
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
