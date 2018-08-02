package ru.startandroid.purchaselist.di;

import dagger.Subcomponent;
import ru.startandroid.purchaselist.views.ShoppingListFragment;

/**
 * Created by user on 21/02/2018.
 */

@Subcomponent(modules = {GoodsListModule.class})
public interface GoodsListComponent {

    void inject(ShoppingListFragment fragment);
}