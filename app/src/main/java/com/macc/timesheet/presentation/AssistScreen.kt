package com.macc.timesheet.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.macc.timesheet.R
import com.macc.timesheet.presentation.model.EmployeeModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AssistScreen(
    navController: NavController,
    viewModel: EmployeeListViewModel = hiltViewModel(navController.getBackStackEntry("welcome"))
) {
    val employees = viewModel.employees.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            singleLine = true,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Buscar Empleado") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            viewModel.updateSearchQuery("")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = Color.Gray
                        )
                    }
                }
            }
        )

        when {
            !isSearching && employees.loadState.refresh is LoadState.Loading && employees.itemCount == 0 -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = Color.Black
                    )
                }
            }

            !isSearching && employees.loadState.refresh is LoadState.NotLoading && employees.itemCount == 0 -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay empleados disponibles")
                }
            }

            isSearching && searchResults?.isEmpty() == true -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No se encontraron empleados")
                }
            }

            employees.loadState.hasError || (isSearching && searchResults == null) -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Ha ocurrido un error")
                        Button(
                            onClick = {
                                if (isSearching) {
                                    viewModel.updateSearchQuery(searchQuery)
                                } else {
                                    employees.retry()
                                }
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            else -> {
                if (isSearching && searchResults != null) {
                    searchResults?.let { results ->
                        EmployeesList(
                            employees = results,
                            onEmployeeSelected = { employee ->
                                coroutineScope.launch {
                                    viewModel.selectEmployee(employee)
                                    delay(100) // Ensure state propagates
                                    navController.popBackStack()
                                }
                            }
                        )
                    }
                } else {
                    EmployeesList(
                        employees = employees,
                        onEmployeeSelected = { employee ->
                            coroutineScope.launch {
                                viewModel.selectEmployee(employee)
                                delay(100) // Ensure state propagates
                                navController.popBackStack()
                            }
                        }
                    )
                }

                if (!isSearching && employees.loadState.append is LoadState.Loading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeesList(
    employees: List<EmployeeModel>,
    onEmployeeSelected: (EmployeeModel) -> Unit
) {
    LazyColumn {
        itemsIndexed(employees) { _, employee ->
            ItemList(
                employeeModel = employee,
                onClick = { onEmployeeSelected(employee) }
            )
        }
    }
}

@Composable
fun EmployeesList(
    employees: LazyPagingItems<EmployeeModel>,
    onEmployeeSelected: (EmployeeModel) -> Unit
) {
    LazyColumn {
        items(employees.itemCount) {
            employees[it]?.let { employeeModel ->
                ItemList(
                    employeeModel = employeeModel,
                    onClick = { onEmployeeSelected(employeeModel) }
                )
            }
        }
    }
}

@Composable
fun ItemList(employeeModel: EmployeeModel, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val fontBold = FontFamily(Font(R.font.roboto_semibold))
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

    val gradientColors = listOf(
        Color(0xFFECEFF1), // Light gray (neutral, modern)
        Color(0xFFD1D9E1)  // Slightly darker gray-blue
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(72.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(1000f, 1000f) // Diagonal gradient
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = employeeModel.nombreCompleto,
                color = Color(0xFF212121), // Dark gray for contrast
                fontSize = 18.sp,
                fontFamily = fontBold,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp).align(Alignment.Center)
            )
        }
    }
}
