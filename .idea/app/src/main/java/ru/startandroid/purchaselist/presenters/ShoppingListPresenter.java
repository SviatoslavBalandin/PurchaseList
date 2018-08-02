package ru.startandroid.purchaselist.presenters;

/**
 * Created by user on 16/02/2018.
 */

public interface ShoppingListPresenter {
    void addItem(String title, String price, String amount);
    void deleteItem(int itemID);
    void checkItem(boolean isChecked, String purchaseId);
    void renameList(String newName);
    void fetchProducts();
    double countAmount(int countType);
}
