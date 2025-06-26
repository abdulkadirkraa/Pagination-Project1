package com.abdulkadirkara.paginationsimple.data.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.abdulkadirkara.paginationsimple.data.model.Result
import com.abdulkadirkara.paginationsimple.data.service.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(private val apiService: ApiService) {

     fun getUsers() : Flow<PagingData<Result>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                RandomUserPagingSource(apiService)
            }
        ).flow
    }
}