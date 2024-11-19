package com.smb.jc_mylogin.screens.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

import com.smb.jc_mylogin.R
import com.smb.jc_mylogin.navigation.Screens

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val showLoginForm = rememberSaveable {
        mutableStateOf(true)
    }

    val token = "1059932583740-6gaqvkdnm0ogagpiau66ulg1qiod5enf.apps.googleusercontent.com"
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential) {
                navController.navigate(Screens.HomeScreen.name)
            }
        } catch (ex: Exception) {
            Log.d("My Login", "GoogleSignIn falló")
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Black // Fondo negro
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp) // Ajuste para dejar espacio en la parte superior
        ) {
            // Imagen de Spotify en la parte superior
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "Spotify Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Formulario de inicio de sesión o registro
            if (showLoginForm.value) {
                Text(text = "Inicia sesión", color = Color.Green)
                UserForm(isCreateAccount = false) { email, password ->
                    Log.d("My Login", "Logueando con $email y $password")
                    viewModel.signInWithEmailAndPassword(
                        email,
                        password
                    ) {
                        navController.navigate(Screens.HomeScreen.name)
                    }
                }
            } else {
                Text(text = "Crear una cuenta", color = Color.Green)
                UserForm(isCreateAccount = true) { email, password ->
                    Log.d("My Login", "Creando cuenta con $email y $password")
                    viewModel.createUserWithEmailAndPassword(
                        email,
                        password
                    ) {
                        navController.navigate(Screens.HomeScreen.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text1 = if (showLoginForm.value) "¿No tienes cuenta?" else "¿Ya tienes cuenta?"
                val text2 = if (showLoginForm.value) "Regístrate" else "Inicia sesión"
                Text(text = text1, color = Color.Green)
                Text(
                    text = text2,
                    modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 5.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Botón de Google
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        val opciones = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, opciones)
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Login con Google",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                )

                Text(
                    text = "Login con Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green
                )
            }
        }
    }
}


@Composable
fun UserForm(
    isCreateAccount: Boolean,
    onDone: (String, String) -> Unit = { email, pwd -> }
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val valido = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp) // Padding general
          // .background(Color.Black) // Fondo de formulario
    ) {
        EmailInput(emailState = email)

        Spacer(modifier = Modifier.height(10.dp)) // Espacio entre campos

        PasswordInput(passwordState = password, passwordVisible = passwordVisible)

        Spacer(modifier = Modifier.height(20.dp)) // Espacio entre el campo y el botón


        SubmitButton(
            textId = if (isCreateAccount) "Crear cuenta" else "Login",
            inputValido = valido
        ){
            onDone(email.value.trim(), password.value.trim())
            //se oculta el teclado, el ? es que se llama la función en modo seguro
            keyboardController?.hide()
        }
        }
    }




@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClic: () -> Unit
) {
    Button(
        onClick = onClic,
        modifier = Modifier.background(Color.Green, shape = CircleShape),
          //  .padding(12.dp),
        shape = RoundedCornerShape(20.dp) ,
        enabled = inputValido,
       colors = ButtonDefaults.buttonColors(
            containerColor = Color.Green, // Fondo blanco
            contentColor = Color.White // Texto negro
        )
    ) {
        Text(
            text = textId,
            modifier = Modifier.padding(5.dp),
            color = Color.Black,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmailInput(
    emailState: MutableState<String>,
    labelId: String = "Email",
) {
    InputField(
        valuestate = emailState,
        labelId = labelId,
        keyboardType = KeyboardType.Email,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    valuestate: MutableState<String>,
    labelId: String,
    keyboardType: KeyboardType,
    isSingleLine: Boolean = true,
) {
    OutlinedTextField(
        value = valuestate.value,
        onValueChange = { valuestate.value = it },
        label = { Text(text = labelId, color = Color.Green) },
        singleLine = isSingleLine,
        modifier = Modifier
            .padding(bottom = 12.dp, start = 16.dp, end = 16.dp) // Ajustamos los padding
           // .fillMaxWidth()
            .height(56.dp), // Altura constante para los campos
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(color = Color.White),
    )
}

@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    labelId: String = "Password",
    passwordVisible: MutableState<Boolean>
) {
    val visualTransformation = if (passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = labelId, color = Color.Green) },
        singleLine = true,
        modifier = Modifier
            .padding(bottom = 12.dp, start = 16.dp, end = 16.dp) // Ajustamos los padding
            //.fillMaxWidth()
            .height(56.dp), // Altura constante para los campos
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,
        textStyle = TextStyle(color = Color.White),
        trailingIcon = {
            if (passwordState.value.isNotBlank()) {
                PasswordVisibleIcon(passwordVisible)
            } else null
        }
    )
}

@Composable
fun PasswordVisibleIcon(
    passwordVisible: MutableState<Boolean>
) {
    val image = if (passwordVisible.value)
        Icons.Default.VisibilityOff
    else
        Icons.Default.Visibility

    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
        Icon(
            imageVector = image,
            contentDescription = "Toggle password visibility"
        )
    }
}


