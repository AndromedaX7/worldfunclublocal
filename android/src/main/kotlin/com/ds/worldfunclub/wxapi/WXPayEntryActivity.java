package com.ds.worldfunclub.wxapi;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ds.worldfunclublocal.WorldfunclublocalPlugin;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.Arrays;


public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

//    public static void setExtData(PayReq req, String orderId, GoodsType orderType, String pay) {
//        req.extData = orderId + "<->" + orderType.getValue() + "<->" + pay;
//    }


    public static void setExtData(PayReq req, String orderId, String  orderType, String pay) {
        req.extData = orderId + "<->" + orderType  + "<->" + pay;
    }

    public static ArrayList<String> getExtData(PayResp resp) {
        return new ArrayList<>(Arrays.asList(resp.extData.split("<->")));
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("微信", getIntent().toString());
        WXAPIFactory.createWXAPI(this, "wx43736892a139b092").handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("微信", intent.toString());
        WXAPIFactory.createWXAPI(this, "wx43736892a139b092").handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

        int type = baseResp.getType();
        ArrayList<String> extData = getExtData((PayResp) baseResp);
        switch (type) {
            
            case 5: {
                switch (baseResp.errCode) {
                    case 0:
                        WorldfunclublocalPlugin.paySuccess(extData.get(0), extData.get(1),extData.get(2));
                        finish();

                        break;
                    case -1:
                        new AlertDialog.Builder(this)
                                .setTitle("支付失败")
                                .setCancelable(false)
                                .setMessage(baseResp.errStr)
                                .setPositiveButton("回到订单", (dialogInterface, i) -> {

                                    WorldfunclublocalPlugin.openOrderListWillPay( extData.get(1));
                                    finish ();
                                })
                                .create()
                                .show();
                        break;
                    case -2:
                        new AlertDialog.Builder(this)
                                .setTitle("订单未支付")
                                .setCancelable(false)
                                .setMessage(baseResp.errStr)
                                .setPositiveButton("查看订单", (dialogInterface, i) -> {
                                    WorldfunclublocalPlugin.openOrderListWillPay( extData.get(1));
                                    finish ();
                                })
                                .setNegativeButton("取消支付", (d, i) -> {
                                    WorldfunclublocalPlugin.openHome();
                                    finish ();
                                })
                                .create()
                                .show();
                        break;
                }
            }
            break;
            default:
        }
    }

}
