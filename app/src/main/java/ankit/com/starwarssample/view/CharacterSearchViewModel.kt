package ankit.com.starwarssample.view

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ankit.com.domain.core.successOr
import ankit.com.domain.usecase.SearchCharactersUseCase
import ankit.com.starwarssample.mapper.toPresentationCharacterList
import ankit.com.starwarssample.model.CharacterPresentationModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

/**
 * Created by AnkitSingh on 12/12/20.
 */
private const val SEARCH_DELAY_MILLIS = 500L
private const val MIN_QUERY_LENGTH = 3

class CharacterSearchViewModel @ViewModelInject constructor(
        private val searchCharactersUseCase: SearchCharactersUseCase
) : ViewModel() {

    private val _characters = MutableLiveData<List<CharacterPresentationModel>>()
    val characters: LiveData<List<CharacterPresentationModel>> = _characters

    fun searchStarWarCharacters(characterName: String) {
        viewModelScope.launch {
            getStarWarsCharacters(characterName)
        }
    }

    private suspend fun getStarWarsCharacters(characterName: String) {
        searchCharactersUseCase(characterName)
                .debounce(SEARCH_DELAY_MILLIS)
                .mapLatest {
                    if (characterName.length >= MIN_QUERY_LENGTH) {
                        searchCharactersUseCase(characterName)
                        it.successOr(emptyList())
                    } else {
                        emptyList()
                    }
                }
                .collect {
                    _characters.value = it.toPresentationCharacterList()
                }
    }
}