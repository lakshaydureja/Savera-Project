package com.example.savera.Screens.homeScreen

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.savera.Navigation.mainScreenNavigation.mainScreen
import com.example.savera.R
import com.example.savera.Screens.events.loadBitmap
import com.example.savera.Screens.events.uploadImageToFirebase
import com.example.savera.ui.theme.ralewaybold
import com.example.savera.ui.theme.ralewayfamilt
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun homeScreen(
    selectindex: MutableIntState,
    mainscreennav: NavHostController,
    homeScreenViewModel: HomeScreenViewModel = viewModel(),
) {



    val youtubestate = remember {
        mutableStateOf(0f)
    }

    val newUser = remember {
        mutableStateOf(false)
    }

    val email = Firebase.auth.currentUser?.email

    LaunchedEffect(Unit) {
        if (email != null) {
            homeScreenViewModel.checking(email,
                exists = {},
                notexists = {
                    newUser.value = true
                }
            )
        }
    }



    BackHandler {
        selectindex.value = 2
        mainscreennav.navigate(route = mainScreen.Dashboard.name)
    }

    homeui(youtubestate, homeScreenViewModel)

    if (newUser.value) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            newUserUi(homeScreenViewModel,email,newUser)
        }
    }

}



@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun newUserUi(
    homeScreenViewModel: HomeScreenViewModel,
    email: String?,
    newUser: MutableState<Boolean>
) {

  val loading  = remember {
      mutableStateOf(0)
  }

   val name = remember {
       mutableStateOf("")
   }
    val phone = remember {
        mutableStateOf("")
    }

  val year = remember {
      mutableStateOf("")
  }

  val gender = remember {
      mutableStateOf("Male")
  }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    val error = remember {
        mutableStateOf("")
    }




    Surface(modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10),
        shadowElevation = 20.dp
    ) {
        if (loading.value==0)
        Column(
            modifier = Modifier.padding(top = 10.dp, start = 4.dp, end = 4.dp,
                bottom = 10.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {



            Text(
                text = "Enter Your Details",
                fontFamily = ralewaybold,
                style = MaterialTheme.typography.titleLarge
            )

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clickable {
                        launcher.launch("image/*")
                    }

            ) {

                if (imageUri==null)
                Image(
                    painter = painterResource(id = R.drawable.adduser), contentDescription = "",
                    modifier = Modifier.background(color = Color.Gray, shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
                else{
                    imageUri.let {
                        val bitmap = it?.let { it1 -> loadBitmap(context = context, uri = it1) }
                            .let {
                                it?.asImageBitmap()?.let { it1 ->
                                    Image(bitmap = it1,
                                        contentDescription ="" ,
                                        modifier = Modifier
                                            .background(color = Color.Gray, shape = CircleShape)
                                            .clip(shape = CircleShape)
                                        ,
                                        contentScale = ContentScale.Crop
                                        )
                                }
                            }

                    }
                }



                Icon(
                    imageVector = Icons.Filled.AddCircle, contentDescription = "",
                    modifier = Modifier.align(Alignment.BottomEnd)
                )


            }
            inputValue(name,"Name","text")

            inputValue(name = phone, placeholder = "Phone no.", keyboard = "number")

            inputValue(name = year, placeholder = "Year", keyboard ="number" )


      Surface(modifier = Modifier.padding(10.dp),
          color = Color(0xff003566),
          shape = CircleShape
      ) {
          Row(modifier = Modifier.padding(10.dp)) {
              button(text = "Male",
                  modifier = Modifier
                      .background(color =
                      if (gender.value =="Male")
                      Color(0xffFB5607)
                          else{
                              Color.Transparent
                              }
                          ,
                          shape = CircleShape)
              ) {
                  gender.value = "Male"

              }
              button(text = "Female",
                  modifier = Modifier
                      .background(color = if (gender.value =="Female")
                          Color(0xffFB5607)
                      else{
                          Color.Transparent
                      },
                          shape = CircleShape)
                  ) {
                  gender.value = "Female"

              }
          }
      }



            button(text = "Submit",
                modifier = Modifier.background(
                    color =  Color(0xff003566)
                    , shape = RoundedCornerShape(10.dp)
                )) {



                if (name.value.isNullOrBlank()||phone.value.isNullOrBlank()||year.value.isNullOrBlank()
                    ||imageUri==null){
                    error.value ="*Please Ensure No Field Is Empty \n" +
                            "*Also Choosing Profile Pic is Compulsory"
                }




                    else
                loading.value=1

                imageUri?.let { it ->
                    uploadImageToFirebase(
                        context = context,
                        eventname = name.value,
                        imageUri = it,
                        mainfoldername = "Profile Pictures",
                        exception = {error1->

                                    loading.value =2
                                   error.value =error1



                        },
                        onSuccess = {

                       val date =  LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-YY"))

                            if (email != null) {
                                homeScreenViewModel.addnewuser(
                                    documentPath = email,
                                    data = hashMapOf(
                                        "Admin" to "False",
                                        "Attendance" to "0",
                                        "Date of Join" to date,
                                        "Name" to name.value,
                                        "Phone" to phone.value,
                                        "ProfilePic" to it,
                                        "Year" to year.value
                                    ),
                                    error = {error2->
                                            loading.value = 2
                                        error.value  =error2

                                    },
                                    successfull = {
                                        loading.value=3
                                    }
                                )
                            }

                        }


                        )
                }






            }

            textout(title = error.value, modifier = Modifier, fontStyle = MaterialTheme.typography.bodyMedium)

        }




        if (loading.value==2) {
            Column(modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = error.value, fontFamily = ralewayfamilt,
                    color = Color.Red
                )
                button(text = "Try Again") {
                    loading.value = 0
                }

            }
        }
    }

    if (loading.value ==1) {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ) {


            CircularProgressIndicator(
                color = Color(0xffF9A825)
            )

    }}
    if (loading.value==3){
     animation(newUser, false, state = loading)

    }

}

@Composable
fun animation(newUser: MutableState<Boolean>, Boolean: Boolean, state: MutableState<Int>) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
LaunchedEffect(Unit) {
    scale.animateTo(
        targetValue = 1f,
                animationSpec = tween(
                durationMillis =1000
        ,easing = {
            OvershootInterpolator(2f)
                .getInterpolation(it)
        }
    )


    )
if (!Boolean)
    newUser.value = false
else {
    newUser.value = true
state.value = 0

}

}
Surface(modifier = Modifier.fillMaxSize(),
    color = Color.Transparent) {

Column(verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize()) {


    Image(
        painter = painterResource(id = R.drawable.checklist),
        contentDescription = "",
        modifier = Modifier.scale(scale.value)
    )
}
}
}


@Composable
fun inputValue(name: MutableState<String>,placeholder:String,keyboard:String) {
TextField(value = name.value, onValueChange = {
    name.value = it
}

, singleLine = true
    ,
maxLines = 1
    , keyboardOptions = KeyboardOptions(keyboardType =
    if (keyboard =="number")
    KeyboardType.Number
    else
        KeyboardType.Text
    )
,
    label = { Text(text = placeholder)}

)

}
