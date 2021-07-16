package kr.co.metisinfo.sharingcharger.db.repository;

import android.content.Context;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import kr.co.metisinfo.sharingcharger.db.RoomDatabaseInfo;
import kr.co.metisinfo.sharingcharger.db.dao.UserDAO;
import kr.co.metisinfo.sharingcharger.model.UserModel;

public class UserRepository  {

    private static final String TAG = UserRepository.class.getSimpleName();

    private static UserRepository repository;
    UserDAO userDAO;

    Context context;
    Executor executor;

    public static UserRepository getInstance(Context context) {
        if (repository == null) {
            synchronized (UserRepository.class) {
                if (repository == null) {
                    repository = new UserRepository(context);
                }
            }
        }
        return repository;
    }

    private UserRepository(Context context) {
        this.context = context;
        executor = Executors.newSingleThreadExecutor();

        RoomDatabaseInfo database = RoomDatabaseInfo.getDatabase(context);

        //DAO
        userDAO = database.getUserDAO();
    }

    public void insertUser(UserModel userModel) {

        executor.execute(() -> userDAO.Insert(userModel));
    }

    public UserModel selectAutoLoginUser(boolean autoLogin) {
        return userDAO.selectAutoLoginUser(autoLogin);
    }

//    public UserModel selectGetLoginUser(String email, String password) {
//
//        return userDAO.selectGetLoginUser(email, password);
//    }
//
    public UserModel selectGetLoginUserEmail(String email) {

        return userDAO.selectGetLoginUserEmail(email);
    }
//
//    public void updateUserPoint(UserModel userModel) {
//        executor.execute(() -> userDAO.updateUserPoint(userModel) );
//    }
//
//    public void deleteUser(String email) {
//
//        executor.execute(() -> userDAO.deleteUser(email));
//    }

}
