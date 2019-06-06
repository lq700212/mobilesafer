package com.itheima.mobilesafer.bean;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-06-05 14:52
 */
public class Contact {
    /**
     * 联系人的姓名
     */
    private String name;

    /**
     * 联系人的电话
     */
    private String phone;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
