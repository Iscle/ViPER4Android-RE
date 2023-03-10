package com.aam.viper4android.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ValuePicker(
    title: String,
    values: Array<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { showDialog = true }) {
        Text(text = title)
        Text(text = values[selectedIndex])
    }

    if (showDialog) {
        ValuePickerDialog(
            title,
            values,
            selectedIndex,
            onSelectedIndexChange,
            onDismissRequest = { showDialog = false })
    }
}

@Composable
private fun ValuePickerDialog(
    title: String,
    values: Array<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var internalSelectedIndex by rememberSaveable { mutableStateOf(selectedIndex) }
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            onDismissRequest()
        },
        title = {
            Text(text = title)
        },
        text = {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                values.forEachIndexed { index, value ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { internalSelectedIndex = index },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = index == internalSelectedIndex,
                            onClick = { internalSelectedIndex = index })
                        Text(text = value)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSelectedIndexChange(internalSelectedIndex)
                    onDismissRequest()
                }
            ) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}