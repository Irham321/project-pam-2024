package ibm.mobile.appscal.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class User {
    private String uid;       // Firebase Key
    private String username;
    private String email;
    private String password;
    private String nama;
    private String jenisKelamin;
    private Float tinggiBadan;
    private Float beratBadan;
    private int umur;

    // Constructor kosong (diperlukan untuk Firebase)
    public User() {}

    public User(String uid, String username, String email, String password, String nama, String jenisKelamin, Float tinggiBadan, Float beratBadan, int umur) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.tinggiBadan = tinggiBadan;
        this.beratBadan = beratBadan;
        this.umur = umur;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public Float getTinggiBadan() {
        return tinggiBadan;
    }

    public void setTinggiBadan(Float tinggiBadan) {
        this.tinggiBadan = tinggiBadan;
    }

    public Float getBeratBadan() {
        return beratBadan;
    }

    public void setBeratBadan(Float beratBadan) {
        this.beratBadan = beratBadan;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }
}
