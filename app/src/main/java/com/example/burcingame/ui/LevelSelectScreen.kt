package com.example.burcingame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * =====================================================================
 * BÖLÜM SEÇİM EKRANI (LevelSelectScreen.kt)
 * =====================================================================
 * Bu ekran, oyuncunun oynamak istediği seviyeyi (bölümü) ızgara (grid) yapısında 
 * seçmesini sağlar. TV kumandası ile kartlar arasında gezilebilir.
 * =====================================================================
 */

/**
 * LevelSelectScreen Composable Fonksiyonu
 * Bölüm seçim ekranını ve ızgara kartlarını oluşturur.
 * 
 * @param onSelectLevel [Function] Seçilen bölüm numarasını (Int) ana akışa ileten callback
 * @param onBack [Function] Ana menüye dönmeyi sağlayan callback
 */
@Composable
fun LevelSelectScreen(
    onSelectLevel: (Int) -> Unit,
    onBack: () -> Unit
) {
    // [Data] Toplam bölüm listesi (1'den 6'ya kadar). Burayı örn: 1..10 yaparak bölüm sayısını artırabilirsiniz.
    val levels = (1..6).toList()

    // [UI Layout] Ekran kök Box container
    Box(
        modifier = Modifier
            .fillMaxSize() // [UI Property] Ekranı tam boy kapla
            .background( // [UI Property] Arka plan degrade renk geçişi
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F3460), // [Color] Degrade üst renk (Derin mavi)
                        Color(0xFF1A1A2E)  // [Color] Degrade alt renk (Koyu lacivert)
                    )
                )
            )
            .padding(48.dp) // [UI Property / Spacing] Dış kenar boşlukları (48 dp)
    ) {
        // [UI Layout] İçerikleri dikey hizada tutan Column container
        Column(
            modifier = Modifier.fillMaxSize(), // [UI Property] Tam boy
            horizontalAlignment = Alignment.CenterHorizontally // [UI Property] Yatayda ortalama
        ) {
            // [UI Property] Bölüm Seçim Başlığı Metni
            Text(
                text = "🗺️ Bölüm Seçimi (Level Selection)", // [Parameter] Başlık içeriği
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold, // [UI Property] Kalın yazı tipi
                    fontSize = 32.sp             // [UI Property] Yazı boyutu (32 sp)
                ),
                color = Color.White // [Color] Metin rengi (Saf beyaz)
            )

            Spacer(modifier = Modifier.height(32.dp)) // [UI Property / Spacing] Başlık ile ızgara arası dikey boşluk (32 dp)

            // [UI Component] Bölüm Kartları Izgarası (LazyVerticalGrid)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // [Parameter] Yatayda 3 sütun (TV / Tablet için ideal yapı)
                modifier = Modifier
                    .weight(1f) // [UI Property] Kalan boş alanın tamamını doldur
                    .fillMaxWidth() // [UI Property] Maksimum genişlik
                    .padding(horizontal = 32.dp), // [UI Property / Spacing] Yatay iç kenar boşlukları (32 dp)
                horizontalArrangement = Arrangement.spacedBy(24.dp), // [UI Property / Spacing] Kartlar arası yatay boşluk (24 dp)
                verticalArrangement = Arrangement.spacedBy(24.dp)   // [UI Property / Spacing] Kartlar arası dikey boşluk (24 dp)
            ) {
                items(levels) { level ->
                    LevelCard(level = level, onClick = { onSelectLevel(level) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // [UI Property / Spacing] Izgara ile menü butonu arası boşluk (16 dp)

            // [UI Component] Ana Menüye Dönüş Butonu
            TvMenuButton(
                text = "⬅️ Ana Menüye Dön", // [Parameter] Buton metni
                onClick = onBack           // [Parameter] Geri dönüş callback fonksiyonu
            )
        }
    }
}

/**
 * LevelCard Composable Fonksiyonu
 * Bölüm seçim ızgarasındaki her bir bölümü temsil eden odaklanabilir kart.
 * 
 * @param level [Int] Bölüm numarası
 * @param onClick [Function] Karta tıklandığında ilgili bölümü başlatan fonksiyon
 */
@Composable
fun LevelCard(
    level: Int,
    onClick: () -> Unit
) {
    // [State] Kartın odaklanma (focus) durumu
    var isFocused by remember { mutableStateOf(false) }
    
    // [UI Property / Animation] Odaklanıldığında kartın büyüme oranı (1.08f = %8 büyüme, 1.0f = normal boyut)
    val scale = if (isFocused) 1.08f else 1.0f
    
    // [Color] Odaklanıldığında kartın rengi (Kırmızı/Mercan: 0xFFFF6B6B) vs normal rengi (Koyu Mavi: 0xFF1F4068)
    val containerColor = if (isFocused) Color(0xFFFF6B6B) else Color(0xFF1F4068)
    val contentColor = Color.White // [Color] Kart içerik / metin rengi (Saf beyaz)

    // [UI Component] Material 3 Card bileşeni
    Card(
        onClick = onClick, // [Parameter] Tıklama dinleyicisi
        modifier = Modifier
            .height(120.dp) // [UI Property / Dimension] Kart yüksekliği (120 dp)
            .scale(scale)   // [UI Property / Animation] Odaklanma ölçeklendirme animasyonu
            .onFocusChanged { isFocused = it.isFocused }, // [UI Property / Event] Odak değişim takibi
        shape = RoundedCornerShape(16.dp), // [UI Property] Köşe yuvarlaklığı (16 dp)
        colors = CardDefaults.cardColors(
            containerColor = containerColor, // [Parameter] Kart zemin rengi
            contentColor = contentColor       // [Parameter] İçerik rengi
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isFocused) 12.dp else 4.dp // [UI Property] Odaklanınca gölge derinliği artar (12 dp / 4 dp)
        )
    ) {
        // [UI Layout] Kart içi hizalama Box container
        Box(
            modifier = Modifier.fillMaxSize(), // [UI Property] Tam boy kaplama
            contentAlignment = Alignment.Center // [UI Property] Ortalanmış içerik
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // [UI Property] Bölüm Numarası Metni
                Text(
                    text = "Bölüm $level", // [Parameter] Bölüm başlığı (Örn: Bölüm 1)
                    fontSize = 22.sp,      // [UI Property] Yazı boyutu (22 sp)
                    fontWeight = FontWeight.Bold // [UI Property] Kalın yazı tipi
                )
                Spacer(modifier = Modifier.height(4.dp)) // [UI Property / Spacing] Başlık ile alt yazı arası boşluk (4 dp)
                // [UI Property] Kart alt yazısı / Slogan
                Text(
                    text = "⭐ Sihirli Macera", // [Parameter] Slogan metni
                    fontSize = 14.sp,          // [UI Property] Yazı boyutu (14 sp)
                    color = Color(0xFFFFE66D)  // [Color] Sarı vurgu rengi (0xFFFFE66D)
                )
            }
        }
    }
}
