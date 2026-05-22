package uts.sdk.modules.openDinglogin

import android.app.Activity
import android.content.Intent
import com.android.dingtalk.openauth.AuthLoginParam
import com.android.dingtalk.openauth.DDAuthApiFactory
import com.android.dingtalk.openauth.utils.DDAuthConstant
import io.dcloud.uts.UTSAndroid
import io.dcloud.uts.UTSPromise
import io.dcloud.uts.console
import java.util.UUID

/**
 * 钉钉登录原生Kotlin实现类
 */
object DingTalkAuthNative {

    private var authResolve: ((AuthResult) -> Unit)? = null
    private var authReject: ((AuthResult) -> Unit)? = null

    /**
     * 钉钉登录结果数据类
     */
    data class AuthResult(
        val errMsg: String,
        val authCode: String? = null,
        val state: String? = null
    )

    /**
     * 执行钉钉登录
     */
    fun authLoginKotlin(
        appId: String,
        redirectUrl: String,
        responseType: String,
        scope: String,
        prompt: String,
        state: String
    ): UTSPromise<AuthResult> {
        return UTSPromise { resolve: (AuthResult) -> Unit, reject: (Any?) -> Unit ->
            try {
                val activity = UTSAndroid.getUniActivity()
                if (activity == null) {
                    reject(AuthResult("authLogin:fail no activity"))
                    return@UTSPromise
                }

                if (appId.isBlank() || redirectUrl.isBlank() || responseType.isBlank()
                    || scope.isBlank() || prompt.isBlank()
                ) {
                    reject(AuthResult("authLogin:fail invalid options"))
                    return@UTSPromise
                }

                val safeState = if (state.isBlank()) UUID.randomUUID().toString() else state

                // 构建登录参数
                val builder = AuthLoginParam.AuthLoginParamBuilder.newBuilder()
                    .appId(appId)
                    .redirectUri(redirectUrl)
                    .responseType(responseType)
                    .scope(scope)
                    .prompt(prompt)
                    .state(safeState)

                authResolve = resolve
                authReject = reject

                val authApi = DDAuthApiFactory.createDDAuthApi(activity, builder.build())
                authApi.authLogin()
			console.log("打开钉钉登录成功！")
            } catch (e: Exception) {
                console.log("钉钉登录异常: ${e.message}")
                reject(AuthResult("authLogin:fail ${e.message}"))
            }
        }
    }

    /**
     * 处理钉钉登录回调
     */
    fun handleAuthCallbackKotlin(intent: Intent?) {
        val resolve = authResolve
        val reject = authReject
			console.log("处理钉钉登录回调！")
        if (intent == null) {
            reject?.invoke(AuthResult("authLogin:fail no intent"))
            clearCallbacks()
            return
        }

        val authCode = intent.getStringExtra(DDAuthConstant.CALLBACK_EXTRA_AUTH_CODE)
        val state = intent.getStringExtra(DDAuthConstant.CALLBACK_EXTRA_STATE)
        val error = intent.getStringExtra(DDAuthConstant.CALLBACK_EXTRA_ERROR)

        console.log("钉钉回调数据: authCode=$authCode, state=$state, error=$error")

        if (!authCode.isNullOrEmpty()) {
            resolve?.invoke(AuthResult(
                errMsg = "authLogin:ok",
                authCode = authCode,
                state = state ?: ""
            ))
        } else {
            reject?.invoke(AuthResult(
                errMsg = "authLogin:fail ${error ?: ""}"
            ))
        }

        clearCallbacks()
    }

    /**
     * 清理回调函数引用
     */
    private fun clearCallbacks() {
        authResolve = null
        authReject = null
    }
}
