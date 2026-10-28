package com.example.burcingame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.burcingame.model.GameState

/**
 * =====================================================================
 * ANA MENÜ EKRANI (MainMenuScreen.kt)
 * =====================================================================
 * Bu ekran, oyun açıldığında ilk karşılaşılan ana menüdür. TV ve mobil uyumlu 
 * odaklanabilir (focusable) menü butonları ve gökkuşağı temalı degrade arka plan içerir.
 * =====================================================================
 */

/**
 * MainMenuScreen Composable Fonksiyonu
 * Ana menü arayüzünü ve buton yönlendirmelerini oluşturur.
 * 
 * @param onNavigate [Function] Oyun durumunu (GameState) değiştiren callback (Örn: PLAYING veya LEVEL_SELECT)
 * @param onQuit [Function] Oyundan çıkış yapmayı sağlayan callback
 */
@Composable
fun MainMenuScreen(
    onNavigate: (GameState) -> Unit,
    onQuit: () -> Unit
) {
    // [UI Layout] Ana menü kök Box container bileşeni
    Box(
        modifier = Modifier
            .fillMaxSize() // [UI Property] Ekranı tam boy kapla
            .background( // [UI Property] Arka plan görsel stili
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E), // [Color] Degrade üst renk (Koyu lacivert)
                        Color(0xFF16213E), // [Color] Degrade orta renk (Koyu mavi)
                        Color(0xFF0F3460)  // [Color] Degrade alt renk (Derin mavi)
                    )
                )
            )
            .padding(48.dp), // [UI Property / Spacing] Dış kenar boşlukları (48 dp)
        contentAlignment = Alignment.Center // [UI Property] İçerikleri dikey ve yatayda ortala
    ) {
        // [UI Layout] İçerikleri dikey hizada sıralayan Column container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // [UI Property] Yatayda ortalama
            verticalArrangement = Arrangement.Center,           // [UI Property] Dikeyde ortalama
            modifier = Modifier.fillMaxWidth()                  // [UI Property] Genişliği maksimum yap
        ) {
            // [UI Property] Ana Oyun Başlığı Metni
            Text(
                text = "🌈 Burçin'in Sihirli Gökkuşağı Maceraları 🌈", // [Parameter] Başlık metin içeriği
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold, // [UI Property] Kalın yazı tipi
                    fontSize = 36.sp             // [UI Property] Yazı boyutu (36 sp)
                ),
                color = Color.White // [Color] Metin rengi (Saf beyaz)
            )

            Spacer(modifier = Modifier.height(16.dp)) // [UI Property / Spacing] Başlıklar arası dikey boşluk (16 dp)

            // [UI Property] Alt Başlık / Slogan Metni
            Text(
                text = "Magical Rainbow Adventures on TV", // [Parameter] Alt başlık metni
                style = MaterialTheme.typography.titleMedium,  // [UI Property] Material typography stili
                color = Color(0xFFEAEAEA)                      // [Color] Açık gri alt başlık rengi (0xFFEAEAEA)
            )

            Spacer(modifier = Modifier.height(48.dp)) // [UI Property / Spacing] Başlık grubu ile butonlar arası boşluk (48 dp)

            // [UI Property] Oyun Başlatma Butonu
            TvMenuButton(
                text = "🚀 Oyuna Başla (Play)", // [Parameter] Buton üzerindeki metin
                onClick = { onNavigate(GameState.PLAYING) } // [Parameter] Tıklama aksiyonu (Oyun durumunu PLAYING yap)
            )

            Spacer(modifier = Modifier.height(16.dp)) // [UI Property / Spacing] Butonlar arası dikey boşluk (16 dp)

            // [UI Property] Bölüm Seçim Ekranına Gitme Butonu
            TvMenuButton(
                text = "🗺️ Bölüm Seç (Level Select)", // [Parameter] Buton metni
                onClick = { onNavigate(GameState.LEVEL_SELECT) } // [Parameter] Tıklama aksiyonu (LEVEL_SELECT yap)
            )

            Spacer(modifier = Modifier.height(16.dp)) // [UI Property / Spacing] Butonlar arası dikey boşluk (16 dp)

            // [UI Property] Oyundan Çıkış Butonu
            TvMenuButton(
                text = "🚪 Çıkış (Quit)", // [Parameter] Buton metni
                onClick = onQuit // [Parameter] Çıkış callback fonksiyonu
            )
        }
    }
}

/**
 * TvMenuButton Composable Fonksiyonu
 * TV kumandası veya klavye odağı (focus) aldığında büyüyen ve renk değiştiren özel menü butonu.
 * 
 * @param text [String] Buton üzerinde görünecek metin
 * @param onClick [Function] Butona tıklandığında (veya OK tuşuna basıldığında) çalışacak fonksiyon
 */
@Composable
fun TvMenuButton(
    text: String,
    onClick: () -> Unit
) {
    // [State] Odaklanma (Focus) durumunu takip eden remember mutable state
    var isFocused by remember { mutableStateOf(false) }

    // [UI Property / Animation] Odaklanıldığında butonun büyüme oranı (1.05f = %5 büyüme, 1.0f = normal boyut)
    val scale = if (isFocused) 1.05f else 1.0f
    
    // [Color] Odaklanıldığında arka plan rengi (Altın sarısı) vs normal arka plan rengi (Koyu mavi: 0xFF1F4068)
    val backgroundColor = if (isFocused) Color(0xFFFFE66D) else Color(0xFF1F4068)
    
    // [Color] Odaklanıldığında yazı rengi (Koyu lacivert) vs normal yazı rengi (Beyaz)
    val contentColor = if (isFocused) Color(0xFF1A1A2E) else Color.White

    // [UI Component] Material 3 Button bileşeni
    Button(
        onClick = onClick, // [Parameter] Tıklama dinleyicisi
        modifier = Modifier
            .width(400.dp) // [UI Property / Dimension] Buton genişliği (TV ekranları için ideal: 400 dp)
            .height(64.dp) // [UI Property / Dimension] Buton yüksekliği (TV kumanda dokunma hedefi için ideal: 64 dp)
            .scale(scale)  // [UI Property / Animation] Odaklanma animasyon ölçeği
            .onFocusChanged { isFocused = it.isFocused }, // [UI Property / Event] Odak değişim algılayıcısı
        shape = RoundedCornerShape(16.dp), // [UI Property] Buton köşe yuvarlaklığı (16 dp yumuşak köşe radius)
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor, // [Parameter] Buton zemin rengi
            contentColor = contentColor       // [Parameter] İçerik / metin rengi
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isFocused) 12.dp else 4.dp // [UI Property] Odaklanınca gölge derinliği artar (12 dp / 4 dp)
        )
    ) {
        // [UI Component] Buton içi metin bileşeni
        Text(
            text = text,       // [Parameter] Görüntülenecek metin
            fontSize = 20.sp,  // [UI Property] Yazı boyutu (20 sp)
            fontWeight = FontWeight.Bold // [UI Property] Kalın yazı tipi
        )
    }
}
