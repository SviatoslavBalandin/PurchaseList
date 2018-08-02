package ru.startandroid.purchaselist.presenters;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.model.Purchase;
import ru.startandroid.purchaselist.views.ShoppingListView;

/**
 * Created by user on 18/02/2018.
 */

public class ShoppingListPresenterImpl implements ShoppingListPresenter {

    private DatabaseReference  goodsListReference;
    private DatabaseReference amountReference;
    private FirebaseDatabase database;
    private ShoppingListView shoppingListView;
    private final String listId;
    private ValueEventListener valueEventListener;

    public ShoppingListPresenterImpl(FirebaseDatabase database, ShoppingListView shoppingListView){
        this.shoppingListView = shoppingListView;
        this.database = database;
        listId = shoppingListView.getListId();
        goodsListReference = database.getReference().child("Shopping Lists").child(listId).child("purchases");
        amountReference = database.getReference().child("Shopping Lists").child(listId).child("productsAmount");
    }


    @Override
    public void addItem(String title, String price, String amount) {
        String purchaseReference = goodsListReference.push().getKey();
        Purchase purchase = new Purchase(purchaseReference, title, price, amount, false);
        goodsListReference.child(purchaseReference).setValue(purchase);
        shoppingListView.getGoodsList().add(purchase);
        amountReference.setValue(shoppingListView.getGoodsList().size());
        shoppingListView.refreshList();
    }

    @Override
    public void deleteItem(int itemID) {
        goodsListReference.child(shoppingListView.getGoodsList().get(itemID).getId()).removeValue();
        shoppingListView.getGoodsList().remove(itemID);
        amountReference.setValue(shoppingListView.getGoodsList().size());
        shoppingListView.refreshList();
    }

    @Override
    public void checkItem(boolean isChecked, String purchaseId) {

        goodsListReference.child(purchaseId).child("checked").setValue(isChecked);

        goodsListReference.child(purchaseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    Purchase purchase = dataSnapshot.getValue(Purchase.class);
                    Observable.fromIterable(shoppingListView.getGoodsList())
                            .filter(product -> product != null)
                            .filter(purchasePredicate -> purchasePredicate.getId().equals(purchaseId))
                            .subscribe((Purchase purchasePredicate) ->{
                                try {
                                    shoppingListView.getGoodsList().set(shoppingListView.getGoodsList().indexOf(purchasePredicate), purchase);
                                }catch (OnErrorNotImplementedException e){
                                    //skip
                                }
                    });
                     goodsListReference.removeEventListener(this);
                }catch (OnErrorNotImplementedException | NoSuchMethodError e) {
                    e.fillInStackTrace();
                } catch (Exception e){
                    //nothing to show
                }
            }
             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Log.e("", databaseError.getMessage());
             }
        });
    }

    @Override
    public void renameList(String newName) {
        DatabaseReference listNameReference = database.getReference("Shopping Lists").child(listId).child("title");
        listNameReference.setValue(newName);
    }

    @Override
    public void fetchProducts() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Observable.fromIterable(dataSnapshot.getChildren())
                            .map(child -> child.getValue(Purchase.class))
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(purchaseList -> {

                                shoppingListView.getGoodsList().clear();
                                shoppingListView.getGoodsList().addAll(purchaseList);
                                shoppingListView.showProducts();
                                shoppingListView.refreshList();

                                if(!shoppingListView.doesHaveConnection())
                                    stopListen();
                            });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        };
        startListen();
    }
    @Override
    public double countAmount(int countType, boolean isCheckable) {
        int countOnlyQuantity = 1;
        int countOnlyPrice = 2;
        int countBoth = 3;
        double quantity = 0;
        double price = 0;
        double total = 0;

        try {
            for (Purchase p : shoppingListView.getGoodsList()) {

               if(isCheckable && p.isChecked()) {
                   //skip
               }else {
                   if (countType == countOnlyQuantity) {
                       if (p.getAmount() != null)
                           total += Double.parseDouble(p.getAmount());
                   } else if (countType == countOnlyPrice) {
                       if (p.getPrice() != null && !TextUtils.isEmpty(p.getPrice()))
                           total += Double.parseDouble(p.getPrice());
                   } else if (countType == countBoth) {
                       if (p.getAmount() != null)
                           quantity = Double.parseDouble(p.getAmount());
                       if (p.getPrice() != null && !TextUtils.isEmpty(p.getPrice()))
                           price = Double.parseDouble(p.getPrice());
                       total += (quantity * price);
                   }
               }
            }
        } catch (ClassCastException e) {
            return -1;
        }
        return total;
    }

    @Override
    public void stopListen() {
        goodsListReference.removeEventListener(valueEventListener);
    }

    private void  startListen(){
        if(valueEventListener != null)
            goodsListReference.addValueEventListener(valueEventListener);

    }

}
