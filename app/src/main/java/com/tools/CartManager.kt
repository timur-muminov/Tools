package com.tools

import kotlinx.coroutines.flow.*

open class CartManager<PRODUCT>(
    val initialAmount: Int = 0,
    // Product item, amount
    var initialCartStorage: Map<PRODUCT, Int> = mutableMapOf()
) {
    private val cartStorageMutableStateFlow = MutableStateFlow(initialCartStorage)
    val cartFlow = cartStorageMutableStateFlow.asStateFlow()

    fun getCurrentAmount(product: PRODUCT) = cartFlow.value.getOrElse(product) { initialAmount }

    open fun setAmount(product: PRODUCT, amount: Int) {
        cartStorageMutableStateFlow.update { map ->
            map.toMutableMap().also {
                if (amount < 0) it.remove(product) else it[product] = amount
            }
        }
    }

    open fun clearCart() {
        cartStorageMutableStateFlow.value = initialCartStorage
    }
}

fun <PRODUCT> CartManager<PRODUCT>.amountFlow(product: PRODUCT): Flow<Int> =
    cartFlow.map { it.getOrElse(product) { initialAmount } }

fun <PRODUCT> CartManager<PRODUCT>.plusOne(product: PRODUCT) {
    setAmount(product, getCurrentAmount(product) + 1)
}

fun <PRODUCT> CartManager<PRODUCT>.minusOne(product: PRODUCT) {
    setAmount(product, getCurrentAmount(product) - 1)
}

fun <PRODUCT> CartManager<PRODUCT>.remove(product: PRODUCT) {
    setAmount(product, -1)
}

