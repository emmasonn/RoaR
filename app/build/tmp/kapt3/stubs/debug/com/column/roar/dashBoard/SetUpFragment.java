package com.column.roar.dashBoard;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u009e\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J!\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\u001a2\u0006\u0010&\u001a\u00020\'H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010(J\b\u0010)\u001a\u00020*H\u0002J\"\u0010+\u001a\u00020*2\u0006\u0010,\u001a\u00020\'2\u0006\u0010-\u001a\u00020\'2\b\u0010.\u001a\u0004\u0018\u00010/H\u0016J\u0012\u00100\u001a\u00020*2\b\u00101\u001a\u0004\u0018\u000102H\u0016J&\u00103\u001a\u0004\u0018\u0001042\u0006\u00105\u001a\u0002062\b\u00107\u001a\u0004\u0018\u0001082\b\u00101\u001a\u0004\u0018\u000102H\u0016J\b\u00109\u001a\u00020*H\u0003J\u0019\u0010:\u001a\u00020*2\u0006\u0010;\u001a\u00020\u001aH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010<J\u0010\u0010=\u001a\u00020*2\u0006\u0010>\u001a\u00020\u000fH\u0002J\b\u0010?\u001a\u00020*H\u0002J\u001b\u0010@\u001a\u0004\u0018\u00010$2\u0006\u0010%\u001a\u00020\u001aH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010<J\u0012\u0010A\u001a\u00020*2\b\u0010B\u001a\u0004\u0018\u00010$H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\"X\u0082.\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006C"}, d2 = {"Lcom/column/roar/dashBoard/SetUpFragment;", "Landroidx/fragment/app/Fragment;", "()V", "accountSpinner", "Lcom/google/android/material/textfield/TextInputLayout;", "adminsRef", "Lcom/google/firebase/firestore/Query;", "brandName", "Lcom/google/android/material/textfield/TextInputEditText;", "campusSpinner", "clientCollection", "Lcom/google/firebase/firestore/CollectionReference;", "clientDocument", "Lcom/google/firebase/firestore/DocumentReference;", "clientId", "", "clientImageView", "Landroid/widget/ImageView;", "fireStore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "firebaseAuth", "Lcom/google/firebase/auth/FirebaseAuth;", "fullNameView", "password", "phoneTextView", "profileBitmap", "Landroid/graphics/Bitmap;", "progressBar", "Landroid/widget/ProgressBar;", "saveBtn", "Lcom/google/android/material/button/MaterialButton;", "sharedPref", "Landroid/content/SharedPreferences;", "storage", "Lcom/google/firebase/storage/FirebaseStorage;", "getBytesFromBitmap", "", "bitmap", "quality", "", "(Landroid/graphics/Bitmap;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "hideProgress", "", "onActivityResult", "requestCode", "resultCode", "data", "Landroid/content/Intent;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "openStorageIntent", "processProfileImage", "imageBitmap", "(Landroid/graphics/Bitmap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveDetails", "image", "showProgress", "startCompressing", "startUploadingProfileImage", "imageByte", "app_debug"})
public final class SetUpFragment extends androidx.fragment.app.Fragment {
    private com.google.firebase.auth.FirebaseAuth firebaseAuth;
    private com.google.firebase.firestore.DocumentReference clientDocument;
    private com.google.firebase.firestore.CollectionReference clientCollection;
    private com.google.firebase.firestore.FirebaseFirestore fireStore;
    private android.widget.ImageView clientImageView;
    private com.google.android.material.textfield.TextInputEditText fullNameView;
    private com.google.firebase.storage.FirebaseStorage storage;
    private android.graphics.Bitmap profileBitmap;
    private android.content.SharedPreferences sharedPref;
    private com.google.android.material.textfield.TextInputEditText phoneTextView;
    private com.google.android.material.textfield.TextInputEditText password;
    private com.google.android.material.textfield.TextInputEditText brandName;
    private com.google.android.material.textfield.TextInputLayout accountSpinner;
    private com.google.android.material.textfield.TextInputLayout campusSpinner;
    private java.lang.String clientId;
    private com.google.android.material.button.MaterialButton saveBtn;
    private android.widget.ProgressBar progressBar;
    private com.google.firebase.firestore.Query adminsRef;
    
    public SetUpFragment() {
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
    
    @android.annotation.SuppressLint(value = {"QueryPermissionsNeeded"})
    private final void openStorageIntent() {
    }
    
    @java.lang.Override
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable
    android.content.Intent data) {
    }
    
    private final java.lang.Object processProfileImage(android.graphics.Bitmap imageBitmap, kotlin.coroutines.Continuation<? super kotlin.Unit> p1) {
        return null;
    }
    
    private final java.lang.Object startCompressing(android.graphics.Bitmap bitmap, kotlin.coroutines.Continuation<? super byte[]> p1) {
        return null;
    }
    
    private final java.lang.Object getBytesFromBitmap(android.graphics.Bitmap bitmap, int quality, kotlin.coroutines.Continuation<? super byte[]> p2) {
        return null;
    }
    
    private final void startUploadingProfileImage(byte[] imageByte) {
    }
    
    private final void saveDetails(java.lang.String image) {
    }
    
    private final void showProgress() {
    }
    
    private final void hideProgress() {
    }
}