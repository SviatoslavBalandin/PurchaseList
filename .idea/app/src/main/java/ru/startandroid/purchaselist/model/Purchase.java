package ru.startandroid.purchaselist.model;

/**
 * Created by user on 04/02/2018.
 */

public class Purchase {
    private String id;
    private String title;
    private String price;
    private String amount;
    private boolean isChecked;

    public Purchase(String id, String title, String price, String amount, boolean isChecked){
        this.id = id;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.isChecked = isChecked;
    }
    public Purchase(){}

    public String getAmount() {

        return amount;
    }

    public String getId() {
        return id;
    }

    public String getPrice() {

        return price;
    }

    public String getTitle() {

        return title;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {

        return isChecked;
    }
}
