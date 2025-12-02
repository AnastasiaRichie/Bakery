package com.bakery_tm.bakery.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bakery_tm.bakery.common.SessionManager
import com.bakery_tm.bakery.data.FoodRepositoryImpl
import com.bakery_tm.bakery.data.OrderRepositoryImpl
import com.bakery_tm.bakery.data.OrderStore
import com.bakery_tm.bakery.data.UserRepositoryImpl
import com.bakery_tm.bakery.data.database.BakeryDatabase
import com.bakery_tm.bakery.data.database.entity.ProductEntity
import com.bakery_tm.bakery.domain.FoodRepository
import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.FoodType
import com.bakery_tm.bakery.view_model.FoodViewModel
import com.bakery_tm.bakery.view_model.ProfileViewModel
import com.bakery_tm.bakery.view_model.RegistrationViewModel
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
    single { SessionManager(get()) }
    single { OrderStore }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get()) }
    single<FoodRepository> { FoodRepositoryImpl(get()) }

    viewModel { RegistrationViewModel(get()) }
    viewModel { FoodViewModel(get(), get()) }
    viewModel { ProfileViewModel() }
    viewModel { UserViewModel(get()) }
}

val predefinedProducts = listOf(

    // --- ГОРЯЧИЙ КОФЕ ---
    ProductEntity(
        name = "Эспрессо",
        weight = "60 мл",
        description = "Горячий напиток",
        fullDescription = "Состав: молотый кофе, вода. Аллергены: отсутствуют.",
        price = "2.50 BYN",
        foodImageName = "coffee_espresso",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Американо",
        weight = "250 мл",
        description = "Горячий напиток",
        fullDescription = "Состав: молотый кофе, вода. Аллергены: отсутствуют.",
        price = "2.80 BYN",
        foodImageName = "coffee_americano",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Капучино",
        weight = "300 мл",
        description = "Горячий напиток",
        fullDescription = "Состав: эспрессо, молоко, молочная пена. Аллергены: молочные продукты.",
        price = "3.20 BYN",
        foodImageName = "coffee_cappuccino",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Латте",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "Состав: эспрессо, молоко. Аллергены: молочные продукты.",
        price = "3.50 BYN",
        foodImageName = "coffee_latte",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Мокко",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "Состав: эспрессо, молоко, шоколадный сироп. Аллергены: молочные продукты.",
        price = "3.80 BYN",
        foodImageName = "coffee_mocha",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Флэт Уайт",
        weight = "250 мл",
        description = "Горячий напиток",
        fullDescription = "Состав: двойной эспрессо, молоко. Аллергены: молочные продукты.",
        price = "3.60 BYN",
        foodImageName = "coffee_flatwhite",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Раф кофе",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "Состав: эспрессо, сливки, ванильный сахар. Аллергены: молочные продукты.",
        price = "3.90 BYN",
        foodImageName = "coffee_raf",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Айс Латте",
        weight = "400 мл",
        description = "Холодный кофе",
        fullDescription = "Состав: эспрессо, молоко, лёд. Аллергены: молочные продукты.",
        price = "3.70 BYN",
        foodImageName = "coffee_ice_latte",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Карамельный макиато",
        weight = "350 мл",
        description = "Горячий напиток",
        fullDescription = "Состав: эспрессо, молоко, карамельный сироп. Аллергены: молочные продукты.",
        price = "4.00 BYN",
        foodImageName = "coffee_caramel_macchiato",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Кофе по-ирландски",
        weight = "250 мл",
        description = "Горячий напиток",
        fullDescription = "Состав: кофе, сливки, сахар, ирландский ликёр. Аллергены: молочные продукты. Алкоголь.",
        price = "4.50 BYN",
        foodImageName = "coffee_irish",
        foodType = FoodType.DRINK,
    ),

    // --- ХОЛОДНЫЕ НАПИТКИ ---
    ProductEntity(
        name = "Холодный чай с лимоном",
        weight = "400 мл",
        description = "Холодный напиток",
        fullDescription = "Состав: чай, лимон, сахар, лёд. Аллергены: отсутствуют.",
        price = "2.50 BYN",
        foodImageName = "cold_tea_lemon",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Домашний лимонад",
        weight = "450 мл",
        description = "Холодный напиток",
        fullDescription = "Состав: вода, лимон, сахар, мята. Аллергены: отсутствуют.",
        price = "2.80 BYN",
        foodImageName = "cold_lemonade",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Матча латте (холодный)",
        weight = "350 мл",
        description = "Холодный напиток",
        fullDescription = "Состав: матча, молоко, лёд. Аллергены: молочные продукты.",
        price = "4.00 BYN",
        foodImageName = "cold_matcha_latte",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Молочный коктейль ванильный",
        weight = "350 мл",
        description = "Молочный напиток",
        fullDescription = "Состав: молоко, мороженое, ваниль. Аллергены: молочные продукты.",
        price = "3.50 BYN",
        foodImageName = "milkshake_vanilla",
        foodType = FoodType.DRINK,
    ),
    ProductEntity(
        name = "Молочный коктейль шоколадный",
        weight = "350 мл",
        description = "Молочный напиток",
        fullDescription = "Состав: молоко, мороженое, шоколадный сироп. Аллергены: молочные продукты.",
        price = "3.50 BYN",
        foodImageName = "milkshake_chocolate",
        foodType = FoodType.DRINK,
    ),

    // --- ДЕСЕРТЫ ---
    ProductEntity(
        name = "Чизкейк Нью-Йорк",
        weight = "140 г",
        description = "Десерт",
        fullDescription = "Состав: сливочный сыр, печенье, яйца, сахар. Аллергены: глютен, молочные продукты, яйца.",
        price = "4.20 BYN",
        foodImageName = "dessert_cheesecake_ny",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Тирамису",
        weight = "140 г",
        description = "Десерт",
        fullDescription = "Состав: маскарпоне, яйца, сахар, кофе, печенье савоярди. Аллергены: глютен, молочные продукты, яйца.",
        price = "4.50 BYN",
        foodImageName = "dessert_tiramisu",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Маффин с черникой",
        weight = "90 г",
        description = "Выпечка",
        fullDescription = "Состав: мука, яйца, сахар, масло, черника. Аллергены: глютен, молочные продукты, яйца.",
        price = "2.20 BYN",
        foodImageName = "dessert_muffin_blueberry",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Круассан с миндалём",
        weight = "90 г",
        description = "Выпечка",
        fullDescription = "Состав: слоёное тесто, миндальная начинка. Аллергены: глютен, орехи, молочные продукты.",
        price = "2.80 BYN",
        foodImageName = "croissant_almond",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Шоколадный брауни",
        weight = "110 г",
        description = "Десерт",
        fullDescription = "Состав: шоколад, мука, сахар, масло, яйца. Аллергены: глютен, молочные продукты, яйца.",
        price = "3.00 BYN",
        foodImageName = "dessert_brownie",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Пирог с яблоками и корицей",
        weight = "130 г",
        description = "Выпечка",
        fullDescription = "Состав: мука, яблоки, корица, сахар. Аллергены: глютен.",
        price = "3.20 BYN",
        foodImageName = "pie_apple_cinnamon",
        foodType = FoodType.FLOUR,
    ),

    // --- ЗАВТРАКИ / СЭНДВИЧИ ---
    ProductEntity(
        name = "Сэндвич с ветчиной и сыром",
        weight = "180 г",
        description = "Сэндвич",
        fullDescription = "Состав: хлеб, ветчина, сыр, салат. Аллергены: глютен, молочные продукты.",
        price = "3.80 BYN",
        foodImageName = "sandwich_ham_cheese",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Бейгл с лососем и сливочным сыром",
        weight = "200 г",
        description = "Бейгл",
        fullDescription = "Состав: бейгл, лосось, сливочный сыр, салат. Аллергены: глютен, рыба, молочные продукты.",
        price = "4.50 BYN",
        foodImageName = "bagel_salmon",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Тост с авокадо",
        weight = "160 г",
        description = "Завтрак",
        fullDescription = "Состав: хлеб, авокадо, специи. Аллергены: глютен.",
        price = "3.90 BYN",
        foodImageName = "toast_avocado",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Овсяная каша с ягодами",
        weight = "250 г",
        description = "Завтрак",
        fullDescription = "Состав: овсянка, молоко, ягоды. Аллергены: глютен, молочные продукты.",
        price = "3.00 BYN",
        foodImageName = "oatmeal_berries",
        foodType = FoodType.FLOUR,
    ),
    ProductEntity(
        name = "Йогурт с гранолой",
        weight = "200 г",
        description = "Завтрак",
        fullDescription = "Состав: йогурт, гранола, ягоды. Аллергены: молочные продукты, орехи (в граноле).",
        price = "2.80 BYN",
        foodImageName = "yogurt_granola",
        foodType = FoodType.FLOUR,
    ),
)