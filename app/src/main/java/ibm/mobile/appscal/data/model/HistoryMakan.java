package ibm.mobile.appscal.data.model;

import java.util.Comparator;

public class HistoryMakan {
    private String id;
    private String menuId;
    private String userUid;
    private int waktuMakan;
    private int gramMenu;
    private Long timeStampHistory;


    public HistoryMakan() {
    }

    public HistoryMakan(String id, String menuId, String userUid, int waktuMakan, int gramMenu, Long timeStampHistory) {
        this.id = id;
        this.menuId = menuId;
        this.userUid = userUid;
        this.waktuMakan = waktuMakan;
        this.gramMenu = gramMenu;
        this.timeStampHistory = timeStampHistory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public int getGramMenu() {
        return gramMenu;
    }

    public void setGramMenu(int gramMenu) {
        this.gramMenu = gramMenu;
    }

    public Long getTimeStampHistory() {
        return timeStampHistory;
    }

    public void setTimeStampHistory(Long timeStampHistory) {
        this.timeStampHistory = timeStampHistory;
    }

    public int getWaktuMakan() {
        return waktuMakan;
    }

    public void setWaktuMakan(int waktuMakan) {
        this.waktuMakan = waktuMakan;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
