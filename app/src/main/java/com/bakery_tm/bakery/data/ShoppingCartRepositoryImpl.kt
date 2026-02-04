package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.data.database.CartDao
import com.bakery_tm.bakery.data.database.ProductDao
import com.bakery_tm.bakery.data.database.entity.CartItemEntity
import com.bakery_tm.bakery.data.database.relations.CartItemWithProduct
import com.bakery_tm.bakery.domain.ShoppingCartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class ShoppingCartRepositoryImpl(
    private val cartDao: CartDao,
    private val productDao: ProductDao,
): ShoppingCartRepository {

    override suspend fun addProduct(userId: Int, productId: Long) {
        withContext(Dispatchers.IO) {
            val item = cartDao.getCart(productId).firstOrNull()
            if (item != null) {
                cartDao.updateQuantity(item.cartItemId, item.quantity + 1)
            } else {
                cartDao.addToCart(
                    CartItemEntity(userId = userId, productId = productId, quantity = 1)
                )
            }
        }
    }

    override suspend fun minusProduct(productId: Long) {
        withContext(Dispatchers.IO) {
            val item = cartDao.getCart(productId).firstOrNull()
            when {
                item != null && item.quantity == 1 -> {
                    cartDao.deleteCartItem(item.cartItemId)
                }
                item != null -> cartDao.updateQuantity(item.cartItemId, item.quantity - 1)
            }
        }
    }

    override suspend fun addProductToCart(userId: Int, productId: Long) {
        cartDao.addToCart(CartItemEntity(userId = userId, productId = productId, quantity = 1))
    }

    override suspend fun removeProductFromCart(cartItemId: Long) {
        cartDao.deleteCartItem(cartItemId)
    }

    override fun getCartFull(userId: Int): Flow<List<CartItemWithProduct>> {
        return cartDao.getCarts(userId).mapNotNull { carts ->
            carts.mapNotNull { item ->
                val product = productDao.getAllProducts().find { it.productId == item.productId }
                product?.let {
                    CartItemWithProduct(
                        item = item,
                        product = product
                    )
                }
            }
        }
    }

    override fun calculateCartTotal(userId: Int): Flow<Double> {
        val items = getCartFull(userId)
        return items.map { it.sumOf { it.product.price.replace(" BYN", "").toDouble() * it.item.quantity } }
    }

    override fun getCart(productId: Long): Flow<CartItemEntity?> = cartDao.getCart(productId)
}