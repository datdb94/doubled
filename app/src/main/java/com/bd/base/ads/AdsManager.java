package com.bd.base.ads;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdsManager {
    private static AdsManager adsManager;
    private static Context context;
    private static String INTERSTITIAL_ID, REWARDED_ID;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedVideoAd;
    private int count = 0;
    private int frequency = 3;
    private boolean isPro = false;

    private AdsManager() {
    }

    public static AdsManager getInstance() {
        if (adsManager == null) {
            adsManager = new AdsManager();
        }
        return adsManager;
    }

    public static void init(Context ctx, String interstitialID, String rewardedID) {
        context = ctx;
        INTERSTITIAL_ID = interstitialID;
        REWARDED_ID = rewardedID;
        MobileAds.initialize(context);
    }

    public void setPro(boolean pro) {
        this.isPro = pro;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void loadInterstitialAds() {
        if (isPro) {
            return;
        }
        if (interstitialAd == null || !interstitialAd.isLoaded()) {
            interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(INTERSTITIAL_ID);
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    /**
     * @param a:         true luôn hiện quảng cáo, false sau 3 lần mới hiển thị
     * @param adListener
     */
    public void showInterstitialAds(boolean a, final OnInterstitialAdListener adListener) {
        if (isPro) {
            adListener.onAdClosed();
            return;
        }
        if (interstitialAd != null && interstitialAd.isLoaded() && (count % frequency == 0 || a)) {
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    adsInterstitialClose(adListener);
                    super.onAdClosed();
                }
            });
            interstitialAd.show();
        } else {
            adsInterstitialClose(adListener);
        }

        if (!a) {
            count++;
        }
    }

    private void adsInterstitialClose(OnInterstitialAdListener adListener) {
        if (adListener != null) {
            adListener.onAdClosed();
        }
        loadInterstitialAds();
    }

    public void showRewardedAds(final Activity activity, final OnRewardedAdListener onRewardedAdListener) {
        rewardedVideoAd = new RewardedAd(activity, REWARDED_ID);
        rewardedVideoAd.loadAd(new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                rewardedVideoAd.show(activity, new RewardedAdCallback() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        onRewardedAdListener.onUserEarnedReward(rewardItem);
                    }

                    @Override
                    public void onRewardedAdOpened() {
                        super.onRewardedAdOpened();
                        onRewardedAdListener.onRewardedAdOpened();
                    }

                    @Override
                    public void onRewardedAdClosed() {
                        super.onRewardedAdClosed();
                        onRewardedAdListener.onRewardedAdClosed();
                    }

                    @Override
                    public void onRewardedAdFailedToShow(AdError adError) {
                        super.onRewardedAdFailedToShow(adError);
                        onRewardedAdListener.onRewardedAdFailed();
                    }
                });
                super.onRewardedAdLoaded();
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                super.onRewardedAdFailedToLoad(loadAdError);
                onRewardedAdListener.onRewardedAdFailed();
            }
        });
    }

    public void loadBanner(AdView adView) {
        if (isPro) {
            adView.setVisibility(View.GONE);
            return;
        }
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
