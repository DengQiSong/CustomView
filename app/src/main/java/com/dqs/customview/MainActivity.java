package com.dqs.customview;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.dqs.customview.util.SnackbarUtil;
import com.dqs.customview.widget.CommentView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.cv_comment)
    CommentView commentView;
    @BindView(R.id.coordinator)
    CoordinatorLayout layout;


    String action="这是action";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        commentView.setOnClickComment(new CommentView.OnClickComment() {
            @Override
            public void onClickLike(String Like) {

                Snackbar.make(layout,Like,Snackbar.LENGTH_LONG).setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "你点击了action", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }

            @Override
            public void onClickDis(String Dis) {
                SnackbarUtil.ShortSnackbar(layout,"妹子向你发来一条消息",SnackbarUtil.red,SnackbarUtil.blue).show();
            }
        });
    }

}
