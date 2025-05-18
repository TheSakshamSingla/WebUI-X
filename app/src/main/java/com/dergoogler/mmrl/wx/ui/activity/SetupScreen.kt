package com.dergoogler.mmrl.wx.ui.activity

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dergoogler.mmrl.datastore.model.WorkingMode
import com.dergoogler.mmrl.platform.Platform
import com.dergoogler.mmrl.ui.component.card.Card
import com.dergoogler.mmrl.ui.component.listItem.ListItem
import com.dergoogler.mmrl.wx.R
import com.dergoogler.mmrl.wx.model.FeaturedManager
import com.dergoogler.mmrl.wx.model.managers
import com.dergoogler.mmrl.wx.util.toWorkingMode

@Composable
fun SetupScreen(setWorkingMode: (WorkingMode) -> Unit) {
    var currentSelection: FeaturedManager? by remember { mutableStateOf(null) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.welcome),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.select_your_platform),
                        fontSize = 20.sp,
                        modifier = Modifier.alpha(.3f)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = managers,
                        key = { it.platform.name }
                    ) { manager ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val selected =
                            remember(currentSelection) { currentSelection == manager }

                        Card(
                            modifier = {
                                column = Modifier.padding(0.dp)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .toggleable(
                                        value = selected,
                                        onValueChange = {
                                            currentSelection = manager
                                        },
                                        role = Role.RadioButton,
                                        interactionSource = interactionSource,
                                        indication = ripple()
                                    )
                                    .padding(end = 25.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ListItem(
                                    modifier = Modifier.weight(1f),
                                    icon = manager.icon,
                                    title = stringResource(manager.name)
                                )

                                RadioButton(
                                    selected = selected,
                                    onClick = null
                                )
                            }
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        enabled = currentSelection != null
                                && currentSelection!!.platform != Platform.NonRoot,
                        onClick = {
                            setWorkingMode(currentSelection!!.platform.toWorkingMode())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .absolutePadding(bottom = 5.dp),
                    ) {
                        Text(
                            text =
                                if (currentSelection != null) {
                                    stringResource(
                                        R.string.continue_with,
                                        currentSelection!!.name
                                    )
                                } else {
                                    stringResource(R.string.select)
                                }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.non_root_note),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(.3f)
                    )
                }
            }
        }
    }
}
