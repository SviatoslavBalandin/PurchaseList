package ru.startandroid.purchaselist.di;

import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.purchaselist.presenters.ShoppingListPresenter;
import ru.startandroid.purchaselist.presenters.ShoppingListPresenterImpl;
import ru.startandroid.purchaselist.views.ShoppingListView;

/**
 * Created by user on 21/02/2018.
 */
@Module
public class GoodsListModule {

    private final ShoppingListView shoppingListView;

    public GoodsListModule(ShoppingListView shoppingListView){
        this.shoppingListView = shoppingListView;
    }
    @Provides
    public ShoppingListPresenter getShoppingListPresenter(FirebaseDatabase database){

        return new ShoppingListPresenterImpl(database, shoppingListView);
    }

}
