package com.mryu.richeditor.RichEditor.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mryu.richeditor.R;
import com.mryu.richeditor.RichEditor.RichEditor;
import com.mryu.richeditor.RichEditor.Util.ScreenUtils;
import com.mryu.richeditor.RichEditor.adapter.ExpressionPagerAdapter;
import com.mryu.richeditor.RichEditor.adapter.SmileImageExpressionAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MryU93 on 2017/6/15.
 * Desc:
 */

public class EmojiLayout extends LinearLayout {

    private static int[] IMG_LIST = new int[]{
            R.drawable.e1, R.drawable.e2, R.drawable.e3, R.drawable.e4, R.drawable.e5, R.drawable.e6,
            R.drawable.e7, R.drawable.e8, R.drawable.e9, R.drawable.e10, R.drawable.e11, R.drawable.e12,
            R.drawable.e13, R.drawable.e14, R.drawable.e15, R.drawable.e16, R.drawable.e17, R.drawable.e18,
            R.drawable.e19, R.drawable.e20, R.drawable.e21, R.drawable.e22, R.drawable.e23, R.drawable.e24,
            R.drawable.e25, R.drawable.e26, R.drawable.e27, R.drawable.e28, R.drawable.e29, R.drawable.e30, R.drawable.e31,
            R.drawable.e32, R.drawable.e33, R.drawable.e34, R.drawable.e35, R.drawable.e36, R.drawable.e37,
    };

    @BindView(R.id.edittext_bar_vPager)
    ViewPager edittextBarVPager;
    @BindView(R.id.edittext_bar_viewGroup_face)
    LinearLayout edittextBarViewGroupFace;
    @BindView(R.id.edittext_bar_ll_face_container)
    LinearLayout edittextBarLlFaceContainer;
    @BindView(R.id.edittext_bar_more)
    LinearLayout edittextBarMore;

    private RichEditor editTextEmoji;
    private List<String> reslist = new ArrayList<>();
    private ImageView[] imageFaceViews;

    public EmojiLayout(Context context) {
        super(context);
        init(context);
    }

    public EmojiLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmojiLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_emoji_container, this, true);
        if (isInEditMode())
            return;
        ButterKnife.bind(this, view);
        initViews();

    }


    /**
     * 初始化View
     */
    private void initViews() {
        int size = ScreenUtils.dip2px(getContext(), 5);
        int marginSize = ScreenUtils.dip2px(getContext(), 5);
        // 表情list
        List<String> smile = new ArrayList<>();
        for (int i = 1; i <= IMG_LIST.length; i++) {
            smile.add("[e" + i + "]");
        }
        reslist.addAll(smile);
        // 初始化表情viewpager
        List<View> views = new ArrayList<>();
        int gridChildSize = (int) Math.ceil((double) reslist.size() / (double) 21);
        for (int i = 1; i <= gridChildSize; i++) {
            views.add(getGridChildView(i));
        }
        ImageView imageViewFace;
        imageFaceViews = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            LayoutParams margin = new LayoutParams(size, size);
            margin.setMargins(marginSize, 0, 0, 0);
            imageViewFace = new ImageView(getContext());
            imageViewFace.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            imageFaceViews[i] = imageViewFace;
            if (i == 0) {
                imageFaceViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageFaceViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            edittextBarViewGroupFace.addView(imageFaceViews[i], margin);
        }
        edittextBarVPager.setAdapter(new ExpressionPagerAdapter(views));
        edittextBarVPager.addOnPageChangeListener(new GuidePageChangeListener());

    }

    /**
     * 获取表情的gridview的子view
     */
    private View getGridChildView(int i) {
        View view = View.inflate(getContext(), R.layout.expression_gridview, null);
        LockGridView gv = (LockGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<>();
        int startInd = (i - 1) * 21;
        if ((startInd + 21) >= reslist.size()) {
            list.addAll(reslist.subList(startInd, startInd + (reslist.size() - startInd)));
        } else {
            list.addAll(reslist.subList(startInd, startInd + 21));
        }
        final SmileImageExpressionAdapter smileImageExpressionAdapter = new SmileImageExpressionAdapter(getContext(), 1, list);
        gv.setAdapter(smileImageExpressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = smileImageExpressionAdapter.getItem(position);
                editTextEmoji.insertIcon(filename);
            }
        });
        return view;
    }

    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {

            for (int i = 0; i < imageFaceViews.length; i++) {
                imageFaceViews[arg0].setBackgroundResource(R.drawable.page_indicator_focused);

                if (arg0 != i) {
                    imageFaceViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                }
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        Activity context = (Activity) getContext();
        if (context.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (context.getCurrentFocus() != null) {
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 显示键盘
     */
    public void showKeyboard() {
        editTextEmoji.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editTextEmoji.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editTextEmoji, 0);
    }


    public EditText getEditTextSmile() {
        return editTextEmoji;
    }

    public void setEditTextSmile(RichEditor editTextSmile) {
        this.editTextEmoji = editTextSmile;
        editTextSmile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });
    }
}