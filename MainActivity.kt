package com.axiom.pubgcompanion

import android.os.Bundle
import android.os.BatteryManager
import android.content.Context
import android.app.ActivityManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

private val Bg = Color(0xFF0B0F14)
private val Panel = Color(0xFF151C24)
private val Lime = Color(0xFFB8F34A)
private val Muted = Color(0xFF9AA8B6)
private val White = Color(0xFFF2F5F7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AxiomDashboard(this) }
    }
}

data class Preset(
    val name: String,
    val camera: Int,
    val ads: Int,
    val gyro: Int,
    val note: String
)

private val presets = listOf(
    Preset("Balanced", 120, 100, 250, "Starting point for mixed close- and mid-range fights."),
    Preset("Low recoil", 110, 95, 300, "Higher gyro starting point; tune gradually to your device."),
    Preset("Close range", 135, 110, 280, "Faster camera movement for quick target transitions."),
    Preset("Steady aim", 95, 80, 220, "More controlled starting point for slower tracking.")
)

@Composable
fun AxiomDashboard(context: Context) {
    var selected by remember { mutableStateOf(presets.first()) }
    var tab by remember { mutableStateOf("Dashboard") }
    var camera by remember(selected) { mutableFloatStateOf(selected.camera.toFloat()) }
    var ads by remember(selected) { mutableFloatStateOf(selected.ads.toFloat()) }
    var gyro by remember(selected) { mutableFloatStateOf(selected.gyro.toFloat()) }
    var battery by remember { mutableIntStateOf(0) }
    var temp by remember { mutableStateOf("Unavailable") }
    var memory by remember { mutableStateOf("Unavailable") }

    LaunchedEffect(Unit) {
        while (true) {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            battery = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceAtLeast(0)
            val intent = context.registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
            val tenths = intent?.getIntExtra("temperature", -1) ?: -1
            temp = if (tenths >= 0) String.format(Locale.US, "%.1f°C", tenths / 10f) else "Unavailable"
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            val used = mi.totalMem - mi.availMem
            memory = String.format(Locale.US, "%.1f / %.1f GB", used / 1e9, mi.totalMem / 1e9)
            delay(3000)
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg, surface = Panel, primary = Lime, onPrimary = Bg,
        onBackground = White, onSurface = White, secondary = Muted
    )) {
        Scaffold(containerColor = Bg, bottomBar = {
            NavigationBar(containerColor = Panel) {
                listOf("Dashboard", "Sensitivity").forEach { item ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = { tab = item },
                        icon = { Text(if (item == "Dashboard") "◈" else "◎", fontSize = 20.sp) },
                        label = { Text(item) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Lime, selectedTextColor = Lime,
                            indicatorColor = Color(0xFF28351B), unselectedTextColor = Muted
                        )
                    )
                }
            }
        }) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            Text("AXIOM // COMPANION", color = Lime, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Text(if (tab == "Dashboard") "Performance" else "Sensitivity lab", color = White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        }
                        Box(Modifier.background(Color(0xFF26331B), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text("SAFE MODE", color = Lime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (tab == "Dashboard") {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("DEVICE STATUS", color = Muted, fontSize = 11.sp, letterSpacing = 1.5.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Metric("BATTERY", "$battery%", Modifier.weight(1f))
                                    Metric("BATTERY TEMP", temp, Modifier.weight(1f))
                                }
                                Metric("SYSTEM MEMORY", memory, Modifier.fillMaxWidth())
                                Text("These are Android device metrics, not PUBG's internal FPS or game temperature.", color = Muted, fontSize = 12.sp)
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("QUICK TUNE", color = Lime, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text("Active preset: ${selected.name}", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(selected.note, color = Muted, fontSize = 13.sp)
                                Button(onClick = { tab = "Sensitivity" }, modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Bg)) {
                                    Text("OPEN SENSITIVITY LAB", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    item {
                        Text("SESSION CHECKLIST", color = Muted, fontSize = 12.sp, letterSpacing = 1.5.sp)
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                listOf("Close unused background apps", "Keep vents clear", "Use a stable graphics preset", "Take a break if the phone overheats").forEach {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("✓", color = Lime, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(10.dp))
                                        Text(it, color = White, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Text("PRESET PROFILES", color = Muted, fontSize = 12.sp, letterSpacing = 1.5.sp)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            presets.forEach { preset ->
                                val active = preset.name == selected.name
                                Card(
                                    onClick = {
                                        selected = preset
                                        camera = preset.camera.toFloat()
                                        ads = preset.ads.toFloat()
                                        gyro = preset.gyro.toFloat()
                                    },
                                    colors = CardDefaults.cardColors(containerColor = if (active) Color(0xFF26331B) else Panel),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text(preset.name, color = if (active) Lime else White, fontWeight = FontWeight.Bold)
                                            Text(preset.note, color = Muted, fontSize = 12.sp)
                                        }
                                        if (active) Text("ACTIVE", color = Lime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("CUSTOM VALUES", color = Lime, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                TuneSlider("Camera sensitivity", camera, 0f..200f) { camera = it }
                                TuneSlider("ADS sensitivity", ads, 0f..200f) { ads = it }
                                TuneSlider("Gyroscope sensitivity", gyro, 0f..400f) { gyro = it }
                                OutlinedButton(onClick = {
                                    val share = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Axiom sensitivity preset")
                                        putExtra(android.content.Intent.EXTRA_TEXT,
                                            "Axiom PUBG Companion\\nPreset: ${selected.name}\\nCamera: ${camera.toInt()}\\nADS: ${ads.toInt()}\\nGyroscope: ${gyro.toInt()}\\nManual entry required in game settings.")
                                    }
                                    context.startActivity(android.content.Intent.createChooser(share, "Export preset"))
                                }, modifier = Modifier.fillMaxWidth()) {
                                    Text("EXPORT PRESET")
                                }
                                Text("Values are suggestions only. Enter them manually in the game's settings and adjust for your device, FPS, and play style.", color = Muted, fontSize = 12.sp)
                            }
                        }
                    }
                }
                item {
                    Text("AXIOM NOTE: Companion only. No game access, overlays, injection, or anti-cheat changes.", color = Muted, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.background(Color(0xFF202A34), RoundedCornerShape(14.dp)).padding(14.dp)) {
        Text(label, color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(6.dp))
        Text(value, color = Lime, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TuneSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = White, modifier = Modifier.weight(1f), fontSize = 14.sp)
            Text(value.toInt().toString(), color = Lime, fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onChange, valueRange = range)
    }
}
