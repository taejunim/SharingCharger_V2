package kr.co.metisinfo.sharingcharger.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import kr.co.metisinfo.sharingcharger.base.DBConstants;
import kr.co.metisinfo.sharingcharger.model.BookmarkModel;

@Dao
public interface BookmarkDAO extends BaseDAO<BookmarkModel> {

    @Query("SELECT * FROM " + DBConstants.TABLE_BOOKMARK
            + " WHERE 1=1 "
            + " AND userId = :userId")
    LiveData<List<BookmarkModel>> selectAllBookmark(int userId);

    @Query("SELECT * FROM " + DBConstants.TABLE_BOOKMARK
            + " WHERE 1=1 "
            + " AND userId = :userId"
            + " AND chargerId = :chargerId")
    BookmarkModel selectOneBookmark(int userId,int chargerId);

    @Query("DELETE FROM " + DBConstants.TABLE_BOOKMARK
            + " WHERE 1=1 "
            + " AND userId = :userId"
            + " AND chargerId = :chargerId")
    void deleteBookmarkItem(int userId,int chargerId);

}