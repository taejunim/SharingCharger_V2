package kr.co.metisinfo.sharingcharger.utils;

import kr.co.metisinfo.sharingcharger.base.WebServiceAPI;
import kr.co.metisinfo.sharingcharger.network.RetrofitFactory;

public class ApiUtils {

    private static final String TAG = ApiUtils.class.getSimpleName();

    private RetrofitFactory retrofitFactory = new RetrofitFactory();
    private WebServiceAPI webServiceAPI = retrofitFactory.build().create(WebServiceAPI.class);

    private CommonUtils cu = new CommonUtils();

    /**
     * 로그인 정보
     **/
//    public Response<UserModel> login(UserModel model) throws Exception {
//
//        Response<UserModel> response = webServiceAPI.login(model).execute();
//
//        return response;
//    }

}
