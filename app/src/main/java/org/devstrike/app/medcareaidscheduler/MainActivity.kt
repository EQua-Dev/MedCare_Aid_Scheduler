package org.devstrike.app.medcareaidscheduler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.devstrike.app.medcareaidscheduler.navigation.SetupNavGraph
import org.devstrike.app.medcareaidscheduler.ui.auth.SignIn
import org.devstrike.app.medcareaidscheduler.ui.theme.MedCareAidSchedulerTheme
import org.devstrike.app.medcareaidscheduler.utils.SessionManager

class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    val sessionManager = SessionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedCareAidSchedulerTheme {


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MedCareAidSchedulerApp{
                        navController = rememberNavController()
                        SetupNavGraph(navController = navController)
                    }

                }
            }
        }
    }
}

@Composable
fun MedCareAidSchedulerApp(content: @Composable () -> Unit) {
    content()
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MedCareAidSchedulerTheme {
    }
}