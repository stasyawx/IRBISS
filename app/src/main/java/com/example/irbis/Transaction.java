package com.example.irbis;

import java.util.Date;

public class Transaction {
    private String id;
    private String userId;
    private String gasStationId;
    private String fuelId;
    private String pumpId;
    private double liters;
    private double totalPrice;
    private double bonusPoints;
    private Date transactionDate;

    public Transaction() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getGasStationId() { return gasStationId; }
    public void setGasStationId(String gasStationId) { this.gasStationId = gasStationId; }
    public String getFuelId() { return fuelId; }
    public void setFuelId(String fuelId) { this.fuelId = fuelId; }
    public String getPumpId() { return pumpId; }
    public void setPumpId(String pumpId) { this.pumpId = pumpId; }
    public double getLiters() { return liters; }
    public void setLiters(double liters) { this.liters = liters; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public double getBonusPoints() { return bonusPoints; }
    public void setBonusPoints(double bonusPoints) { this.bonusPoints = bonusPoints; }
    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
}
