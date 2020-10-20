package com.mariem.Springboot_hibernate_shoppingCart.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="accounts")
public class Account implements Serializable {
    private static final long serialVersionUID = -2054386655979281969L;


    public static final String ROLE_MANAGER="MANAGER" ;
    public static final String ROLE_EMPLOYEE="EMPLOYEE" ;
    @Id
    @Column(name = "user_name",length=20,nullable = false)
    private String userName;

    @Column(name = "encrypted_password",length=20,nullable = false)
    private String encryptedPassword;

    @Column(name = "active",length=20,nullable = false)
    private Boolean active;

    @Column(name = "user_role",length=20,nullable = false)
    private String userRole;



    //methods
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active){
        this.active=active ;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Account() {}

    @Override
    public String toString() {
        return "[" + this.userName + "," + this.encryptedPassword + "," + this.userRole + "]";
    }
}
