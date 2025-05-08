package com.example.e_library.navigation


import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.e_library.ui.theme.screens.admin.AdminUploadScreen
import com.example.e_library.ui.theme.screens.auth.LoginScreen
import com.example.e_library.ui.theme.screens.auth.RegisterScreen
import com.example.e_library.ui.theme.screens.details.BookDetailsScreen
import com.example.e_library.ui.theme.screens.details.EditProfileScreen
import com.example.e_library.ui.theme.screens.home.HomeScreen
import com.example.e_library.ui.theme.screens.details.FavoritesScreen
import com.example.e_library.ui.theme.screens.details.ProfileScreen
import com.example.e_library.ui.theme.screens.reader.ReaderScreen


@Composable
fun NavigationGraph(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }

        composable(Routes.HOME) {
            HomeScreen(navController)
        }

        composable("${Routes.DETAILS}/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailsScreen(bookId = bookId, navController = navController)
        }

//        composable(
//            route = Routes.READER + "/{pdfUrl}",
//            arguments = listOf(navArgument("pdfUrl") { defaultValue = "" })
//        ) { backStackEntry ->
//            val pdfUrl = backStackEntry.arguments?.getString("pdfUrl") ?: ""
//            PdfViewerScreen(pdfUrl)
//        }

        // In your NavGraph
        composable(Routes.ADMIN_UPLOAD) {
            AdminUploadScreen(context = LocalContext.current)
        }

        composable("favorites") {
            FavoritesScreen(navController = navController, onBack = { navController.popBackStack() })
        }




        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }


        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController = navController)
        }

        composable(
            route = Routes.READER + "/{pdfUrl}",
            arguments = listOf(navArgument("pdfUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val pdfUrl = Uri.decode(backStackEntry.arguments?.getString("pdfUrl") ?: "")
            ReaderScreen(pdfUrl = pdfUrl)
        }



    }


}
