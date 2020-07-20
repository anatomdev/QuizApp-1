package com.heathkev.quizado.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.data.User
import com.heathkev.quizado.firebase.FirebaseRepository

private const val TAG = "ProfileViewModel"
class ProfileViewModel(private val currentUser: User) : ViewModel(){

    private val firebaseRepository = FirebaseRepository()
    private val NO_RECORD : Int = 0

    private val _user = MutableLiveData<User>()
    private val _quizzesNumber = MutableLiveData<Int>()
    private val _passedNumber = MutableLiveData<Int>()
    private val _failedNumber = MutableLiveData<Int>()

    val user: LiveData<User>
        get() = _user

    val quizzesNumber: LiveData<String> = Transformations.map(_quizzesNumber){
        it.toString()
    }

    val passedNumber: LiveData<String> = Transformations.map(_passedNumber){
        it.toString()
    }

    val failedNumber: LiveData<String> = Transformations.map(_failedNumber){
        it.toString()
    }

    init {
        _user.value = currentUser
        getResult()
    }

    private fun getResult(){
        firebaseRepository.getResultsByUserId(currentUser.userId).addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                setToZero()
                return@EventListener
            }

            val resultsList: MutableList<Result> = mutableListOf()
            for (doc in value!!) {
                val resultItem = doc.toObject(Result::class.java)

                resultsList.add(resultItem)
            }

            countQuizResult(resultsList)
        })
    }

    private fun countQuizResult(results: List<Result>){
        var passed = 0
        var failed = 0

        for (i in results) if(i.correct > i.wrong) passed++ else failed++

        _passedNumber.value = passed
        _failedNumber.value = failed
        _quizzesNumber.value = results.count()

    }

    private fun setToZero(){
        _quizzesNumber.value = NO_RECORD
        _passedNumber.value = NO_RECORD
        _failedNumber.value = NO_RECORD
    }
}