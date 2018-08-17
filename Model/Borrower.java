package Model;

public class Borrower {

    private String card_id;
    private String ssn;
    private String bname;
    private String address;
    private String phone;

    public Borrower() {
        super();
    }

    public Borrower(String card_id,String ssn, String bname, String address, String phone) {
        super();
        this.card_id = card_id;
        this.ssn = ssn;
        this.bname = bname;
        this.address = address;
        this.phone = phone;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
