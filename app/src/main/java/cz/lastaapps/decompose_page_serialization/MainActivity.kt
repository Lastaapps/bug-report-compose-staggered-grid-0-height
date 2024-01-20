@file:OptIn(ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class)

package cz.lastaapps.decompose_page_serialization

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.value.Value
import cz.lastaapps.decompose_page_serialization.ui.theme.DecomposepageserializationTheme
import kotlinx.serialization.Serializable

// --------------------------------
// Activity
// --------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = DefaultRootComponent(
            componentContext = defaultComponentContext(),
        )

        setContent {
            DecomposepageserializationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RootContent(
                        root,
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

// --------------------------------
// Components
// --------------------------------
interface RootComponent {
    val content: Value<ChildPages<*, Child>>

    sealed interface Child {
        @JvmInline
        value class Page(val component: PageComponent) : Child
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {
    private val navigation = PagesNavigation<Config>()

    override val content: Value<ChildPages<*, RootComponent.Child>> =
        childPages(navigation,
            Config.serializer(),
            initialPages = {
                Pages(List(10) { Config(it) }, 0)
            }) { configuration, componentContext ->
            RootComponent.Child.Page(DefaultPageComponent(componentContext, configuration.number))
        }

    @JvmInline
    @Serializable
    value class Config(val number: Int) {}
}

interface PageComponent {
    val num: Int
}

class DefaultPageComponent(
    componentContext: ComponentContext,
    override val num: Int,
) : PageComponent, ComponentContext by componentContext

// --------------------------------
// Compose
// --------------------------------
@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    Pages(
        component.content.subscribeAsState(),
        onPageSelected = {},
    ) { _, page ->
        when (page) {
            is RootComponent.Child.Page -> PageContent(page.component, modifier)
        }
    }
}

@Composable
fun PageContent(component: PageComponent, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(component.num.toString())
    }
}
