package com.study.molingran.qujixue.tools.bomb;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * @author MoLingran
 * @create 2019/02/19 19:06
 */
public class User extends BmobUser {

    private String nickname;

    private Integer age;

    private Integer sex;

    private BmobGeoPoint address;

    private BmobFile avatar;


    public String getNickname() {
        return nickname;
    }

    public User setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }
    public Integer getAge() {
        return age;
    }

    public User setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Integer getSex() {
        return sex;
    }

    public User setSex(Integer gender) {
        this.sex = gender;
        return this;
    }

    public BmobGeoPoint getAddress() {
        return address;
    }

    public User setAddress(BmobGeoPoint address) {
        this.address = address;
        return this;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public User setAvatar(BmobFile avatar) {
        this.avatar = avatar;
        return this;
    }
}
