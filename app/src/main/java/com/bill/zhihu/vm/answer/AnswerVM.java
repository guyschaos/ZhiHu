package com.bill.zhihu.vm.answer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;

import com.bill.zhihu.api.bean.feeds.FeedsItem;
import com.bill.zhihu.databinding.AnswerViewBinding;
import com.bill.zhihu.model.AnswerModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.BuglyLog;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by bill_lv on 2016/3/29.
 */
public class AnswerVM {

    private static final int PROGRESS_ANIM_DURATION = 500;

    private static final String TAG = "AnswerVM";

    private AnswerModel model;
    private Activity activity;
    private AnswerViewBinding binding;

    public AnswerVM(Activity activity, AnswerViewBinding binding) {
        this.activity = activity;
        this.binding = binding;
        this.model = new AnswerModel();
        binding.answer.bindJs(binding.answer, "ZhihuAndroid");
    }

    public void loadAnswer(String id) {
        model.getAnswer(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("load answer completed");
                        stopLoadingAnim();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("load answer error");
                        BuglyLog.d(TAG, e.toString());
                    }

                    @Override
                    public void onNext(String content) {
                        binding.answer.setContent(content);
                    }
                });
    }

    public void setAuthor(FeedsItem item) {
        String avatarUrl = item.target.author.avatarUrl.replace("_s", "_l");
        ImageLoader.getInstance().displayImage(avatarUrl,binding.avatar);
        binding.name.setText(item.target.author.name);
        binding.intro.setText(item.target.author.headline);
        binding.vote.setText(item.target.voteupCount + "");
    }

    /**
     * webview加载动画
     */
    public void playLoadingAnim() {
        binding.loadingImg.spin();
    }

    /**
     * 加载动画消失
     */
    private void stopLoadingAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.loadingImg, "alpha", 1, 0);
        animator.setDuration(PROGRESS_ANIM_DURATION);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.loadingImg.stopSpinning();
            }
        });
        animator.start();
    }

}
