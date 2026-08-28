package cn.classfun.droidvm.ui.zn
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.classfun.droidvm.BuildConfig
import cn.classfun.droidvm.R
import cn.classfun.droidvm.ui.main.MainActivity
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.Capsule

/** 首页配色：液态玻璃风格的鲜艳渐变 */
object ZNColors {
    val Top = Color(0xFF4F86F7)
    val Mid = Color(0xFF8A6CF6)
    val Bottom = Color(0xFF38D9C0)
    val Accent = Color(0xFFFF7AB8)
    val Amber = Color(0xFFFFC24B)
}
class ZNToolboxHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val onOpenVM = {
                startActivity(Intent(this, MainActivity::class.java))
            }
            val onOpenRootXposed = {
                startActivity(Intent(this, RootXposedActivity::class.java))
            }
            val onOpenAbout = {
                startActivity(Intent(this, AboutActivity::class.java))
            }
            val onOpenGithub = {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(BuildConfig.GITHUB_URL)
                        )
                    )
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(this, "未找到浏览器", Toast.LENGTH_SHORT).show()
                }
            }
            val onOpenUrl: (String) -> Unit = { url ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(this, "未找到浏览器", Toast.LENGTH_SHORT).show()
                }
            }
            ZNToolboxHomeScreen(
                onOpenVM = onOpenVM,
                onOpenRootXposed = onOpenRootXposed,
                onOpenAbout = onOpenAbout,
                onOpenGithub = onOpenGithub,
                onOpenUrl = onOpenUrl,
            )
        }
    }
}
@Composable
fun ZNToolboxHomeScreen(
    onOpenVM: () -> Unit,
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
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---- 头部 ----
            Header(backdrop)
            // ---- 工具卡片 ----
            ToolCard(
                backdrop = backdrop,
                icon = Icons.Rounded.DesktopWindows,
                tint = ZNColors.Top,
                title = "虚拟机管理",
                subtitle = "启动 / 停止 / 管理虚拟系统 · 支持 crosvm 与 QEMU 后端",
                onClick = onOpenVM,
            )
            ToolCard(
                backdrop = backdrop,
                icon = Icons.Rounded.Security,
                tint = ZNColors.Amber,
                title = "Root & Xposed",
                subtitle = "宿主 Root 检测 · 内置 Magisk + LSPosed 虚拟系统方案",
                onClick = onOpenRootXposed,
            )
            ToolCard(
                backdrop = backdrop,
                icon = Icons.Rounded.Android,
                tint = ZNColors.Bottom,
                title = "Android 虚拟系统",
                subtitle = "在虚拟机内启动 Android 系统 · 自带 Root 与 Xposed",
                onClick = onOpenRootXposed,
            )
            ToolCard(
                backdrop = backdrop,
                icon = Icons.Rounded.Memory,
                tint = ZNColors.Accent,
                title = "磁盘与镜像",
                subtitle = "创建 / 转换 / 扩容 qcow2 · raw 磁盘 · 导入 LXC",
                onClick = onOpenVM,
            )
            ToolCard(
                backdrop = backdrop,
                icon = Icons.Rounded.Cloud,
                tint = ZNColors.Mid,
                title = "虚拟网络",
                subtitle = "NAT 桥接 · DHCP · 共享目录 (VirtFS)",
                onClick = onOpenVM,
            )
            Spacer(Modifier.height(4.dp))
            // ---- 底部：快手主页 ----
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassChip(
                    backdrop = backdrop,
                    icon = Icons.Rounded.PlayCircle,
                    label = "快手 · 好友",
                    tint = ZNColors.Bottom,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenUrl(BuildConfig.KS_FRIEND_URL) },
                )
                GlassChip(
                    backdrop = backdrop,
                    icon = Icons.Rounded.Star,
                    label = "快手 · ZN",
                    tint = ZNColors.Amber,
                    modifier = Modifier.weight(1f),
                    onClick = { onOpenUrl(BuildConfig.KS_OWN_URL) },
                )
            }
            // ---- 底部：关于 / 开源 ----
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassChip(
                    backdrop = backdrop,
                    icon = Icons.Rounded.Code,
                    label = "开源仓库",
                    tint = ZNColors.Top,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenGithub,
                )
                GlassChip(
                    backdrop = backdrop,
                    icon = Icons.Rounded.Settings,
                    label = "关于 ZN工具箱",
                    tint = ZNColors.Mid,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAbout,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "ZN工具箱 v${BuildConfig.VERSION_NAME} · 基于 DroidVM 与 AndroidLiquidGlass 开源项目",
                Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
@Composable
private fun LiquidGlassBackground(backdrop: LayerBackdrop) {
    // 背景渐变与光斑都画进 backdrop 图层，玻璃折射/模糊才能采样到完整彩色背景
    Canvas(
        Modifier
            .fillMaxSize()
            .layerBackdrop(backdrop)
    ) {
        drawRect(
            Brush.verticalGradient(listOf(ZNColors.Top, ZNColors.Mid, ZNColors.Bottom))
        )
        val w = size.width
        val h = size.height
        drawCircle(
            color = Color.White.copy(alpha = 0.35f),
            radius = w * 0.32f,
            center = Offset(w * 0.85f, h * 0.15f),
        )
        drawCircle(
            color = ZNColors.Amber.copy(alpha = 0.45f),
            radius = w * 0.30f,
            center = Offset(w * 0.12f, h * 0.32f),
        )
        drawCircle(
            color = ZNColors.Top.copy(alpha = 0.55f),
            radius = w * 0.38f,
            center = Offset(w * 0.80f, h * 0.78f),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.22f),
            radius = w * 0.26f,
            center = Offset(w * 0.18f, h * 0.88f),
        )
    }
}
@Composable
private fun Header(backdrop: Backdrop) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier
                .size(72.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { CircleShape },
                    effects = {
                        vibrancy()
                        blur(16f.dp.toPx())
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painterResource(R.drawable.zn_logo),
                contentDescription = "ZN工具箱 图标",
                modifier = Modifier
                    .padding(4.dp)
                    .size(64.dp),
                contentScale = ContentScale.Crop,
            )
        }
        Column {
            Text(
                "ZN工具箱",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "虚拟机工具箱 · Root & Xposed 一站式",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
            )
        }
    }
}
@Composable
private fun ToolCard(
    backdrop: Backdrop,
    icon: ImageVector,
    tint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = remember { com.kyant.shapes.RoundedRectangle(28.dp) }
    Row(
        modifier
            .fillMaxWidth()
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    vibrancy()
                    blur(24f.dp.toPx())
                    lens(
                        refractionHeight = 60f.dp.toPx(),
                        refractionAmount = 1.5f,
                    )
                },
                layerBlock = {
                    clip = true
                    shape = com.kyant.shapes.RoundedRectangle(28.dp)
                },
            )
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier
                .size(48.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { CircleShape },
                    effects = {
                        vibrancy()
                        blur(12f.dp.toPx())
                    },
                )
                .background(tint.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                subtitle,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
        Icon(
            androidx.compose.material.icons.rounded.KeyboardArrowRight,
            null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp),
        )
    }
}
@Composable
private fun GlassChip(
    backdrop: Backdrop,
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { Capsule() },
                effects = {
                    vibrancy()
                    blur(20f.dp.toPx())
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
        Text(
            label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
