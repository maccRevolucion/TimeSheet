package com.macc.timesheet.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.macc.timesheet.data.response.EmployeeInfo
import com.macc.timesheet.presentation.model.EmployeeModel
import java.io.IOException
import javax.inject.Inject

class EmployeePagingSource @Inject constructor(private val api: ApiService) :
    PagingSource<Int, EmployeeModel>() {

    override fun getRefreshKey(state: PagingState<Int, EmployeeModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EmployeeModel> {
        return try {
            val page = params.key ?: 1
            val response = api.getEmployees(page)
            val employees = response.data

            val prevKey = if (page > 1) page - 1 else null
            val nextKey = page + 1

            LoadResult.Page(
                data = employees.map { it.toPresentation() },
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            println("Pagination error: ${e.message}")
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: Exception) {
            println("Unexpected pagination error: ${e.message}")
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}