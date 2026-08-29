package cn.zntoolbox.ui.zn
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.twoyi.R
import io.twoyi.ui.SettingsActivity
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.Capsule

class RootXposedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RootXposedScreen(
                onBack = { finish() },
                onOpenSettings = {
                    startActivity(Intent(this, SettingsActivity::class.java))
                },
            )
        }
    }
}

@Composable
private fun RootXposedScreen(onBack: () -> Unit, onOpenSettings: () -> Unit) {
    val backdrop = rememberLayerBackdrop()
    Box(Modifier.fillMaxSize()) {
        LiquidGlassBackground(backdrop)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassTopBar(backdrop, "Root & Xposed 管理", onBack)
            FeatureCard(
                backdrop = backdrop,
                icon = Icons.Rounded.Lock,
                tint = ZNColors.Bottom,
                title = "宿主免 Root",
                subtitle = "本应用以容器方式运行，宿主设备无需获取 Root 权限。",
            )
            FeatureCard(
                backdrop = backdrop,
                icon = Icons.Rounded.Verified,
                tint = ZNColors.Amber,
                title = "容器内自带 Root",
                subtitle = "虚拟 Android 系统内默认拥有完整 Root 权限，无需额外刷机。",
            )
            FeatureCard(
                backdrop = backdrop,
                icon = Icons.Rounded.Security,
                tint = ZNColors.Top,
                title = "内置 LSPosed (Xposed)",
                subtitle = "容器内预置 LSPosed 框架，Xposed 模块开箱即用。",
            )
            Spacer(Modifier.height(4.dp))
            GlassButton(
                backdrop = backdrop,
                icon = Icons.Rounded.Android,
                label = "打开容器设置",
                tint = ZNColors.Mid,
                onClick = onOpenSettings,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "提示：启动 Android 虚拟系统后，在系统内即可使用 Root 与 LSPosed 模块。",
                Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }
}

@Composable
private fun FeatureCard(
    backdrop: Backdrop,
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String,
) {
    val cardShape = remember { com.kyant.shapes.RoundedRectangle(26.dp) }
    Row(
        Modifier
            .fillMaxWidth()
            .drawBackdrop(
                backdrop = backdrop,
                shape = { cardShape },
                effects = {
                    vibrancy()
                    blur(22f.dp.toPx())
                },
                layerBlock = {
                    clip = true
                    shape = com.kyant.shapes.RoundedRectangle(26.dp)
                },
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier
                .size(46.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { CircleShape },
                    effects = { vibrancy(); blur(12f.dp.toPx()) },
                )
                .background(tint.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
            )
        }
    }
}
