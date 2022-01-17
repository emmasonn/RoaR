package com.column.roar.cloudModel;

import java.lang.System;

@kotlinx.parcelize.Parcelize
@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b]\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u00bd\u0002\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u001e\u0012\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\u001e\u00a2\u0006\u0002\u0010 J\u000b\u0010_\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010`\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010a\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010c\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010d\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010e\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010g\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010h\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010i\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010j\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010k\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010l\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010m\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010n\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010o\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010p\u001a\u0004\u0018\u00010\u001eH\u00c6\u0003J\u000b\u0010q\u001a\u0004\u0018\u00010\u001eH\u00c6\u0003J\u000b\u0010r\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010s\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010t\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010u\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010v\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010w\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003\u00a2\u0006\u0002\u0010=J\u0010\u0010x\u001a\u0004\u0018\u00010\rH\u00c6\u0003\u00a2\u0006\u0002\u00100J\u00c6\u0002\u0010y\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\n\b\u0002\u0010\u001f\u001a\u0004\u0018\u00010\u001eH\u00c6\u0001\u00a2\u0006\u0002\u0010zJ\t\u0010{\u001a\u00020|H\u00d6\u0001J\u0013\u0010}\u001a\u00020\r2\b\u0010~\u001a\u0004\u0018\u00010\u007fH\u00d6\u0003J\n\u0010\u0080\u0001\u001a\u00020|H\u00d6\u0001J\n\u0010\u0081\u0001\u001a\u00020\u0003H\u00d6\u0001J\u001e\u0010\u0082\u0001\u001a\u00030\u0083\u00012\b\u0010\u0084\u0001\u001a\u00030\u0085\u00012\u0007\u0010\u0086\u0001\u001a\u00020|H\u00d6\u0001R \u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R \u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010\"\"\u0004\b&\u0010$R \u0010\u0016\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\'\u0010\"\"\u0004\b(\u0010$R \u0010\u0010\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b)\u0010\"\"\u0004\b*\u0010$R \u0010\u000e\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b+\u0010\"\"\u0004\b,\u0010$R \u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b-\u0010\"\"\u0004\b.\u0010$R\"\u0010\f\u001a\u0004\u0018\u00010\r8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u00103\u001a\u0004\b/\u00100\"\u0004\b1\u00102R \u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b4\u0010\"\"\u0004\b5\u0010$R \u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b6\u0010\"\"\u0004\b7\u0010$R \u0010\b\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b8\u0010\"\"\u0004\b9\u0010$R \u0010\t\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b:\u0010\"\"\u0004\b;\u0010$R\"\u0010\n\u001a\u0004\u0018\u00010\u000b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u0010@\u001a\u0004\b<\u0010=\"\u0004\b>\u0010?R \u0010\u001f\u001a\u0004\u0018\u00010\u001e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bA\u0010B\"\u0004\bC\u0010DR \u0010\u0019\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bE\u0010\"\"\u0004\bF\u0010$R \u0010\u001b\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bG\u0010\"\"\u0004\bH\u0010$R \u0010\u0012\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bI\u0010\"\"\u0004\bJ\u0010$R \u0010\u001d\u001a\u0004\u0018\u00010\u001e8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bK\u0010B\"\u0004\bL\u0010DR \u0010\u0017\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bM\u0010\"\"\u0004\bN\u0010$R \u0010\u0013\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bO\u0010\"\"\u0004\bP\u0010$R \u0010\u001a\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bQ\u0010\"\"\u0004\bR\u0010$R \u0010\u001c\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bS\u0010\"\"\u0004\bT\u0010$R \u0010\u0014\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bU\u0010\"\"\u0004\bV\u0010$R \u0010\u0018\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bW\u0010\"\"\u0004\bX\u0010$R \u0010\u0011\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bY\u0010\"\"\u0004\bZ\u0010$R \u0010\u000f\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b[\u0010\"\"\u0004\b\\\u0010$R \u0010\u0015\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b]\u0010\"\"\u0004\b^\u0010$\u00a8\u0006\u0087\u0001"}, d2 = {"Lcom/column/roar/cloudModel/FirebaseUser;", "Landroid/os/Parcelable;", "account", "", "adminId", "campus", "clientId", "clientImage", "clientName", "clientPhone", "counter", "", "certified", "", "businessPhone", "realtorPhone", "businessComplaint", "realtorComplaint", "errand", "marquee", "partner", "slots", "brand", "free", "password", "enuguImg", "nsukkaImg", "enuguPhone", "nsukkaPhone", "expired", "Ljava/util/Date;", "date", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Date;Ljava/util/Date;)V", "getAccount", "()Ljava/lang/String;", "setAccount", "(Ljava/lang/String;)V", "getAdminId", "setAdminId", "getBrand", "setBrand", "getBusinessComplaint", "setBusinessComplaint", "getBusinessPhone", "setBusinessPhone", "getCampus", "setCampus", "getCertified", "()Ljava/lang/Boolean;", "setCertified", "(Ljava/lang/Boolean;)V", "Ljava/lang/Boolean;", "getClientId", "setClientId", "getClientImage", "setClientImage", "getClientName", "setClientName", "getClientPhone", "setClientPhone", "getCounter", "()Ljava/lang/Long;", "setCounter", "(Ljava/lang/Long;)V", "Ljava/lang/Long;", "getDate", "()Ljava/util/Date;", "setDate", "(Ljava/util/Date;)V", "getEnuguImg", "setEnuguImg", "getEnuguPhone", "setEnuguPhone", "getErrand", "setErrand", "getExpired", "setExpired", "getFree", "setFree", "getMarquee", "setMarquee", "getNsukkaImg", "setNsukkaImg", "getNsukkaPhone", "setNsukkaPhone", "getPartner", "setPartner", "getPassword", "setPassword", "getRealtorComplaint", "setRealtorComplaint", "getRealtorPhone", "setRealtorPhone", "getSlots", "setSlots", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Date;Ljava/util/Date;)Lcom/column/roar/cloudModel/FirebaseUser;", "describeContents", "", "equals", "other", "", "hashCode", "toString", "writeToParcel", "", "parcel", "Landroid/os/Parcel;", "flags", "app_debug"})
@com.google.firebase.firestore.IgnoreExtraProperties
public final class FirebaseUser implements android.os.Parcelable {
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "account")
    private java.lang.String account;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "adminId")
    private java.lang.String adminId;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "campus")
    private java.lang.String campus;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "clientId")
    private java.lang.String clientId;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "clientImage")
    private java.lang.String clientImage;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "clientName")
    private java.lang.String clientName;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "clientPhone")
    private java.lang.String clientPhone;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "counter")
    private java.lang.Long counter;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "certified")
    private java.lang.Boolean certified;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "businessPhone")
    private java.lang.String businessPhone;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "realtorPhone")
    private java.lang.String realtorPhone;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "businessComplaint")
    private java.lang.String businessComplaint;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "realtorComplaint")
    private java.lang.String realtorComplaint;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "errand")
    private java.lang.String errand;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "marquee")
    private java.lang.String marquee;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "partner")
    private java.lang.String partner;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "slots")
    private java.lang.String slots;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "brand")
    private java.lang.String brand;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "free")
    private java.lang.String free;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "password")
    private java.lang.String password;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "enuguImg")
    private java.lang.String enuguImg;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "nsukkaImg")
    private java.lang.String nsukkaImg;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "enuguPhone")
    private java.lang.String enuguPhone;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.PropertyName(value = "nsukkaPhone")
    private java.lang.String nsukkaPhone;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.ServerTimestamp
    @com.google.firebase.firestore.PropertyName(value = "expired")
    private java.util.Date expired;
    @org.jetbrains.annotations.Nullable
    @com.google.firebase.firestore.ServerTimestamp
    @com.google.firebase.firestore.PropertyName(value = "date")
    private java.util.Date date;
    public static final android.os.Parcelable.Creator<com.column.roar.cloudModel.FirebaseUser> CREATOR = null;
    
    @org.jetbrains.annotations.NotNull
    public final com.column.roar.cloudModel.FirebaseUser copy(@org.jetbrains.annotations.Nullable
    java.lang.String account, @org.jetbrains.annotations.Nullable
    java.lang.String adminId, @org.jetbrains.annotations.Nullable
    java.lang.String campus, @org.jetbrains.annotations.Nullable
    java.lang.String clientId, @org.jetbrains.annotations.Nullable
    java.lang.String clientImage, @org.jetbrains.annotations.Nullable
    java.lang.String clientName, @org.jetbrains.annotations.Nullable
    java.lang.String clientPhone, @org.jetbrains.annotations.Nullable
    java.lang.Long counter, @org.jetbrains.annotations.Nullable
    java.lang.Boolean certified, @org.jetbrains.annotations.Nullable
    java.lang.String businessPhone, @org.jetbrains.annotations.Nullable
    java.lang.String realtorPhone, @org.jetbrains.annotations.Nullable
    java.lang.String businessComplaint, @org.jetbrains.annotations.Nullable
    java.lang.String realtorComplaint, @org.jetbrains.annotations.Nullable
    java.lang.String errand, @org.jetbrains.annotations.Nullable
    java.lang.String marquee, @org.jetbrains.annotations.Nullable
    java.lang.String partner, @org.jetbrains.annotations.Nullable
    java.lang.String slots, @org.jetbrains.annotations.Nullable
    java.lang.String brand, @org.jetbrains.annotations.Nullable
    java.lang.String free, @org.jetbrains.annotations.Nullable
    java.lang.String password, @org.jetbrains.annotations.Nullable
    java.lang.String enuguImg, @org.jetbrains.annotations.Nullable
    java.lang.String nsukkaImg, @org.jetbrains.annotations.Nullable
    java.lang.String enuguPhone, @org.jetbrains.annotations.Nullable
    java.lang.String nsukkaPhone, @org.jetbrains.annotations.Nullable
    java.util.Date expired, @org.jetbrains.annotations.Nullable
    java.util.Date date) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object p0) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public FirebaseUser() {
        super();
    }
    
    public FirebaseUser(@org.jetbrains.annotations.Nullable
    java.lang.String account, @org.jetbrains.annotations.Nullable
    java.lang.String adminId, @org.jetbrains.annotations.Nullable
    java.lang.String campus, @org.jetbrains.annotations.Nullable
    java.lang.String clientId, @org.jetbrains.annotations.Nullable
    java.lang.String clientImage, @org.jetbrains.annotations.Nullable
    java.lang.String clientName, @org.jetbrains.annotations.Nullable
    java.lang.String clientPhone, @org.jetbrains.annotations.Nullable
    java.lang.Long counter, @org.jetbrains.annotations.Nullable
    java.lang.Boolean certified, @org.jetbrains.annotations.Nullable
    java.lang.String businessPhone, @org.jetbrains.annotations.Nullable
    java.lang.String realtorPhone, @org.jetbrains.annotations.Nullable
    java.lang.String businessComplaint, @org.jetbrains.annotations.Nullable
    java.lang.String realtorComplaint, @org.jetbrains.annotations.Nullable
    java.lang.String errand, @org.jetbrains.annotations.Nullable
    java.lang.String marquee, @org.jetbrains.annotations.Nullable
    java.lang.String partner, @org.jetbrains.annotations.Nullable
    java.lang.String slots, @org.jetbrains.annotations.Nullable
    java.lang.String brand, @org.jetbrains.annotations.Nullable
    java.lang.String free, @org.jetbrains.annotations.Nullable
    java.lang.String password, @org.jetbrains.annotations.Nullable
    java.lang.String enuguImg, @org.jetbrains.annotations.Nullable
    java.lang.String nsukkaImg, @org.jetbrains.annotations.Nullable
    java.lang.String enuguPhone, @org.jetbrains.annotations.Nullable
    java.lang.String nsukkaPhone, @org.jetbrains.annotations.Nullable
    java.util.Date expired, @org.jetbrains.annotations.Nullable
    java.util.Date date) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getAccount() {
        return null;
    }
    
    public final void setAccount(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getAdminId() {
        return null;
    }
    
    public final void setAdminId(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getCampus() {
        return null;
    }
    
    public final void setCampus(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getClientId() {
        return null;
    }
    
    public final void setClientId(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getClientImage() {
        return null;
    }
    
    public final void setClientImage(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getClientName() {
        return null;
    }
    
    public final void setClientName(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getClientPhone() {
        return null;
    }
    
    public final void setClientPhone(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long getCounter() {
        return null;
    }
    
    public final void setCounter(@org.jetbrains.annotations.Nullable
    java.lang.Long p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Boolean component9() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Boolean getCertified() {
        return null;
    }
    
    public final void setCertified(@org.jetbrains.annotations.Nullable
    java.lang.Boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getBusinessPhone() {
        return null;
    }
    
    public final void setBusinessPhone(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getRealtorPhone() {
        return null;
    }
    
    public final void setRealtorPhone(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getBusinessComplaint() {
        return null;
    }
    
    public final void setBusinessComplaint(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getRealtorComplaint() {
        return null;
    }
    
    public final void setRealtorComplaint(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getErrand() {
        return null;
    }
    
    public final void setErrand(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component15() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getMarquee() {
        return null;
    }
    
    public final void setMarquee(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component16() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getPartner() {
        return null;
    }
    
    public final void setPartner(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component17() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getSlots() {
        return null;
    }
    
    public final void setSlots(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component18() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getBrand() {
        return null;
    }
    
    public final void setBrand(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component19() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getFree() {
        return null;
    }
    
    public final void setFree(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component20() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getPassword() {
        return null;
    }
    
    public final void setPassword(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component21() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getEnuguImg() {
        return null;
    }
    
    public final void setEnuguImg(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component22() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getNsukkaImg() {
        return null;
    }
    
    public final void setNsukkaImg(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component23() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getEnuguPhone() {
        return null;
    }
    
    public final void setEnuguPhone(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component24() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getNsukkaPhone() {
        return null;
    }
    
    public final void setNsukkaPhone(@org.jetbrains.annotations.Nullable
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.util.Date component25() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.util.Date getExpired() {
        return null;
    }
    
    public final void setExpired(@org.jetbrains.annotations.Nullable
    java.util.Date p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.util.Date component26() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.util.Date getDate() {
        return null;
    }
    
    public final void setDate(@org.jetbrains.annotations.Nullable
    java.util.Date p0) {
    }
    
    @java.lang.Override
    public int describeContents() {
        return 0;
    }
    
    @java.lang.Override
    public void writeToParcel(@org.jetbrains.annotations.NotNull
    android.os.Parcel parcel, int flags) {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 3)
    public static final class Creator implements android.os.Parcelable.Creator<com.column.roar.cloudModel.FirebaseUser> {
        
        public Creator() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        @java.lang.Override
        public final com.column.roar.cloudModel.FirebaseUser createFromParcel(@org.jetbrains.annotations.NotNull
        android.os.Parcel in) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        @java.lang.Override
        public final com.column.roar.cloudModel.FirebaseUser[] newArray(int size) {
            return null;
        }
    }
}