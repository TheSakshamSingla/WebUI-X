package com.dergoogler.mmrl.wx.ui.screens.modules

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dergoogler.mmrl.datastore.providable.LocalUserPreferences
import com.dergoogler.mmrl.ui.component.LabelItem
import com.dergoogler.mmrl.wx.R
import com.dergoogler.mmrl.ui.component.card.Card
import com.dergoogler.mmrl.ui.component.card.CardDefaults.cardStyle
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.ext.takeTrue
import com.dergoogler.mmrl.platform.Platform
import com.dergoogler.mmrl.platform.content.LocalModule
import com.dergoogler.mmrl.platform.content.State
import com.dergoogler.mmrl.platform.file.SuFile.Companion.toFormattedFileSize
import com.dergoogler.mmrl.ui.component.LabelItemDefaults
import com.dergoogler.mmrl.wx.ui.activity.webui.WebUIActivity
import com.dergoogler.mmrl.wx.util.toFormattedDateSafely
import com.dergoogler.mmrl.wx.util.versionDisplay

@Composable
fun ModuleItem(
    module: LocalModule,
    alpha: Float = 1f,
    decoration: TextDecoration = TextDecoration.None,
    indicator: @Composable() (BoxScope.() -> Unit?)? = null,
) {
    val userPreferences = LocalUserPreferences.current
    val menu = userPreferences.modulesMenu
    val context = LocalContext.current

    val canWenUIAccessed = Platform.isAlive && module.features.webui && module.state != State.REMOVE
    val clicker: (() -> Unit)? = canWenUIAccessed nullable {
        WebUIActivity.start(
            context = context,
            modId = module.id
        )
    }

    Card(
        modifier = {
            column = Modifier.padding(0.dp)
        },
        style = cardStyle.copy(
            boxContentAlignment = Alignment.Center,
        ),
        absolute = {
            indicator?.invoke(this)
        },
        onClick = clicker
    ) {
        Row(
            modifier = Modifier.padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .alpha(alpha = alpha)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = module.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(
                        id = R.string.author,
                        module.versionDisplay, module.author
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = decoration,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (module.lastUpdated != 0L && menu.showUpdatedTime) {
                    Text(
                        text = stringResource(
                            id = R.string.update_at,
                            module.lastUpdated.toFormattedDateSafely
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = decoration,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        Text(
            modifier = Modifier
                .alpha(alpha = alpha)
                .padding(horizontal = 16.dp),
            text = module.description,
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = decoration,
            color = MaterialTheme.colorScheme.outline
        )

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            userPreferences.developerMode.takeTrue {
                LabelItem(
                    text = module.id,
                    upperCase = false
                )
            }

            LabelItem(
                text = module.size.toFormattedFileSize(),
                style = LabelItemDefaults.style.copy(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}

@Composable
fun StateIndicator(
    @DrawableRes icon: Int,
    color: Color = MaterialTheme.colorScheme.outline,
) = Image(
    modifier = Modifier.requiredSize(150.dp),
    painter = painterResource(id = icon),
    contentDescription = null,
    alpha = 0.1f,
    colorFilter = ColorFilter.tint(color)
)
