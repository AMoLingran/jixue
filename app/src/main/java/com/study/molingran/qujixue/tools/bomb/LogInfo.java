package com.study.molingran.qujixue.tools.bomb;

import cn.bmob.v3.BmobObject;

/**
 * @author Molingran
 * @create 2019/02/19 15:00
 */
public class LogInfo extends BmobObject {
    private String name;


    private String address;
    private String IMIE;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIMIE() {
        return IMIE;
    }

    public void setIMIE(String IMIE) {
        this.IMIE = IMIE;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
