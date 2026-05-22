# open-dinglogin

钉钉授权登录 UTS 插件（Android + iOS）。

## 支持平台

- Android 5.0+（API 21+）
- iOS 12+
- UniApp 3.1.0+ / UniApp X 3.1.0+
- HBuilderX 3.6.8+

## API

### authLogin

```ts
import { authLogin } from '@/uni_modules/open-dinglogin'

const res = await authLogin({
  appId: '你的ClientID',
  bundleId: '你的iOS BundleId，安卓可不填',
  redirectUrl: 'https://your.domain/callback',
  responseType: 'code',
  scope: 'openid',
  prompt: 'consent',
  state: '可选，推荐随机值'
})

if (res.authCode) {
  console.log(res.authCode)
}
```

返回：

```ts
type AuthLoginResult = {
  errMsg: string
  authCode?: string
  state?: string
}
```

## Android 使用说明

### 1. 依赖和权限

插件已在 Android UTS 配置中引入：

- `com.alibaba.android:ddopenauth:1.5.0.10`

权限已在插件内声明：

- `INTERNET`
- `ACCESS_NETWORK_STATE`
- `QUERY_ALL_PACKAGES`

### 2. 回调 Activity

插件通过 Activity 接收钉钉回调，并通过 UTS Promise 返回。

注意：Android 必须使用自定义基座（包含 UTS 插件原生代码）。

## iOS 使用说明

### 1. 引入 SDK

插件已包含：

`/uni_modules/jcfe-dinglogin/utssdk/app-ios/Frameworks/ADTOpenAuthSDK.xcframework`

确保 iOS 重新打包自定义基座。

### 2. Info.plist 配置

插件内置 Info.plist 会合并到 App：

`/uni_modules/jcfe-dinglogin/utssdk/app-ios/Info.plist`

必须保证：

- `CFBundleURLSchemes` 中的值与 `ClientID` 完全一致
- `LSApplicationQueriesSchemes` 至少包含：`dingtalk`、`dingtalk-openauth2`

### 3. BundleId 必须一致

`authLogin` 传入的 `bundleId` 必须与 iOS 实际包名一致。

否则会出现：

- `authLogin:fail register app failed`

## 注意事项

1. `ClientID / bundleId / redirectUrl` 必须与钉钉开放平台配置一致。
2. iOS 端 `CFBundleURLSchemes` 必须等于 `ClientID`。
3. 变更 UTS 原生代码或 iOS Framework 后必须重新打包自定义基座。
4. `state` 建议传随机值，用于防重放和 CSRF 校验。
5. Android 和 iOS 都需要真机安装钉钉客户端。

## 常见问题

### iOS 报 `register app failed`

- 检查 `bundleId` 是否与实际包名一致
- 检查 `ClientID` 是否与 `CFBundleURLSchemes` 一致
- 确保重新打包自定义基座

### Android 无回调

- 确保使用自定义基座
- 确保 `DDAuthActivity` 已被打包进 APK
