package com.malist.app.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.malist.app.data.api.JikanModels;
import com.malist.app.data.repository.JikanRepository;

import java.util.List;

public class AnimeFullDetailViewModel extends ViewModel {

    private final JikanRepository repository = new JikanRepository();

    private final MutableLiveData<List<JikanModels.JikanCharacterData>> _characters = new MutableLiveData<>();
    public LiveData<List<JikanModels.JikanCharacterData>> getCharacters() { return _characters; }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() { return _isLoading; }

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> getError() { return _error; }

    public void loadCharacters(int malId) {
        _isLoading.setValue(true);
        _error.setValue(null);

        repository.getAnimeCharacters(malId, new JikanRepository.ApiCallback<JikanModels.JikanCharacterResponse>() {
            @Override
            public void onSuccess(JikanModels.JikanCharacterResponse result) {
                _characters.setValue(result.data);
                _isLoading.setValue(false);
            }

            @Override
            public void onError(String message) {
                _error.setValue(message);
                _isLoading.setValue(false);
            }
        });
    }
}
