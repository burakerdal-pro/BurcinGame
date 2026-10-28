package com.example.burcingame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * =====================================================================
 * ZAFER / KAZANMA EKRANI (VictoryScreen.kt)
 * =====================================================================
 * Bu ekran, oyuncu tüm koleksiyon objelerini toplayarak bölümü başarıyla 
 * tamamladığında (Zafer) gösterilir. Kutlama mesajı, skor bilgisi ve 
 * sonraki bölüme geçiş veya ana menüye dönüş seçenekleri sunar.
 * 
 * 🎨 KULLANICI DEĞİŞİKLİK REHBERİ (NE DEĞİŞTİRİLEBİLİR?):
 * - Arka Plan Degrade Renkleri: `Color(0xFF0F3460)`, `Color(0xFF16213E)` ve 
 *   `Color(0xFF1A1A2E)` geçiş renklerini dilediğiniz gibi değiştirebilirsiniz.
 * - Tebrik Metinleri: `"🎉 Tebrikler! Zafer! (Victory!) 🎉"` veya 
 *   `"Harika Macera! Skor: $score"` yazılarını zevkinize göre düzenleyebilirsiniz.
 * - Butonlar: "Sonraki Bölüm" ve "Ana Menü" buton metinleri ve aksiyonları özelleştirilebilir.
 * =====================================================================
 */

/**
 * VictoryScreen Composable Fonksiyonu
 * Zafer ekranını ve buton yönlendirmelerini oluşturur.
 * 
 * @param score Bölüm sonunda kazanılan toplam skor
 * @param onNextLevel Bir sonraki bölüme geçişi sağlayan callback
 * @param onMenu Ana menüye dönmeyi sağlayan callback
 */
@Composable
fun VictoryScreen(
    score: Int,
    onNextLevel: () -> Unit,
    onMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Ekran arka plan dikey degrade renkleri
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F3460), Color(0xFF16213E), Color(0xFF1A1A2E))
                )
            )
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Zafer / Kutlama Başlık Metni
            Text(
                text = "🎉 Tebrikler! Zafer! (Victory!) 🎉",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                color = Color(0xFFFFE66D) // Kutlama sarısı vurgu rengi
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Skor ve Tebrik Mesajı
            Text(
                text = "Harika Macera! Skor: $score",
                fontSize = 24.sp,
                color = Color(0xFF4ECDC4) // Turkuaz başarı rengi
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Sonraki Bölüm Butonu
            TvMenuButton(
                text = "✨ Sonraki Bölüm (Next Level)",
                onClick = onNextLevel
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ana Menü Butonu
            TvMenuButton(
                text = "🏠 Ana Menü (Main Menu)",
                onClick = onMenu
            )
        }
    }
}

