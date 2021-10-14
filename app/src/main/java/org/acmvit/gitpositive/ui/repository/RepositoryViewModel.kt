package org.acmvit.gitpositive.ui.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.acmvit.gitpositive.remote.ApiInterface
import org.acmvit.gitpositive.remote.model.Repository
import org.acmvit.gitpositive.remote.model.RepositoryResponseItem
import org.acmvit.gitpositive.ui.following.FollowingViewModel
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class RepositoryViewModel @Inject constructor(
    private val retrofit: ApiInterface
) : ViewModel() {

    private val _repoList: MutableState<List<RepositoryResponseItem>> = mutableStateOf(emptyList())
    val repoList: State<List<RepositoryResponseItem>> = _repoList

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    fun getUserRepositories(username: String?) {
        _viewState.value = ViewState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val repoList = retrofit.getReposForUser(username = username)
                _repoList.value = repoList.toList()
                _viewState.value = ViewState.Success(repoList.map {
                    Repository(
                        it.name.orEmpty(),
                        it.html_url,
                        it.description.orEmpty(),
                        it.language.orEmpty(),
                        it.stargazers_count,
                        it.forks_count
                    )
                })
            } catch (e: Exception) {
                Log.e("RepositoryViewModel", e.message.toString())
                _viewState.value = ViewState.Error(e.message.toString())
            }
        }
    }


    sealed class ViewState {
        object Loading : ViewState()
        data class Error(val message: String) : ViewState()
        data class Success(val followingList: List<Repository>) : ViewState()
    }
}