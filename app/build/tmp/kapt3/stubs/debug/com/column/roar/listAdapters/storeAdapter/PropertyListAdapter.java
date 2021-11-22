package com.column.roar.listAdapters.storeAdapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 \u00182\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001:\u0002\u0018\u0019B\u0015\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0016J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\rH\u0016J\u0018\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\rH\u0016J\u000e\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u000bR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/column/roar/listAdapters/storeAdapter/PropertyListAdapter;", "Landroidx/recyclerview/widget/ListAdapter;", "Lcom/column/roar/cloudModel/FirebaseProperty;", "Lcom/column/roar/listAdapters/storeAdapter/StaggeredCardViewHolder;", "propertyListener", "Lcom/column/roar/listAdapters/storeAdapter/PropertyListAdapter$PropertyClickListener;", "lifecycleOwner", "Landroidx/lifecycle/LifecycleOwner;", "(Lcom/column/roar/listAdapters/storeAdapter/PropertyListAdapter$PropertyClickListener;Landroidx/lifecycle/LifecycleOwner;)V", "mutableNativeAd", "Landroidx/lifecycle/MutableLiveData;", "Lcom/google/android/gms/ads/nativead/NativeAd;", "getItemViewType", "", "position", "onBindViewHolder", "", "holder", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setNativeAd", "_nativeAd", "Companion", "PropertyClickListener", "app_debug"})
public final class PropertyListAdapter extends androidx.recyclerview.widget.ListAdapter<com.column.roar.cloudModel.FirebaseProperty, com.column.roar.listAdapters.storeAdapter.StaggeredCardViewHolder> {
    private final com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.PropertyClickListener propertyListener = null;
    private final androidx.lifecycle.LifecycleOwner lifecycleOwner = null;
    private final androidx.lifecycle.MutableLiveData<com.google.android.gms.ads.nativead.NativeAd> mutableNativeAd = null;
    @org.jetbrains.annotations.NotNull
    public static final com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.Companion Companion = null;
    @org.jetbrains.annotations.NotNull
    private static final androidx.recyclerview.widget.DiffUtil.ItemCallback<com.column.roar.cloudModel.FirebaseProperty> diffUtil = null;
    
    public PropertyListAdapter(@org.jetbrains.annotations.NotNull
    com.column.roar.listAdapters.storeAdapter.PropertyListAdapter.PropertyClickListener propertyListener, @org.jetbrains.annotations.NotNull
    androidx.lifecycle.LifecycleOwner lifecycleOwner) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public com.column.roar.listAdapters.storeAdapter.StaggeredCardViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    public final void setNativeAd(@org.jetbrains.annotations.NotNull
    com.google.android.gms.ads.nativead.NativeAd _nativeAd) {
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.column.roar.listAdapters.storeAdapter.StaggeredCardViewHolder holder, int position) {
    }
    
    @java.lang.Override
    public int getItemViewType(int position) {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001Bt\u0012!\u0010\u0002\u001a\u001d\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\b0\u0003\u0012!\u0010\t\u001a\u001d\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\b0\u0003\u0012\'\b\u0002\u0010\n\u001a!\u0012\u0015\u0012\u0013\u0018\u00010\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\b\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\f\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0004J\u0017\u0010\r\u001a\u0004\u0018\u00010\b2\b\u0010\u0007\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0002\u0010\u000eJ\u000e\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0004R-\u0010\n\u001a!\u0012\u0015\u0012\u0013\u0018\u00010\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\b\u0018\u00010\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R)\u0010\u0002\u001a\u001d\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\b0\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R)\u0010\t\u001a\u001d\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\b0\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/column/roar/listAdapters/storeAdapter/PropertyListAdapter$PropertyClickListener;", "", "listener", "Lkotlin/Function1;", "Lcom/column/roar/cloudModel/FirebaseProperty;", "Lkotlin/ParameterName;", "name", "data", "", "longClick", "justClick", "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "onAction", "onJustClick", "(Lcom/column/roar/cloudModel/FirebaseProperty;)Lkotlin/Unit;", "onLongClick", "app_debug"})
    public static final class PropertyClickListener {
        private final kotlin.jvm.functions.Function1<com.column.roar.cloudModel.FirebaseProperty, kotlin.Unit> listener = null;
        private final kotlin.jvm.functions.Function1<com.column.roar.cloudModel.FirebaseProperty, kotlin.Unit> longClick = null;
        private final kotlin.jvm.functions.Function1<com.column.roar.cloudModel.FirebaseProperty, kotlin.Unit> justClick = null;
        
        public PropertyClickListener(@org.jetbrains.annotations.NotNull
        kotlin.jvm.functions.Function1<? super com.column.roar.cloudModel.FirebaseProperty, kotlin.Unit> listener, @org.jetbrains.annotations.NotNull
        kotlin.jvm.functions.Function1<? super com.column.roar.cloudModel.FirebaseProperty, kotlin.Unit> longClick, @org.jetbrains.annotations.Nullable
        kotlin.jvm.functions.Function1<? super com.column.roar.cloudModel.FirebaseProperty, kotlin.Unit> justClick) {
            super();
        }
        
        public final void onAction(@org.jetbrains.annotations.NotNull
        com.column.roar.cloudModel.FirebaseProperty data) {
        }
        
        public final void onLongClick(@org.jetbrains.annotations.NotNull
        com.column.roar.cloudModel.FirebaseProperty data) {
        }
        
        @org.jetbrains.annotations.Nullable
        public final kotlin.Unit onJustClick(@org.jetbrains.annotations.Nullable
        com.column.roar.cloudModel.FirebaseProperty data) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lcom/column/roar/listAdapters/storeAdapter/PropertyListAdapter$Companion;", "", "()V", "diffUtil", "Landroidx/recyclerview/widget/DiffUtil$ItemCallback;", "Lcom/column/roar/cloudModel/FirebaseProperty;", "getDiffUtil", "()Landroidx/recyclerview/widget/DiffUtil$ItemCallback;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final androidx.recyclerview.widget.DiffUtil.ItemCallback<com.column.roar.cloudModel.FirebaseProperty> getDiffUtil() {
            return null;
        }
    }
}