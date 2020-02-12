package dataTypes;

public class Accident {
    public String vehicleId;
    public String amount;
    public String date;
    public String description;
    public String driver;
    public String odometerReading;
    public String place;
    public String id;

    public Accident(String id,String vehicleId, String amount, String date, String description, String driver, String odometerReading, String place) {
            this.vehicleId = vehicleId;
            this.amount = amount;
            this.date = date;
            this.description = description;
            this.driver = driver;
            this.odometerReading = odometerReading;
            this.place = place;
            this.id=id;
        }

    public Accident(String id,String date, String place) {
        this.date = date;
        this.place = place;
        this.id=id;
    }
}
