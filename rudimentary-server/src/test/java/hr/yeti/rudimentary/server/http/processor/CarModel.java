package hr.yeti.rudimentary.server.http.processor;

import hr.yeti.rudimentary.http.content.Model;

public class CarModel extends Model {

    private String manufacturer;

    public CarModel() {
    }

    public CarModel(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

}
