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
import androidx.compose.runtime.remember
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
import com.bakery_tm.bakery.screen.FoodDetailsScreen
import com.bakery_tm.bakery.screen.FoodScreen
import com.bakery_tm.bakery.screen.ForgotPasswordScreen
import com.bakery_tm.bakery.screen.HistoryScreen
import com.bakery_tm.bakery.screen.LoginScreen
import com.bakery_tm.bakery.screen.ProfileScreen
import com.bakery_tm.bakery.screen.RegistrationScreen
import com.bakery_tm.bakery.screen.ShoppingCartScreen
import com.bakery_tm.bakery.screen.SplashScreen
import com.bakery_tm.bakery.view_model.FoodViewModel
import com.bakery_tm.bakery.view_model.UserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    userViewModel: UserViewModel = koinViewModel(),
    foodViewModel: FoodViewModel = koinViewModel(),
) {
    val navController = rememberNavController()
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

    val context = LocalContext.current
    isLoggedIn?.let {
        NavHost(
            navController = navController,
            startDestination = if (it) FOOD else LOGIN
        ) {
            composable(LOGIN) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
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
                        Modifier.padding(innerPadding)
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
                    )
                }
            }

            composable(FOOD) {
                val tabsIcon = listOf(
                    R.drawable.ic_food_bank,
                    R.drawable.ic_shopping_cart,
                    R.drawable.ic_clock,
                    R.drawable.profile_icon
                )
                var selectedTabIndex by remember { mutableIntStateOf(0) }
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
                        0 -> FoodScreen(foodViewModel) {
                            navController.navigate(foodDetails(it))
                        }

                        1 -> ShoppingCartScreen(Modifier.padding(innerPadding))
                        2 -> HistoryScreen(Modifier.padding(innerPadding)) {
                            navController.navigate(foodDetails(it))
                        }

                        3 -> ProfileScreen(
                            Modifier.padding(innerPadding),
                            onLogOutClicked = {
                                navController.navigate(LOGIN) {
                                    //TODO(clean backstack)
                                }
                            },
                            onLogInClicked = {
                                navController.navigate(LOGIN)
                            },
                            onRegisterClicked = {
                                navController.navigate(REGISTRATION)
                            }
                        )
                    }
                }
            }
            composable(
                FOOD_DETAILS,
                arguments = listOf(navArgument(FOOD_ID) { type = NavType.LongType })
            ) {
                val foodId = it.arguments?.getLong(FOOD_ID)
                foodId?.let {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        FoodDetailsScreen(
                            foodViewModel,
                            Modifier.padding(innerPadding),
                            foodId
                        ) {
                            navController.popBackStack()
                        }
                    }
                } ?: run {
                    Toast.makeText(context, "Screen not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    } ?: run {
        SplashScreen()
    }
}

const val REGISTRATION = "registration"
const val LOGIN = "login"
const val FORGOT_PASS = "forgot"
const val FOOD = "food"
const val FOOD_ID = "foodId"
const val FOOD_DETAILS = "food/{foodId}"

fun foodDetails(foodId: Long) = "food/$foodId"
