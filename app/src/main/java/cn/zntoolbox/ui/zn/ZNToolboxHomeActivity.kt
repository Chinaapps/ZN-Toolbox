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
import androidx.compose.material.icons.automirrored.rounded.ArrowRight
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.twoyi.BuildConfig
import io.twoyi.R
import io.twoyi.ui.ProfileListActivity
import io.twoyi.ui.SettingsActivity
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.Capsule

class ZNToolboxHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val onOpenProfiles = { startActivity(Intent(this, ProfileListActivity::class.java)) }
            val onOpenSettings = { startActivity(Intent(this, SettingsActivity::class.java)) }
            val onOpenRootXposed = { startActivity(Intent(this, RootXposedActivity::class.java)) }
            val onOpenAbout = { startActivity(Intent(this, AboutActivity::class.java)) }
            val onOpenGithub = {
                try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.GITHUB_URL))) }
                catch (_: ActivityNotFoundException) { Toast.makeText(this, "未找到浏览器", Toast.LENGTH_SHORT).show() }
            }
            val onOpenUrl: (String) -> Unit = { url ->
                try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                catch (_: ActivityNotFoundException) { Toast.makeText(this, "未找到浏览器", Toast.LENGTH_SHORT).show() }
            }
            ZNToolboxHomeScreen(onOpenProfiles, onOpenSettings, onOpenRootXposed, onOpenAbout, onOpenGithub, onOpenUrl)
        }
    }
}

@Composable
fun ZNToolboxHomeScreen(
    onOpenProfiles: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenRootXposed: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenGithub: () -> Unit,
    onOpenUrl: (String) -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    Box(Modifier.fillMaxSize()) {
        LiquidGlassBackground(backdrop)
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .padding(bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header(backdrop)
            ToolCard(backdrop = backdrop, icon = Icons.Rounded.Storage, tint = ZNColors.Top,
                title = "虚拟机 / 配置管理", subtitle = "创建与选择容器配置，多系统共存", onClick = onOpenProfiles)
            ToolCard(backdrop = backdrop, icon = Icons.Rounded.Settings, tint = ZNColors.Mid,
                title = "容器设置", subtitle = "根文件系统导入 · 服务器 · 控制台", onClick = onOpenSettings)
            ToolCard(backdrop = backdrop, icon = Icons.Rounded.Security, tint = ZNColors.Amber,
                title = "Root & Xposed 管理", subtitle = "容器内自带 Root · 内置 LSPosed 框架", onClick = onOpenRootXposed)
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassChip(backdrop = backdrop, icon = Icons.Rounded.PlayCircle, label = "快手 · 好友",
                    tint = ZNColors.Bottom, modifier = Modifier.weight(1f), onClick = { onOpenUrl(BuildConfig.KS_FRIEND_URL) })
                GlassChip(backdrop = backdrop, icon = Icons.Rounded.Star, label = "快手 · ℤ𝕟𝕏",
                    tint = ZNColors.Amber, modifier = Modifier.weight(1f), onClick = { onOpenUrl(BuildConfig.KS_OWN_URL) })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassChip(backdrop = backdrop, icon = Icons.Rounded.Code, label = "开源仓库",
                    tint = ZNColors.Top, modifier = Modifier.weight(1f), onClick = onOpenGithub)
                GlassChip(backdrop = backdrop, icon = Icons.Rounded.Settings, label = "关于 ℤ𝕟𝕏工具",
                    tint = ZNColors.Mid, modifier = Modifier.weight(1f), onClick = onOpenAbout)
            }
            Spacer(Modifier.height(8.dp))
            Text("ℤ𝕟𝕏工具 v${BuildConfig.VERSION_NAME} · 免 Root 容器虚拟机",
                Modifier.fillMaxWidth(), color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            LiquidGlassBottomBar(backdrop = backdrop, onLaunch = onOpenProfiles)
        }
    }
}

@Composable
private fun LiquidGlassBottomBar(backdrop: Backdrop, onLaunch: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { Capsule() },
                    effects = { vibrancy(); blur(28f.dp.toPx()) },
                    layerBlock = {
                        clip = true
                        shape = Capsule()
                    },
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier
                        .size(44.dp)
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { CircleShape },
                            effects = { vibrancy(); blur(12f.dp.toPx()) },
                        )
                        .background(ZNColors.Bottom.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Android, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text("Android 容器", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("容器内自带 Root & Xposed", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                }
                Box(
                    Modifier
                        .background(
                            Brush.horizontalGradient(listOf(ZNColors.Bottom, ZNColors.Mid, ZNColors.Accent)),
                            Capsule()
                        )
                        .clickable(onClick = onLaunch)
                        .padding(horizontal = 28.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Rounded.PlayCircle, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Text("启动", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(backdrop: Backdrop) {
    Row(
        Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier.size(72.dp).drawBackdrop(
                backdrop = backdrop, shape = { CircleShape },
                effects = { vibrancy(); blur(16f.dp.toPx()) },
            ),
            contentAlignment = Alignment.Center,
        ) {
            Image(painterResource(R.drawable.zn_logo), contentDescription = "ℤ𝕟𝕏工具 图标",
                modifier = Modifier.padding(4.dp).size(64.dp), contentScale = ContentScale.Crop)
        }
        Column {
            Text("ℤ𝕟𝕏工具", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text("免 Root 虚拟机 · 容器内自带 Root & Xposed", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun ToolCard(
    backdrop: Backdrop, icon: ImageVector, tint: Color, title: String, subtitle: String,
    onClick: () -> Unit, modifier: Modifier = Modifier,
) {
    val cardShape = remember { com.kyant.shapes.RoundedRectangle(28.dp) }
    Row(
        modifier.fillMaxWidth().drawBackdrop(
            backdrop = backdrop, shape = { cardShape },
            effects = { vibrancy(); blur(24f.dp.toPx()); lens(refractionHeight = 60f.dp.toPx(), refractionAmount = 1.5f) },
            layerBlock = { clip = true; shape = com.kyant.shapes.RoundedRectangle(28.dp) },
        ).clickable(onClick = onClick).padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(48.dp).drawBackdrop(
                backdrop = backdrop, shape = { CircleShape }, effects = { vibrancy(); blur(12f.dp.toPx()) },
            ).background(tint.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Color.White.copy(alpha = 0.82f), fontSize = 12.sp, lineHeight = 17.sp)
        }
        Icon(Icons.AutoMirrored.Rounded.ArrowRight, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
    }
}
