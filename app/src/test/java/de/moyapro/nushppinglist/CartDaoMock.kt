package de.moyapro.nushppinglist

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn

class CartDaoMock(
    private val externalScope: CoroutineScope
) : CartDao {

    private val itemTable: MutableList<Item> = mutableListOf()
    private val cartItemPropertiesTable: MutableList<CartItemProperties> = mutableListOf()

    val cartItemFlow: Flow<List<CartItem>> = flow<List<CartItem>> {
        emptyList<CartItem>()
    }.shareIn(
        externalScope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )


    override fun save(vararg cartItemProperties: CartItemProperties) {
        TODO("Not yet implemented")
    }

    override fun findAll(): Flow<List<CartItem>> {
        TODO("Not yet implemented")
    }

}
