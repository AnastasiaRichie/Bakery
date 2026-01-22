package com.bakery_tm.bakery.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bakery_tm.bakery.common.SessionManager
import com.bakery_tm.bakery.data.FoodRepositoryImpl
import com.bakery_tm.bakery.data.OrderRepositoryImpl
import com.bakery_tm.bakery.data.ShoppingCartRepositoryImpl
import com.bakery_tm.bakery.data.UserRepositoryImpl
import com.bakery_tm.bakery.data.database.BakeryDatabase
import com.bakery_tm.bakery.data.database.entity.ProductEntity
import com.bakery_tm.bakery.domain.AvatarPreferences
import com.bakery_tm.bakery.domain.FoodRepository
import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.domain.ShoppingCartRepository
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.FoodType
import com.bakery_tm.bakery.view_model.FoodViewModel
import com.bakery_tm.bakery.view_model.ForgotPasswordViewModel
import com.bakery_tm.bakery.view_model.OrderViewModel
import com.bakery_tm.bakery.view_model.RegistrationViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel
import com.bakery_tm.bakery.view_model.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            BakeryDatabase::class.java,
            "bakery_database"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = get<BakeryDatabase>().productDao()
                    dao.insertAllProducts(predefinedProducts)
                }
            }
        }).build()
    }
    single { get<BakeryDatabase>().userDao() }
    single { get<BakeryDatabase>().orderDao() }
    single { get<BakeryDatabase>().productDao() }
    single { get<BakeryDatabase>().cartDao() }
    single { SessionManager(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<FoodRepository> { FoodRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get(), get()) }
    single<ShoppingCartRepository> { ShoppingCartRepositoryImpl(get(), get()) }
    single { AvatarPreferences(androidContext()) }

    viewModel { RegistrationViewModel(get()) }
    viewModel { FoodViewModel(get()) }
    viewModel { UserViewModel(get(), get()) }
    viewModel { OrderViewModel(get(), get()) }
    viewModel { ShoppingCartViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
}

val predefinedProducts = listOf(

    // --- ГОРЯЧИЙ КОФЕ ---
    ProductEntity(
        name = "Эспрессо",
        weight = "60 мл",
        description = "Горячий напиток",
        fullDescription = "молотый кофе, вода.",
        allergens = emptyList(),
        price = "2.50 BYN",
        foodImageName = "coffee_espresso",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Американо",
        weight = "250 мл",
        description = "Горячий напиток",
        fullDescription = "молотый кофе, вода.",
        allergens = emptyList(),
        price = "2.80 BYN",
        foodImageName = "coffee_americano",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Капучино",
        weight = "300 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, молоко, молочная пена.",
        allergens = listOf("молочные продукты"),
        price = "3.20 BYN",
        foodImageName = "coffee_cappuccino",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Латте",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, молоко.",
        allergens = listOf("молочные продукты"),
        price = "3.50 BYN",
        foodImageName = "coffee_latte",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Мокко",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, молоко, шоколадный сироп.",
        allergens = listOf("молочные продукты"),
        price = "3.80 BYN",
        foodImageName = "coffee_mocha",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Флэт Уайт",
        weight = "250 мл",
        description = "Горячий напиток",
        fullDescription = "двойной эспрессо, молоко.",
        allergens = listOf("молочные продукты"),
        price = "3.60 BYN",
        foodImageName = "coffee_flatwhite",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Раф кофе",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, сливки, ванильный сахар.",
        allergens = listOf("молочные продукты"),
        price = "3.90 BYN",
        foodImageName = "coffee_raf",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Айс Латте",
        weight = "400 мл",
        description = "Холодный кофе",
        fullDescription = "эспрессо, молоко, лёд.",
        allergens = listOf("молочные продукты"),
        price = "3.70 BYN",
        foodImageName = "coffee_ice_latte",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Карамельный макиато",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "эспрессо, молоко, карамельный сироп.",
        allergens = listOf("молочные продукты"),
        price = "4.00 BYN",
        foodImageName = "coffee_caramel_macchiato",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Кофе по-ирландски",
        weight = "250 мл",
        description = "Горячий напиток",
        fullDescription = "кофе, сливки, сахар, ирландский ликёр.",
        allergens = listOf("молочные продукты", "Алкоголь"),
        price = "4.50 BYN",
        foodImageName = "coffee_irish",
        foodType = FoodType.DRINK,
    ),

    // --- ХОЛОДНЫЕ НАПИТКИ ---
    ProductEntity(
        name = "Холодный чай с лимоном",
        weight = "400 мл",
        description = "Холодный напиток",
        fullDescription = "чай, лимон, сахар, лёд.",
        allergens = listOf("лимон"),
        price = "2.50 BYN",
        foodImageName = "cold_tea_lemon",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Домашний лимонад",
        weight = "450 мл",
        description = "Холодный напиток",
        fullDescription = "вода, лимон, сахар, мята.",
        allergens = listOf("лимон", "мята"),
        price = "2.80 BYN",
        foodImageName = "cold_lemonade",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Матча латте (холодный)",
        weight = "350 мл",
        description = "Холодный напиток",
        fullDescription = "матча, молоко, лёд.",
        allergens = listOf("молочные продукты"),
        price = "4.00 BYN",
        foodImageName = "cold_matcha_latte",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Молочный коктейль ванильный",
        weight = "350 мл",
        description = "Молочный напиток",
        fullDescription = "молоко, мороженое, ваниль.",
        allergens = listOf("молочные продукты"),
        price = "3.50 BYN",
        foodImageName = "milkshake_vanilla",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Молочный коктейль шоколадный",
        weight = "350 мл",
        description = "Молочный напиток",
        fullDescription = "молоко, мороженое, шоколадный сироп.",
        allergens = listOf("молочные продукты"),
        price = "3.50 BYN",
        foodImageName = "milkshake_chocolate",
        foodType = FoodType.DRINK,
    ),

    // --- ДЕСЕРТЫ ---
    ProductEntity(
        name = "Чизкейк Нью-Йорк",
        weight = "140 г",
        description = "Десерт",
        fullDescription = "сливочный сыр, печенье, яйца, сахар.",
        allergens = listOf("молочные продукты", "глютен", "яйца"),
        price = "4.20 BYN",
        foodImageName = "dessert_cheesecake_ny",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Тирамису",
        weight = "140 г",
        description = "Десерт",
        fullDescription = "маскарпоне, яйца, сахар, кофе, печенье савоярди.",
        allergens = listOf("молочные продукты", "глютен", "яйца"),
        price = "4.50 BYN",
        foodImageName = "dessert_tiramisu",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Маффин с черникой",
        weight = "90 г",
        description = "Выпечка",
        fullDescription = "мука, яйца, сахар, масло, черника.",
        allergens = listOf("молочные продукты", "глютен", "яйца"),
        price = "2.20 BYN",
        foodImageName = "dessert_muffin_blueberry",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Круассан с миндалём",
        weight = "90 г",
        description = "Выпечка",
        fullDescription = "слоёное тесто, миндальная начинка.",
        allergens = listOf("молочные продукты", "глютен", "орехи"),
        price = "2.80 BYN",
        foodImageName = "croissant_almond",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Шоколадный брауни",
        weight = "110 г",
        description = "Десерт",
        fullDescription = "шоколад, мука, сахар, масло, яйца.",
        allergens = listOf("молочные продукты", "глютен", "орехи"),
        price = "3.00 BYN",
        foodImageName = "dessert_brownie",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Пирог с яблоками и корицей",
        weight = "130 г",
        description = "Выпечка",
        fullDescription = "мука, яблоки, корица, сахар.",
        allergens = listOf("глютен", "яблоки"),
        price = "3.20 BYN",
        foodImageName = "pie_apple_cinnamon",
        foodType = FoodType.FLOUR,
    ),

    // --- ЗАВТРАКИ / СЭНДВИЧИ ---
    ProductEntity(
        name = "Сэндвич с ветчиной и сыром",
        weight = "180 г",
        description = "Сэндвич",
        fullDescription = "хлеб, ветчина, сыр, салат.",
        allergens = listOf("молочные продукты", "глютен"),
        price = "3.80 BYN",
        foodImageName = "sandwich_ham_cheese",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Бейгл с лососем и сливочным сыром",
        weight = "200 г",
        description = "Бейгл",
        fullDescription = "бейгл, лосось, сливочный сыр, салат.",
        allergens = listOf("молочные продукты", "глютен", "рыба"),
        price = "4.50 BYN",
        foodImageName = "bagel_salmon",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Тост с авокадо",
        weight = "160 г",
        description = "Завтрак",
        fullDescription = "хлеб, авокадо, специи.",
        allergens = listOf("глютен"),
        price = "3.90 BYN",
        foodImageName = "toast_avocado",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Овсяная каша с ягодами",
        weight = "250 г",
        description = "Завтрак",
        fullDescription = "овсянка, молоко, ягоды.",
        allergens = listOf("молочные продукты", "глютен"),
        price = "3.00 BYN",
        foodImageName = "oatmeal_berries",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Йогурт с гранолой",
        weight = "200 г",
        description = "Завтрак",
        fullDescription = "йогурт, гранола, ягоды.",
        allergens = listOf("молочные продукты", "орехи (в граноле)"),
        price = "2.80 BYN",
        foodImageName = "yogurt_granola",
        foodType = FoodType.FLOUR,
    ),
)