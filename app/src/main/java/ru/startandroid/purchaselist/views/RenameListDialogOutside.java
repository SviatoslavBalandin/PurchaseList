package ru.startandroid.purchaselist.views;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.R;
import ru.startandroid.purchaselist.model.GoodsList;
import ru.startandroid.purchaselist.presenters.AccountPresenter;

/**
 * Created by user on 11/04/2018.
 */

public class RenameListDialogOutside extends DialogFragment {

    @BindView(R.id.nameOfList)
    EditText nameOfList;
    @BindView(R.id.btnOk)
    TextView ok;
    @BindView(R.id.btnCancel) TextView cancel;

    private AccountPresenter presenter;
    private int position;
    private List<GoodsList> goodsLists;

    public RenameListDialogOutside(){

    }
    @SuppressLint("ValidFragment")
    RenameListDialogOutside(AccountPresenter presenter, int position, List<GoodsList> goodsLists){
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        this.presenter = presenter;
        this.position = position;
        this.goodsLists = goodsLists;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rename_list_dialog, null);
        ButterKnife.bind(this, v);
        nameOfList.append(goodsLists.get(position).getTitle());
        return v;
    }

    @OnClick(R.id.btnOk)
    public void onClickBtnOk(){
        String newListName = nameOfList.getText().toString();
        if(TextUtils.isEmpty(newListName)){
            nameOfList.setError("Required!");
            return;
        }else {
            presenter.renameList(newListName, goodsLists.get(position).getListId());
            dismiss();
        }
    }
    @OnClick(R.id.btnCancel)
    public void onClickBtnCancel(){
        dismiss();
    }

}
