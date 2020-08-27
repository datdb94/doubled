package com.bd.base.billing;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bd.base.R;

public class BillingManager {
    private static BillingManager instance;

    private static final String LICENSE_KEY = null;
    private static final String MERCHANT_ID = null;
    private BillingProcessor billingProcessor;
    private Activity context;
    private OnBillingListener onBillingListener;
    private OnRestorePurchaseListener onRestorePurchaseListener;
    private String idRestore;

    private BillingManager() {
    }

    public static void init() {
        instance = new BillingManager();
    }

    public void initBilling(Activity ctx, final OnBillingListener onBillingListener) {
        this.onBillingListener = onBillingListener;
        this.context = ctx;
        setupBillingProcessor();
    }

    private void setupBillingProcessor() {
        billingProcessor = new BillingProcessor(context, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                Toast.makeText(context, R.string.purchase_success, Toast.LENGTH_SHORT).show();
                onBillingListener.onProductPurchased(productId, details);
            }

            @Override
            public void onPurchaseHistoryRestored() {
            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {
                Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingInitialized() {
                onBillingListener.onBillingInitialized();
                if (onRestorePurchaseListener != null && idRestore != null) {
                    onRestorePurchaseListener.restorePurchase(billingProcessor.isPurchased(idRestore));
                }
            }
        });
    }

    public static BillingManager getInstance() {
        return instance;
    }

    public SkuDetails getSkuDetails(String id) {
        if (billingProcessor != null && billingProcessor.isInitialized()) {
            return billingProcessor.getPurchaseListingDetails(id);
        }
        return null;
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return billingProcessor.handleActivityResult(requestCode, resultCode, data);
    }

    public boolean isPurchase(String id) {
        if (billingProcessor != null && billingProcessor.isInitialized()) {
            return billingProcessor.isPurchased(id);
        }
        return false;
    }

    public void purchase(String id) {
        this.idRestore = null;
        this.onRestorePurchaseListener = null;
        if (billingProcessor != null && billingProcessor.isInitialized()) {
            billingProcessor.purchase(context, id);
        }
    }

    public void restorePurchase(final String id, OnRestorePurchaseListener onRestorePurchaseListener) {
        if (billingProcessor != null && billingProcessor.isInitialized()) {
            if (billingProcessor.isPurchased(id)) {
                onRestorePurchaseListener.restorePurchase(true);
                return;
            }
            billingProcessor.release();
        }
        this.idRestore = id;
        this.onRestorePurchaseListener = onRestorePurchaseListener;
        setupBillingProcessor();
    }

}
