package com.example.irbis;

public class Pump {
    private String id;
    private String gasStationId;
    private String number;
    private boolean isActive;

    public Pump() {}

    public Pump(String id, String gasStationId, String number, boolean isActive) {
        this.id = id;
        this.gasStationId = gasStationId;
        this.number = number;
        this.isActive = isActive;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getGasStationId() { return gasStationId; }
    public void setGasStationId(String gasStationId) { this.gasStationId = gasStationId; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "Колонка " + number;
    }
}
