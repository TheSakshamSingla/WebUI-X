package com.dergoogler.mmrl.wx.ui.screens.modules

import com.dergoogler.mmrl.platform.content.LocalModule
import com.dergoogler.mmrl.platform.content.State
import com.dergoogler.mmrl.wx.R
import com.dergoogler.mmrl.ui.component.scrollbar.VerticalFastScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ModulesList(
    list: List<LocalModule>,
    state: LazyListState,
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(
            items = list.filter { it.features.webui },
            key = { it.id }
        ) { module ->
            ModuleItem(
                module = module,
            )
        }
    }

    VerticalFastScrollbar(
        state = state,
        modifier = Modifier.align(Alignment.CenterEnd)
    )
}

@Composable
fun ModuleItem(
    module: LocalModule,
) = ModuleItem(
    module = module,
    indicator = {
        when (module.state) {
            State.REMOVE -> StateIndicator(R.drawable.trash)
            State.UPDATE -> StateIndicator(R.drawable.device_mobile_down)
            else -> {}
        }
    }
)
