package com.heathkev.quizado.ui.start

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData
import kotlinx.coroutines.*

//TODO HILT
class LoginViewModel : ViewModel() {

    private var firebaseRepository = FirebaseRepository()
    val currentUser = FirebaseUserLiveData()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState: LiveData<AuthenticationState> = map(currentUser) { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    fun registerUser() {
        uiScope.launch {
            currentUser.value?.let {
                val userMap = HashMap<String, Any?>()
                userMap["name"] = it.displayName
                userMap["email"] = it.email
                userMap["image"] =
                    if (Uri.EMPTY != it.photoUrl) it.photoUrl.toString() else it.photoUrl


                register(it.uid, userMap)
            }

        }
    }

    private suspend fun register(userId: String, userMap: HashMap<String, Any?>) {
        withContext(Dispatchers.IO) {
            firebaseRepository.registerUser(userId, userMap)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
