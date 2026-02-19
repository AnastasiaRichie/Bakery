package com.bakery_tm.bakery.common

interface UpdateOrderListener {

    fun requireOrderUpdate(orderId: Long)
}
