package kr.co.metisinfo.sharingcharger.view.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import java.net.URISyntaxException;
import java.text.DecimalFormat;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.databinding.ActivityPurchaseWebviewBinding;

public class PurchaseWebViewActivity extends BaseActivity {


    ActivityPurchaseWebviewBinding binding;

    WebView mWebView;
    MyWebViewClient mMyWebViewClient;

    // ////////////////////////////////////////////////
    //
    // 결제수단별 결제 APP을 구분하기 위한 상수값
    // (해당 값은 변경하지 마세요.)
    //
    // ////////////////////////////////////////////////
    final int ISP_CALL = 1;
    final int BANK_CALL = 2;
    final int SUCCESS = 0;
    final int FAIL = 1;

    private String TID = "";


    private String resultString = null;
    private String dataURI[] = {
            "http://market.android.com" ,
            "mvaccine" ,
            "vguard" ,
            "droidxantivirus" ,
            "smhyundaiansimclick://" ,
            "smshinhanansimclick://" ,
            "smshinhancardusim://" ,
            "kebcard" ,
            "smartwall://" ,
            "appfree://" ,
            "market://" ,
            "v3mobile" ,
            ".apk" ,
            "ansimclick" ,
            "http://m.ahnlab.com/kr/site/download" ,
            "cloudpay" ,
            "com.lotte.lottesmartpay" ,
            "lottesmartpay://" ,
            "lottecard" ,
            "com.hanaskcard.paycla",
            "citispayapp",
            "hanaansim",
            "com.ahnlab.v3mobileplus",
            "citicardapp",
            "com.TouchEn.mVaccine.webs",
            "nhallonepayansimclick://",
            "smartpay" ,
            "mvaccinestart" ,
            "kb-acp"
    };

    boolean isOwn = false;

    @Override
    public void initLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_webview);
        changeStatusBarColor(false);
    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {
        binding.includeHeader.btnBack.setOnClickListener(view -> finish());
    }

    @Override
    public void init() {
        binding.includeHeader.txtTitle.setText("포인트 구매");                                           //HEADER TXT SET
        binding.includeHeader.btnMenu.setVisibility(View.INVISIBLE);

        String url = getIntent().getStringExtra("url");

        Log.e("KDH", "url = "+url);

        mWebView = (WebView)findViewById(R.id.purchase_webView);

        mMyWebViewClient = new MyWebViewClient();
        mWebView.setWebViewClient(mMyWebViewClient);
        mWebView.setWebChromeClient(new LoginChromeClient());

        mWebView.loadUrl(url);


        mWebView.getSettings().setSaveFormData(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        mWebView.addJavascriptInterface(new AndroidBridge(),"myInterface");

    }

    private Handler handler2 = new Handler();

    private class AndroidBridge {

        @JavascriptInterface
        public void payOrder(final String arg, final String arg2, String arg3)
        {
            handler2.post(new Runnable()
            {
                public void run()
                {
                    Log.e("KDH", "payOrder = "+arg +"  2 = "+arg2 + "  3 = "+arg3);

//                    Code : 1 – 성공(1),실패(1), 2 –메세지 , 종료 3: -> 총합포인트
                    if("1".equals(arg))
                    {
                        //Toast.makeText(getApplicationContext(), arg2+" 총합 포인트 = "+arg3, Toast.LENGTH_SHORT).show();
                        DecimalFormat decimalFormat = new DecimalFormat("###,###");
                        Intent intent = new Intent();
                        //종합 포인트에 컴마를 찍어 보내준다.
                        intent.putExtra("totalPoint", decimalFormat.format(Integer.parseInt(arg3)));
                        setResult(RESULT_OK, intent);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), arg2, Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
            });
        }
    }

    class MyWebViewClient extends WebViewClient {
        //        ProgressBar pb;
        //        public MyWebViewClient(ProgressBar _pb) {
        //            pb = _pb;
        //        }
        public MyWebViewClient() {
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            //웹뷰 내 표준창에서 외부앱(통신사 인증앱)을 호출하려면 intent:// URI를 별도로 처리해줘야 합니다.
            //다음 소스를 적용 해주세요.

            if(isOwn)
            {
                Intent intent = null;
                if (url.startsWith("intent://")) {
//                Intent intent = null;
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            //앱실행
                            startActivity(intent);
                        }
                    } catch (URISyntaxException e) {
                        //URI 문법 오류 시 처리 구간
                    } catch (ActivityNotFoundException e) {
                        String packageName = intent.getPackage();
                        if (!packageName.equals("")) {
                            // 앱이 설치되어 있지 않을 경우 구글마켓 이동
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //return  값을 반드시 true로 해야 합니다.
                    return true;
                }
                else
                {
                    view.loadUrl(url);
                }
            }
            else
            {
                Intent intent = null;
                if (url.contains("ispmobile")) { // ISP 결제시 ISP 인증 APP 호출

                    try{
                        String[] arrTid = url.split("=");
                        TID = arrTid[arrTid.length-1];

                        if (installCheck("ISP")) {
                            Intent isp = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivityForResult(isp, ISP_CALL);
                        } else { // ISP APP 이 설치 되지 않았을 경우 Market으로 연결
                            Uri uri = Uri.parse("market://details?id=kvp.jjy.MispAndroid320");
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                            }
                        } catch (Exception e){                                                      //비씨 페이북은 결제가 안되므로 앱이 설치되어 있어도 연결되지 않게 처리
                            Toast.makeText(getBaseContext(), "지원하지 않는 결제 방식입니다.\n다른 카드사를 선택하여 주십시오.",Toast.LENGTH_LONG).show();
                            mWebView.goBack();
                    }
                    return true;

                }else if(url.contains("bankpay")){
                    try{
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    if (url.startsWith("intent")) { // chrome 버젼 방식 // 앱설치 체크를 합니다.
                        if (getPackageManager().resolveActivity(intent, 0) == null) {
                            String packagename = intent.getPackage();
                            if (packagename != null) {
                                Uri uri = Uri.parse("market://details?id=" + packagename); // 마켓으로 바로 이동
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                return true;
                            }
                        } else { // 앱이 설치되어 있으면
                            int runType = Integer.parseInt(android.os.Build.VERSION.SDK);
                            if (runType <= 18) {
                                Uri uri = Uri.parse(intent.getDataString());
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } else {
                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                intent.setComponent(null);
                                try {
                                    if (startActivityIfNeeded(intent, -1)) {
                                        return true;
                                    }
                                } catch (ActivityNotFoundException ex) {
                                    return false;
                                }
                            }

                        }
                    }else { // 구 방식
                        Uri uri = Uri.parse(url);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }

                    return true;
                }else if(checkUri(url)){
                    Log.d("url  " , url );
                    try {

                        try {
                            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        } catch (URISyntaxException ex) {
                            return false;
                        }
                        if (url.startsWith("intent")) { // chrome 버젼 방식 // 앱설치 체크를 합니다.

                            if (getPackageManager().resolveActivity(intent, 0) == null) {
                                String packagename = intent.getPackage();
                                if (packagename != null) {
                                    Uri uri = Uri.parse("market://search?q=pname:"+ packagename);
                                    intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                    return true;
                                }
                            }
                            int runType = Integer.parseInt(android.os.Build.VERSION.SDK);
                            if (runType <= 18) {
                                Uri uri = Uri.parse(intent.getDataString());
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } else {
                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                intent.setComponent(null);
                                try {
                                    if (startActivityIfNeeded(intent, -1)) {
                                        return true;
                                    }
                                } catch (ActivityNotFoundException ex) {
                                    return false;
                                }
                            }
                        } else { // 구 방식
                            Uri uri = Uri.parse(url);
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                        return true;
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        return false;
                    }
                } else if (url.startsWith("tel:")) {
                    Intent phone = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    // 현재의 activity 에 대해 startActivity 호출
                    startActivity(phone);
                    return true;
                }
                else if (url.startsWith("https://play.google.com/store/apps/details?id=") || url.startsWith("market://details?id=")) {
                    //표준창 내 앱설치하기 버튼 클릭 시 PlayStore 앱으로 연결하기 위한 로직
                    Uri uri = Uri.parse(url);
                    String packageName = uri.getQueryParameter("id");
                    if (packageName != null && !packageName.equals("")) {
                        // 구글마켓 이동
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    }
                    //return  값을 반드시 true로 해야 합니다.
                    return true;
                }
                else if (url.indexOf("jjy.MispAndroid320") > -1) {                                  //비씨 페이북은 결제가 안되므로 다운로드 링크로 연결되지 않게 처리
                    Toast.makeText(getBaseContext(), "지원하지 않는 결제 방식입니다.\n다른 카드사를 선택하여 주십시오.",Toast.LENGTH_LONG).show();
                    mWebView.goBack();
                    return true;
                }
                else {
                    view.loadUrl(url);
                }
            }

            return false;

            //return  값을 반드시 false로 해야 합니다.
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (view == null)
                return;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub

            //            pb.setVisibility(View.GONE);
            //            			view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }

    public class LoginChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            final JsResult finalRes = result;
            if (!TextUtils.isEmpty(message))
            {

                switch (message.substring(0,4)){
                    case "W002" :
                        //결제 웹뷰 취소 버튼
                        finish();
                        break;
                    default:
                        // 정상 alert() 함수 처리
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                view.getContext());
                        builder.setTitle(message).setIcon(R.mipmap.ic_launcher).setMessage(message)
                                .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        result.confirm();
                                    }
                                }).create().show();
                        break;
                }
                return true;
            }
            else
            {
                // 정상 alert() 함수 처리
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        view.getContext());
                builder.setIcon(R.mipmap.ic_launcher).setMessage(message)
                        .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                result.confirm();
                            }
                        }).setCancelable(false).create().show();
            }

            return true;
        }
    }


    public boolean checkUri(String url){
        boolean result = false;
        if(url != null){
            if(dataURI == null){
                return result;
            }

            for(int i=0; i<dataURI.length; i++ ){
                dataURI[i] = dataURI[i].replace(System.getProperty("line.separator"), "");
                if(url.contains(dataURI[i])){
                    if(dataURI[i].equals(".apk")){
                        if(url.endsWith(dataURI[i])){
                            result = true;
                        }else{
                            result = false;
                        }
                    }else{
                        result = true;
                    }

                    break;
                }
            }
        }
        return result;
    }

    /**
     * APP 설치체크
     *
     * @param searchApp
     * @return
     */
    private boolean installCheck(String searchApp) {
        try {
            PackageManager pm = getPackageManager();
            if ("ISP".equals(searchApp)) {
                pm.getPackageInfo("kvp.jjy.MispAndroid320", PackageManager.GET_META_DATA);
            } else if ("bankpay".equals(searchApp)) {
                pm.getPackageInfo("com.kftc.bankpay.android", PackageManager.GET_META_DATA);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
