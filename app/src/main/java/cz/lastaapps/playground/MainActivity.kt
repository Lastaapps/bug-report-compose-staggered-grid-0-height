package cz.lastaapps.playground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import cz.lastaapps.playground.ui.theme.DecomposepageserializationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DecomposepageserializationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PageContent(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageContent(modifier: Modifier) {
    val state = rememberPullToRefreshState()
    // should be persistent, I'm to lazy to add the dependency
    val data = remember { List(100) { it.plus(1).toString() } }

    Box(
        modifier.nestedScroll(state.nestedScrollConnection),
    ) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(200.dp),
            content = {
                // Breaks the pull to refresh feature - any of those
//                item {}
//                item { Spacer(Modifier) }
//                item { Spacer(Modifier.width(1.dp)) }

                // this works
//                item { Spacer(Modifier.height(1.dp)) }

                items(data) { text ->
                    Text(text = text)
                }
            },
        )

        PullToRefreshContainer(
            state = state,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
