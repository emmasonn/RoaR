package com.beaconinc.roarhousing.notification.api

import com.beaconinc.roarhousing.notification.Constant.Companion.CONTENT_TYPE
import com.beaconinc.roarhousing.notification.Constant.Companion.SERVER_KEY
import com.beaconinc.roarhousing.notification.data.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization:key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
   suspend fun postNotification(
        @Body pushNotification: PushNotification): Response<ResponseBody>
}