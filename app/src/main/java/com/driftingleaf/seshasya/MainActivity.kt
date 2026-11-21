package com.driftingleaf.seshasya

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.driftingleaf.seshasya.data.PracticeStore
import com.driftingleaf.seshasya.reminders.Reminders
import com.driftingleaf.seshasya.ui.LedgerScreen
import com.driftingleaf.seshasya.ui.LibraryDetail
import com.driftingleaf.seshasya.ui.LibraryScreen
import com.driftingleaf.seshasya.ui.SettingsScreen
import com.driftingleaf.seshasya.ui.SitScreen
import com.driftingleaf.seshasya.ui.TodayScreen
import com.driftingleaf.seshasya.ui.theme.SeshasyaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        val store = PracticeStore(applicationContext)
        Reminders.createChannel(applicationContext)
        if (store.remindersOn) Reminders.schedule(applicationContext, store)
        setContent {
            SeshasyaTheme { SeshasyaApp(store) }
        }
    }
}

private data class Tab(val route: String, val base: String, val label: String, val icon: ImageVector)

// Four destinations only (Hick's law), in a bottom bar within thumb reach (Fitts's law).
private val tabs = listOf(
    Tab("today", "today", "Today", Icons.Rounded.WbTwilight),
    Tab("sit?tab=0", "sit", "Sit", Icons.Rounded.SelfImprovement),
    Tab("ledger?tab=0", "ledger", "Ledger", Icons.Rounded.EditNote),
    Tab("library", "library", "Library", Icons.Rounded.AutoStories),
)

fun NavHostController.goTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Links from Today open a tab at a specific sub-view, so they must not restore an old state. */
fun NavHostController.goLink(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id)
        launchSingleTop = true
    }
}

@Composable
fun SeshasyaApp(store: PracticeStore) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "today"
    val showBar = route == "today" || route.startsWith("sit") || route.startsWith("ledger") || route == "library"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                    tabs.forEach { t ->
                        NavigationBarItem(
                            selected = route.startsWith(t.base),
                            onClick = { nav.goTab(t.route) },
                            icon = { Icon(t.icon, contentDescription = null) },
                            label = { Text(t.label) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = "today", modifier = Modifier.padding(padding)) {
            composable("today") {
                TodayScreen(
                    store = store,
                    onOpen = { target -> if (target.startsWith("library/")) nav.navigate(target) else nav.goLink(target) },
                    onSettings = { nav.navigate("settings") },
                )
            }
            composable(
                "sit?tab={tab}",
                arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 }),
            ) { e -> SitScreen(store, e.arguments?.getInt("tab") ?: 0) }
            composable(
                "ledger?tab={tab}",
                arguments = listOf(navArgument("tab") { type = NavType.IntType; defaultValue = 0 }),
            ) { e -> LedgerScreen(store, e.arguments?.getInt("tab") ?: 0) }
            composable("library") { LibraryScreen(onOpen = { nav.navigate("library/$it") }) }
            composable("library/{section}") { e ->
                LibraryDetail(e.arguments?.getString("section") ?: "creed", onBack = { nav.popBackStack() })
            }
            composable("settings") { SettingsScreen(store, onBack = { nav.popBackStack() }) }
        }
    }
}
