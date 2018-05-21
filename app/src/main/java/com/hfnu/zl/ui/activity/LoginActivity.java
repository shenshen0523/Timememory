package com.hfnu.zl.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hfnu.zl.R;
import com.hfnu.zl.tool.Tool;

/**
 * 登陆界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_user_password;
    private Button ok;
    private CheckBox save_pass_word;
    private SharedPreferences sp;
    private String passWord;
    public final static int NO_PASSWORD = 0;
    public final static int SAVE_PASSWORD = 1;
    public final static int DO_NOT_SAVE_THE_PASSWORD = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindView();
        initData();
    }

    /**
     * 初始化界面
     */
    private void bindView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        et_user_password = Tool.findViewById(this, R.id.et_user_password);
        save_pass_word = Tool.findViewById(this, R.id.save_pass_word);
        save_pass_word.setFocusable(false);
        save_pass_word.setOnClickListener(this);
        ok = Tool.findViewById(this, R.id.ok);
        ok.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        sp = getSharedPreferences("baseData", Context.MODE_PRIVATE);
        if (sp.getBoolean("firstLoading", true)) {//如果是第一次使用软件就弹出对话框让用户设置管理密码
            LinearLayout ll_parent = Tool.findViewById(this,R.id.ll_parent);
            ll_parent.setVisibility(View.GONE);
            showManageDialog();
        } else {
            passWord = sp.getString("passWord", "");
            switch (sp.getInt("savePassWord", DO_NOT_SAVE_THE_PASSWORD)) {
                case NO_PASSWORD:
                    jumpInterface();
                    break;
                case SAVE_PASSWORD:
                    save_pass_word.setChecked(true);
                    et_user_password.setText(passWord);
                    break;
/*                case DO_NOT_SAVE_THE_PASSWORD:
                    break;*/
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                String inputPassWord = et_user_password.getText().toString().trim();
                if (TextUtils.isEmpty(inputPassWord)) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (passWord.equals(inputPassWord)) {
                    savePassword();
                } else {
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.save_pass_word:
                save_pass_word.setFocusable(true);
                break;
        }
    }

    /**
     *是否保存密码
     */
    private void savePassword() {
        SharedPreferences.Editor editor = sp.edit();
        if (save_pass_word.isChecked())
            editor.putInt("savePassWord", SAVE_PASSWORD);
        else
            editor.putInt("savePassWord", DO_NOT_SAVE_THE_PASSWORD);
        editor.commit();
        jumpInterface();
    }

    /**
     * 第一使用该软件时弹出对话框让用户设置密码
     */
    private void showManageDialog() {
        AlertDialog.Builder manageDialog = new AlertDialog.Builder(this);
        manageDialog.setTitle(R.string.str_set_password);
        View view = getLayoutInflater().inflate(R.layout.manage_password_view, null);
        view.setPadding(Tool.dip2px(this, 8), Tool.dip2px(this, 16), Tool.dip2px(this, 8), 0);
        final EditText passWordEd = Tool.findViewById(view, R.id.et_user_password);
        manageDialog.setCancelable(false);
        manageDialog.setView(view);
        final SharedPreferences.Editor editor = sp.edit();
        manageDialog.setPositiveButton(R.string.str_ok, null);//设置确认按钮，点击事件设置为空这样对话框就不会关闭
        manageDialog.setNegativeButton(R.string.str_no_set_pass_word, new DialogInterface.OnClickListener() {//设置不设置密码按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putInt("savePassWord", NO_PASSWORD);
                editor.putBoolean("firstLoading", false);
                editor.commit();
                jumpInterface();
            }
        });
        final AlertDialog alertDialog = manageDialog.create();//得到对话框实例
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {//设置确认按钮点击事件
            @Override
            public void onClick(View v) {
                String inputPassWord = passWordEd.getText().toString().trim();
                if (TextUtils.isEmpty(inputPassWord)) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    savePassword();
                    editor.putString("passWord", inputPassWord);
                    editor.putBoolean("firstLoading", false);
                    editor.commit();
                    alertDialog.dismiss();
                }
            }
        });
    }

    /**
     * 跳转界面
     */
    private void jumpInterface() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
