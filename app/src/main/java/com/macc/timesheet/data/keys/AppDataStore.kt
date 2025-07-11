package com.macc.timesheet.data.keys

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.macc.timesheet.presentation.model.EmployeeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timesheet_prefs")

@Singleton
class AppDataStore @Inject constructor(
    private val context: Context
) {
    suspend fun saveLastCheckedEmployee(employee: EmployeeModel?, timestamp: String?) {
        context.dataStore.edit { prefs ->
            if (employee != null && timestamp != null) {
                prefs[DataStoreKeys.LAST_CHECKED_EMPLOYEE_ID] = employee.idEmpleado
                prefs[DataStoreKeys.LAST_CHECKED_EMPLOYEE_NAME] = employee.nombreCompleto
                prefs[DataStoreKeys.LAST_CHECKED_TIMESTAMP] = timestamp
                println("Saving last checked employee: ${employee.nombreCompleto} at $timestamp")
            } else {
                prefs.remove(DataStoreKeys.LAST_CHECKED_EMPLOYEE_ID)
                prefs.remove(DataStoreKeys.LAST_CHECKED_EMPLOYEE_NAME)
                prefs.remove(DataStoreKeys.LAST_CHECKED_TIMESTAMP)
                println("Cleared last checked employee")
            }
        }
    }

    val lastCheckedEmployee: Flow<Pair<EmployeeModel?, String>?> = context.dataStore.data
        .map { prefs ->
            val id = prefs[DataStoreKeys.LAST_CHECKED_EMPLOYEE_ID]
            val name = prefs[DataStoreKeys.LAST_CHECKED_EMPLOYEE_NAME]
            val timestamp = prefs[DataStoreKeys.LAST_CHECKED_TIMESTAMP]
            if (id != null && name != null && timestamp != null) {
                Pair(
                    EmployeeModel(
                        idEmpleado = id,
                        nombreCompleto = name
                    ),
                    timestamp
                )
            } else {
                null
            }
        }

    suspend fun saveSelectedEmployee(employee: EmployeeModel?) {
        context.dataStore.edit { prefs ->
            if (employee != null) {
                prefs[DataStoreKeys.SELECTED_EMPLOYEE_ID] = employee.idEmpleado
                prefs[DataStoreKeys.SELECTED_EMPLOYEE_NAME] = employee.nombreCompleto
            } else {
                prefs.remove(DataStoreKeys.SELECTED_EMPLOYEE_ID)
                prefs.remove(DataStoreKeys.SELECTED_EMPLOYEE_NAME)
            }
        }
    }

    val selectedEmployee: Flow<EmployeeModel?> = context.dataStore.data
        .map { prefs ->
            val id = prefs[DataStoreKeys.SELECTED_EMPLOYEE_ID]
            val name = prefs[DataStoreKeys.SELECTED_EMPLOYEE_NAME]
            if (id != null && name != null) {
                EmployeeModel(
                    idEmpleado = id,
                    nombreCompleto = name
                )
            } else {
                null
            }
        }

}