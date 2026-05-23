package com.bakery_tm.bakery.models

class SimplePasswordException(override val message: String? = "Пароль должен состоять минимум из 6 символов") : Exception()