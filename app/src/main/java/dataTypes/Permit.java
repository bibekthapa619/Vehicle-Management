package dataTypes;

public class Permit {
    public String vehicleId;
    public String permitType;
    public String permitNum;
    public String permitCost;
    public String issuedDate;
    public String expiryDate;
    public String description;
    public String id;
    public Permit(String id,String vehicleId, String permitType, String permitNum, String permitCost,String expiryDate, String issuedDate, String description) {
        this.vehicleId = vehicleId;
        this.permitType = permitType;
        this.permitNum = permitNum;
        this.permitCost = permitCost;
        this.issuedDate = issuedDate;
        this.description = description;
        this.expiryDate=expiryDate;
        this.id=id;
    }

    public Permit(String id,String issuedDate, String expiryDate,String permitType) {
        this.permitType = permitType;
        this.issuedDate = issuedDate;
        this.id=id;
        this.expiryDate = expiryDate;
    }
}
