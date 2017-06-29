package com.mryu.richeditor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mryu.richeditor.RichEditor.RichEditor;
import com.mryu.richeditor.RichEditor.widget.EmojiLayout;
import com.mryu.richeditor.model.InsertModel;
import com.mryu.richeditor.model.UserModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_USER_CODE_CLICK = 2222;
    public final static int REQUEST_STOCK_CODE_CLICK = 3333;
    @BindView(R.id.richEditor)
    RichEditor richEditor;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.btn_at)
    Button btnAt;
    @BindView(R.id.btn_topic)
    Button btnTopic;
    @BindView(R.id.btn_get1)
    Button btnGet1;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.btn_emoji)
    Button btnEmoji;
    @BindView(R.id.emojiLayout)
    EmojiLayout emojiLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        emojiLayout.setEditTextSmile(richEditor);

    }


    @OnClick({R.id.btn_at, R.id.btn_topic, R.id.btn_get1, R.id.btn_emoji, R.id.activity_main})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_at:
                startActivityForResult(new Intent(MainActivity.this, UserListActivity.class), REQUEST_USER_CODE_CLICK);
                break;
            case R.id.btn_topic:
                startActivityForResult(new Intent(MainActivity.this, StockListActivity.class), REQUEST_STOCK_CODE_CLICK);
                break;
            case R.id.btn_get1:
                tvContent.setText(richEditor.getRichContent());
                break;
            case R.id.btn_emoji:
                emojiLayout.hideKeyboard();
                if (emojiLayout.getVisibility() == View.VISIBLE) {
                    emojiLayout.setVisibility(View.GONE);
                } else {
                    emojiLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.activity_main:
                emojiLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_USER_CODE_CLICK:
                    UserModel userModel = (UserModel) data.getSerializableExtra(UserListActivity.DATA);
                    richEditor.insertSpecialStr(new InsertModel("@", userModel.getUser_name(), "#f77500"));
                    break;
                case REQUEST_STOCK_CODE_CLICK:
                    UserModel stockModel = (UserModel) data.getSerializableExtra(StockListActivity.DATA);
                    richEditor.insertSpecialStr(new InsertModel("#", stockModel.getUser_name(), "#f77500"));
                    break;
            }
        }

    }

}
