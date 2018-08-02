package ru.startandroid.purchaselist.presenters;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.model.UserInformation;
import ru.startandroid.purchaselist.views.AccountScreenView;

/**
 * Created by user on 19/11/2017.
 */

public class AccountPresenterImpl implements AccountPresenter{

    private AccountScreenView accountScreenView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference shoppingListsReference;
    private String today;
    private SimpleDateFormat sdf;

    private final String SHOPPING_LISTS_KEY = "Shopping Lists";
    private final String CURRENT_USER_NAME = "current user name";
    private final String CURRENT_LIST_PRODUCTS_AMOUNT = "productsAmount";


    @Inject
    public AccountPresenterImpl(FirebaseDatabase database, FirebaseAuth auth, AccountScreenView accountScreenView){
        this.accountScreenView = accountScreenView;
        firebaseAuth = auth;
        sdf = new SimpleDateFormat("dd.MM.yyyy");
        shoppingListsReference = database.getReference().child(SHOPPING_LISTS_KEY);
        today = sdf.format(Calendar.getInstance().getTime());
    }

    @Override
    public void addList(){
        String goodsListReference =  shoppingListsReference.push().getKey();
        GoodsList newList = new GoodsList("New List", today, goodsListReference,
                new UserInformation(firebaseAuth.getCurrentUser().getEmail(),
                        accountScreenView.getPrivatePreferences().getString(CURRENT_USER_NAME, "Unknown User"),
                        firebaseAuth.getCurrentUser().getUid()));
        shoppingListsReference.child(goodsListReference).setValue(newList);
        accountScreenView.getMainList().add(0, newList);
        accountScreenView.refreshList();
    }

    @Override
    public void deleteList() {
        if(accountScreenView.getMainList().size() == 0)
            return;
        shoppingListsReference.child(accountScreenView.getMainList().get(0).getListId()).removeValue();
        accountScreenView.getMainList().remove(0);
        accountScreenView.refreshList();
    }

    @Override
    public void fetchLists() {

                shoppingListsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.hasChildren()) {
                                Observable.fromIterable(dataSnapshot.getChildren())
                                        .map(dataSnapshot1 -> dataSnapshot1.getValue(GoodsList.class))
                                        .filter(list -> list.getOwner().getId().equals(firebaseAuth.getCurrentUser().getUid()))
                                        .doOnNext(goodsList -> shoppingListsReference.child(goodsList.getListId())
                                                .child(CURRENT_LIST_PRODUCTS_AMOUNT).setValue(goodsList.getProductsAmount()))
                                        .toList()
                                        .map(list -> flipList(list))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(list -> {
                                            accountScreenView.getMainList().addAll(0, list);
                                            accountScreenView.showProducts();
                                            accountScreenView.refreshList();
                                        });
                                shoppingListsReference.removeEventListener(this);
                            }
                        }catch (Exception e){
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("", databaseError.getMessage());
                    }
                });
    }
    private List<GoodsList> flipList(List<GoodsList> oldList){
        List<GoodsList> newList = new ArrayList<>();
        for(int i = oldList.size() - 1; i >= 0; i-- ){
            newList.add(oldList.get(i));
        }
        return newList;
    }
}

