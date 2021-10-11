package com.column.roar.notification.api

import com.column.roar.notification.Constant.Companion.CONTENT_TYPE
import com.column.roar.notification.Constant.Companion.SERVER_KEY
import com.column.roar.notification.data.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization:key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
   suspend fun postNotification(
        @Body pushNotification: PushNotification): Response<ResponseBody>
}