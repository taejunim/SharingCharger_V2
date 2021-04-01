package kr.co.metisinfo.sharingcharger.db.dao;


import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

public interface BaseDAO <T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long Insert(T Object);
}
