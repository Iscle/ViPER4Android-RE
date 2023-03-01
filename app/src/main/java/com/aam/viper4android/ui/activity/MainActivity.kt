package com.aam.viper4android.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aam.viper4android.*
import com.aam.viper4android.ui.effect.*
import com.aam.viper4android.ui.theme.ViPER4AndroidTheme
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var viperManager: ViPERManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViPER4AndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }

        requestIgnoreBatteryOptimizations()
        Intent(this, ViPERService::class.java).let {
            try {
                ContextCompat.startForegroundService(this, it)
            } catch (e: Exception) {
                Log.e(TAG, "onCreate: Failed to start service", e)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    // Required for creating foreground services with the app in the background
    @SuppressLint("BatteryLife")
    private fun requestIgnoreBatteryOptimizations() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).also {
                it.data = Uri.parse("package:$packageName")
                try {
                    startActivityForResult(it, 69)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to request ignore battery optimizations", e)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 69) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            Log.d(TAG, "onActivityResult: Battery optimizations ignored: ${powerManager.isIgnoringBatteryOptimizations(packageName)}")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen(mainViewModel: MainViewModel = viewModel()) {
        val effectState = mainViewModel.uiState.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var openStatusDialog by remember { mutableStateOf(false) }
        var openPresetDialog by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "ViPER4Android",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        IconButton(onClick = { openSettingsActivity() }) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Settings"
                            )
                        }
                        IconButton(onClick = { openStatusDialog = true }) {
                            Icon(
                                Icons.Filled.Memory,
                                contentDescription = "Status",
                            )
                        }
                        IconButton(onClick = { openPresetDialog = true }) {
                            Icon(
                                Icons.Filled.Bookmark,
                                contentDescription = "Presets",
                            )
                        }
                    },
                    floatingActionButton = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var isEnabled by rememberSaveable { mutableStateOf(false) }
                            FloatingActionButton(
                                onClick = {
                                    isEnabled = !isEnabled
                                    if (isEnabled) {
                                        lifecycleScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "ViPER4Android enabled",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    } else {
                                        lifecycleScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "ViPER4Android disabled",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                },
                                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                            ) {
                                Icon(Icons.Filled.PowerSettingsNew, "Localized description")
                            }
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) {
            Box(modifier = Modifier.padding(it)) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(8.dp)
                ) {
                    val effectSpacer = 8.dp
                    MasterLimiterEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    PlaybackGainControlEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    FETCompressorEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    ViPERDDCEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    SpectrumExtensionEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    FIREqualizerEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    ConvolverEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    FieldSurroundEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    DifferentialSurroundEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    HeadphoneSurroundPlusEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    ReverberationEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    DynamicSystemEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    TubeSimulator6N1JEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    ViPERBassEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    ViPERClarityEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    AuditorySystemProtectionEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    AnalogXEffect()
                    Spacer(modifier = Modifier.height(effectSpacer))
                    SpeakerOptimizationEffect()
                }
                if (openStatusDialog) {
                    StatusDialog(viperManager = viperManager, onDismissRequest = { openStatusDialog = false })
                }
                if (openPresetDialog) {
                    PresetDialog(onDismissRequest = { openPresetDialog = false })
                }
            }
        }
    }

    private fun openSettingsActivity() {
        Intent(this, SettingsActivity::class.java).also {
            startActivity(it)
        }
    }
}

//@Composable
//fun Greeting(name: String) {
//    Text(text = "Hello $name!")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    ViPER4AndroidTheme {
//        Greeting("Android")
//    }
//}