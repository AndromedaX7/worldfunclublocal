package com.ds.worldfunclublocal

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.ds.worldfunclub.wxapi.WXPayEntryActivity
import com.google.zxing.client.android.CaptureActivity
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
/** WorldfunclublocalPlugin */
class WorldfunclublocalPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "worldfunclublocal")
        context = flutterPluginBinding.applicationContext
        channel.setMethodCallHandler(this)
        internalChannel = channel
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "loginWithWeChat" -> {
                val api = WXAPIFactory.createWXAPI(context, "wx43736892a139b092")
                val req = SendAuth.Req()
                req.scope = "snsapi_userinfo"
                req.state = "wechat_login_world_fun_club"
                api.sendReq(req)
                result.success(true)
            }


            "kWeChatPay" -> {
                val orderId = call.argument<String>("orderId") ?: ""
                val goodsType = call.argument<String>("goodsType") ?: ""
                val payMoney = call.argument<String>("payMoney") ?: ""
                val prepayId = call.argument<String>("prepayid") ?: ""
                val nonceStr = call.argument<String>("noncestr") ?: ""
                val timeStamp = call.argument<String>("timestamp") ?: ""
                val sign = call.argument<String>("sign") ?: ""
                val api = WXAPIFactory.createWXAPI(context, null)
                val request = PayReq()
                WXPayEntryActivity.setExtData(request, orderId, goodsType, payMoney)
                request.appId = "wx43736892a139b092"
                request.partnerId = "1602989977"
                request.prepayId = prepayId
                request.packageValue = "Sign=WXPay"
                request.nonceStr = nonceStr
                request.timeStamp = timeStamp
                request.sign = sign
                api.sendReq(request)
                result.success(null)
            }

            "startActivityWithUrl" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(call.arguments as String)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ContextCompat.startActivity(context, intent, null)
                result.success(null)
            }
            "callPhone" -> {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${call.arguments}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ContextCompat.startActivity(context, intent, null)
                result.success(null)
            }
            "scan" -> {
                CaptureActivity.scan(context)
                result.success(null)
            }

            "openLocation" -> {
                openLocalLocation(call.argument<Double>("lat") ?: 0.0,
                        call.argument<Double>("lon") ?: 0.0,
                        call.argument<String>("name") ?: "")
                result.success(null)
            }
        }
    }

    private fun openLocalLocation(dlat: Double, dlon: Double, dname: String) {
        if (checkMapAppsIsExist(context, "com.autonavi.minimap")) {
            openGaoDeMap(dlat, dlon, dname)
        } else if (checkMapAppsIsExist(context, "com.baidu.BaiduMap")) {
            openBaiduMap(dlat, dlon, dname)
        } else {
            openTencent(dlat, dlon, dname)
        }
    }


    /**
     * 打开高德地图（公交出行，起点位置使用地图当前位置）
     *
     * t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）
     *
     * @param dlat  终点纬度
     * @param dlon  终点经度
     * @param dname 终点名称
     */

    private fun openGaoDeMap(dlat: Double, dlon: Double, dname: String) {
        if (checkMapAppsIsExist(context, "com.autonavi.minimap")) {
            val intent = Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.autonavi.minimap")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse("androidamap://route?sourceApplication=" + "环球途乐会"
                    + "&sname=我的位置&dlat=" + dlat
                    .toString() + "&dlon=" + dlon
                    .toString() + "&dname=" + dname + "&dev=0&m=0&t=1")
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "高德地图未安装", Toast.LENGTH_SHORT).show()
        }
    }

    /** 打开百度地图（公交出行，起点位置使用地图当前位置）
     *
     * mode = transit（公交）、driving（驾车）、walking（步行）和riding（骑行）. 默认:driving
     * 当 mode=transit 时 ： sy = 0：推荐路线 、 2：少换乘 、 3：少步行 、 4：不坐地铁 、 5：时间短 、 6：地铁优先
     *
     * @param dlat  终点纬度
     * @param dlon  终点经度
     * @param dname 终点名称
     */
    private fun openBaiduMap(dlat: Double, dlon: Double, dname: String) {
        if (checkMapAppsIsExist(context, "com.baidu.BaiduMap")) {
            val intent = Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("baidumap://map/direction?origin=我的位置&destination=name:"
                    + dname
                    + "|latlng:" + dlat + "," + dlon
                    + "&mode=transit&sy=3&index=0&target=1")
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "百度地图未安装", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 打开腾讯地图（公交出行，起点位置使用地图当前位置）
     *
     * 公交：type=bus，policy有以下取值
     * 0：较快捷 、 1：少换乘 、 2：少步行 、 3：不坐地铁
     * 驾车：type=drive，policy有以下取值
     * 0：较快捷 、 1：无高速 、 2：距离短
     * policy的取值缺省为0
     *
     * @param dlat  终点纬度
     * @param dlon  终点经度
     * @param dname 终点名称
     */
    private fun openTencent(dlat: Double, dlon: Double, dname: String) {
        if (checkMapAppsIsExist(context, "com.tencent.map")) {
            val intent = Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("qqmap://map/routeplan?type=bus&from=我的位置&fromcoord=0,0"
                    + "&to=" + dname
                    + "&tocoord=" + dlat + "," + dlon
                    + "&policy=1&referer=myapp")
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "腾讯地图未安装", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkMapAppsIsExist(context: Context, packagename: String?): Boolean {
        var packageInfo: PackageInfo?
        try {
            packageInfo = context.packageManager.getPackageInfo(packagename!!, 0)
        } catch (e: Exception) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    companion object {
        @JvmStatic
        fun responseWechatCode(code: String) {
            internalChannel.invokeMethod("weChatCodeResponse", code)
        }

        @JvmStatic
        fun paySuccess(orderId: String, orderType: String, pay: String) {
            val map = HashMap<String, String>()
            map["orderId"] = orderId
            map["orderType"] = orderType
            map["pay"] = pay
            internalChannel.invokeMethod("paySuccess", map)
        }

        @JvmStatic
        fun openOrderListWillPay(orderType: String) {
            val map = HashMap<String, String>()
            map["orderType"] = orderType
            internalChannel.invokeMethod("openOrderList", map)
        }

        @JvmStatic
        fun openHome() {
            internalChannel.invokeMethod("openHome", null)
        }

        @JvmStatic
        fun payFailed(orderId: String, orderType: String, pay: String, errorCode: String, errorMessage: String) {

            val map = HashMap<String, String>()
            map["orderId"] = orderId
            map["orderType"] = orderType
            map["errorCode"] = errorCode
            map["errorMessage"] = errorMessage
            map["pay"] = pay
            internalChannel.invokeMethod("payFailed", map)
        }

        @JvmStatic
        fun responseScan(result: String) {
            internalChannel.invokeMethod("responseScan", result)
        }

        @JvmStatic
        private lateinit var internalChannel: MethodChannel

    }

}
