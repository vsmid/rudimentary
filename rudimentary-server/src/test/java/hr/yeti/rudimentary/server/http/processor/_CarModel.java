package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.content.Model;

public class _CarModel extends Model {

    private String manufacturer;

    public _CarModel() {
    }

    public _CarModel(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

}
