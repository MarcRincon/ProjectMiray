package com.example.newentry;

public class DeviceManager {
    private String Nom_tablet;
    private Integer ID_tablet;
    private String App_status;
    private String Ultima_Accion;
    private String Last_check;
    private Integer BatteryLvl;
    private String IsBatteryCharging;

    public DeviceManager(){

    }


    public String getNom_tablet() {
        return Nom_tablet;
    }

    public void setNom_tablet(String nom_tablet) {
        this.Nom_tablet = nom_tablet;
    }

    public Integer getID_tablet() {
        return ID_tablet;
    }

    public void setID_tablet(Integer ID_tablet) {
        this.ID_tablet = ID_tablet;
    }

    public String getApp_status() {
        return App_status;
    }

    public void setApp_status(String app_status) {
        this.App_status = app_status;
    }

    public String getUltima_Accion() {
        return Ultima_Accion;
    }

    public void setUltima_Accion(String ultima_Accion) {
        this.Ultima_Accion = ultima_Accion;
    }

    public String getLast_check() {
        return Last_check;
    }

    public void setLast_check(String last_check) {
        this.Last_check = last_check;
    }

    public Integer getBatteryLvl() {
        return BatteryLvl;
    }

    public void setBatteryLvl(Integer batteryLvl) {
        BatteryLvl = batteryLvl;
    }

    public String getIsBatteryCharging() {
        return IsBatteryCharging;
    }

    public void setIsBatteryCharging(String isBatteryCharging) {
        IsBatteryCharging = isBatteryCharging;
    }
}
