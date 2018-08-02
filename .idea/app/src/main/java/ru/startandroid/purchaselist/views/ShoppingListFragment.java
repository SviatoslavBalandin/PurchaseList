package ru.startandroid.purchaselist.views;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.MyApp;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.chat.ChatRootFragment;
import ru.startandroid.purchaselist.di.GoodsListModule;
import ru.startandroid.purchaselist.model.Purchase;
import ru.startandroid.purchaselist.presenters.ShoppingListPresenter;
import ru.startandroid.purchaselist.views.helpers.PurchaseListRecyclerViewAdapter;

/**
 * Created by user on 04/02/2018.
 */

public class ShoppingListFragment extends Fragment implements ShoppingListView {

    @Inject
    ShoppingListPresenter presenter;

    @BindView(R.id.listOfGoods)
    RecyclerView listOfGoods;
    @BindView(R.id.fabAddPurchase)
    FloatingActionButton fabAddPurchase;
    @BindView(R.id.purchaseListToolBar)
    Toolbar toolbar;
    @BindView(R.id.totalButton)
    Button totalButton;
    @BindView(R.id.footerContent)
    TextView footerContent;
    @BindView(R.id.GoodsFooter)
    LinearLayout footerLayout;

    private PurchaseListRecyclerViewAdapter adapter;
    private List<Purchase> goodsList;
    private final String listId;
    private final String listTitle;
    private boolean prefIsQuantity;
    private boolean prefIsPrice;
    private MainViewInterface mainView;

    public ShoppingListFragment(String shoppingListId, String listTitle){
        listId = shoppingListId;
        this.listTitle = listTitle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolveDependencies();
        setHasOptionsMenu(true);
        mainView = (MainViewInterface) getActivity();
    }

    private void resolveDependencies(){
        MyApp.getAppComponent().createGoodsListComponent(new GoodsListModule(this)).inject(this);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_purchase_list_view, null);
        ButterKnife.bind(this, v);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v1 -> mainView.openAccountView(true));
        toolbar.setTitle(listTitle);
        toolbar.setOnMenuItemClickListener(initMenuItemClickListener());
        listOfGoods.setLayoutManager(new LinearLayoutManager(getActivity()));
        goodsList = new ArrayList<>();
        adapter = new PurchaseListRecyclerViewAdapter(this);
        presenter.fetchProducts();
        prefIsQuantity = getMainPreferences().getBoolean("show_quant", false);
        prefIsPrice = getMainPreferences().getBoolean("show_price", false);
        checkPrefAboutFooter(prefIsQuantity, prefIsPrice);
        new ItemTouchHelper(initItemTouchHelperSimpleCallback()).attachToRecyclerView(listOfGoods);
        adapter.notifyDataSetChanged();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.purchase_list_action_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.fabAddPurchase)
    public void showCreatePurchaseDialog() {
  new CreatePurchaseFragment(this, presenter).show(getChildFragmentManager(), "createShoppingListFragment");
    }
    @OnClick(R.id.totalButton)
    public void getTotal(){
        int countType = 0;

        if(!prefIsPrice && prefIsQuantity)
            countType = 1;
        else if(prefIsPrice && !prefIsQuantity)
            countType = 2;
        else if(prefIsQuantity && prefIsPrice)
            countType = 3;


        footerContent.setText(String.valueOf(presenter.countAmount(countType)));
    }
    private void openRenameListDialog() {
        new RenameMainListFragment(presenter, toolbar, listTitle).show(getChildFragmentManager(), "renameMainListFragment");
    }

    @Override
    public String getListId() {
        return listId;
    }
    @Override
    public List<Purchase> getGoodsList() {
        return goodsList;
    }

    @Override
    public SharedPreferences getMainPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public SharedPreferences getPrivatePreferences() {
        return mainView.getPrivatePreferences();
    }

    @Override
    public void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showProducts() {
        listOfGoods.setAdapter(adapter);
    }

    @Override
    public void verifyCheckedItems(boolean isChecked, String id) {
        presenter.checkItem(isChecked, id);
    }

    private Toolbar.OnMenuItemClickListener initMenuItemClickListener(){
        return item -> {
            switch (item.getItemId()){
                case R.id.purchase_list_a_b_edit:
                    openRenameListDialog();
                    break;
                case R.id.purchase_list_a_b_chat:
                    getPrivatePreferences().edit().putString("listTitle", listTitle).putString("listId", listId).commit();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, new ChatRootFragment(((AppCompatActivity)getActivity()).getSupportFragmentManager(),listId, listTitle))
                            .commit();
                    break;
            }
            return true;
        };
    }
    private ItemTouchHelper.SimpleCallback initItemTouchHelperSimpleCallback(){
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();

                if(direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT){
                    presenter.deleteItem(position);
                }
            }
        };
    }
    private void checkPrefAboutFooter(boolean isQuant, boolean isPrice){
        if(!isQuant && !isPrice)
            footerLayout.setVisibility(View.GONE);
        else
            footerLayout.setVisibility(View.VISIBLE);
    }

}
