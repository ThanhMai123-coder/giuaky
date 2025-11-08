package com.example.midterm_ltdd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.midterm_ltdd.ui.theme.Midterm_LTDDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Midterm_LTDDTheme {
                val navController = rememberNavController()
                val phoneViewModel: PhoneViewModel = viewModel()
                AppNav(navController = navController, vm = phoneViewModel)
            }
        }
    }
}

@Composable
private fun AppNav(
    navController: NavHostController,
    vm: PhoneViewModel
) {
    val loggedIn by vm.loggedIn.collectAsState()
    val startDestination = if (loggedIn) "list" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLogin = { username, password ->
                    vm.login(username, password)
                    if (vm.loggedIn.value) {
                        navController.navigate("list") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("list") {
            PhoneListScreen(
                vm = vm,
                onAdd = { navController.navigate("add") }
            )
        }

        composable("add") {
            AddPhoneScreen(
                vm = vm,
                onDone = { navController.popBackStack() }
            )
        }
    }
}