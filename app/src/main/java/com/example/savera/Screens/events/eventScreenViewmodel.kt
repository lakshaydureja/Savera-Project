package com.example.savera.Screens.events


import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savera.Model.events_Data
import com.example.savera.Repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class eventScreenViewmodel:ViewModel() {
// add events
    fun addeventsDetail(collection:String,
                        document: String,
                        data:HashMap<String,String>,
                        onsuccess:()->Unit,
                        failure:(String)->Unit){

        AppRepository.addevents(
            collectionName = collection,
            documentPath = document,
            list = data,
            successfull = {
onsuccess()
            },
            failure = {
failure(it)
            }
        )

    }



    // getting events
   var events :MutableStateFlow<List<events_Data>> =MutableStateFlow(emptyList())
    init {


        events = AppRepository.getEvents()
    }







}