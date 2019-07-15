package com.njuss.collection.beans;

import java.util.Objects;

/**
 * 零售店POJO
 *
 * @author wangj
 * @since 1.0
 * @see GridConductor
 */
public class Store {
    private String      licenseID;
    private Integer     conductorID;    //foreign key
    private String      storeName;
    private String      storeAddress;
    private String      GPSAddress;
    private String      GPSLongitude;   //经度
    private String      GPSLatitude;    //纬度
    private String      storePicM;
    private String      storePicL;
    private String      storePicR;
    private String      licensePic;

    public String getLicenseID() {
        return licenseID;
    }

    public void setLicenseID(String licenseID) {
        this.licenseID = licenseID;
    }

    public Integer getConductorID() {
        return conductorID;
    }

    public void setConductorID(Integer conductorID) {
        this.conductorID = conductorID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getGPSAddress() {
        return GPSAddress;
    }

    public void setGPSAddress(String GPSAddress) {
        this.GPSAddress = GPSAddress;
    }

    public String getGPSLongitude() {
        return GPSLongitude;
    }

    public void setGPSLongitude(String GPSLongitude) {
        this.GPSLongitude = GPSLongitude;
    }

    public String getGPSLatitude() {
        return GPSLatitude;
    }

    public void setGPSLatitude(String GPSLatitude) {
        this.GPSLatitude = GPSLatitude;
    }

    public String getStorePicM() {
        return storePicM;
    }

    public void setStorePicM(String storePicM) {
        this.storePicM = storePicM;
    }

    public String getStorePicL() {
        return storePicL;
    }

    public void setStorePicL(String storePicL) {
        this.storePicL = storePicL;
    }

    public String getStorePicR() {
        return storePicR;
    }

    public void setStorePicR(String storePicR) {
        this.storePicR = storePicR;
    }

    public String getLicensePic() {
        return licensePic;
    }

    public void setLicensePic(String licensePic) {
        this.licensePic = licensePic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(licenseID, store.licenseID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licenseID);
    }

    @Override
    public String toString() {
        return "Store{" +
                "licenseID='" + licenseID + '\'' +
                ", storeName='" + storeName + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
                ", GPSAddress='" + GPSAddress + '\'' +
                ", GPSLongitude='" + GPSLongitude + '\'' +
                ", GPSLatitude='" + GPSLatitude + '\'' +
                '}';
    }
}
