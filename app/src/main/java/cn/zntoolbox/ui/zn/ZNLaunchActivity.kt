package cn.zntoolbox.ui.zn
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.twoyi.R
import io.twoyi.RemoteRenderActivity
import io.twoyi.Render2Activity
import io.twoyi.ui.ProfileListActivity
import io.twoyi.ui.SettingsActivity
import io.twoyi.utils.AppKV
import io.twoyi.utils.Profile
import io.twoyi.utils.ProfileManager
import io.twoyi.utils.RomManager
import io.twoyi.utils.ServerManager
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.Capsule
import java.io.File

class ZNLaunchActivity : ComponentActivity() {
    private var isBooting by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val profileManager = ProfileManager.getInstance(this)
            val activeProfile = profileManager.activeProfile
            val profileName = activeProfile?.name ?: "默认配置"
            val profileMode = when {
                activeProfile?.isLegacyMode == true -> "兼容模式 (OpenGL)"
                activeProfile?.isServerMode == true -> "服务器模式 (Scrcpy)"
                else -> "兼容模式 (OpenGL)"
            }
            ZNLaunchScreen(
                profileName = profileName,
                profileMode = profileMode,
                isBooting = isBooting,
                onBack = { finish() },
                onLaunch = { bootContainer() },
                onManageProfiles = { startActivity(Intent(this, ProfileListActivity::class.java)) },
                onSettings = { startActivity(Intent(this, SettingsActivity::class.java)) },
            )
        }
    }

    private fun bootContainer() {
        if (isBooting) return
        isBooting = true
        val profileManager = ProfileManager.getInstance(this)
        val activeProfile = profileManager.activeProfile
        activeProfile?.updateLastUsed()
        profileManager.updateProfile(activeProfile)
        val rootfsDir = activeProfile?.let { profileManager.getRootfsDir(it) }
            ?: RomManager.getRootfsDir(this)
        val useLegacy = activeProfile?.isLegacyMode != false
        if (useLegacy) { bootLegacy(rootfsDir, activeProfile) } else { bootServer(rootfsDir, activeProfile) }
    }

    private fun bootLegacy(rootfsDir: File, profile: Profile?) {
        val romExist = RomManager.romExist(rootfsDir)
        if (!romExist) {
            val dialog = ProgressDialog(this).apply {
                setMessage(getString(R.string.extracting_tips))
                setCancelable(false)
                show()
            }
            Thread {
                try {
                    val factoryRomUpdated = RomManager.needsUpgrade(this, rootfsDir)
                    val forceInstall = AppKV.getBooleanConfig(this, AppKV.FORCE_ROM_BE_RE_INSTALL, false)
                    val use3rdRom = profile?.isUse3rdPartyRom
                        ?: AppKV.getBooleanConfig(this, AppKV.SHOULD_USE_THIRD_PARTY_ROM, false)
                    RomManager.extractRootfs(applicationContext, rootfsDir, false, factoryRomUpdated, forceInstall, use3rdRom, true)
                    RomManager.initRootfs(applicationContext, rootfsDir)
                    runOnUiThread {
                        dialog.dismiss()
                        if (RomManager.romExist(rootfsDir)) { startLegacyRenderer() }
                        else { Toast.makeText(this, "根文件系统解压失败", Toast.LENGTH_LONG).show(); isBooting = false }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        dialog.dismiss()
                        Toast.makeText(this, "启动失败: ${e.message}", Toast.LENGTH_LONG).show()
                        isBooting = false
                    }
                }
            }.start()
        } else { startLegacyRenderer() }
    }

    private fun startLegacyRenderer() {
        val intent = Intent(this, Render2Activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun bootServer(rootfsDir: File, profile: Profile?) {
        val address = profile?.serverAddress ?: AppKV.DEFAULT_SERVER_ADDRESS
        val dialog = ProgressDialog(this).apply {
            setMessage(getString(R.string.server_connecting))
            setCancelable(false)
            show()
        }
        Thread {
            try {
                val romExist = RomManager.romExist(rootfsDir)
                if (!romExist) {
                    runOnUiThread { dialog.setMessage(getString(R.string.extracting_tips)) }
                    val factoryRomUpdated = RomManager.needsUpgrade(this, rootfsDir)
                    val forceInstall = AppKV.getBooleanConfig(this, AppKV.FORCE_ROM_BE_RE_INSTALL, false)
                    val use3rdRom = profile?.isUse3rdPartyRom
                        ?: AppKV.getBooleanConfig(this, AppKV.SHOULD_USE_THIRD_PARTY_ROM, false)
                    RomManager.extractRootfs(applicationContext, rootfsDir, romExist, factoryRomUpdated, forceInstall, use3rdRom)
                    RomManager.initRootfs(applicationContext, rootfsDir)
                    if (!RomManager.romExist(rootfsDir)) { throw Exception("根文件系统解压失败") }
                    runOnUiThread { dialog.setMessage(getString(R.string.server_connecting)) }
                }
                RomManager.ensureBootFiles(applicationContext, rootfsDir)
                val metrics: DisplayMetrics = resources.displayMetrics
                ServerManager.startServer(this, address, metrics.widthPixels, metrics.heightPixels)
                var ready = false
                for (i in 0..9) { Thread.sleep(500); if (ServerManager.testConnection(address)) { ready = true; break } }
                if (!ready) { ServerManager.stopServer(); throw Exception("服务器启动超时") }
                runOnUiThread {
                    dialog.dismiss()
                    Toast.makeText(this, R.string.server_started, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RemoteRenderActivity::class.java)
                    intent.putExtra("server_address", address)
                    startActivity(intent)
                    isBooting = false
                }
            } catch (e: Exception) {
                runOnUiThread {
                    dialog.dismiss()
                    Toast.makeText(this, "启动失败: ${e.message}", Toast.LENGTH_LONG).show()
                    isBooting = false
                }
            }
        }.start()
    }
}

@Composable
private fun ZNLaunchScreen(
    profileName: String, profileMode: String, isBooting: Boolean,
    onBack: () -> Unit, onLaunch: () -> Unit, onManageProfiles: () -> Unit, onSettings: () -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    Box(Modifier.fillMaxSize()) {
        LiquidGlassBackground(backdrop)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .statusBarsPadding().navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassTopBar(backdrop, "启动容器", onBack)
            ProfileInfoCard(backdrop, profileName, profileMode)
            Spacer(Modifier.height(8.dp))
            val launchTint = if (isBooting)
                Brush.horizontalGradient(listOf(Color.Gray.copy(alpha = 0.4f), Color.DarkGray.copy(alpha = 0.3f)))
            else
                Brush.horizontalGradient(listOf(ZNColors.Bottom.copy(alpha = 0.55f), ZNColors.Mid.copy(alpha = 0.55f), ZNColors.Accent.copy(alpha = 0.55f)))
            Box(
                Modifier.fillMaxWidth()
                    .drawBackdrop(backdrop = backdrop, shape = { Capsule() },
                        effects = { vibrancy(); blur(28f.dp.toPx()); lens(refractionHeight = 50f.dp.toPx(), refractionAmount = 2f) },
                        layerBlock = { clip = true; shape = Capsule() })
                    .background(launchTint, Capsule())
                    .clickable(enabled = !isBooting, onClick = onLaunch)
                    .padding(vertical = 22.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(if (isBooting) Icons.Rounded.Info else Icons.Rounded.PlayArrow, null,
                        tint = Color.White, modifier = Modifier.size(28.dp))
                    Text(if (isBooting) "正在启动…" else "启动 Android 容器",
                        color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(backdrop, Icons.Rounded.Storage, "配置管理", ZNColors.Top, Modifier.weight(1f), onManageProfiles)
                QuickActionCard(backdrop, Icons.Rounded.Settings, "容器设置", ZNColors.Mid, Modifier.weight(1f), onSettings)
            }
            Spacer(Modifier.height(8.dp))
            Text("首次启动会自动解压系统镜像，请耐心等待。容器内自带 Root 与 LSPosed。",
                Modifier.fillMaxWidth(), color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun ProfileInfoCard(backdrop: Backdrop, name: String, mode: String) {
    val cardShape = remember { com.kyant.shapes.RoundedRectangle(28.dp) }
    Row(
        Modifier.fillMaxWidth()
            .drawBackdrop(backdrop = backdrop, shape = { cardShape },
                effects = { vibrancy(); blur(24f.dp.toPx()); lens(refractionHeight = 60f.dp.toPx(), refractionAmount = 1.5f) },
                layerBlock = { clip = true; shape = com.kyant.shapes.RoundedRectangle(28.dp) })
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier.size(56.dp)
                .drawBackdrop(backdrop = backdrop, shape = { CircleShape }, effects = { vibrancy(); blur(12f.dp.toPx()) })
                .background(ZNColors.Bottom.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Rounded.Android, null, tint = Color.White, modifier = Modifier.size(30.dp)) }
        Column(Modifier.weight(1f)) {
            Text("当前配置", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text(name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(mode, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun QuickActionCard(
    backdrop: Backdrop, icon: ImageVector, label: String, tint: Color,
    modifier: Modifier = Modifier, onClick: () -> Unit,
) {
    val cardShape = remember { com.kyant.shapes.RoundedRectangle(20.dp) }
    Column(
        modifier
            .drawBackdrop(backdrop = backdrop, shape = { cardShape }, effects = { vibrancy(); blur(20f.dp.toPx()) },
                layerBlock = { clip = true; shape = com.kyant.shapes.RoundedRectangle(20.dp) })
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp, horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.size(40.dp)
                .drawBackdrop(backdrop = backdrop, shape = { CircleShape }, effects = { vibrancy(); blur(10f.dp.toPx()) })
                .background(tint.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
