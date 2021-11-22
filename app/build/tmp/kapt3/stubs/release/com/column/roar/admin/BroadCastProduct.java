package com.column.roar.admin;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u0015H\u0003J\b\u0010\u0016\u001a\u00020\u0010H\u0002J\u0010\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0015H\u0002J\u0010\u0010\u0019\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u0012\u0010\u001a\u001a\u00020\u00102\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0016J&\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\u0006\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\"2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0016J\u001a\u0010#\u001a\u00020\u00102\u0006\u0010$\u001a\u00020\u001e2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0016J\u0010\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020(H\u0002J\u0010\u0010)\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lcom/column/roar/admin/BroadCastProduct;", "Landroidx/fragment/app/Fragment;", "()V", "editDialog", "Landroidx/appcompat/app/AlertDialog;", "fireStore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "managePropertyAdapter", "Lcom/column/roar/listAdapters/ManagePropertyListAdapter;", "productCollection", "Lcom/google/firebase/firestore/CollectionReference;", "productsRef", "Lcom/google/firebase/firestore/Query;", "swipeContainer", "Landroidx/swiperefreshlayout/widget/SwipeRefreshLayout;", "approveItem", "", "id", "", "deleteCard", "firebaseProperty", "Lcom/column/roar/cloudModel/FirebaseProperty;", "fetchProducts", "navigateToAddOthers", "others", "notifySubscribers", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "onViewCreated", "view", "sendNotification", "Lkotlinx/coroutines/Job;", "notification", "Lcom/column/roar/notification/data/PushNotification;", "suspendAccount", "app_release"})
public final class BroadCastProduct extends androidx.fragment.app.Fragment {
    private com.column.roar.listAdapters.ManagePropertyListAdapter managePropertyAdapter;
    private com.google.firebase.firestore.FirebaseFirestore fireStore;
    private com.google.firebase.firestore.Query productsRef;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeContainer;
    private androidx.appcompat.app.AlertDialog editDialog;
    private com.google.firebase.firestore.CollectionReference productCollection;
    
    public BroadCastProduct() {
        super();
    }
    
    @java.lang.Override
    public void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @org.jetbrains.annotations.Nullable
    @java.lang.Override
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override
    public void onViewCreated(@org.jetbrains.annotations.NotNull
    android.view.View view, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void fetchProducts() {
    }
    
    private final void navigateToAddOthers(com.column.roar.cloudModel.FirebaseProperty others) {
    }
    
    private final void notifySubscribers(com.column.roar.cloudModel.FirebaseProperty firebaseProperty) {
    }
    
    private final kotlinx.coroutines.Job sendNotification(com.column.roar.notification.data.PushNotification notification) {
        return null;
    }
    
    @android.annotation.SuppressLint(value = {"InflateParams"})
    private final androidx.appcompat.app.AlertDialog editDialog(com.column.roar.cloudModel.FirebaseProperty firebaseProperty) {
        return null;
    }
    
    private final void approveItem(java.lang.String id) {
    }
    
    private final void suspendAccount(java.lang.String id) {
    }
    
    private final void deleteCard(java.lang.String id) {
    }
}