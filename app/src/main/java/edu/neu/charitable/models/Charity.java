package edu.neu.charitable.models;

public class Charity {
    public int activeProjects;
    public String addressLine1;
    public String addressLine2;
    public String city;
    public String country;
    public int id;
    public String countryCode;
    public String logoUrl;
    public String mission;
    public String name;
    public String postal;
    public String state;
    public int totalProjects;
    public String url;
    public String ein;

    public Charity() {

    }

    public Charity(CharityString c) {

        try {
            this.activeProjects = Integer.parseInt(c.activeProjects);
        } catch (Exception e) {
            this.activeProjects = 0;
        }

        this.addressLine1 = c.addressLine1;
        this.addressLine2 = c.addressLine2;
        this.city = c.city;
        this.country = c.country;

        try {
            this.id = Integer.parseInt(c.id);
        } catch (Exception e) {
            this.id = -1;
        }



        this.countryCode = c.countryCode;
        this.logoUrl = c.logoUrl;
        this.mission = c.mission;
        this.name = c.name;
        this.postal = c.postal;
        this.state = c.state;

        try {
            this.totalProjects = Integer.parseInt(c.totalProjects);
        } catch (Exception e) {
            this.totalProjects = 0;
        }


        this.url = c.url;
        this.ein = c.ein;

    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Charity)) {
            return false;
        }

        Charity c = (Charity) o;

        return (c.id == this.id && c.name.equals(this.name));

    }


}
