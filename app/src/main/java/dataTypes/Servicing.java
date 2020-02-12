package dataTypes;

public class Servicing
{
    public   String vehicleId;
    public   String serviceType;
    public   String serviceCenter;
    public   String odometerReading;
    public   String description;
    public   String date;
    public   String contactNum;
    public   String amount;
    public   String id;
    public Servicing(String id,String vehicleId, String serviceType, String serviceCenter, String odometerReading, String description, String date, String contactNum, String amount) {
        this.vehicleId = vehicleId;
        this.serviceType = serviceType;
        this.serviceCenter = serviceCenter;
        this.odometerReading = odometerReading;
        this.description = description;
        this.date = date;
        this.contactNum = contactNum;
        this.amount = amount;
        this.id=id;
    }

    public Servicing(String id,String date,String serviceType) {
        this.serviceType = serviceType;
        this.date = date;
        this.id=id;
    }
}