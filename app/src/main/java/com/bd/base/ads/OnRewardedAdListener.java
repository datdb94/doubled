package com.bd.base.ads;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.rewarded.RewardItem;

public interface OnRewardedAdListener {
    void onUserEarnedReward(@NonNull RewardItem rewardItem);

    void onRewardedAdFailed();

    void onRewardedAdOpened();

    void onRewardedAdClosed();
}
