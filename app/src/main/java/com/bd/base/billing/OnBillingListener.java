package com.bd.base.billing;

import com.anjlab.android.iab.v3.TransactionDetails;

public interface OnBillingListener {
    void onProductPurchased(String productId, TransactionDetails details);

    void onBillingInitialized();
}
