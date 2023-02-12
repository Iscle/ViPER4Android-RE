package com.aam.viper4android.ui.effect

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import com.aam.viper4android.EffectCard
import com.aam.viper4android.R
import com.aam.viper4android.ui.ValueSlider

@Composable
fun ViPERBassEffect() {
    var enabled by remember { mutableStateOf(false) }
    EffectCard(icon = painterResource(R.drawable.ic_bass), name = "ViPER bass", enabled = enabled, onEnabledChange = { enabled = it }) {
        var bassMode by rememberSaveable { mutableStateOf(0) } // TODO: Move to state
        var bassFrequency by rememberSaveable { mutableStateOf(55) } // TODO: Move to state
        var bassGain by rememberSaveable { mutableStateOf(0) } // TODO: Move to state
        Column {
            // TODO: Bass mode picker
            ValueSlider(
                title = "Bass frequency",
                summary = (bassFrequency + 15).toString(),
                summaryUnit = "Hz",
                value = bassFrequency,
                onValueChange = { bassFrequency = it },
                valueRange = 0..135
            )
            ValueSlider(
                title = "Bass gain",
                summary = (bassGain + 1).toString(),
                summaryUnit = "dB",
                value = bassGain,
                onValueChange = { bassGain = it },
                valueRange = 0..11
            )
        }
    }
}