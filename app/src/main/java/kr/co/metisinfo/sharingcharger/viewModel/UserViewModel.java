package kr.co.metisinfo.sharingcharger.viewModel;

import androidx.lifecycle.LiveData;
import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.db.repository.UserRepository;
import kr.co.metisinfo.sharingcharger.model.UserModel;

public class UserViewModel extends DisposableViewModel {

    private static final String TAG = UserViewModel.class.getSimpleName();
    private UserRepository repository;

    public UserViewModel() {
        repository = UserRepository.getInstance(ThisApplication.context);
    }

    public void insertUser(UserModel userModel) {
        repository.insertUser(userModel);
    }

    public UserModel selectAutoLoginUser(boolean autoLogin) {

        return repository.selectAutoLoginUser(autoLogin);
    }

    public UserModel selectGetLoginUser(String email, String password) {

        return repository.selectGetLoginUser(email, password);
    }

    public UserModel selectGetLoginUserEmail(String email) {

        return repository.selectGetLoginUserEmail(email);
    }


    public void updateUserPoint(UserModel userModel) {
        repository.updateUserPoint(userModel);
    }

    public void deleteUser(String email) {

        repository.deleteUser(email);
    }

}
