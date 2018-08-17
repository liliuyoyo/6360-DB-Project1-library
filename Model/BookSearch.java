package Model;

public class BookSearch {

    private String fieldTxt;

    public BookSearch(String fieldTxt) {
        super();
        this.fieldTxt = fieldTxt;
    }

    public BookSearch() {
        super();
    }

    public String getFieldTxt() {
        return fieldTxt;
    }

    public void setFieldTxt(String fieldTxt) {
        this.fieldTxt = fieldTxt;
    }


}
