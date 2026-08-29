package cn.zntoolbox.ui.zn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.Capsule

object ZNColors {
    val Top = Color(0xFF4F86F7)
    val Mid = Color(0xFF8A6CF6)
    val Bottom = Color(0xFF38D9C0)
    val Accent = Color(0xFFFF7AB8)
    val Amber = Color(0xFFFFC24B)
}

@Composable
fun LiquidGlassBackground(backdrop: LayerBackdrop) {
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
        drawCircle(color = Color.White.copy(alpha = 0.35f), radius = w * 0.32f, center = Offset(w * 0.85f, h * 0.15f))
        drawCircle(color = ZNColors.Amber.copy(alpha = 0.45f), radius = w * 0.30f, center = Offset(w * 0.12f, h * 0.32f))
        drawCircle(color = ZNColors.Top.copy(alpha = 0.55f), radius = w * 0.38f, center = Offset(w * 0.80f, h * 0.78f))
        drawCircle(color = Color.White.copy(alpha = 0.22f), radius = w * 0.26f, center = Offset(w * 0.18f, h * 0.88f))
    }
}

@Composable
fun GlassTopBar(backdrop: Backdrop, title: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(44.dp).drawBackdrop(
                backdrop = backdrop, shape = { CircleShape },
                effects = { vibrancy(); blur(14f.dp.toPx()) },
            ).clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GlassChip(backdrop: Backdrop, icon: ImageVector, label: String, tint: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().drawBackdrop(
            backdrop = backdrop, shape = { Capsule() },
            effects = { vibrancy(); blur(20f.dp.toPx()) },
        ).clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun GlassButton(backdrop: Backdrop, icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().drawBackdrop(
            backdrop = backdrop, shape = { Capsule() },
            effects = { vibrancy(); blur(20f.dp.toPx()) },
        ).clickable(onClick = onClick).padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
        Spacer(Modifier.size(8.dp))
        Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}
