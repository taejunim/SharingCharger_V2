package kr.co.metisinfo.sharingcharger.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import kr.co.metisinfo.sharingcharger.base.DBConstants;
import kr.co.metisinfo.sharingcharger.db.dao.UserDAO;
import kr.co.metisinfo.sharingcharger.model.UserModel;

@Database(entities = { UserModel.class}, version = 1, exportSchema = false)

public abstract class RoomDatabaseInfo extends RoomDatabase implements DBConstants {

    public abstract UserDAO getUserDAO();

    public static RoomDatabaseInfo instance;

    public static RoomDatabaseInfo getDatabase(Context context) {
        if (instance == null) {
            synchronized (RoomDatabaseInfo.class) {
                instance = Room.databaseBuilder(context.getApplicationContext(), RoomDatabaseInfo.class, DB_NAME).build();
            }
        }

        return instance;
    }
}
