import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:worldfunclublocal/worldfunclublocal.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with LocalChannelResponse{
  @override
  void initState() {
    super.initState();
    Worldfunclublocal.listener(this, (fromCall) => null);
  }

  String result="";
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: CustomScrollView(
          slivers: [
            SliverToBoxAdapter(
              child: Container(
                child: Column(
                  children: [
                    FlatButton(
                      onPressed: () async {
                        if(await Permission.camera.isGranted)
                          Worldfunclublocal.startScan();
                        else{
                          await Permission.camera.request().then((value) {
                            if(value.isGranted){
                              Worldfunclublocal.startScan();
                            }else{
                              ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text("权限不足")));
                            }
                          });
                        }
                      },
                      child: Text("扫一扫 $result"),
                    ),

                    FlatButton(
                      onPressed: () async {
                       if(await Permission.phone.isGranted)
                         Worldfunclublocal.callPhone("10010");
                       else{
                         await Permission.phone.request().then((value) {
                           if(value.isGranted){
                             Worldfunclublocal.callPhone("10010");
                           }else{
                             ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text("权限不足")));
                           }
                         });
                       }
                      },
                      child: Text("打电话：10010"),
                    ),
                    FlatButton(
                      onPressed: () {
                        Worldfunclublocal.loginWithWechat();
                      },
                      child: Text("打开微信"),
                    ),
                    FlatButton(
                      onPressed: () {
                        Worldfunclublocal.startActivityWithUrl("weixin://wap/pay?appid%3Dwxa");
                      },
                      child: Text("打开url对应的Activity weixin://wap/pay?appid%3Dwxa"),
                    ),
                    FlatButton(
                      onPressed: () {
                        Worldfunclublocal.startActivityWithUrl("http://www.baidu.com");
                      },
                      child: Text("打开url对应的Activity http://www.baidu.com"),
                    ),
                    FlatButton(
                      onPressed: () {
                        Worldfunclublocal.openLocation(116.0,39.0,"");
                      },
                      child: Text("打开位置"),
                    ),
                  ],
                ),
              ),
            )
          ],
        ),
      ),
    );
  }

  @override
  void responseScan(String result) {
     setState(() {
       this.result=result;
     });
  }

  @override
  void wechatCode(String code) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(code)));
  }
}
