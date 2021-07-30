package kr.co.metisinfo.sharingcharger.db.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kr.co.metisinfo.sharingcharger.db.RoomDatabaseInfo;
import kr.co.metisinfo.sharingcharger.db.dao.BookmarkDAO;
import kr.co.metisinfo.sharingcharger.model.BookmarkModel;

public class BookmarkRepository {

    private static final String TAG = BookmarkRepository.class.getSimpleName();

    private static BookmarkRepository repository;
    BookmarkDAO bookmarkDAO;

    Context context;
    Executor executor;

    public static BookmarkRepository getInstance(Context context) {
        if (repository == null) {
            synchronized (BookmarkRepository.class) {
                if (repository == null) {
                    repository = new BookmarkRepository(context);
                }
            }
        }
        return repository;
    }

    private BookmarkRepository(Context context) {
        this.context = context;
        executor = Executors.newSingleThreadExecutor();

        RoomDatabaseInfo database = RoomDatabaseInfo.getDatabase(context);

        //DAO
        bookmarkDAO = database.getBookmarkDAO();
    }

    public void insertBookmark(BookmarkModel model) {

        executor.execute(() -> bookmarkDAO.Insert(model));
    }

    public LiveData<List<BookmarkModel>> selectAllBookmark(int userId) {
        return bookmarkDAO.selectAllBookmark(userId);
    }

    public BookmarkModel selectOneBookmark(int userId,int chargerId) {
        return bookmarkDAO.selectOneBookmark(userId, chargerId);
    }

    public void deleteBookmarkItem(int userId,int chargerId){
        executor.execute(() -> bookmarkDAO.deleteBookmarkItem(userId, chargerId));
    }
}
