package com.example.newentry;

public class AlarmasMedic {
    private String tabletnom;
    private Integer idtablet;
    private String alarmtype;
    private String nombre_user;
    private String password_user;
    private String time;

    public AlarmasMedic(){

    }

    public String getTabletnom() {
        return tabletnom;
    }

    public void setTabletnom(String tabletnom) {
        this.tabletnom = tabletnom;
    }

    public Integer getIdtablet() {
        return idtablet;
    }

    public void setIdtablet(Integer idtablet) {
        this.idtablet = idtablet;
    }

    public String getAlarmtype() {
        return alarmtype;
    }

    public void setAlarmtype(String alarmtype) {
        this.alarmtype = alarmtype;
    }

    public String getNombre_user() {
        return nombre_user;
    }

    public void setNombre_user(String nombre_user) {
        this.nombre_user = nombre_user;
    }

    public String getPassword_user() {
        return password_user;
    }

    public void setPassword_user(String password_user) {
        this.password_user = password_user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
