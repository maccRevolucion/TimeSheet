package com.macc.timesheet.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.macc.timesheet.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    navController: NavController,
    viewModel: EmployeeListViewModel = hiltViewModel(navController.getBackStackEntry("welcome"))
) {
    val selectedEmployee by viewModel.selectedEmployee
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val fontMedium = FontFamily(Font(R.font.roboto_medium))
    val fontLight = FontFamily(Font(R.font.roboto_light))
    val fontItalic = FontFamily(Font(R.font.roboto_semicondensed_italic))
    val lastCheckedEmployee by viewModel.lastCheckedEmployee

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8FA))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = selectedEmployee?.nombreCompleto ?: "Selecciona un empleado para empezar!",
                fontSize = 18.sp,
                fontFamily = fontMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    navController.navigate("employee_list") {
                        popUpTo("welcome") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp)
                    .padding(bottom = 8.dp),
                elevation = ButtonDefaults.buttonElevation(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Buscar Empleado",
                    fontFamily = fontLight
                )
            }

            Button(
                onClick = {
                    selectedEmployee?.idEmpleado?.let { id ->
                        viewModel.postAttendance(id) { response ->
                            // Result is handled via attendanceResult flow
                        }
                    } ?: coroutineScope.launch {
                        snackbarHostState.showSnackbar("Por favor, selecciona un empleado primero")
                    }
                },
                modifier = Modifier.width(250.dp),
                enabled = selectedEmployee != null,
                elevation = ButtonDefaults.buttonElevation(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5BA67C),
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Filled.Check,
                    contentDescription = "Check Icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = "Checar Asistencia",
                    fontFamily = fontLight
                )
            }
            Text(
                text = lastCheckedEmployee?.let { "Ultima asistencia: ${it.first?.nombreCompleto} a las ${it.second}" }
                    ?: "",
                fontSize = 14.sp,
                fontFamily = fontItalic,
                color = Color(0xFF616161),
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )

        }
    }

    LaunchedEffect(Unit) {
        viewModel.attendanceResult.collectLatest { result ->
            result?.let {
                val message = when {
                    it.success -> "Éxito al registrar asistencia"
                    it.message?.contains("asistencia ya", ignoreCase = true) == true -> "Asistencia ya registrada"
                    else -> it.message ?: "Error al registrar asistencia"
                }
                snackbarHostState.showSnackbar(message)
                viewModel.clearAttendanceResult()
            }
        }
    }
}

