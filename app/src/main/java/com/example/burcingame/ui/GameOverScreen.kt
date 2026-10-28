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
 * OYUN BİTTİ EKRANI (GameOverScreen.kt)
 * =====================================================================
 * Bu ekran, oyuncu tüm canlarını kaybettiğinde veya düştüğünde gösterilen 
 * Game Over (Oyun Bitti) ekranıdır. Toplanan skor bilgisini gösterir ve 
 * tekrar deneme veya ana menüye dönme seçenekleri sunar.
 * 
 * 🎨 KULLANICI DEĞİŞİKLİK REHBERİ (NE DEĞİŞTİRİLEBİLİR?):
 * - Arka Plan Degrade Renkleri: `Color(0xFF2D142C)` ve `Color(0xFF1A1A2E)` 
 *   renklerini dilediğiniz tonlarla değiştirebilirsiniz.
 * - Başlık ve Skor Metinleri: `"💥 Oyun Bitti (Game Over) 💥"` metnini veya 
 *   skor görünümünü isteğinize göre özelleştirebilirsiniz.
 * - Butonlar: `TvMenuButton` bileşenleri kullanılarak oluşturulan "Tekrar Dene" 
 *   ve "Ana Menü" butonlarının metinleri ve işlevleri değiştirilebilir.
 * =====================================================================
 */

/**
 * GameOverScreen Composable Fonksiyonu
 * Oyun bitti ekranını ve buton etkileşimlerini oluşturur.
 * 
 * @param score Oyuncunun oyun sırasında topladığı toplam skor
 * @param onRetry Oyunu yeniden başlatmayı (tekrar deneme) sağlayan callback
 * @param onMenu Ana menüye dönmeyi sağlayan callback
 */
@Composable
fun GameOverScreen(
    score: Int,
    onRetry: () -> Unit,
    onMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Ekran arka plan dikey degrade (gradient) renkleri
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2D142C), Color(0xFF1A1A2E))
                )
            )
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Oyun Bitti Başlık Metni
            Text(
                text = "💥 Oyun Bitti (Game Over) 💥",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                color = Color(0xFFFF6B6B) // Dikkat çekici kırmızı/mercan başlık rengi
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Skor Gösterge Metni
            Text(
                text = "Skor: $score",
                fontSize = 24.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Tekrar Dene Butonu
            TvMenuButton(
                text = "🔄 Tekrar Dene (Retry)",
                onClick = onRetry
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

