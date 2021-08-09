package edu.neu.charitable.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "activeProjects", "addressLine1", "addressLine2",
        "city", "country", "id", "countryCode", "logoUrl",
        "mission", "name", "postal", "state", "totalProjects", "url", "ein"})
public class CharityString {
    @JsonProperty("activeProjects")
    public String activeProjects;
    @JsonProperty("addressLine1")
    public String addressLine1;
    @JsonProperty("addressLine2")
    public String addressLine2;
    @JsonProperty("city")
    public String city;
    @JsonProperty("country")
    public String country;
    @JsonProperty("id")
    public String id;
    @JsonProperty("countryCode")
    public String countryCode;
    @JsonProperty("logoUrl")
    public String logoUrl;
    @JsonProperty("mission")
    public String mission;
    @JsonProperty("name")
    public String name;
    @JsonProperty("postal")
    public String postal;
    @JsonProperty("state")
    public String state;
    @JsonProperty("totalProjects")
    public String totalProjects;
    @JsonProperty("url")
    public String url;
    @JsonProperty("ein")
    public String ein;


    public CharityString() {

    }
    public CharityString(String activeProjects, String addressLine1, String addressLine2, String city,
                         String country, String id, String countryCode, String logoUrl,
                         String mission, String name, String postal, String state,
                         String totalProjects, String url, String ein) {

        this.activeProjects = activeProjects;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.country = country;
        this.id = id;
        this.countryCode = countryCode;
        this.logoUrl = logoUrl;
        this.mission = mission;
        this.name = name;
        this.postal = postal;
        this.state = state;
        this.totalProjects = totalProjects;
        this.url = url;
        this.ein = ein;
    }


}
