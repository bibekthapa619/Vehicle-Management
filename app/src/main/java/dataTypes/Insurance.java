package dataTypes;

public class Insurance {

    public String vehicleId;
    public String issuedDate;
    public String expiryDate;
    public String insuranceType;
    public String insuranceNum;
    public String insuranceCost;
    public String description;
    public String id;
    public Insurance(String id,String vehicleId, String issuedDate, String expiryDate, String insuranceType, String insuranceNum, String insuranceCost, String description) {
        this.vehicleId = vehicleId;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
        this.insuranceType = insuranceType;
        this.insuranceNum = insuranceNum;
        this.insuranceCost = insuranceCost;
        this.description = description;
        this.id=id;
    }

    public Insurance(String id,String issuedDate, String expiryDate, String insuranceType) {
        this.issuedDate = issuedDate;
        this.id=id;
        this.expiryDate = expiryDate;
        this.insuranceType = insuranceType;
    }
}
