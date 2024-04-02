package com.example.studyapp.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavBarBody(
    items: List<NavigationItem>,
    currentRoute: String?,
    onClick: (NavigationItem) -> Unit
) {
    items.forEachIndexed { index, navigationItem ->
        NavigationDrawerItem(
            colors = NavigationDrawerItemDefaults.colors(

            ),
            label = {
                Text(text = navigationItem.title)
            },
            selected = currentRoute == navigationItem.route,
            onClick = { 
                onClick(navigationItem)
            },
            icon = {
                Icon(
                    imageVector = if (currentRoute == navigationItem.route) {
                        navigationItem.selectedIcon
                    } else {
                        navigationItem.unSelectedIcon
                    },
                    contentDescription = navigationItem.title
                )
            },
            badge = {
                    navigationItem.badgeCount?.let {it ->
                        Text(text = it.toString())
                    }
            },
            modifier = Modifier.padding(
                PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            )
        )
    }
}