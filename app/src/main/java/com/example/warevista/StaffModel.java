package com.example.warevista;

public class StaffModel {

    private String userId;
    private String password;
    private String warehouse;
    private String role;
    private String mobile;
    private String createDate;
    private String staffName;

    public StaffModel(
            String userId,
            String password,
            String warehouse,
            String role,
            String mobile,
            String createDate,
            String staffName) {

        this.userId = userId;
        this.password = password;
        this.warehouse = warehouse;
        this.role = role;
        this.mobile = mobile;
        this.createDate = createDate;
        this.staffName = staffName;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public String getRole() {
        return role;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
}