package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TweetsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TweetsViewModel() {
        mText = new MutableLiveData<>();
    }

    public void setText(String text) {
        mText.setValue(text);
    }

    public LiveData<String> getText() {
        return mText;
    }
}