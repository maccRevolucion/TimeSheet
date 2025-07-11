package com.macc.timesheet.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.macc.timesheet.data.response.ApiResponseEmployee
import com.macc.timesheet.presentation.model.EmployeeModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class EmployeeRepository @Inject constructor(val api: ApiService) {

    companion object{
        const val MAX_ITEMS = 10
        const val PREFETCH_ITEM = 3
    }

    fun getAllEmployees(): Flow<PagingData<EmployeeModel>> {
        return Pager(config = PagingConfig(pageSize = MAX_ITEMS, prefetchDistance = PREFETCH_ITEM),
            pagingSourceFactory = {
                EmployeePagingSource(api)
            }).flow
    }

    suspend fun searchEmployeesByName(name: String, cedisId: Int): ApiResponseEmployee {
        return api.searchEmployeeByName(name, cedisId)
    }

}