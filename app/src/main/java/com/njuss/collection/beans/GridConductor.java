package com.njuss.collection.beans;

import java.util.Objects;

/**
 * Grid Conductor JOPO
 * 网格员，与数据库字段一一对应
 * @author wangj
 * @since 1.0
 */
public class GridConductor {
    private Integer     conductorID;
    private String      conductorMobile;
    private String      conductorName;
    private String      conductorPwd;
    private String      DistrictCode;

    public Integer getConductorID() {
        return conductorID;
    }

    public void setConductorID(Integer conductorID) {
        this.conductorID = conductorID;
    }

    public String getConductorMobile() {
        return conductorMobile;
    }

    public void setConductorMobile(String conductorMobile) {
        this.conductorMobile = conductorMobile;
    }

    public String getConductorName() {
        return conductorName;
    }

    public void setConductorName(String conductorName) {
        this.conductorName = conductorName;
    }

    public String getConductorPwd() {
        return conductorPwd;
    }

    public void setConductorPwd(String conductorPwd) {
        this.conductorPwd = conductorPwd;
    }

    public String getDistrictCode() {
        return DistrictCode;
    }

    public void setDistrictCode(String DistrictCode) {
        this.DistrictCode = DistrictCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridConductor that = (GridConductor) o;
        return Objects.equals(conductorMobile, that.conductorMobile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conductorMobile);
    }

    @Override
    public String toString() {
        return "GridConductor{" +
                "conductorID=" + conductorID +
                ", conductorMobile='" + conductorMobile + '\'' +
                ", conductorName='" + conductorName + '\'' +
                ", conductorPwd='" + conductorPwd + '\'' +
                ", DistrictCode='" + DistrictCode + '\'' +
                '}';
    }
}
