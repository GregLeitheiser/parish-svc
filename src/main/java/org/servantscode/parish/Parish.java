package org.servantscode.parish;

import org.servantscode.commons.Address;
import org.servantscode.commons.Identity;

public class Parish {
    private int id;
    private int orgId;
    private String name;
    private String bannerGuid;
    private String portraitGuid;
    private Address address;
    private String website;
    private String phoneNumber;
    private Identity pastor;

    // ----- Accessors -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrgId() { return orgId; }
    public void setOrgId(int orgId) { this.orgId = orgId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBannerGuid() { return bannerGuid; }
    public void setBannerGuid(String bannerGuid) { this.bannerGuid = bannerGuid; }

    public String getPortraitGuid() { return portraitGuid; }
    public void setPortraitGuid(String portraitGuid) { this.portraitGuid = portraitGuid; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Identity getPastor() { return pastor; }
    public void setPastor(Identity pastor) { this.pastor = pastor; }
}
