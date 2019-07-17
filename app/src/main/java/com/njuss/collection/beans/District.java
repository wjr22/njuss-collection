package com.njuss.collection.beans;

import java.util.Objects;

/**
 * 行政区域划分
 * @author wangj
 * @
 */
public class District {
    private Integer     distictLevel;
    private String      districtCode;
    private String      districtName;
    private String      parentCode;
    private String      districtFullname;

    public Integer getDistictLevel() {
        return distictLevel;
    }

    public void setDistictLevel(Integer distictLevel) {
        this.distictLevel = distictLevel;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getDistrictFullname() {
        return districtFullname;
    }

    public void setDistrictFullname(String districtFullname) {
        this.districtFullname = districtFullname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        District district = (District) o;
        return Objects.equals(districtCode, district.districtCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(districtCode);
    }
}
