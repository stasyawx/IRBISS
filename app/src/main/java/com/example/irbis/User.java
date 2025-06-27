package com.example.irbis;

import java.util.Date;

public class User {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String birthDate;
    private String gender;
    private int bonusPoints;
    private int roleId;

    public User() {}

    public User(String email, String password, String firstName, String id) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = "";
        this.middleName = "";
        this.birthDate = "";
        this.gender = "";
        this.roleId = 2;
        this.bonusPoints = 0;
        this.id = id;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public int getBonusPoints() { return bonusPoints; }
    public void setBonusPoints(int bonusPoints) { this.bonusPoints = bonusPoints; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
}