package com.column.roar.listAdapters;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u00112\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001:\u0003\u0010\u0011\u0012B\r\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\f\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000bH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/column/roar/listAdapters/ClientListAdapter;", "Landroidx/recyclerview/widget/ListAdapter;", "Lcom/column/roar/cloudModel/FirebaseUser;", "Lcom/column/roar/listAdapters/ClientListAdapter$ClientListViewHolder;", "clickListener", "Lcom/column/roar/listAdapters/ClientListAdapter$UserClickListener;", "(Lcom/column/roar/listAdapters/ClientListAdapter$UserClickListener;)V", "onBindViewHolder", "", "holder", "position", "", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "ClientListViewHolder", "Companion", "UserClickListener", "app_debug"})
public final class ClientListAdapter extends androidx.recyclerview.widget.ListAdapter<com.column.roar.cloudModel.FirebaseUser, com.column.roar.listAdapters.ClientListAdapter.ClientListViewHolder> {
    private final com.column.roar.listAdapters.ClientListAdapter.UserClickListener clickListener = null;
    @org.jetbrains.annotations.NotNull
    public static final com.column.roar.listAdapters.ClientListAdapter.Companion Companion = null;
    @org.jetbrains.annotations.NotNull
    private static final androidx.recyclerview.widget.DiffUtil.ItemCallback<com.column.roar.cloudModel.FirebaseUser> diffUtil = null;
    
    public ClientListAdapter(@org.jetbrains.annotations.NotNull
    com.column.roar.listAdapters.ClientListAdapter.UserClickListener clickListener) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public com.column.roar.listAdapters.ClientListAdapter.ClientListViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.column.roar.listAdapters.ClientListAdapter.ClientListViewHolder holder, int position) {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \u0007*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n \u0007*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000f\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\n \u0007*\u0004\u0018\u00010\u00110\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/column/roar/listAdapters/ClientListAdapter$ClientListViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Landroid/view/View;", "(Landroid/view/View;)V", "brandName", "Landroid/widget/TextView;", "kotlin.jvm.PlatformType", "coverImage", "Landroid/widget/ImageView;", "fullName", "isCertified", "phoneNumber", "resource", "Landroid/content/res/Resources;", "subscription", "viewAccount", "Lcom/google/android/material/button/MaterialButton;", "bind", "", "data", "Lcom/column/roar/cloudModel/FirebaseUser;", "clickListener", "Lcom/column/roar/listAdapters/ClientListAdapter$UserClickListener;", "app_debug"})
    public static final class ClientListViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final android.view.View itemView = null;
        private final android.widget.TextView fullName = null;
        private final android.widget.ImageView coverImage = null;
        private final android.widget.TextView phoneNumber = null;
        private final com.google.android.material.button.MaterialButton viewAccount = null;
        private final android.widget.TextView brandName = null;
        private final android.widget.TextView subscription = null;
        private final android.widget.TextView isCertified = null;
        private final android.content.res.Resources resource = null;
        
        public ClientListViewHolder(@org.jetbrains.annotations.NotNull
        android.view.View itemView) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull
        com.column.roar.cloudModel.FirebaseUser data, @org.jetbrains.annotations.NotNull
        com.column.roar.listAdapters.ClientListAdapter.UserClickListener clickListener) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B(\u0012!\u0010\u0002\u001a\u001d\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\b0\u0003\u00a2\u0006\u0002\u0010\tR,\u0010\u0002\u001a\u001d\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\b0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lcom/column/roar/listAdapters/ClientListAdapter$UserClickListener;", "", "listener", "Lkotlin/Function1;", "Lcom/column/roar/cloudModel/FirebaseUser;", "Lkotlin/ParameterName;", "name", "user", "", "(Lkotlin/jvm/functions/Function1;)V", "getListener", "()Lkotlin/jvm/functions/Function1;", "app_debug"})
    public static final class UserClickListener {
        @org.jetbrains.annotations.NotNull
        private final kotlin.jvm.functions.Function1<com.column.roar.cloudModel.FirebaseUser, kotlin.Unit> listener = null;
        
        public UserClickListener(@org.jetbrains.annotations.NotNull
        kotlin.jvm.functions.Function1<? super com.column.roar.cloudModel.FirebaseUser, kotlin.Unit> listener) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final kotlin.jvm.functions.Function1<com.column.roar.cloudModel.FirebaseUser, kotlin.Unit> getListener() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/column/roar/listAdapters/ClientListAdapter$Companion;", "", "()V", "diffUtil", "Landroidx/recyclerview/widget/DiffUtil$ItemCallback;", "Lcom/column/roar/cloudModel/FirebaseUser;", "getDiffUtil", "()Landroidx/recyclerview/widget/DiffUtil$ItemCallback;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final androidx.recyclerview.widget.DiffUtil.ItemCallback<com.column.roar.cloudModel.FirebaseUser> getDiffUtil() {
            return null;
        }
    }
}