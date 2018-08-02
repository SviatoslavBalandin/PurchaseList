package ru.startandroid.purchaselist.views;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
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
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.model.Purchase;
import ru.startandroid.purchaselist.presenters.ShoppingListPresenter;
import ru.startandroid.purchaselist.views.helpers.ClickListener;
import ru.startandroid.purchaselist.views.helpers.InfoTotal;
import ru.startandroid.purchaselist.views.helpers.ProductInfo;
import ru.startandroid.purchaselist.views.helpers.PurchaseListRecyclerViewAdapter;
import ru.startandroid.purchaselist.views.helpers.RecyclerItemClickListener;

/**
 * Created by user on 04/02/2018.
 */

public class ShoppingListFragment extends Fragment implements ShoppingListView {

    @Inject
    ShoppingListPresenter presenter;

    @BindView(R.id.purchaseListParentLayout)
    CoordinatorLayout parentLayout;
    @BindView(R.id.listOfGoods)
    RecyclerView recyclerListOfGoods;
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

    private List<Purchase> goodsList;
    private String listId;
    private String listTitle;
    private boolean prefIsQuantity;
    private boolean prefIsPrice;
    private boolean prefIsCheckable;
    private MainViewInterface mainView;
    private AppCompatActivity activity;
    private GoodsList parentReference;
    private PurchaseListRecyclerViewAdapter adapter;
    private int RECYCLER_STATE = 0;

    public ShoppingListFragment(){}

    @SuppressLint("ValidFragment")
    public ShoppingListFragment(GoodsList parentReference){
        this.parentReference = parentReference;
        listId = parentReference.getListId();
        listTitle = parentReference.getTitle();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolveDependencies();
        setHasOptionsMenu(true);
        activity = (AppCompatActivity) getActivity();
        mainView = (MainViewInterface) activity;
    }

    private void resolveDependencies(){
        MyApp.getAppComponent().createGoodsListComponent(new GoodsListModule(this)).inject(this);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_purchase_list_view, null);
        ButterKnife.bind(this, v);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v1 -> mainView.openAccountView(true));
        toolbar.setTitle(listTitle);
        toolbar.setOnMenuItemClickListener(initMenuItemClickListener());
        recyclerListOfGoods.setLayoutManager(new LinearLayoutManager(activity));
        addRecyclerScrollListener(recyclerListOfGoods);
        checkWallpaperSettings(parentLayout);
        attachOnClickListenerToRecyclerView(recyclerListOfGoods);
        attachLongClickListenerToAmount(footerContent);
        goodsList = new ArrayList<>();
        adapter = new PurchaseListRecyclerViewAdapter(this);
        resolveDependencies();
        presenter.fetchProducts();
        prefIsQuantity = getMainPreferences().getBoolean("show_quant", false);
        prefIsPrice = getMainPreferences().getBoolean("show_price", false);
        prefIsCheckable = getMainPreferences().getBoolean("markable", false);
        checkPrefAboutFooter(prefIsQuantity, prefIsPrice);
        new ItemTouchHelper(initItemTouchHelperSimpleCallback()).attachToRecyclerView(recyclerListOfGoods);
        return v;
    }
    private void addRecyclerScrollListener(RecyclerView recyclerView){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RECYCLER_STATE += dy;
             }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.purchase_list_action_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.stopListen();
    }

    @OnClick(R.id.fabAddPurchase)
    public void showCreatePurchaseDialog() {
  new CreatePurchaseFragment(this, presenter).show(getChildFragmentManager(), "createShoppingListFragment");
    }
    @OnClick(R.id.totalButton)
    public void getTotal(){
        int countType = 0;
        boolean isCheckable = false;

        if(!prefIsPrice && prefIsQuantity)
            countType = 1;
        else if(prefIsPrice && !prefIsQuantity)
            countType = 2;
        else if(prefIsQuantity && prefIsPrice)
            countType = 3;

        if(prefIsCheckable)
            isCheckable = true;

        footerContent.setText(String.valueOf(presenter.countAmount(countType, isCheckable)));
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
        return PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public SharedPreferences getPrivatePreferences() {
        return mainView.getPrivatePreferences();
    }

    @Override
    public void refreshList() {
        adapter.notifyDataSetChanged();
        recyclerListOfGoods.scrollBy(0, RECYCLER_STATE);
    }

    @Override
    public void showProducts() {
        recyclerListOfGoods.setAdapter(adapter);
    }

    @Override
    public void verifyCheckedItems(boolean isChecked, String id) {
        presenter.checkItem(isChecked, id);
    }

    @Override
    public boolean isIOwner() {

        return parentReference.isOwner;
    }

    @Override
    public boolean doesHaveConnection() {
        if(parentReference.getConnectionId().equals(""))
            return false;

        return true;
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
                            .replace(R.id.frag_container,
                                    new ChatRootFragment((activity).getSupportFragmentManager(),parentReference))
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
    private void checkWallpaperSettings(CoordinatorLayout parentLayout) {
        StringBuilder resource = new StringBuilder();
        final String prefValue = getMainPreferences().getString("p_l_wallpapers_get", "0");
        switch (prefValue) {
            case "1":
                resource.delete(0, resource.length());
                resource.append("@drawable/p_l_wallpaper_football");
                break;
            case "2":
                resource.delete(0, resource.length());
                resource.append("@drawable/p_l_wallpaper_blue");
                break;
            case "3":
                resource.delete(0, resource.length());
                resource.append("@drawable/p_l_wallpaper_pizza");
                break;
            case "4":
                resource.delete(0, resource.length());
                resource.append("@drawable/p_l_wallpaper_metal");
                break;
            case "5":
                resource.delete(0, resource.length());
                resource.append("@drawable/p_l_wallpaper_soft");
                break;
            default:
                break;
        }
        if(!prefValue.equals("0")) {
            int id = activity.getResources().getIdentifier(resource.toString(), "id", activity.getPackageName());
            parentLayout.setBackground(activity.getResources().getDrawable(id, null));
        }
    }
    private void attachOnClickListenerToRecyclerView(RecyclerView recyclerView){
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View v, int position) {

            }
            @Override
            public void onLongClick(View v, int position) {
                if(!mainView.getDrawerStatus())
                    new ProductInfo(goodsList.get(position)).show(getChildFragmentManager(), "ProductInfo");
            }
        }));
    }

    private void attachLongClickListenerToAmount(TextView amount){
        amount.setOnLongClickListener(v -> {
       final InfoTotal infoTotal = new InfoTotal(footerContent.getText().toString());
        infoTotal.show(getFragmentManager(), "InfoTotal");
        amount.setOnTouchListener((v1, event) -> {
            Log.e("HistorySize", "on touch listener");
            if(event.getAction() == MotionEvent.ACTION_UP)
                infoTotal.dismiss();
            else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                infoTotal.setAmount(footerContent.getText().toString());
                infoTotal.show(getFragmentManager(), "InfoTotal");
            }
            return true;
        });

            return true;
        });
    }
}
