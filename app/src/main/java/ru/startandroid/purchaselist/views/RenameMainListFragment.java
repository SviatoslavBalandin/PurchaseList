package ru.startandroid.purchaselist.views;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.presenters.ShoppingListPresenter;
public class RenameMainListFragment extends DialogFragment {

    @BindView(R.id.nameOfList) EditText nameOfList;
    @BindView(R.id.btnOk) TextView ok;
    @BindView(R.id.btnCancel) TextView cancel;

    private ShoppingListPresenter presenter;
    private Toolbar toolbar;
    private String oldName;

    public RenameMainListFragment(){}

    @SuppressLint("ValidFragment")
    RenameMainListFragment(ShoppingListPresenter presenter, Toolbar toolbar, String oldName){
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        this.presenter = presenter;
        this.toolbar = toolbar;
        this.oldName = oldName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rename_list_dialog, null);
        ButterKnife.bind(this, v);
        nameOfList.append(oldName);
        return v;
    }

    @OnClick(R.id.btnOk)
    public void onClickBtnOk(){
        String newListName = nameOfList.getText().toString();
        if(TextUtils.isEmpty(newListName)){
            nameOfList.setError("Required!");
            return;
        }else {
            presenter.renameList(newListName);
            toolbar.setTitle(newListName);
            dismiss();
        }
    }
    @OnClick(R.id.btnCancel)
    public void onClickBtnCancel(){
        dismiss();
    }

}
