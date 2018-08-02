package ru.startandroid.purchaselist.views.helpers;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.startandroid.purchaselist.R;

/**
 * Created by user on 11/04/2018.
 */

public class RenameDeleteDialog extends DialogFragment {

    @BindView(R.id.renameDeleteDialogDelete)
    TextView delete;
    @BindView(R.id.renameDeleteDialogRename)
    TextView rename;

    private RenameDeleteDialogListener listener;

    public RenameDeleteDialog(){}

    @SuppressLint("ValidFragment")
    public RenameDeleteDialog(RenameDeleteDialogListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rename_delete_dialog, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.renameDeleteDialogRename)
    public void renameList(){
        listener.onRename();
        dismiss();
    }
    @OnClick(R.id.renameDeleteDialogDelete)
    public void deleteList(){
        listener.onDelete();
        dismiss();
    }
}
