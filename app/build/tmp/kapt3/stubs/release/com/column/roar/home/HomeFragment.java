package com.column.roar.home;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u00b0\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020\u000b2\b\u0010-\u001a\u0004\u0018\u00010\u000bH\u0002J\u0012\u0010.\u001a\u00020+2\b\u0010/\u001a\u0004\u0018\u00010\u000bH\u0002J\u0012\u00100\u001a\u00020+2\b\u00101\u001a\u0004\u0018\u00010\u000bH\u0002J\u0010\u00102\u001a\u00020+2\u0006\u00103\u001a\u00020\u000bH\u0002J\u0011\u00104\u001a\u00020+H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u00105J\u0012\u00106\u001a\u00020+2\b\u00107\u001a\u0004\u0018\u000108H\u0016J&\u00109\u001a\u0004\u0018\u00010:2\u0006\u0010;\u001a\u00020<2\b\u0010=\u001a\u0004\u0018\u00010>2\b\u00107\u001a\u0004\u0018\u000108H\u0016J\u001a\u0010?\u001a\u00020+2\u0006\u0010@\u001a\u00020:2\b\u00107\u001a\u0004\u0018\u000108H\u0016J\u0012\u0010A\u001a\u00020+2\b\u0010B\u001a\u0004\u0018\u00010\u000bH\u0002J\b\u0010C\u001a\u00020+H\u0002J\u0012\u0010D\u001a\u00020+2\b\u0010E\u001a\u0004\u0018\u00010\u000bH\u0002J\b\u0010F\u001a\u00020+H\u0002J\u0010\u0010G\u001a\u00020+2\u0006\u0010H\u001a\u00020IH\u0007J\b\u0010J\u001a\u00020+H\u0002J\u0010\u0010K\u001a\u00020+2\u0006\u0010L\u001a\u00020MH\u0002J\u001a\u0010N\u001a\u00020+2\u0006\u0010O\u001a\u00020\u000b2\b\u0010-\u001a\u0004\u0018\u00010\u000bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082.\u00a2\u0006\u0004\n\u0002\u0010\fR\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0019X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020%X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\'X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020)X\u0082.\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006P"}, d2 = {"Lcom/column/roar/home/HomeFragment;", "Landroidx/fragment/app/Fragment;", "()V", "backDrop", "Landroid/widget/LinearLayout;", "callback", "Landroidx/activity/OnBackPressedCallback;", "chipGroup", "Lcom/google/android/material/chip/ChipGroup;", "chipsCategory", "", "", "[Ljava/lang/String;", "connectionView", "Lcom/google/android/material/card/MaterialCardView;", "counter", "", "emptyList", "fireStore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "homeRecycler", "Landroidx/recyclerview/widget/RecyclerView;", "lodgeDao", "Lcom/column/roar/database/LodgeDao;", "lodgesRef", "Lcom/google/firebase/firestore/Query;", "menuIcon", "Landroid/widget/ImageView;", "networkError", "player", "Lcom/google/android/exoplayer2/SimpleExoPlayer;", "playerView", "Lcom/google/android/exoplayer2/ui/PlayerView;", "propertiesQuery", "retryBtn", "Lcom/google/android/material/button/MaterialButton;", "roarItemsAdapter", "Lcom/column/roar/listAdapters/NewListAdapter;", "sharedPref", "Landroid/content/SharedPreferences;", "swipeContainer", "Landroidx/swiperefreshlayout/widget/SwipeRefreshLayout;", "callDialog", "", "number", "brand", "chatWhatsApp", "pNumber", "dialPhoneNumber", "phoneNumber", "fetchLodges", "filter", "initializeHome", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "onViewCreated", "view", "resolveUrl", "id", "setUpChips", "setUpExoPlayer", "videoUrl", "setUpOnBackPressedCallback", "showAdDialog", "product", "Lcom/column/roar/cloudModel/FirebaseProperty;", "showExitDialog", "showNetworkError", "error", "", "whatsAppDialog", "data", "app_release"})
public final class HomeFragment extends androidx.fragment.app.Fragment {
    private androidx.recyclerview.widget.RecyclerView homeRecycler;
    private com.google.android.material.chip.ChipGroup chipGroup;
    private android.widget.LinearLayout backDrop;
    private android.widget.ImageView menuIcon;
    private com.column.roar.listAdapters.NewListAdapter roarItemsAdapter;
    private com.google.firebase.firestore.FirebaseFirestore fireStore;
    private com.google.firebase.firestore.Query propertiesQuery;
    private java.lang.String[] chipsCategory;
    private com.google.firebase.firestore.Query lodgesRef;
    private com.google.android.material.card.MaterialCardView connectionView;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeContainer;
    private androidx.activity.OnBackPressedCallback callback;
    private com.google.android.material.card.MaterialCardView emptyList;
    private com.google.android.material.card.MaterialCardView networkError;
    private com.google.android.exoplayer2.SimpleExoPlayer player;
    private com.google.android.exoplayer2.ui.PlayerView playerView;
    private int counter = 0;
    private com.google.android.material.button.MaterialButton retryBtn;
    private android.content.SharedPreferences sharedPref;
    private com.column.roar.database.LodgeDao lodgeDao;
    
    public HomeFragment() {
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
    
    private final void setUpOnBackPressedCallback() {
    }
    
    @java.lang.Override
    public void onViewCreated(@org.jetbrains.annotations.NotNull
    android.view.View view, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setUpChips() {
    }
    
    private final java.lang.Object initializeHome(kotlin.coroutines.Continuation<? super kotlin.Unit> p0) {
        return null;
    }
    
    private final void fetchLodges(java.lang.String filter) {
    }
    
    private final void showNetworkError(boolean error) {
    }
    
    private final void resolveUrl(java.lang.String id) {
    }
    
    @android.annotation.SuppressLint(value = {"InflateParams"})
    public final void showAdDialog(@org.jetbrains.annotations.NotNull
    com.column.roar.cloudModel.FirebaseProperty product) {
    }
    
    private final void showExitDialog() {
    }
    
    private final void dialPhoneNumber(java.lang.String phoneNumber) {
    }
    
    private final void chatWhatsApp(java.lang.String pNumber) {
    }
    
    private final void callDialog(java.lang.String number, java.lang.String brand) {
    }
    
    private final void whatsAppDialog(java.lang.String data, java.lang.String brand) {
    }
    
    private final void setUpExoPlayer(java.lang.String videoUrl) {
    }
}