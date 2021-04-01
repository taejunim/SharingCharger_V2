package kr.co.metisinfo.sharingcharger.db.dao;

import androidx.room.Dao;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import kr.co.metisinfo.sharingcharger.base.DBConstants;
import kr.co.metisinfo.sharingcharger.model.UserModel;

@Dao
public interface UserDAO extends BaseDAO<UserModel> {

    @Query("SELECT * FROM " + DBConstants.TABLE_USER
            + " WHERE 1=1 "
            + " AND autoLogin = :autoLogin")
    UserModel selectAutoLoginUser(boolean autoLogin);

    @Query("SELECT * FROM " + DBConstants.TABLE_USER
            + " WHERE 1=1 "
            + " AND email = :email"
            + " AND password = :password")
    UserModel selectGetLoginUser(String email, String password);

    @Query("SELECT * FROM " + DBConstants.TABLE_USER
            + " WHERE 1=1 "
            + " AND email = :email")
    UserModel selectGetLoginUserEmail(String email);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUserPoint(UserModel userModel);

    @Query("DELETE FROM " + DBConstants.TABLE_USER
            + " WHERE 1=1 "
            + " AND email = :email")
    void deleteUser(String email);
}
