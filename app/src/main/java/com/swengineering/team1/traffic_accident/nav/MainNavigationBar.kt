package com.swengineering.team1.traffic_accident.nav

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.screen.AdminScreen
import com.swengineering.team1.traffic_accident.screen.HotspotScreen
import com.swengineering.team1.traffic_accident.screen.NotificationScreen
import com.swengineering.team1.traffic_accident.screen.TrendsScreen

/**
 * 실제 화면을 담당하는 부분
 * [NavBar]에서 클릭한 파트에 따라 화면을 전환해줌
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = ScreenType.Hotspot.route
    ) {
        composable(ScreenType.Hotspot.route) { HotspotScreen(Modifier.padding(paddingValues)) }
        composable(ScreenType.Notification.route) { NotificationScreen(Modifier.padding(paddingValues)) }
        composable(ScreenType.Trends.route) { TrendsScreen(Modifier.padding(paddingValues)) }
        composable(ScreenType.Admin.route) { AdminScreen(Modifier.padding(paddingValues)) }
    }
}

/**
 * 밑에 NavBar 부분에 대한 코드
 */
@Composable
fun NavBar(navController: NavController) {
    val context = LocalContext.current

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        ScreenType.entries.forEach { screen ->
            NavItem(
                navController,
                screen,
                currentRoute == screen.route
            )
        }
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_library),
                    contentDescription = stringResource(R.string.label_license),
                    modifier = Modifier
                        .width(26.dp)
                        .height(26.dp)
                )
            },
            label = { Text(stringResource(R.string.label_license), fontSize = 9.sp) },
            selected = false,
            onClick = {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            }
        )
    }
}

/**
 * [NavBar]에서 각각의 아이템을 정의하는 코드
 */
@Composable
fun RowScope.NavItem(navController: NavController, screen: ScreenType, selected: Boolean) {
    NavigationBarItem(
        icon = {
            val iconId = if (selected) screen.selectedIcon else screen.unselectedIcon
            Icon(
                painter = painterResource(iconId),
                contentDescription = stringResource(screen.title),
                modifier = Modifier
                    .width(26.dp)
                    .height(26.dp)
            )
        },
        label = { Text(stringResource(screen.title), fontSize = 9.sp) },
        selected = selected,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}