package uts.sdk.modules.openDinglogin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.android.dingtalk.openauth.utils.DDAuthConstant
import io.dcloud.uts.console
import uts.sdk.modules.openDinglogin.DingTalkAuthNative

/**
 * 钉钉登录回调处理Activity
 */
class DDAuthActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        console.log("DDAuthActivity onCreate")
        
        // 处理钉钉登录回调
        DingTalkAuthNative.handleAuthCallbackKotlin(intent)
        
        // 关闭Activity
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        console.log("DDAuthActivity onNewIntent")
        
        // 处理新的Intent
        intent?.let {
            DingTalkAuthNative.handleAuthCallbackKotlin(it)
        }
        
        // 关闭Activity
        finish()
    }
}
