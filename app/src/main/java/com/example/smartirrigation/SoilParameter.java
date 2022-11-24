package com.example.smartirrigation;

public class SoilParameter {
    private String id;
    private String currentDate;
    private String currentTime;
    private String moisture;
    private String ph;
    private String temperature;
    private String latitude;
    private String longitude;

    public SoilParameter(){

    }


    public SoilParameter(String id, String currentDate, String currentTime, String moisture, String ph, String temperature, String latitude, String longitude) {
        this.id = id;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.moisture = moisture;
        this.ph = ph;
        this.temperature = temperature;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getMoisture() {
        return moisture;
    }

    public void setMoisture(String moisture) {
        this.moisture = moisture;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
