package kr.co.metisinfo.sharingcharger.utils;

import kr.co.metisinfo.sharingcharger.base.WebServiceAPI;
import kr.co.metisinfo.sharingcharger.model.UserModel;
import kr.co.metisinfo.sharingcharger.network.RetrofitFactory;
import retrofit2.Response;

public class ApiUtils {

    private static final String TAG = ApiUtils.class.getSimpleName();

    private RetrofitFactory retrofitFactory = new RetrofitFactory();
    private WebServiceAPI webServiceAPI = retrofitFactory.build().create(WebServiceAPI.class);

    private CommonUtils cu = new CommonUtils();

    /**
     *  회원가입
     **/
    public Response<Object> signUp(UserModel model) throws Exception {

        Response<Object> response = webServiceAPI.signUp(model).execute();

        return response;
    }

}
