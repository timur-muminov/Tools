package com.tools

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


class Pagination<ITEM, ARGUMENTS>(
    private val initialPage: Int = 0,
    private val initialData: List<ITEM> = emptyList(),
    private val loadData: suspend (ARGUMENTS, page: Int) -> List<ITEM>,
) {
    private val dataMutableState = MutableStateFlow(initialData)
    fun dataState(): Flow<List<ITEM>> = dataMutableState

    private val mutex: Mutex = Mutex()

    var page = initialPage
        private set

    fun reset() {
        page = initialPage
        dataMutableState.value = initialData
    }

    suspend fun next(arguments: ARGUMENTS) {
        mutex.withLock {
            val list = loadData(arguments, page)
            if (list.isEmpty()) return@withLock
            dataMutableState.value += list
            page++
        }
    }
}
