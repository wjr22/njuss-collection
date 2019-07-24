package com.njuss.collection;
public class store {

    private String storename;
    private String address;


    private int licenseID;
    private String locate;



    public store() {
        super();
    }




    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address= address;
    }



    public int getLicenseID() {
        return licenseID;
    }

    public void setLicenseID(int licenseID) {
        this.licenseID = licenseID;
    }

    public String getLocate() {
        return locate;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }


}