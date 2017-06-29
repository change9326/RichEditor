package com.mryu.richeditor.RichEditor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mryu.richeditor.RichEditor.Util.ScreenUtils;
import com.mryu.richeditor.model.InsertModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MryU93 on 2017/6/13.
 * <p>
 * Desc:
 */

public class RichEditor extends AppCompatEditText {

    private static String TAG = "RichEditor";
    private int size;
    private int maxLength = 2000;
    private List<InsertModel> insertModelList = new ArrayList<>();
    private static final int BACKGROUND_COLOR = Color.parseColor("#FFDEAD"); // 默认,话题背景高亮颜色
    private static Context mContext;

    public RichEditor(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public RichEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        if (isInEditMode())
            return;
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength)};
        setFilters(filters);
        size = ScreenUtils.dip2px(context, 20);
        initView();
    }

    public static int ParseIconResId(String name) {
        name = name.substring(1, name.length() - 1);
        int resId = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
        return resId;
    }

    public void insertIcon(String name) {
        String curString = getText().toString();
        if ((curString.length() + name.length()) > maxLength) {
            return;
        }
        Drawable drawable = ContextCompat.getDrawable(mContext, ParseIconResId(name));

        if (drawable == null)
            return;
        drawable.setBounds(0, 0, size, size);//这里设置图片的大小
        ImageSpan imageSpan = new ImageSpan(drawable);
        SpannableString spannableString = new SpannableString(name);
        spannableString.setSpan(imageSpan, 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        int index = Math.max(getSelectionStart(), 0);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getText());
        spannableStringBuilder.insert(index, spannableString);

        setText(spannableStringBuilder);
        setSelection(index + spannableString.length());
    }


    /**
     * 初始化控件,一些监听
     */
    private void initView() {

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                resolveDeleteSpecialStr();
            }
        });

        /**
         * 监听删除键 <br/>
         * 1.光标在话题后面,将整个话题内容删除 <br/>
         * 2.光标在普通文字后面,删除一个字符
         *
         */
        this.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {

                    int selectionStart = getSelectionStart();
                    int selectionEnd = getSelectionEnd();

                    /**
                     * 如果光标起始和结束不在同一位置,删除文本
                     */
                    if (selectionStart != selectionEnd) {
                        // 查询文本是否属于目标对象,若是移除列表数据
                        String tagetText = getText().toString().substring(
                                selectionStart, selectionEnd);
                        for (int i = 0; i < insertModelList.size(); i++) {
                            InsertModel object = insertModelList.get(i);
                            if (tagetText.equals(object.getInsertContent())) {
                                insertModelList.remove(object);
                            }
                        }
                        return false;
                    }


                    int lastPos = 0;
                    Editable editable = getText();
                    // 遍历判断光标的位置
                    for (int i = 0; i < insertModelList.size(); i++) {
                        String objectText = insertModelList.get(i).getInsertContent();
                        lastPos = getText().toString().indexOf(objectText, lastPos);
                        if (lastPos != -1) {
                            if (selectionStart != 0 && selectionStart >= lastPos && selectionStart <= (lastPos + objectText.length())) {
                                // 选中话题
                                setSelection(lastPos, lastPos + objectText.length());
                                // 设置背景色
                                editable.setSpan(new BackgroundColorSpan(BACKGROUND_COLOR), lastPos, lastPos + objectText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                return true;
                            }
                        }
                        lastPos += objectText.length();
                    }
                }

                return false;
            }
        });
    }

    /**
     * 监听光标的位置,若光标处于话题内容中间则移动光标到话题结束位置
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (insertModelList == null || insertModelList.size() == 0)
            return;
        int startPostion = 0;
        int endPostion = 0;
        String insertContent = "";
        for (int i = 0; i < insertModelList.size(); i++) {
            insertContent = insertModelList.get(i).getInsertContent();
            startPostion = getText().toString().indexOf(insertContent);
            endPostion = startPostion + insertContent.length();
            if (startPostion != -1 && selStart > startPostion
                    && selStart <= endPostion) {// 若光标处于话题内容中间则移动光标到话题结束位置
                setSelection(endPostion);
            }
        }
    }

    /**
     * @param insertModel 插入对象
     */
    public void insertSpecialStr(InsertModel insertModel) {
        if (insertModel == null)
            return;
        //避免插入相同的数据
        for (InsertModel model : insertModelList) {
            if ((model.getInsertContent().replace(model.getInsertRule(), "")).equals(insertModel.getInsertContent()) && model.getInsertRule().equals(insertModel.getInsertRule())) {
                Toast.makeText(mContext, "不可重复插入", Toast.LENGTH_LONG).show();
                return;
            }
        }
        String insertRule = insertModel.getInsertRule();
        String insertContent = insertModel.getInsertContent();
        String insertColor = insertModel.getInsertColor();
        if (TextUtils.isEmpty(insertRule) || TextUtils.isEmpty(insertContent))
            return;
        if (insertRule.equals("@"))
            insertContent = insertRule + insertContent;
        else
            insertContent = insertRule + insertContent + insertRule;
        insertModel.setInsertContent(insertContent);

        insertModelList.add(insertModel);

        //将特殊字符插入到EditText 中显示
        int index = getSelectionStart();//光标位置
        Editable editable = getText();//原先内容
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editable);
        Spanned htmlText = Html.fromHtml(String.format(String.format("<font color='%s'>" + insertContent + "</font>", insertColor)));
        spannableStringBuilder.insert(index, htmlText);
        spannableStringBuilder.insert(index + htmlText.length(), "\b");
        setText(spannableStringBuilder);
        setSelection(index + htmlText.length() + 1);
    }

    /**
     * 获取普通文本内容
     */
    public String getRichContent() {
        String content = getText().toString();
        if (insertModelList != null && insertModelList.size() > 0) {
            for (int i = 0; i < insertModelList.size(); i++) {
                InsertModel inertModel = insertModelList.get(i);
                content = content.replace(inertModel.getInsertContent(), "");
            }
        }
        return content.trim();
    }

    /**
     * 获取特殊字符列表
     */
    public List<InsertModel> getRichInsertList() {
        List<InsertModel> objectsList = new ArrayList<>();
        if (insertModelList != null && insertModelList.size() > 0) {
            for (int i = 0; i < insertModelList.size(); i++) {
                InsertModel inertModel = insertModelList.get(i);
                objectsList.add(new InsertModel(inertModel.getInsertRule(), inertModel.getInsertContent().replace(inertModel.getInsertRule(), ""), inertModel.getInsertColor()));
            }
        }
        return objectsList;
    }


    /**
     * 删除缓存列表
     */
    private void resolveDeleteSpecialStr() {
        String tagetText = getText().toString();
        if (TextUtils.isEmpty(tagetText)) {
            insertModelList.clear();
            return;
        }
        for (int i = 0; i < insertModelList.size(); i++) {
            InsertModel object = insertModelList.get(i);
            if (tagetText.indexOf(object.getInsertContent()) == -1) {
                insertModelList.remove(object);
            }
        }
    }


    private boolean isRequest = false;

    public boolean isRequest() {
        return isRequest;
    }

    //是否可以点击滑动
    public void setIsRequest(boolean isRequest) {
        this.isRequest = isRequest;
    }


    public int getEditTextMaxLength() {
        return maxLength;
    }

    //最大可输入长度
    public void setEditTextMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(isRequest);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(event);
    }
}
