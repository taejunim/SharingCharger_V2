package kr.co.metisinfo.sharingcharger.viewModel;

import androidx.lifecycle.LiveData;

import java.util.List;

import kr.co.metisinfo.sharingcharger.base.ThisApplication;
import kr.co.metisinfo.sharingcharger.db.repository.BookmarkRepository;
import kr.co.metisinfo.sharingcharger.model.BookmarkModel;

public class BookmarkViewModel extends DisposableViewModel{

    private static final String TAG = BookmarkViewModel.class.getSimpleName();
    private BookmarkRepository repository;

    public BookmarkViewModel() {
        repository = BookmarkRepository.getInstance(ThisApplication.context);
    }

    public void insertBookmark(BookmarkModel model) {
        repository.insertBookmark(model);
    }

    public LiveData<List<BookmarkModel>> selectAllBookmark(int userId) {

        return repository.selectAllBookmark(userId);
    }

    public BookmarkModel selectOneBookmark(int userId,int chargerId) {

        return repository.selectOneBookmark(userId, chargerId);
    }

    public void deleteBookmarkItem(int userId,int chargerId){

        repository.deleteBookmarkItem(userId, chargerId);
    }

}
