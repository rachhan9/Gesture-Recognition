package ca.uwaterloo.cs349;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    static ArrayList<OneStroke> savedStrokes = new ArrayList<>();


    public SharedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(savedStrokes.toString());
    }

    public LiveData<String> getText() {
        return mText;
    }



}