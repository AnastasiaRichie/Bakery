package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.data.database.CartDao
import com.bakery_tm.bakery.data.database.OrderDao
import com.bakery_tm.bakery.data.database.ProductDao
import com.bakery_tm.bakery.data.database.entity.CartItemEntity
import com.bakery_tm.bakery.data.database.entity.OrderEntity
import com.bakery_tm.bakery.data.database.entity.OrderItemEntity
import com.bakery_tm.bakery.data.database.relations.CartItemWithProduct
import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import com.bakery_tm.bakery.domain.ShoppingCartRepository
import com.bakery_tm.bakery.models.Address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class ShoppingCartRepositoryImpl(
    private val orderDao: OrderDao,
    private val cartDao: CartDao,
    private val productDao: ProductDao,
): ShoppingCartRepository {

    // Cart
    override suspend fun addProduct(userId: Int, productId: Long) {
        withContext(Dispatchers.IO) {
            val item = cartDao.getCart(productId).firstOrNull()
            if (item != null) {
                cartDao.updateQuantity(item.id, item.quantity + 1)
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
                    cartDao.deleteCartItem(item.id)
                }
                item != null -> cartDao.updateQuantity(item.id, item.quantity - 1)
            }
        }
    }

    override suspend fun addProductToCart(userId: Int, productId: Long) {
        cartDao.addToCart(CartItemEntity(userId = userId, productId = productId, quantity = 1))
    }

    override suspend fun removeProductFromCart(id: Long) {
        cartDao.deleteCartItem(id)
    }

    override fun getCartFull(userId: Int): Flow<List<CartItemWithProduct>> {
        return cartDao.getCarts(userId).mapNotNull { carts ->
            carts.mapNotNull { item ->
                val product = productDao.getAllProducts().firstOrNull()?.find { it.productId == item.productId }
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
        return items.map { it.sumOf { it.product.price.toDouble() * it.item.quantity } }
    }

    override fun getCart(productId: Long): Flow<CartItemEntity?> = cartDao.getCart(productId)
    //

    // Order
    override suspend fun createOrder(userId: Int, address: Address) {
        val cartItems = cartDao.getCarts(userId).firstOrNull()
        if (cartItems.isNullOrEmpty()) return
        val orderId = orderDao.insertOrder(
            OrderEntity(
                userOwnerId = userId,
                orderAddress = address,
                date = System.currentTimeMillis()
            )
        )

        cartItems.forEach { item ->
            orderDao.insertOrderItem(
                OrderItemEntity(
                    orderId = orderId,
                    productId = item.productId,
                    quantity = item.quantity
                )
            )
        }

        cartDao.clearCart(userId)
    }

    override fun getAllOrders(userId: Int): Flow<List<OrderWithItems>> {
        return orderDao.getOrdersForUser(userId)
    }

    override suspend fun getOrderDetails(orderId: Long): OrderWithItems {
        return orderDao.getOrderDetails(orderId)
    }
    //
}