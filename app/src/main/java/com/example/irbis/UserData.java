package com.example.irbis;

public class UserData {
    private static UserData instance;

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String birthDate;
    private String gender;
    private int bonusPoints;
    private int roleId;

    private UserData() {}

    public static synchronized UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    public void setUserData(String id, String email, String firstName,
                            String lastName, String middleName,
                            String birthDate, String gender,
                            int bonusPoints, int roleId) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.bonusPoints = bonusPoints;
        this.roleId = roleId;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMiddleName() { return middleName; }
    public String getBirthDate() { return birthDate; }
    public String getGender() { return gender; }
    public int getBonusPoints() { return bonusPoints; }
    public int getRoleId() { return roleId; }

    public void clearUserData() {
        this.id = null;
        this.email = null;
        this.firstName = null;
        this.lastName = null;
        this.middleName = null;
        this.birthDate = null;
        this.gender = null;
        this.bonusPoints = 0;
        this.roleId = 2;
    }
}