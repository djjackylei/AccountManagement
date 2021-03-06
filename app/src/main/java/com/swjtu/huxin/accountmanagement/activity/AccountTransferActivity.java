package com.swjtu.huxin.accountmanagement.activity;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.base.OnNumKeyboardItemClickListener;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;
import com.swjtu.huxin.accountmanagement.view.NumKeyboardView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by huxin on 2017/3/11.
 */

public class AccountTransferActivity extends BaseAppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private LinearLayout btnBack;
    
    private Account accountOut;
    private Account accountIn;
    private CardView btnOut;
    private CardView btnIn;
    private RelativeLayout emptyOut;
    private RelativeLayout emptyIn;
    private ImageView iconOut;
    private ImageView iconIn;
    private TextView nameOut;
    private TextView nameIn;
    private TextView detail1Out;
    private TextView detail2Out;
    private TextView detail1In;
    private TextView detail2In;
    private TextView numOut;
    private TextView numIn;
    private TextView symbolOut;
    private TextView symbolIn;

    private String remarkTransfer;
    private Date timeTransfer;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private TextView btnDate;
    private TextView btnTime;
    private ImageView btnRemark;
    private NumKeyboardView numKeyboardView;
    private GridView gridView;

    private PopupWindow accountPopupWindowOut;
    private PopupWindow accountPopupWindowIn;
    private PopupWindow remarkPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_transfer);
        initBackground();
        initView();
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
        btnOut = (CardView) findViewById(R.id.btnOut);
        btnIn = (CardView) findViewById(R.id.btnIn);
        emptyOut = (RelativeLayout) findViewById(R.id.emptyOut);
        emptyIn = (RelativeLayout) findViewById(R.id.emptyIn);
        iconOut = (ImageView) findViewById(R.id.iconOut);
        iconIn = (ImageView) findViewById(R.id.iconIn);
        nameOut = (TextView) findViewById(R.id.nameOut);
        nameIn = (TextView) findViewById(R.id.nameIn);
        detail1Out = (TextView) findViewById(R.id.detail1Out);
        detail1In = (TextView) findViewById(R.id.detail1In);
        detail2Out = (TextView) findViewById(R.id.detail2Out);
        detail2In = (TextView) findViewById(R.id.detail2In);
        numOut = (TextView) findViewById(R.id.numOut);
        numIn = (TextView) findViewById(R.id.numIn);
        symbolOut = (TextView) findViewById(R.id.symbolOut);
        symbolIn = (TextView) findViewById(R.id.symbolIn);
        symbolOut.setText("");
        symbolIn.setText("");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelToast();
                finish();
            }
        });

        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(accountPopupWindowOut);
            }
        });

        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(accountPopupWindowIn);
            }
        });

        btnDate = (TextView) findViewById(R.id.date);
        btnTime = (TextView) findViewById(R.id.time);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
            }
        });

        btnRemark = (ImageView) findViewById(R.id.remark);
        btnRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(remarkPopupWindow);
            }
        });

        numKeyboardView = (NumKeyboardView) findViewById(R.id.numKeyboardView);

        gridView = numKeyboardView.getGridView();
        gridView.setOnItemClickListener(new OnNumKeyboardItemClickListener(numKeyboardView, numOut, symbolOut, new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, String viewName) {
                if(accountOut == null || accountIn == null){
                    showToast("请先选择转出和转入账户", Toast.LENGTH_SHORT);
                }
                else if(accountOut == accountIn)
                {
                    showToast("转入账户不能和转出账户一样哦~", Toast.LENGTH_SHORT);
                }
                else {
                    if (!"0.00".equals(numOut.getText().toString())) {
                        AccountRecord recordOut = new AccountRecord();
                        recordOut.setIcon("icon_zhuanzhang");
                        recordOut.setRecordname("转账");
                        recordOut.setMoney("-"+numOut.getText().toString());
                        recordOut.setRemark(remarkTransfer);
                        recordOut.setRecordtime(timeTransfer.getTime());
                        recordOut.setAccount(accountOut);
                        recordOut.setMember("[转出到"+accountIn.getAccountname()+"]");

                        AccountRecord recordIn = new AccountRecord();
                        recordIn.setIcon("icon_zhuanzhang");
                        recordIn.setRecordname("转账");
                        recordIn.setMoney(numIn.getText().toString());
                        recordIn.setRemark(remarkTransfer);
                        recordIn.setRecordtime(timeTransfer.getTime());
                        recordIn.setAccount(accountIn);
                        recordIn.setMember("[由"+accountOut.getAccountname()+"转入]");

                        AccountRecordService accountRecordService = new AccountRecordService();
                        accountRecordService.addAccountRecord(recordOut);
                        accountRecordService.addAccountRecord(recordIn);

                        setResult(RESULT_OK);
                        finish();
                    } else {//输入金额为0
                        showToast("转账金额必须大于0", Toast.LENGTH_SHORT);
                    }
                }
            }
        }));

        numOut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                numIn.setText(s);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        symbolOut.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                symbolIn.setText(s);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });


        remarkTransfer = "";
        timeTransfer = new Date();

        updateBtnRemark();
        initDatePickerDialog(timeTransfer);
        initTimePickerDialog(timeTransfer);

        updateBtnDateAndTime(timeTransfer);
        initAccountPopupWindowOut();
        initAccountPopupWindowIn();
        initRemarkPopupWindow();
    }

    private void initDatePickerDialog(Date time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int[] attrsArray = { R.attr.dialog_backgound };
        TypedArray typedArray = obtainStyledAttributes(attrsArray);
        int color = typedArray.getColor(0,-1);
        typedArray.recycle();
        datePickerDialog = DatePickerDialog.newInstance(this,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setAccentColor(color);
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_1);
    }

    private void initTimePickerDialog(Date time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int[] attrsArray = { R.attr.dialog_backgound };
        TypedArray typedArray = obtainStyledAttributes(attrsArray);
        int color = typedArray.getColor(0,-1);
        typedArray.recycle();
        timePickerDialog = TimePickerDialog.newInstance(this,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.setAccentColor(color);
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_1);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeTransfer);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if(calendar.getTime().getTime() > new Date().getTime()){
            showToast("不能选取未来的时间哦~~",Toast.LENGTH_SHORT);
        }
        else {
            timeTransfer = calendar.getTime();
            updateBtnDateAndTime(timeTransfer);
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeTransfer);
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        if(calendar.getTime().getTime() > new Date().getTime()){
            showToast("不能选取未来的时间哦~~",Toast.LENGTH_SHORT);
        }
        else {
            timeTransfer = calendar.getTime();
            updateBtnDateAndTime(timeTransfer);
        }
    }

    private void updateBtnDateAndTime(Date time){
        btnDate.setText(new SimpleDateFormat("yy年MM月dd日").format(time));
        btnTime.setText(new SimpleDateFormat("HH:mm").format(time));
    }

    private void updateBtnRemark(){
        if("".equals(remarkTransfer)) {
            int[] attrsArray = { R.attr.more_half_transparent_contrast };
            TypedArray typedArray = obtainStyledAttributes(attrsArray);
            int color = typedArray.getColor(0,-1);
            typedArray.recycle();
            btnRemark.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            btnRemark.invalidate();
        }
        else {
            btnRemark.getBackground().setColorFilter(getResources().getColor(R.color.customBlue), PorterDuff.Mode.SRC_ATOP);
            btnRemark.invalidate();
        }
    }
    
    private void initAccountPopupWindowOut() {
        View contentView = LayoutInflater.from(AccountTransferActivity.this).inflate(R.layout.popupwindow_account, null);
        accountPopupWindowOut = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        //设置各个控件的点击响应
        ListView list = (ListView)contentView.findViewById(R.id.list);

        AccountRecordService accountRecordService = new AccountRecordService();
        MyApplication app = MyApplication.getApplication();
        final List<Account> accounts = new ArrayList<Account>(app.getAccounts().values());
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < accounts.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("item_icon", getPopupWindowIconByType(accounts.get(i).getType()));
            map.put("item_text", accounts.get(i).getAccountname());
            String totalMoney = accountRecordService.getTotalMoneyByAccount(accounts.get(i));
            map.put("item_money", new BigDecimal(accounts.get(i).getMoney()).add(new BigDecimal(totalMoney)).toString());
            map.put("item_selector", null);
            data.add(map);
        }

        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.item_list_account,
                new String[]{"item_icon", "item_text","item_money","item_selector"}, new int[]{R.id.item_icon,
                R.id.item_text,R.id.item_money,R.id.item_selector});
        list.setAdapter(simpleAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < data.size(); i++) {
                    if(position == i)
                        data.get(i).put("item_selector", R.drawable.ic_selector_blue);
                    else
                        data.get(i).put("item_selector", null);
                }
                simpleAdapter.notifyDataSetChanged();
                accountPopupWindowOut.dismiss();

                accountOut = accounts.get(position);
                nameOut.setText(accountOut.getAccountname());
                nameOut.setTextColor(getResources().getColor(R.color.white));
                detail1Out.setVisibility(View.VISIBLE);
                detail2Out.setVisibility(View.VISIBLE);
                detail1Out.setText(getDetail1ByType(accountOut.getType()));
                detail2Out.setText(accountOut.getAccountdetail());
                numOut.setTextColor(getResources().getColor(R.color.white));
                symbolOut.setTextColor(getResources().getColor(R.color.more_half_transparent_white));
                iconOut.setVisibility(View.VISIBLE);
                iconOut.setBackgroundResource(getIconByType(accountOut.getType()));
                btnOut.setCardBackgroundColor(Color.parseColor(accountOut.getColor()));
                emptyOut.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });

        list.setDivider(null);//去除分割线

        View outOfWindow = (View)contentView.findViewById(R.id.outof_popup_window);
        outOfWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountPopupWindowOut.dismiss();
            }
        });
    }

    private void initAccountPopupWindowIn() {
        View contentView = LayoutInflater.from(AccountTransferActivity.this).inflate(R.layout.popupwindow_account, null);
        accountPopupWindowIn = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        //设置各个控件的点击响应
        ListView list = (ListView)contentView.findViewById(R.id.list);

        AccountRecordService accountRecordService = new AccountRecordService();
        MyApplication app = MyApplication.getApplication();
        final List<Account> accounts = new ArrayList<Account>(app.getAccounts().values());
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < accounts.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("item_icon", getPopupWindowIconByType(accounts.get(i).getType()));
            map.put("item_text", accounts.get(i).getAccountname());
            String totalMoney = accountRecordService.getTotalMoneyByAccount(accounts.get(i));
            map.put("item_money", new BigDecimal(accounts.get(i).getMoney()).add(new BigDecimal(totalMoney)).toString());
            map.put("item_selector", null);
            data.add(map);
        }

        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.item_list_account,
                new String[]{"item_icon", "item_text","item_money","item_selector"}, new int[]{R.id.item_icon,
                R.id.item_text,R.id.item_money,R.id.item_selector});
        list.setAdapter(simpleAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < data.size(); i++) {
                    if(position == i)
                        data.get(i).put("item_selector", R.drawable.ic_selector_blue);
                    else
                        data.get(i).put("item_selector", null);
                }
                simpleAdapter.notifyDataSetChanged();
                accountPopupWindowIn.dismiss();

                accountIn = accounts.get(position);
                nameIn.setText(accountIn.getAccountname());
                nameIn.setTextColor(getResources().getColor(R.color.white));
                detail1In.setVisibility(View.VISIBLE);
                detail2In.setVisibility(View.VISIBLE);
                detail1In.setText(getDetail1ByType(accountIn.getType()));
                detail2In.setText(accountIn.getAccountdetail());
                numIn.setTextColor(getResources().getColor(R.color.white));
                symbolIn.setTextColor(getResources().getColor(R.color.more_half_transparent_white));
                iconIn.setVisibility(View.VISIBLE);
                iconIn.setBackgroundResource(getIconByType(accountIn.getType()));
                btnIn.setCardBackgroundColor(Color.parseColor(accountIn.getColor()));
                emptyIn.setBackgroundColor(getResources().getColor(R.color.transparent));

            }
        });

        list.setDivider(null);//去除分割线

        View outOfWindow = (View)contentView.findViewById(R.id.outof_popup_window);
        outOfWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountPopupWindowIn.dismiss();
            }
        });
    }

    private void initRemarkPopupWindow() {
        View contentView = LayoutInflater.from(AccountTransferActivity.this).inflate(R.layout.popupwindow_remark, null);
        remarkPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        //设置各个控件的点击响应
        final EditText editText =  (EditText)contentView.findViewById(R.id.editText);
        editText.setText(remarkTransfer);
        editText.setSelection(remarkTransfer.length());
        ImageView close =  (ImageView)contentView.findViewById(R.id.close);
        ImageView ok =  (ImageView)contentView.findViewById(R.id.ok);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remarkPopupWindow.dismiss();
                editText.setText(remarkTransfer);
                editText.setSelection(remarkTransfer.length());
                updateBtnRemark();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remarkTransfer = editText.getText().toString().trim();
                remarkPopupWindow.dismiss();
                editText.setText(remarkTransfer);
                editText.setSelection(remarkTransfer.length());
                updateBtnRemark();
            }
        });

        View outOfWindow = (View)contentView.findViewById(R.id.outof_popup_window);
        outOfWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remarkPopupWindow.dismiss();
            }
        });
    }

    private void showPopupWindow(PopupWindow popupWindow) {
        View rootview = LayoutInflater.from(AccountTransferActivity.this).inflate(R.layout.activity_account_transfer, null);
        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    private int getPopupWindowIconByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return R.drawable.ic_cash_gray;
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return R.drawable.ic_bank_card_gray;
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return R.drawable.ic_credit_card_gray;
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return R.drawable.ic_alipay_gray;
            case ConstantUtils.ACCOUNT_TYPE_WECHAT:return R.drawable.ic_wechat_gray;
            default:return -1;
        }
    }

    private int getIconByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return R.drawable.ic_cash;
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return R.drawable.ic_bank_card;
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return R.drawable.ic_credit_card;
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return R.drawable.ic_alipay;
            case ConstantUtils.ACCOUNT_TYPE_WECHAT:return R.drawable.ic_wechat;
            default:return -1;
        }
    }

    private String getDetail1ByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return "现金类型：";
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return "发卡行：";
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return "发卡行：";
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return "账号：";
            case ConstantUtils.ACCOUNT_TYPE_WECHAT:return "账号：";
            default:return "";
        }
    }
}
