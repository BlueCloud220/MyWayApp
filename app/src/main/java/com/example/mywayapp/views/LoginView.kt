package com.example.mywayapp.views

import android.content.Context.MODE_PRIVATE
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mywayapp.R
import com.example.mywayapp.components.Space
import com.example.mywayapp.viewModels.UsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavController, viewModel: UsuariosViewModel) {
    val focusManager = LocalFocusManager.current
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }
    val authSuccess by viewModel.authSuccess.collectAsState()
    val authError by viewModel.authError.collectAsState()

    LaunchedEffect(authSuccess) {
        if (authSuccess) {
            navController.navigate("Home") {
                popUpTo("Login") { inclusive = true }
            }
            val sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", true)
            editor.putString("username", state.nombreUsuario)
            editor.putString("password", state.contrasena)
            editor.apply()
        }
    }

    LaunchedEffect(authError) {
        if (authError) {
            Toast.makeText(
                context,
                "Usuario o contraseña incorrectos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.mipmap.logoapp_foreground),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(0.dp)),
                contentScale = ContentScale.Crop
            )

            Text("Iniciar Sesión", fontSize = 26.sp, color = Color(0f, 0.129f, 0.302f, 1f))

            Text(
                "Por favor, inicia sesión para continuar",
                fontSize = 14.sp,
                color = Color(1f, 0.329f, 0.439f, 1f)
            )

            Space(24.dp)

            OutlinedTextField(
                value = state.nombreUsuario,
                onValueChange = { viewModel.onValueChange("nombreUsuario", it) },
                label = {
                    Text(
                        "Usuario",
                        color = if (isSystemInDarkTheme()) Color(0f, 0.129f, 0.302f, 1f) else Color(
                            0f,
                            0.129f,
                            0.302f,
                            1f
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email Icon",
                        tint = Color(0f, 0.129f, 0.302f, 1f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color(0f, 0.129f, 0.302f, 1f)
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0f, 0.129f, 0.302f, 1f),
                    unfocusedBorderColor = Color(0f, 0.129f, 0.302f, 1f),
                    cursorColor = Color(0f, 0.129f, 0.302f, 1f)
                )
            )

            Space(8.dp)

            OutlinedTextField(
                value = state.contrasena,
                onValueChange = { viewModel.onValueChange("contrasena", it) },
                label = {
                    Text(
                        "Contraseña",
                        color = if (isSystemInDarkTheme()) Color(0f, 0.129f, 0.302f, 1f) else Color(
                            0f,
                            0.129f,
                            0.302f,
                            1f
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password Icon",
                        tint = Color(0f, 0.129f, 0.302f, 1f)
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color(0f, 0.129f, 0.302f, 1f),
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0f, 0.129f, 0.302f, 1f),
                    unfocusedBorderColor = Color(0f, 0.129f, 0.302f, 1f),
                    cursorColor = Color(0f, 0.129f, 0.302f, 1f)
                ),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description =
                        if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible },
                        tint = Color(0f, 0.129f, 0.302f, 1f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )

            Space(35.dp)

            Button(
                onClick = {
                    if (state.nombreUsuario.isNotEmpty() && state.contrasena.isNotEmpty()) {
                        viewModel.loadUsuarioAuth(
                            state.nombreUsuario,
                            state.contrasena
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, ingresa usuario y contraseña",
                            Toast.LENGTH_SHORT
                        ).show()
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(
                        0.129f,
                        0.302f,
                        0.986f,
                        1f
                    )
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar", color = Color.White)
            }

            Space(16.dp)

            Row {
                Text("¿No tienes una cuenta? ")
                Text(
                    text = "Regístrate aquí",
                    color = Color(0.129f, 0.302f, 0.986f, 1f),
                    modifier = Modifier
                        .clickable {
                            viewModel.limpiar()
                            navController.navigate("Register") {
                                popUpTo("Login") { inclusive = true }
                            }
                        }
                )
            }
        }
    }
}