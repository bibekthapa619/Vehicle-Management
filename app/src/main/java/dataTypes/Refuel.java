package dataTypes;

public class Refuel {
    public String vehicleId;
    public String date;
    public String fuelStation;
    public String odometerReading;
    public String quantity;
    public String totalAmount;
    public String rate;
    public String id;
    public Refuel(String id,String vehicleId, String date, String fuelStation, String odometerReading, String quantity, String totalAmount, String rate) {
        this.vehicleId = vehicleId;
        this.date = date;
        this.fuelStation = fuelStation;
        this.odometerReading = odometerReading;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.rate = rate;
        this.id=id;
    }

    public Refuel(String id,String date, String t) {
        this.date = date;
        this.totalAmount=t;
        this.id=id;
    }
}
