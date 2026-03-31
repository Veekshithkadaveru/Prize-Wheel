package app.krafted.prizewheel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import app.krafted.prizewheel.data.AppDatabase
import app.krafted.prizewheel.ui.navigation.NavGraph
import app.krafted.prizewheel.ui.theme.PrizeWheelTheme
import app.krafted.prizewheel.viewmodel.WheelViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrizeWheelTheme {
                val context = LocalContext.current
                val db = remember { AppDatabase.getDatabase(context) }
                val wheelViewModel: WheelViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return WheelViewModel(db.wheelDao()) as T
                        }
                    }
                )
                val navController = rememberNavController()
                NavGraph(navController = navController, wheelViewModel = wheelViewModel)
            }
        }
    }
}