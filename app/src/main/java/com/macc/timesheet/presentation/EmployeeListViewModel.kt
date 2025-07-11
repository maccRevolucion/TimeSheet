package com.macc.timesheet.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.macc.timesheet.data.EmployeeRepository
import com.macc.timesheet.data.response.ApiResponse
import com.macc.timesheet.presentation.model.EmployeeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.google.gson.Gson
import com.macc.timesheet.data.keys.AppDataStore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class EmployeeListViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val appDataStore: AppDataStore
) : ViewModel() {

    val employees: Flow<PagingData<EmployeeModel>> = employeeRepository.getAllEmployees()
        .cachedIn(viewModelScope)

    private val _selectedEmployee = mutableStateOf<EmployeeModel?>(null)
    val selectedEmployee: State<EmployeeModel?> = _selectedEmployee

    private val _attendanceResult = MutableStateFlow<ApiResponse?>(null)
    val attendanceResult: StateFlow<ApiResponse?> = _attendanceResult

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<EmployeeModel>?>(null)
    val searchResults: StateFlow<List<EmployeeModel>?> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _lastCheckedEmployee = mutableStateOf<Pair<EmployeeModel?, String>?>(null)
    val lastCheckedEmployee: State<Pair<EmployeeModel?, String>?> = _lastCheckedEmployee

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            try {
                appDataStore.selectedEmployee.collectLatest { storedEmployee: EmployeeModel? ->
                    _selectedEmployee.value = storedEmployee
                }
            } catch (e: Exception) {
                println("Error restoring selected employee: ${e.message}")
            }
            try {
                appDataStore.lastCheckedEmployee.collectLatest { storedEmployee: Pair<EmployeeModel?, String>? ->
                    _lastCheckedEmployee.value = storedEmployee
                }
            } catch (e: Exception) {
                println("Error restoring last checked employee: ${e.message}")
            }
        }
    }

    fun selectEmployee(employee: EmployeeModel?) {
        _selectedEmployee.value = employee
        viewModelScope.launch {
            appDataStore.saveSelectedEmployee(employee)
        }
    }

    fun postAttendance(employeeId: Int, onResult: (ApiResponse) -> Unit) {
        viewModelScope.launch {
            try {
                val response = employeeRepository.api.postAsistencia(employeeId)
                _attendanceResult.value = response
                _lastCheckedEmployee.value = _selectedEmployee.value to getCurrentTime()
                appDataStore.saveLastCheckedEmployee(_selectedEmployee.value, getCurrentTime())
                onResult(response)
            } catch (e: HttpException) {
                val errorResponse = try {
                    val errorBody = e.response()?.errorBody()?.string()
                    val apiResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
                    apiResponse.copy(success = false)
                } catch (jsonException: Exception) {
                    ApiResponse(
                        success = false,
                        message = e.message ?: "Error posting attendance",
                        data = null
                    )
                }
                _attendanceResult.value = errorResponse
                _lastCheckedEmployee.value = _selectedEmployee.value to getCurrentTime()
                appDataStore.saveLastCheckedEmployee(_selectedEmployee.value, getCurrentTime())
                onResult(errorResponse)
            } catch (e: Exception) {
                val errorResponse = ApiResponse(
                    success = false,
                    message = e.message ?: "Error posting attendance",
                    data = null
                )
                _attendanceResult.value = errorResponse
                _lastCheckedEmployee.value = _selectedEmployee.value to getCurrentTime()
                appDataStore.saveLastCheckedEmployee(_selectedEmployee.value, getCurrentTime())
                onResult(errorResponse)
            }
        }
    }

    fun clearAttendanceResult() {
        _attendanceResult.value = null
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _isSearching.value = false
            _searchResults.value = null
            searchJob?.cancel()
        } else {
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(300)
            try {
                val response = employeeRepository.searchEmployeesByName(
                    name = query,
                    cedisId = 1
                )
                _searchResults.value = response.data.map { it.toPresentation() }
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.value = null
            } finally {
                _isSearching.value = query.isNotBlank()
            }
        }
    }

    private fun getCurrentTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"))
    }
}