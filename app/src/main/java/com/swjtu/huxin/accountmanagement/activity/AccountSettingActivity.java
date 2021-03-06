package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.base.OnNumKeyboardItemClickListener;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;
import com.swjtu.huxin.accountmanagement.view.NumKeyboardView;

import java.math.BigDecimal;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by huxin on 2017/3/11.
 */

public class AccountSettingActivity extends BaseAppCompatActivity {

    private LinearLayout btnBack;
    private TextView title;

    private RelativeLayout btnType;
    private TextView textType;
    private RelativeLayout btnName;
    private TextView textName;
    private RelativeLayout btnDetail2;
    private TextView textDetail1;
    private TextView textDetail2;
    private RelativeLayout btnMoney;
    private TextView textMoney;
    private RelativeLayout btnColor;
    private CardView cardColor;

    private Account account;

    private Animation enterAnim;
    private Animation exitAnim;
    private LinearLayout keyNumBoard;
    private TextView keyNum;
    private TextView keySymbol;
    private LinearLayout keyTextBoard;
    private TextView keyLabel;
    private EditText keyText;
    private NumKeyboardView numKeyboardView;
    private ArrayList<String> valueList;
    private GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        initAnim();
        initBackground();
        initView();
    }

    private void initAnim() {
        enterAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
        exitAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);
    }

    void initBackground(){
        ImageView background = (ImageView)findViewById(R.id.background);
        int[] attrsArray1 = { R.attr.mainBackgrount };
        TypedArray typedArray1 = obtainStyledAttributes(attrsArray1);
        int imgResID = typedArray1.getResourceId(0,-1);
        typedArray1.recycle();
        int[] attrsArray2 = { R.attr.theme_alpha };
        TypedArray typedArray2 = obtainStyledAttributes(attrsArray2);
        int alpha = typedArray2.getInteger(0,8);
        typedArray2.recycle();
        Glide.with(this).load(imgResID).dontAnimate().bitmapTransform(new BlurTransformation(this, alpha)).into(background);
    }

    private void initView() {
        btnBack = (LinearLayout) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);

        btnType = (RelativeLayout) findViewById(R.id.type_enter);
        textType = (TextView) findViewById(R.id.type);
        btnName = (RelativeLayout) findViewById(R.id.name_enter);
        textName = (TextView) findViewById(R.id.name);
        btnDetail2 = (RelativeLayout) findViewById(R.id.detail2_enter);
        textDetail1 = (TextView) findViewById(R.id.detail1);
        textDetail2 = (TextView) findViewById(R.id.detail2);
        btnMoney = (RelativeLayout) findViewById(R.id.money_enter);
        textMoney = (TextView) findViewById(R.id.money);
        btnColor = (RelativeLayout) findViewById(R.id.color_enter);
        cardColor = (CardView) findViewById(R.id.color);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        textType.setText(getTypeTextByType(account.getType()));
        textName.setText(account.getAccountname());
        textDetail1.setText(getDetail1ByType(account.getType()));
        textDetail2.setText(account.getAccountdetail());
        textMoney.setText(account.getMoney());
        cardColor.setCardBackgroundColor(Color.parseColor(account.getColor()));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelToast();
                Intent intent = new Intent();
                intent.putExtra("account",account);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        title.setText(getTypeTextByType(account.getType()));

        keyNum = (TextView) findViewById(R.id.key_num);
        keySymbol = (TextView) findViewById(R.id.key_symbol);
        keyNumBoard = (LinearLayout)findViewById(R.id.key_num_board);
        numKeyboardView = (NumKeyboardView) findViewById(R.id.numKeyboardView);
        keyNumBoard.setVisibility(View.GONE);

        valueList = numKeyboardView.getValueList();
        gridView = numKeyboardView.getGridView();
        gridView.setOnItemClickListener(new OnNumKeyboardItemClickListener(numKeyboardView, keyNum, keySymbol, new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, String viewName) {
                textMoney.setText(keyNum.getText());
                account.setMoney(keyNum.getText().toString());
                keyNumBoard.startAnimation(exitAnim);
                keyNumBoard.setVisibility(View.GONE);
            }
        }));

        btnMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyTextBoard.getVisibility() ==  View.VISIBLE){
                    InputMethodManager imm = (InputMethodManager)keyText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                    keyTextBoard.startAnimation(exitAnim);
                    keyTextBoard.setVisibility(View.GONE);
                }
                if(keyNumBoard.getVisibility() !=  View.VISIBLE){//是否弹出键盘
                    keyNum.setText(textMoney.getText());
                    numKeyboardView.setFocusable(true);
                    numKeyboardView.setFocusableInTouchMode(true);
                    keyNumBoard.startAnimation(enterAnim);
                    keyNumBoard.setVisibility(View.VISIBLE);
                }
                else{
                    keySymbol.setVisibility(View.GONE);
                    numKeyboardView.changeBtnOK();
                    numKeyboardView.setAddSymbol(false);
                    numKeyboardView.setSubtractSymbol(false);
                    numKeyboardView.setZero(false);

                    keyNumBoard.startAnimation(exitAnim);
                    keyNumBoard.setVisibility(View.GONE);
                }
            }
        });

        keyLabel = (TextView) findViewById(R.id.key_label);
        keyText = (EditText) findViewById(R.id.key_text);
        keyTextBoard = (LinearLayout)findViewById(R.id.key_text_board);
        keyTextBoard.setVisibility(View.GONE);
        btnName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyNumBoard.getVisibility() ==  View.VISIBLE){
                    keyNumBoard.startAnimation(exitAnim);
                    keyNumBoard.setVisibility(View.GONE);
                }
                if(keyTextBoard.getVisibility() !=  View.VISIBLE){//是否弹出键盘
                    keyLabel.setText("账户名称");
                    keyText.setText(textName.getText());
                    keyText.setSelection(textName.getText().toString().length());
                    keyText.setFocusable(true);
                    keyText.setFocusableInTouchMode(true);
                    keyText.requestFocus();
                    //启动系统服务强制弹出键盘
                    InputMethodManager imm = (InputMethodManager)keyText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                    keyTextBoard.startAnimation(enterAnim);
                    keyTextBoard.setVisibility(View.VISIBLE);

                    keyText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if(actionId == EditorInfo.IME_ACTION_DONE){  //判断是否是“DONE”键
                                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm.isActive()) {
                                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                                    keyTextBoard.startAnimation(exitAnim);
                                    keyTextBoard.setVisibility(View.GONE);
                                }
                                textName.setText(v.getText());
                                account.setAccountname(v.getText().toString());
                                return true;
                            }
                            return false;
                        }
                    });
                }
                else{
                    InputMethodManager imm = (InputMethodManager)keyText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive())
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    keyTextBoard.startAnimation(exitAnim);
                    keyTextBoard.setVisibility(View.GONE);
                    if(getDetail1ByType(account.getType()).equals(keyLabel.getText().toString()))
                        btnName.performClick();
                }
            }
        });
        btnDetail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyNumBoard.getVisibility() ==  View.VISIBLE){
                    keyNumBoard.startAnimation(exitAnim);
                    keyNumBoard.setVisibility(View.GONE);
                }
                if(keyTextBoard.getVisibility() !=  View.VISIBLE){//是否弹出键盘
                    keyLabel.setText(getDetail1ByType(account.getType()));
                    keyText.setText(textDetail2.getText());
                    keyText.setSelection(textDetail2.getText().toString().length());
                    keyText.setFocusable(true);
                    keyText.setFocusableInTouchMode(true);
                    keyText.requestFocus();
                    //启动系统服务强制弹出键盘
                    InputMethodManager imm = (InputMethodManager)keyText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                    keyTextBoard.startAnimation(enterAnim);
                    keyTextBoard.setVisibility(View.VISIBLE);

                    keyText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if(actionId == EditorInfo.IME_ACTION_DONE){  //判断是否是“DONE”键
                                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm.isActive()) {
                                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                                    keyTextBoard.startAnimation(exitAnim);
                                    keyTextBoard.setVisibility(View.GONE);
                                }
                                textDetail2.setText(v.getText());
                                account.setAccountdetail(v.getText().toString());
                                return true;
                            }
                            return false;
                        }
                    });
                }
                else{
                    InputMethodManager imm = (InputMethodManager)keyText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive())
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    keyTextBoard.startAnimation(exitAnim);
                    keyTextBoard.setVisibility(View.GONE);
                    if("账户名称".equals(keyLabel.getText().toString()))
                        btnDetail2.performClick();
                }
            }
        });
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyNumBoard.getVisibility() == View.VISIBLE) {
                    keyNumBoard.startAnimation(exitAnim);
                    keyNumBoard.setVisibility(View.GONE);
                }
                if(keyTextBoard.getVisibility() == View.VISIBLE) {
                    keyTextBoard.startAnimation(exitAnim);
                    keyTextBoard.setVisibility(View.GONE);
                }
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive())
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                Intent intent = new Intent(AccountSettingActivity.this,AccountSelectColorActivity.class);
                intent.putExtra("account",account);
                startActivityForResult(intent,1);
            }
        });
    }

    private String getDetail1ByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return "现金类型";
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return "发卡行";
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return "发卡行";
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return "账号";
            case ConstantUtils.ACCOUNT_TYPE_WECHAT:return "账号";
            default:return "";
        }
    }

    private String getTypeTextByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return "现金";
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return "储蓄卡";
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return "信用卡";
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return "支付宝";
            case ConstantUtils.ACCOUNT_TYPE_WECHAT:return "微信钱包";
            default:return "";
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    account = (Account) intent.getSerializableExtra("account");
                    cardColor.setCardBackgroundColor(Color.parseColor(account.getColor()));
                }
        }
    }

    @Override
    public void onBackPressed() {
        cancelToast();
        if(keyNumBoard.getVisibility() == View.VISIBLE) {
            keyNumBoard.startAnimation(exitAnim);
            keyNumBoard.setVisibility(View.GONE);
            return;
        }
        if(keyTextBoard.getVisibility() == View.VISIBLE) {
            keyTextBoard.startAnimation(exitAnim);
            keyTextBoard.setVisibility(View.GONE);
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("account", account);
        setResult(RESULT_OK, intent);
        finish();
    }
}
