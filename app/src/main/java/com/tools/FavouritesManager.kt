package com.tools

import kotlinx.coroutines.flow.*

open class FavouritesManager<PRODUCT>(
    private val initialFavouriteStorage: List<PRODUCT> = mutableListOf()
) {
    private val favouritesStorageMutableStateFlow = MutableStateFlow(initialFavouriteStorage)
    val favouritesStorageFlow = favouritesStorageMutableStateFlow.asStateFlow()

    fun toggleFavourite(product: PRODUCT) =
        if (favouritesStorageFlow.value.contains(product)) updateStorage { it.remove(product) } else updateStorage { it.add(product) }

    private fun updateStorage(action: (MutableList<PRODUCT>) -> Unit) {
        favouritesStorageMutableStateFlow.update { list ->
            list.toMutableList().also { action(it) }
        }
    }

    fun isFavourite(product: PRODUCT): Boolean = favouritesStorageMutableStateFlow.value.contains(product)
    fun clearFavourites() {
        favouritesStorageMutableStateFlow.value = initialFavouriteStorage
    }
}

fun <PRODUCT> FavouritesManager<PRODUCT>.favouritesFlow(product: PRODUCT): Flow<Boolean> =
    favouritesStorageFlow.map { it.contains(product) }