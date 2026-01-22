package com.bakery_tm.bakery

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bakery_tm.bakery.domain.AuthState
import com.bakery_tm.bakery.screen.EditScreen
import com.bakery_tm.bakery.screen.FoodDetailsScreen
import com.bakery_tm.bakery.screen.FoodScreen
import com.bakery_tm.bakery.screen.ForgotPasswordScreen
import com.bakery_tm.bakery.screen.HistoryDetailsScreen
import com.bakery_tm.bakery.screen.HistoryScreen
import com.bakery_tm.bakery.screen.LoginScreen
import com.bakery_tm.bakery.screen.ProfileScreen
import com.bakery_tm.bakery.screen.RegistrationScreen
import com.bakery_tm.bakery.screen.ShoppingCartScreen
import com.bakery_tm.bakery.screen.SplashScreen
import com.bakery_tm.bakery.view_model.FoodViewModel
import com.bakery_tm.bakery.view_model.OrderViewModel
import com.bakery_tm.bakery.view_model.RegistrationViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel
import com.bakery_tm.bakery.view_model.UserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    userViewModel: UserViewModel = koinViewModel(),
    foodViewModel: FoodViewModel = koinViewModel(),
    shoppingCartViewModel: ShoppingCartViewModel = koinViewModel(),
    orderViewModel: OrderViewModel = koinViewModel(),
    registrationViewModel: RegistrationViewModel = koinViewModel(),
) {
    val navController = rememberNavController()
    val authState by userViewModel.authState.collectAsState(null)
    val context = LocalContext.current
    if (authState == AuthState.Loading) {
        SplashScreen()
    } else {
        NavHost(
            navController = navController,
            startDestination = if (authState == AuthState.Authenticated) FOOD else LOGIN
        ) {
            composable(LOGIN) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = registrationViewModel,
                        orderViewModel = orderViewModel,
                        shoppingCartViewModel = shoppingCartViewModel,
                        onFoodNavigation = {
                            navController.navigate(FOOD) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onSignUpClick = { navController.navigate(REGISTRATION) },
                        onForgotClicked = { navController.navigate(FORGOT_PASS) },
                    )
                }
            }
            composable(REGISTRATION) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegistrationScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = registrationViewModel,
                        onBack = { navController.popBackStack() },
                        onLoginClick = { navController.navigate(LOGIN) },
                    ) {
                        navController.navigate(FOOD) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
            composable(FORGOT_PASS) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ForgotPasswordScreen(
                        Modifier.padding(innerPadding)
                    ) {
                        navController.popBackStack()
                    }
                }
            }

            composable(FOOD) {
                val tabsIcon = listOf(
                    R.drawable.ic_food_bank,
                    R.drawable.ic_shopping_cart,
                    R.drawable.ic_clock,
                    R.drawable.profile_icon
                )
                var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.White,
                            contentColor = Color.White,
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp
                                    )
                                )
                        ) {
                            tabsIcon.forEachIndexed { index, title ->
                                Tab(
                                    icon = {
                                        Image(
                                            painter = painterResource(title),
                                            modifier = Modifier
                                                .width(36.dp)
                                                .height(36.dp)
                                                .padding(4.dp),
                                            contentDescription = null,
                                            colorFilter = ColorFilter.tint(
                                                if (selectedTabIndex == index) Color.Black
                                                else Color.Gray
                                            )
                                        )
                                    },
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    selectedContentColor = Color.Black,
                                    unselectedContentColor = Color.White,
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    when (selectedTabIndex) {
                        0 -> FoodScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = foodViewModel,
                            userViewModel = userViewModel,
                            shoppingCartViewModel = shoppingCartViewModel,
                            isLoggedIn = authState == AuthState.Authenticated,
                            onFoodClicked = { navController.navigate(foodDetails(it)) },
                            onCartClicked = { selectedTabIndex = 1 },
                            onRegisterClicked = { navController.navigate(REGISTRATION) }
                        )
                        1 -> ShoppingCartScreen(
                            viewModel = shoppingCartViewModel,
                            orderViewModel = orderViewModel,
                            isLoggedIn = authState == AuthState.Authenticated,
                            modifier = Modifier.padding(innerPadding),
                            onToFoodListNavigate = { selectedTabIndex = 0 }
                        ) { navController.navigate(LOGIN) }
                        2 -> HistoryScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = orderViewModel,
                            isLoggedIn = authState == AuthState.Authenticated,
                            onStartOrderingClicked = { selectedTabIndex = 0 },
                            onLoginClicked = { navController.navigate(LOGIN) },
                        ) { orderId, index ->
                            navController.navigate(historyDetails(orderId, index))
                        }
                        3 -> ProfileScreen(
                            viewModel = userViewModel,
                            orderViewModel = orderViewModel,
                            shoppingCartViewModel = shoppingCartViewModel,
                            modifier = Modifier.padding(innerPadding),
                            onLogOutClicked = {
                                navController.navigate(LOGIN) {
                                    navController.popBackStack()
                                }
                            },
                            onLogInClicked = { navController.navigate(LOGIN) },
                            onRegisterClicked = { navController.navigate(REGISTRATION) },
                            onEditClicked = {
                                navController.navigate(EDIT)
                            }
                        )
                    }
                }
            }
            composable(
                FOOD_DETAILS,
                arguments = listOf(navArgument(PRODUCT_ID) { type = NavType.LongType })
            ) {
                val productId = it.arguments?.getLong(PRODUCT_ID)
                productId?.let {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        FoodDetailsScreen(
                            viewModel = foodViewModel,
                            shoppingCartViewModel = shoppingCartViewModel,
                            isLoggedIn = authState == AuthState.Authenticated,
                            modifier = Modifier.padding(innerPadding),
                            productId = productId
                        ) { navController.popBackStack() }
                    }
                } ?: run {
                    Toast.makeText(context, "Screen not found", Toast.LENGTH_SHORT).show()
                }
            }
            composable(EDIT) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EditScreen(
                        viewModel = userViewModel,
                        modifier = Modifier.padding(innerPadding),
                        onBackClicked = { navController.popBackStack() }
                    )
                }
            }
            composable(
                HISTORY_DETAILS,
                arguments = listOf(
                    navArgument(ORDER_ID) { type = NavType.LongType },
                    navArgument(ORDER_INDEX) { type = NavType.IntType },
                )
            ) {
                val orderId = it.arguments?.getLong(ORDER_ID)
                val index = it.arguments?.getInt(ORDER_INDEX)
                orderId?.let {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        HistoryDetailsScreen(
                            orderViewModel,
                            modifier = Modifier.padding(innerPadding),
                            orderId = it,
                            index = index ?: 0,
                            onBackClicked = { navController.popBackStack() }
                        )
                    }
                } ?: run {
                    Toast.makeText(context, "Screen not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

const val REGISTRATION = "registration"
const val LOGIN = "login"
const val FORGOT_PASS = "forgot"
const val FOOD = "food"
const val PRODUCT_ID = "productId"
const val ORDER_ID = "orderId"
const val ORDER_INDEX = "orderIndex"
const val FOOD_DETAILS = "food/{productId}"
const val EDIT = "edit"
const val HISTORY_DETAILS = "history/{orderId}/{orderIndex}"

fun foodDetails(productId: Long) = "food/$productId"
fun editType(type: String) = "edit/$type"
fun historyDetails(orderId: Long, index: Int) = "history/$orderId/$index"
