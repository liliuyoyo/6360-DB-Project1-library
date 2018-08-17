package Model;

public class Loan {
    private String Loan_id;
    private String Loan_isbn;
    private String Card_id;
    private String Date_out;
    private String Due_date;
    private String Date_in;
    private float Fine_amt;
    private boolean Paid;

    public Loan(String loan_id, String loan_isbn, String card_id, String date_out, String due_date) {
        Loan_id = loan_id;
        Loan_isbn = loan_isbn;
        Card_id = card_id;
        Date_out = date_out;
        Due_date = due_date;
        Date_in = "1900-01-01";
        Fine_amt = 0;
        Paid = false;
    }

    public float getFine_amt() {
        return Fine_amt;
    }

    public void setFine_amt(float fine_amt) {
        Fine_amt = fine_amt;
    }

    public boolean isPaid() {
        return Paid;
    }

    public void setPaid(boolean paid) {
        Paid = paid;
    }

    public String getLoan_id() {
        return Loan_id;
    }

    public void setLoan_id(String loan_id) {
        Loan_id = loan_id;
    }

    public String getLoan_isbn() {
        return Loan_isbn;
    }

    public void setLoan_isbn(String loan_isbn) {
        Loan_isbn = loan_isbn;
    }

    public String getCard_id() {
        return Card_id;
    }

    public void setCard_id(String card_id) {
        Card_id = card_id;
    }

    public String getDate_out() {
        return Date_out;
    }

    public void setDate_out(String date_out) {
        Date_out = date_out;
    }

    public String getDue_date() {
        return Due_date;
    }

    public void setDue_date(String due_date) {
        Due_date = due_date;
    }

    public String getDate_in() {
        return Date_in;
    }

    public void setDate_in(String date_in) {
        Date_in = date_in;
    }
}
