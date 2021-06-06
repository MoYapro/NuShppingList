package de.moyapro.nushppinglist

data class CartItem(
    val cartItemProperties: CartItemProperties,
    val item: Item
) {
    constructor(newItemName: String) : this(CartItemProperties(0, false), Item(newItemName))
}
