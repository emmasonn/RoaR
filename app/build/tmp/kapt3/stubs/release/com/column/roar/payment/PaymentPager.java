package com.column.roar.payment;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J8\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u0018H\u0002J\b\u0010\u001e\u001a\u00020\u0016H\u0002J\"\u0010\u001f\u001a\u00020\u00162\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!2\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J&\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010*2\b\u0010+\u001a\u0004\u0018\u00010,H\u0016J\b\u0010-\u001a\u00020\u0016H\u0002J\b\u0010.\u001a\u00020\u0016H\u0003R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0007\u001a\u00020\b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\t\u0010\nR\u000e\u0010\r\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006/"}, d2 = {"Lcom/column/roar/payment/PaymentPager;", "Landroidx/fragment/app/Fragment;", "()V", "alertDialog", "Landroidx/appcompat/app/AlertDialog;", "amountView", "Lcom/google/android/material/textfield/TextInputEditText;", "argsNav", "Lcom/column/roar/payment/PaymentPagerArgs;", "getArgsNav", "()Lcom/column/roar/payment/PaymentPagerArgs;", "argsNav$delegate", "Landroidx/navigation/NavArgsLazy;", "emailAddressView", "firstNameView", "lastNameView", "paymentView", "Lcom/google/android/material/textfield/TextInputLayout;", "phoneNumberView", "ravePayment", "Lcom/flutterwave/raveandroid/RaveUiManager;", "makePayment", "", "firstName", "", "lastName", "emailAddress", "amount", "phoneNumber", "description", "moveToPaymentLink", "onActivityResult", "requestCode", "", "resultCode", "data", "Landroid/content/Intent;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "processPayment", "successDialog", "app_release"})
public final class PaymentPager extends androidx.fragment.app.Fragment {
    private com.flutterwave.raveandroid.RaveUiManager ravePayment;
    private com.google.android.material.textfield.TextInputEditText firstNameView;
    private com.google.android.material.textfield.TextInputEditText lastNameView;
    private com.google.android.material.textfield.TextInputEditText emailAddressView;
    private com.google.android.material.textfield.TextInputEditText amountView;
    private com.google.android.material.textfield.TextInputLayout paymentView;
    private com.google.android.material.textfield.TextInputEditText phoneNumberView;
    private androidx.appcompat.app.AlertDialog alertDialog;
    private final androidx.navigation.NavArgsLazy argsNav$delegate = null;
    
    public PaymentPager() {
        super();
    }
    
    private final com.column.roar.payment.PaymentPagerArgs getArgsNav() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    @java.lang.Override
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    private final void processPayment() {
    }
    
    private final void makePayment(java.lang.String firstName, java.lang.String lastName, java.lang.String emailAddress, java.lang.String amount, java.lang.String phoneNumber, java.lang.String description) {
    }
    
    private final void moveToPaymentLink() {
    }
    
    @java.lang.Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable
    android.content.Intent data) {
    }
    
    @android.annotation.SuppressLint(value = {"InflateParams"})
    private final void successDialog() {
    }
}