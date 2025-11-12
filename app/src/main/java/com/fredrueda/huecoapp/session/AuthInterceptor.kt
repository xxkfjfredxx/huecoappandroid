package com.fredrueda.huecoapp.session

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val session: SessionManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val access = runBlocking { session.getAccess() }
        val newReq = if (!access.isNullOrBlank()) {
            req.newBuilder()
                .addHeader("Authorization", "Bearer $access")
                .build()
        } else req
        return chain.proceed(newReq)
    }
}
