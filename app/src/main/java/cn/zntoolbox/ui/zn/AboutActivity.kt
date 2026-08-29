package cn.zntoolbox.ui.zn
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.twoyi.BuildConfig
import io.twoyi.R
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.Capsule

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val openUrl: (String) -> Unit = { url ->
                try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                catch (_: ActivityNotFoundException) { Toast.makeText(this, "未找到浏览器", Toast.LENGTH_SHORT).show() }
            }
            AboutScreen(
                onBack = { finish() },
                onOpenGithub = { openUrl(BuildConfig.GITHUB_URL) },
                onOpenBaseProject = { openUrl(BuildConfig.BASE_PROJECT_URL) },
                onOpenForkProject = { openUrl(BuildConfig.FORK_PROJECT_URL) },
                onOpenFriend = { openUrl(BuildConfig.KS_FRIEND_URL) },
                onOpenOwn = { openUrl(BuildConfig.KS_OWN_URL) },
            )
        }
    }
}

@Composable
private fun AboutScreen(onBack: () -> Unit, onOpenGithub: () -> Unit, onOpenBaseProject: () -> Unit, onOpenForkProject: () -> Unit, onOpenFriend: () -> Unit, onOpenOwn: () -> Unit) {
    val backdrop = rememberLayerBackdrop()
    Box(Modifier.fillMaxSize()) {
        LiquidGlassBackground(backdrop)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassTopBar(backdrop, "关于 ℤ𝕟𝕏工具", onBack)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Box(Modifier.size(96.dp).drawBackdrop(backdrop = backdrop, shape = { CircleShape }, effects = { vibrancy(); blur(16f.dp.toPx()) }), contentAlignment = Alignment.Center) {
                    Image(painterResource(R.drawable.zn_logo), contentDescription = "ℤ𝕟𝕏工具 图标", modifier = Modifier.padding(6.dp).size(84.dp), contentScale = ContentScale.Crop)
                }
            }
            Text("ℤ𝕟𝕏工具 v${BuildConfig.VERSION_NAME}", Modifier.fillMaxWidth(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text("免 Root 容器虚拟机 · 液态玻璃美学", Modifier.fillMaxWidth(), color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            GlassChip(backdrop = backdrop, icon = Icons.Rounded.PlayCircle, label = "快手 · 好友", tint = ZNColors.Bottom, onClick = onOpenFriend)
            GlassChip(backdrop = backdrop, icon = Icons.Rounded.Star, label = "快手 · ℤ𝕟𝕏工具", tint = ZNColors.Amber, onClick = onOpenOwn)
            GlassChip(backdrop = backdrop, icon = Icons.Rounded.Code, label = "GitHub · ℤ𝕟𝕏工具", tint = ZNColors.Top, onClick = onOpenGithub)
            val cardShape = remember { com.kyant.shapes.RoundedRectangle(24.dp) }
            Column(
                Modifier.fillMaxWidth().drawBackdrop(backdrop = backdrop, shape = { cardShape }, effects = { vibrancy(); blur(20f.dp.toPx()) }, layerBlock = { clip = true; shape = com.kyant.shapes.RoundedRectangle(24.dp) }).clickable(onClick = onOpenBaseProject).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("开源声明", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text("本项目基于 Twoyi (MPL-2.0) 及其活跃 fork 二次开发，遵循 MPL-2.0 协议开源。引擎：Twoyi 轻量 Android 容器；UI：AndroidLiquidGlass (Apache-2.0)。", color = Color.White.copy(alpha = 0.85f), fontSize = 12.5.sp, lineHeight = 18.sp)
                Text("基座引擎：Twoyi", color = ZNColors.Bottom, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("容器 Fork：Threetwi", color = ZNColors.Bottom, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))
            Text("Copyright © ℤ𝕟𝕏工具 · 液态玻璃虚拟机", Modifier.fillMaxWidth(), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
