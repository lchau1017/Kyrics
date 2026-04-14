package com.kyrics.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kyrics.demo.presentation.dualsync.DualSyncDemoScreen
import com.kyrics.demo.presentation.dualsync.DualSyncViewModel
import com.kyrics.demo.presentation.screen.DemoScreen
import com.kyrics.demo.presentation.viewmodel.DemoViewModel
import com.kyrics.demo.presentation.wordtap.WordTapDemoScreen
import com.kyrics.demo.presentation.wordtap.WordTapViewModel
import com.kyrics.demo.theme.KyricsDemoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the Kyrics demo app.
 * Uses Hilt for dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KyricsDemoTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                ) {
                    DemoNavigation()
                }
            }
        }
    }
}

private enum class Screen {
    HOME,
    KYRICS_DEMO,
    DUAL_SYNC_DEMO,
    WORD_TAP_DEMO,
}

@Composable
private fun DemoNavigation() {
    var currentScreen by rememberSaveable { mutableStateOf(Screen.HOME) }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                onKyricsDemoClick = { currentScreen = Screen.KYRICS_DEMO },
                onDualSyncDemoClick = { currentScreen = Screen.DUAL_SYNC_DEMO },
                onWordTapDemoClick = { currentScreen = Screen.WORD_TAP_DEMO },
            )
        }
        Screen.KYRICS_DEMO -> {
            val viewModel: DemoViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            DemoScreen(
                state = state,
                onIntent = viewModel::onIntent,
            )
        }
        Screen.DUAL_SYNC_DEMO -> {
            val viewModel: DualSyncViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            DualSyncDemoScreen(
                state = state,
                onIntent = viewModel::onIntent,
            )
        }
        Screen.WORD_TAP_DEMO -> {
            val viewModel: WordTapViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            WordTapDemoScreen(
                state = state,
                onIntent = viewModel::onIntent,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    onKyricsDemoClick: () -> Unit,
    onDualSyncDemoClick: () -> Unit,
    onWordTapDemoClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kyrics") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Choose a demo",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onKyricsDemoClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Kyrics Demo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDualSyncDemoClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("DualSync Demo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onWordTapDemoClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Word Tap Demo")
            }
        }
    }
}
