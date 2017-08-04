package com.dqs.customview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dqs.customview.R;

/**
 * Created by Dengqs on 2017/8/1
 */

public class CommentView extends LinearLayout implements Animator.AnimatorListener {
    private static final String TAG = "CommentView";

    private OnClickComment onClickComment;

    public CommentView(Context context) {
        this(context, null);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        view = LayoutInflater.from(context).inflate(R.layout.comment_view, this);
        init();
        bindListener();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        gao();
    }

    private int like = 11;
    private int disLike = 22; //点赞数,差评数
    private float fLike, fDis;
    private View view;
    private LinearLayout likeBack, disBack;
    private ImageView imageLike, imageDis;
    private TextView likeNum, disNum, likeText, disText;

    private AnimationDrawable animLike, animDis; //笑脸帧动画
    private ValueAnimator animatorBack; //背景拉伸动画

    private String defaultLike = "点赞";
    private String defalutDis = "无感";

    private int type = 0; //选择执行帧动画的笑脸 //0 笑脸 1 哭脸
    private boolean isClose = false; //判断收起动画

    private String defaluteShadow = "#7F484848";

    /***********************对外方法***************************/

    public void setOnClickComment(@Nullable OnClickComment onClickComment) {
        this.onClickComment = onClickComment;
    }

    public void setNum(int like, int dislike) {
        //设置百分比
        float count = like + dislike;
        fLike = like / count;
        fDis = dislike / count;
        this.like = (int) (fLike * 100);
        this.disLike = (int) (fDis * 100);
        this.setLike(this.like);
        this.setDisLike(this.disLike);
    }

    public void setLike(int like) {
        likeNum.setText(like + "%");
        invalidateView();
    }

    public void setDisLike(int disLike) {
        disNum.setText(disLike + "%");
        invalidateView();
    }

    public void setText(String LikeText, String DisLikeText) {
        this.setLikeText(LikeText);
        this.setDisLikeText(DisLikeText);
    }

    public void setLikeText(String like) {
        likeText.setText(like + "%");
        invalidateView();
    }

    public void setDisLikeText(String disLike) {
        disText.setText(disLike + "%");
        invalidateView();
    }

    /**
     * ————————————————————————————————————————————————————————
     **/


    private void init() {
        //初始化
        setBackgroundColor(Color.TRANSPARENT); //开始透明

        //计算百分比
        float count = like + disLike;
        fLike = like / count;
        fDis = disLike / count;
        like = (int) (fLike * 100);
        disLike = (int) (fDis * 100);

        likeBack = (LinearLayout) view.findViewById(R.id.likeBack);
        disBack = (LinearLayout) view.findViewById(R.id.disBack);

        //初始化控件
        imageLike = (ImageView) view.findViewById(R.id.imageLike);
        imageLike.setBackgroundResource(R.drawable.animation_like);
        //获得帧动画
        animLike = (AnimationDrawable) imageLike.getBackground();

        imageDis = (ImageView) view.findViewById(R.id.imageDis);
        imageDis.setBackgroundResource(R.drawable.animation_dislike);
        //获得帧动画
        animDis = (AnimationDrawable) imageDis.getBackground();

        likeNum = (TextView) view.findViewById(R.id.likeNum);
        likeNum.setText(like + "%");
        disNum = (TextView) view.findViewById(R.id.disNum);
        disNum.setText(disLike + "%");
        likeText = (TextView) view.findViewById(R.id.likeText);
        likeText.setText(defaultLike);
        disText = (TextView) view.findViewById(R.id.disText);
        disText.setText(defalutDis);

        //隐藏文字
        setVisibities(GONE);
    }

    public void setVisibities(int v) {
        likeNum.setVisibility(v);
        disNum.setVisibility(v);
        likeText.setVisibility(v);
        disText.setVisibility(v);
    }

    private void gao() {
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.height = 550;
        setLayoutParams(layoutParams);
    }

    //绑定监听
    private void bindListener() {
        imageLike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 0;
                animBack();
                setVisibities(VISIBLE);
                setBackgroundColor(Color.parseColor(defaluteShadow));
                disBack.setBackgroundResource(R.drawable.white_background);
                likeBack.setBackgroundResource(R.drawable.yellow_background);
                imageDis.setBackground(null);
                imageDis.setBackgroundResource(R.drawable.animation_dislike);
                animDis = (AnimationDrawable) imageDis.getBackground();
            }
        });
        imageDis.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 1; //设置动画对象
                animBack(); //拉伸背景
                setVisibities(VISIBLE); //隐藏文字
                //切换背景色
                setBackgroundColor(Color.parseColor(defaluteShadow));
                likeBack.setBackgroundResource(R.drawable.white_background);
                disBack.setBackgroundResource(R.drawable.yellow_background);
                //重置帧动画
                imageLike.setBackground(null);
                imageLike.setBackgroundResource(R.drawable.animation_like);
                animLike = (AnimationDrawable) imageLike.getBackground();
            }
        });

    }

    //背景伸展动画
    private void animBack() {
        //动画执行中不能点击
        imageDis.setClickable(false);
        imageLike.setClickable(false);

        final int max = Math.max(like * 4, disLike * 4);
        animatorBack = ValueAnimator.ofInt(5, max);
        animatorBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int magrin = (int) animation.getAnimatedValue();
                LayoutParams paramsLike = (LayoutParams) imageLike.getLayoutParams();
                paramsLike.bottomMargin = magrin;
                int a = like * 4;
                int b = disLike * 4;
                if (magrin <= like * 4) {
                    imageLike.setLayoutParams(paramsLike);
                    Log.e(TAG, "onAnimationUpdate: " + "magrin:" + magrin + " ,like:" + a);
                }
                if (magrin <= disLike * 4) {
                    imageDis.setLayoutParams(paramsLike);
                    Log.e(TAG, "onAnimationUpdate: " + "magrin:" + magrin + " ,disLike:" + b);
                }
            }
        });
        isClose = false;
        animatorBack.addListener(this);
        animatorBack.setDuration(500);
        animatorBack.start();
    }


    //背景收回动画
    private void setBackUp() {
        final int max = Math.max(like * 4, disLike * 4);
        animatorBack = ValueAnimator.ofInt(max, 5);
        animatorBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int magrin = (int) animation.getAnimatedValue();
                LayoutParams paramsLike = (LayoutParams) imageLike.getLayoutParams();
                paramsLike.bottomMargin = magrin;

                if (magrin <= like * 4) {
                    imageLike.setLayoutParams(paramsLike);
                }
                if (magrin <= disLike * 4) {
                    imageDis.setLayoutParams(paramsLike);
                }
            }
        });
        animatorBack.addListener(this);
        animatorBack.setDuration(500);
        animatorBack.start();
    }

    private void invalidateView() {
        //判断当前线程
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //UI线程则
            invalidate();
        } else {
            //非UI则
            postInvalidate();
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //重置帧动画
        animDis.stop();
        animLike.stop();
        //关闭时不执行帧动画
        if (isClose) {
            //收回后可点击
            imageDis.setClickable(true);
            imageLike.setClickable(true);
            //隐藏文字
            setVisibities(GONE);
            //恢复透明
            setBackgroundColor(Color.TRANSPARENT);
            return;
        }
        isClose = true;

        if (type == 0) {
            animLike.start();
            objectY(imageLike);
            onClickComment.onClickLike(defaultLike+"成功");
        } else {
            animDis.start();
            objectX(imageDis);
            onClickComment.onClickDis(defalutDis+"成功");
        }

    }

    private void objectY(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -10.0f, 0.0f, 10.0f, 0.0f, -10.0f, 0.0f, 10.0f, 0);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setDuration(1500);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setBackUp(); //执行回弹动画
            }
        });
    }

    private void objectX(View view) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", -10.0f, 0.0f, 10.0f, 0.0f, -10.0f, 0.0f, 10.0f, 0);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setDuration(1500);
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setBackUp(); //执行回弹动画
            }
        });
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public interface OnClickComment {
        void onClickLike(String Like);

        void onClickDis(String Dis);

    }
}
