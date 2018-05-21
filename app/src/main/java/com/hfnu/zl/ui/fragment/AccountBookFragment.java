package com.hfnu.zl.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hfnu.zl.R;
import com.hfnu.zl.database.AccountBook;
import com.hfnu.zl.database.AccountBookDao;
import com.hfnu.zl.database.DaoMaster;
import com.hfnu.zl.tool.Tool;
import com.hfnu.zl.ui.activity.AddAccountBookActivity;
import com.hfnu.zl.ui.activity.MainActivity;
import com.hfnu.zl.ui.adapter.ListViewHolder;
import com.hfnu.zl.ui.adapter.OmnipotentBaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 账本fragment  实现BaseFragment接口
 */

public class AccountBookFragment extends BaseFragment {
    private AccountBookDao accountBookDao;
    private List<AccountBook> accountBookList;
    private OmnipotentBaseAdapter<AccountBook> adapter;
    private OmnipotentBaseAdapter<String[]> detailsAdapter;
    private ListView detailsListView;
    private List<String[]> detailsItemName;

    @Override
    void initData() {
        accountBookDao = DaoMaster.getDaoSession(getActivity()).getAccountBookDao();
        accountBookList = accountBookDao.queryBuilder().orderDesc(AccountBookDao.Properties.WriteTime).build().list();
        final int[] icon = {R.mipmap.ic_family, R.mipmap.ic_travel, R.mipmap.ic_company, R.mipmap.ic_other};
        final String[] typeArray = getResources().getStringArray(R.array.type_array);
        adapter = new OmnipotentBaseAdapter<AccountBook>(getActivity(), accountBookList, R.layout.account_book_item_view) {
            @Override
            public void convert(ListViewHolder viewHolder, AccountBook accountBook) {
                viewHolder.setText(R.id.titleText, accountBook.getUse());
                viewHolder.setText(R.id.subtitleText, Tool.formattingDetailDate(accountBook.getWriteTime()));
                viewHolder.setText(R.id.tv_money, accountBook.getTurnoverSymbol() + String.format("%.2f", accountBook.getMoney()));
                for (int i = 0; i < typeArray.length; i++) {
                    if (typeArray[i].equals(accountBook.getType())) {
                        viewHolder.setImageResource(R.id.iv_type, icon[i]);
                        i = typeArray.length;
                    } else if (i == typeArray.length - 1) {
                        viewHolder.setImageResource(R.id.iv_type, R.mipmap.ic_other);
                    }
                }
            }
        };
        listView.setAdapter(adapter);
        if (accountBookList.isEmpty()) {
            isShowNullView(true);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        getListData();
    }

    private void getListData() {
        accountBookList.clear();
        accountBookList.addAll(accountBookDao.queryBuilder().orderDesc(AccountBookDao.Properties.WriteTime).build().list());
        adapter.notifyDataSetChanged();
        if (accountBookList.isEmpty()) {
            isShowNullView(true);
        }else{
            isShowNullView(false);
        }
    }

    /**
     * 显示账单详情对话框
     * @param accountBook AccountBook实例
     */
    private void showDetailsDialog(final AccountBook accountBook) {
        if (detailsAdapter == null) {//如果详情适配器为空则创建
            detailsItemName = new ArrayList<>();
            detailsAdapter = new OmnipotentBaseAdapter<String[]>(getActivity(), detailsItemName, R.layout.account_book_details_item) {
                @Override
                public void convert(ListViewHolder viewHolder, String[] array) {
                    viewHolder.setText(R.id.item_name, array[0]).setText(R.id.item_content, array[1]);
                }
            };
        }
        detailsItemName.clear();
        String[] account_book_details_item_name_array = getResources().getStringArray(R.array.account_book_details_item_name_array);
        for (int i = 0; i < account_book_details_item_name_array.length; i++) {
            String[] itemData = new String[2];
            itemData[0] = account_book_details_item_name_array[i];
            detailsItemName.add(itemData);
        }
        //设置详情适配器的数据
        detailsItemName.get(0)[1] = Tool.formattingDetailDate(accountBook.getWriteTime());
        detailsItemName.get(1)[1] = accountBook.getType();
        detailsItemName.get(2)[1] = accountBook.getUse();
        detailsItemName.get(3)[1] = accountBook.getTurnoverSymbol() + accountBook.getMoney() + "元";
        detailsItemName.get(4)[1] = accountBook.getRemark();
        detailsAdapter.notifyDataSetChanged();
        detailsListView = new ListView(getActivity());
        detailsListView.setDivider(null);
        detailsListView.setPadding(0,Tool.dip2px(getActivity(),8),0,0);
        detailsListView.setAdapter(detailsAdapter);
        AlertDialog.Builder detailsDialog = new AlertDialog.Builder(getActivity());
        detailsDialog.setTitle(R.string.str_account_book_details);
        detailsDialog.setView(detailsListView);
        //dialog添加以下三个按钮做不用的事件
        detailsDialog.setNegativeButton(R.string.str_cancel, new MtyDialogOnClickListener());
        detailsDialog.setPositiveButton(R.string.str_edit, new MtyDialogOnClickListener(accountBook));
        detailsDialog.setNeutralButton(R.string.str_delete, new MtyDialogOnClickListener(accountBook));
        detailsDialog.create().show();
    }

    /**
     * 详情dialog按钮点击响应事件
     */
    private class MtyDialogOnClickListener implements DialogInterface.OnClickListener{
        private AccountBook accountBook;
        public MtyDialogOnClickListener(){}
        public MtyDialogOnClickListener(AccountBook accountBook){
            this.accountBook=accountBook;
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_NEUTRAL://编辑按钮
                    accountBookDao.delete(accountBook);
                    notifyDataSetChanged();
                    break;
                case DialogInterface.BUTTON_POSITIVE://确认按钮
                    Intent intent = new Intent(getActivity(), AddAccountBookActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("title",R.string.str_edit_account_book);
                    bundle.putSerializable("data",accountBook);
                    intent.putExtras(bundle);
                    getActivity().startActivityForResult(intent, MainActivity.RB_ACCOUNT_BOOK);
                    break;
            }//三个按钮都执行以下事件
            dialog.cancel();
            dialog.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击单个账本调用此方法他拿出详情对话框
        showDetailsDialog(accountBookList.get(position));
    }
}
