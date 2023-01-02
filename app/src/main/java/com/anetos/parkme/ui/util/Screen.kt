package com.anetos.parkme.ui.util

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import com.anetos.parkme.core.helper.Theme
import com.anetos.parkme.core.helper.navController
import com.anetos.parkme.ui.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fragment.Screen(
    title: String,
    modifier: Modifier = Modifier,
    onNavigationIconClick: (() -> Unit)? = { navController?.navigateUp() },
    snackbarHost: @Composable () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AppTheme.dimensions.medium),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val theme = Theme.System
    val scope = rememberCoroutineScope()
    AppTheme(theme = theme) {
        Scaffold(
            topBar = {
                Appbar(
                    title = title,
                    onClick = {
                        scope.launch {
                            scrollState.animateScrollTo(0)
                        }
                    },
                    scrollState.value,
                    onNavigationIconClick = onNavigationIconClick,
                )
            },
            snackbarHost = snackbarHost
        ) { contentPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(AppTheme.dimensions.medium)
                    .padding(contentPadding),
                verticalArrangement,
                horizontalAlignment,
                content = content,
            )
        }
    }
}