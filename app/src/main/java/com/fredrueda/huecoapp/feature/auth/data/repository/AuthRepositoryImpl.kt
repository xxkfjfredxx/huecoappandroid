package com.fredrueda.huecoapp.feature.auth.data.repository

import com.fredrueda.huecoapp.core.data.network.ApiResponse
import com.fredrueda.huecoapp.feature.auth.data.remote.api.AuthApi
import com.fredrueda.huecoapp.feature.auth.data.remote.dto.LoginRequest
import com.fredrueda.huecoapp.feature.auth.domain.entity.AuthUser
import com.fredrueda.huecoapp.feature.auth.domain.repository.AuthRepository
import com.fredrueda.huecoapp.session.SessionManager
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val session: SessionManager
): AuthRepository {

    override suspend fun login(email: String, password: String): ApiResponse<AuthUser> {
        return try {
            val resp = api.login(LoginRequest(email, password))
            if (resp.isSuccessful) {
                val body = resp.body()
                session.saveTokens(body?.access, body?.refresh)
                ApiResponse.Success(body?.user!!)
            } else {
                ApiResponse.HttpError(resp.code(), resp.errorBody()?.string())
            }
        } catch (e: HttpException) {
            ApiResponse.HttpError(e.code(), e.message())
        } catch (t: Throwable) {
            ApiResponse.NetworkError(t)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): ApiResponse<AuthUser> {
        return try {
            val resp = api.loginWithGoogle(mapOf("id_token" to idToken))
            if (resp.isSuccessful) {
                val body = resp.body()
                session.saveTokens(body?.access, body?.refresh)
                ApiResponse.Success(body?.user!!)
            } else {
                ApiResponse.HttpError(resp.code(), resp.errorBody()?.string())
            }
        } catch (t: Throwable) {
            ApiResponse.NetworkError(t)
        }
    }

    override suspend fun loginWithFacebook(accessToken: String): ApiResponse<AuthUser> {
        return try {
            val resp = api.loginWithFacebook(mapOf("token" to accessToken))
            if (resp.isSuccessful) {
                val body = resp.body()
                session.saveTokens(body?.access, body?.refresh)
                ApiResponse.Success(body?.user!!)
            } else {
                ApiResponse.HttpError(resp.code(), resp.errorBody()?.string())
            }
        } catch (t: Throwable) {
            ApiResponse.NetworkError(t)
        }
    }

    override suspend fun me(): ApiResponse<AuthUser> = try {
        val r = api.me()
        if (r.isSuccessful) ApiResponse.Success(r.body()!!)
        else ApiResponse.HttpError(r.code(), r.errorBody()?.string())
    } catch (t: Throwable) {
        ApiResponse.NetworkError(t)
    }

    override suspend fun logout(): ApiResponse<Unit> = try {
        val r = api.logout()
        if (r.isSuccessful) {
            session.clear()
            ApiResponse.Success(Unit)
        } else ApiResponse.HttpError(r.code(), r.errorBody()?.string())
    } catch (t: Throwable) {
        ApiResponse.NetworkError(t)
    }
}
