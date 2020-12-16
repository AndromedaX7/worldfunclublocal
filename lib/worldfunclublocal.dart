
import 'dart:async';

import 'package:flutter/services.dart';

class Worldfunclublocal {
  static const MethodChannel _channel =
      const MethodChannel('worldfunclublocal');

  static const kWeChatPay="kWeChatPay";
  static const kWeChatCodeResponse = "weChatCodeResponse";
  static const kLoginWithWeChat = "loginWithWeChat";
  static bool bind = false;
  static List<LocalChannelResponse> cache = [];

  static void listener(LocalChannelResponse resp,Function (MethodCall fromCall) handle) {
    cache.add(resp);
    if (!bind) {
      _channel.setMethodCallHandler((call) {
        switch (call.method) {
          case kWeChatCodeResponse:
            cache.forEach((r) {
              r.wechatCode(call.arguments);
            });
            return null;
          case "responseScan":
            cache.forEach((r) {
              print("scan result =============================${call.arguments}");
              r.responseScan(call.arguments);
            });
            return null;
            break;
          default:
            handle(call);
            return null;
        }
      });
      bind = true;
    }
  }

  static void loginWithWechat()   {
    _channel.invokeMethod(kLoginWithWeChat);
  }


  static void startActivityWithUrl(String url) {
    _channel.invokeMethod("startActivityWithUrl", url);
  }


  /// 外部鉴权
  static void callPhone(String phone) async {
      _channel.invokeMethod("callPhone", phone);
  }

  static void wechatPay(dynamic map) {
    _channel.invokeMethod(kWeChatPay, map);
  }

  static void startScan() {
    _channel.invokeMethod("scan");
  }

  static void openLocation(double lat, double lon, String name) {
    _channel.invokeMethod(
        "openLocation", {"lat": lat, "lon": lon, "name": name});
  }
}

abstract class LocalChannelResponse {
  void wechatCode(String code);

  void  responseScan(String result) ;
}
