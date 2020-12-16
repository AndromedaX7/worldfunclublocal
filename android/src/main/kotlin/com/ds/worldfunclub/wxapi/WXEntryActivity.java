package com.ds.worldfunclub.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ds.worldfunclublocal.WorldfunclublocalPlugin;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

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
        switch (type) {
            case 1:
                if (baseResp.errCode == 0) {
                    SendAuth.Resp r = (SendAuth.Resp) baseResp;
                    Log.e("微信code",r.code);
                    WorldfunclublocalPlugin.responseWechatCode(r.code);
                    finish();
//                    ARouter.getInstance().build(login).withInt(LoginActivity.key_state, LoginActivity.wechat_success).withString(key_auth_wechat_code, r.code).navigation();
                } else {
//                    ARouter.getInstance().build(login).withInt(LoginActivity.key_state, LoginActivity.wechat_failed).navigation();
                    finish();
                }
                break;
            default:
        }
    }
}
