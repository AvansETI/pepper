package com.pepper.care.dialog.repo

import com.pepper.care.common.AppResult

interface AvailableScreenRepository {
    suspend fun fetchScreens() : AppResult<IntArray>
}

class AvailableScreenRepositoryImpl : AvailableScreenRepository {

    override suspend fun fetchScreens(): AppResult<IntArray> {
        return AppResult.Success(intArrayOf(0, 0, 0))
    }
}