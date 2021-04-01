package kr.co.metisinfo.sharingcharger.viewModel;

import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DisposableViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable;


    public void addDisposable(Disposable disposable){
        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(disposable);
    }


    @Override
    protected void onCleared() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }

        compositeDisposable = null;

        super.onCleared();
    }
}
