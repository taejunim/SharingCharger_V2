package kr.co.metisinfo.sharingcharger.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import kr.co.metisinfo.sharingcharger.R;
import kr.co.metisinfo.sharingcharger.base.BaseActivity;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.databinding.ActivityWebViewBinding;
import kr.co.metisinfo.sharingcharger.utils.ApiUtils;

public class WebViewActivity extends BaseActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    ActivityWebViewBinding binding;

    private String getTagName = "";

    private String url = "http://211.43.13.173";                                                  //실서버
    //private String url = "http://211.253.37.97:8101";                                               //테스트 서버

    private boolean chkScroll = false;

    ApiUtils apiUtils = new ApiUtils();

    @Override
    public void initLayout() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (IllegalStateException ignore) {

        }

        changeStatusBarColor(false);
    }

    @Override
    public void initViewModel() {

    }

    @Override
    public void setOnClickListener() {

        binding.personalWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                Log.e("metis", "oldScale : " + oldScale);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.e("metis", "onPageFinished");
            }
        });

        binding.personalWebView.setOnScrollChangeListener(new WebView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!getTagName.equals("setting") && !getTagName.equals("notice")) {
                //스크롤 안의 높이
                    v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

                    Log.e("metis", "View : " + v.getMeasuredHeight());
                    //마지막 높이랑 2200정도 차이가남
                    int getHeight = v.getMeasuredHeight() - 2200;

                    Log.e("metis", "getHeight : " + getHeight);

                    if (!chkScroll) {

                        if (scrollY >= getHeight - 100) {
                            chkScroll = true;

                            binding.personalBtn.setVisibility(View.VISIBLE);
                            v.scrollTo(0, getHeight - 800);

                        }
                    }
                }

            }
        });

        binding.personalBtn.setOnClickListener(view -> {
            if (!getTagName.equals("setting")) {
                Intent intent = new Intent();

                intent.putExtra("getTagName", getTagName);

                setResult(RESULT_OK, intent);

                finish();
            }

        });

    }

    //@SuppressLint("SetJavaScriptEnabled")
    @Override
    public void init() {

        Intent intent = getIntent();

        getTagName = intent.getStringExtra("getTagName");

        binding.personalWebView.setWebViewClient(new WebViewClient());

        WebSettings mWebSettings = binding.personalWebView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부

        //회원 증명서
        if (getTagName.equals("setting")) {
            binding.personalBtn.setVisibility(View.GONE);
            //http://118.67.132.235:8081/Alice
            url = "http://211.253.37.97:52340/information/app/notice";

            binding.personalWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            binding.personalWebView.setMinimumHeight(800);
            mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
            mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
            mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
            mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부

            mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
            mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
            mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
            mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

            binding.personalWebView.loadUrl(url); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작

        }
        //공지사항
        else if (getTagName.equals("notice")) {
            binding.personalBtn.setVisibility(View.GONE);

            //url = "https://monttak.co.kr/information/app/notice";
            url = "http://211.253.37.97:52340/information/app/notice";

            binding.personalWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            binding.personalWebView.setMinimumHeight(800);
            mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
            mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
            mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
            mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부

            mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
            mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
            mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
            mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

            binding.personalWebView.loadUrl(url); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
        }
        //개인정보 동의여부
        else {

            try {

                String getText = apiUtils.getPolicy(getTagName);

                //정보 가져오기 성공
                if(getText != null){
                    System.out.println(getText);

                    String base64 = android.util.Base64.encodeToString(getText.getBytes("UTF-8"), android.util.Base64.DEFAULT);
                    binding.personalWebView.loadData(base64, "text/html; charset=utf-8", "base64");
                }
                //실패
                else{
                    Toast.makeText(WebViewActivity.this, "약관 불러오기에 실패하였습니다. 관리자에게 문의하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(WebViewActivity.this, "약관 불러오기에 실패하였습니다. 관리자에게 문의하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
                Log.e("metis", "PersonalInfo1 Exception: " + e);

            }

        }
    }

    public void onBackPressed() {
        finish();
    }
}
