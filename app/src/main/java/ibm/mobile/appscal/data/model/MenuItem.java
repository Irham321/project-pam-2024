package ibm.mobile.appscal.data.model;

public class MenuItem {
    private String id;
    private String namaMenu;
    private int kalori;
    private int perGram;
    private String linkGambar;

    public MenuItem() {
    }

    public MenuItem(String id, String namaMenu, int kalori, int perGram, String linkGambar) {
        this.id = id;
        this.namaMenu = namaMenu;
        this.kalori = kalori;
        this.perGram = perGram;
        this.linkGambar = linkGambar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public int getKalori() {
        return kalori;
    }

    public void setKalori(int kalori) {
        this.kalori = kalori;
    }

    public int getPerGram() {
        return perGram;
    }

    public void setPerGram(int perGram) {
        this.perGram = perGram;
    }

    public String getLinkGambar() {
        return linkGambar;
    }

    public void setLinkGambar(String linkGambar) {
        this.linkGambar = linkGambar;
    }
}
