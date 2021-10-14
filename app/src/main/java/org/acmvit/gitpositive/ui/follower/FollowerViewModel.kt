package org.acmvit.gitpositive.ui.follower

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.acmvit.gitpositive.remote.ApiInterface
import org.acmvit.gitpositive.remote.model.Follower
import org.json.JSONObject
import javax.inject.Inject


@HiltViewModel
class FollowerViewModel @Inject constructor(private val apiInterface: ApiInterface) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    fun getUserFollowers(userName: String) {
        _viewState.value = ViewState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val response = apiInterface.getUserFollowers(userName)
            if (response.isSuccessful && response.body() != null) {
                _viewState.value = ViewState.Success(response.body()!!)
            } else {
                _viewState.value = ViewState.Error(
                    JSONObject(
                        response.errorBody()?.string().orEmpty()
                    ).get("message") as String
                )
            }
        }
    }

    sealed class ViewState {
        object Loading : ViewState()
        data class Error(val message: String) : ViewState()
        data class Success(val followingList: List<Follower>) : ViewState()
    }
}