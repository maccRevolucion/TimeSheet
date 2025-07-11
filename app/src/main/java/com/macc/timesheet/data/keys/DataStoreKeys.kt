package com.macc.timesheet.data.keys

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKeys {
    val LAST_CHECKED_EMPLOYEE_ID = intPreferencesKey("last_checked_employee_id")
    val LAST_CHECKED_EMPLOYEE_NAME = stringPreferencesKey("last_checked_employee_name")
    val LAST_CHECKED_TIMESTAMP = stringPreferencesKey("last_checked_employee_timestamp")
    val SELECTED_EMPLOYEE_ID = intPreferencesKey("selected_employee_id")
    val SELECTED_EMPLOYEE_NAME = stringPreferencesKey("selected_employee_name")
}