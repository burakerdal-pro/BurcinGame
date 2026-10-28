package com.example.burcingame.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * =====================================================================
 * TEMA VE RENK PALETİ YAPILANDIRMASI (Theme.kt)
 * =====================================================================
 * Bu dosya, oyunun genel renk paletini, karanlık (dark) ve aydınlık (light) 
 * tema geçişlerini ve Android 12+ dinamik renk (Dynamic Color) desteğini yönetir.
 * 
 * 🎨 KULLANICI DEĞİŞİKLİK REHBERİ (NE DEĞİŞTİRİLEBİLİR?):
 * - [RainbowDarkColorScheme]: Karanlık modda kullanılan arka plan, buton ve yazı renkleri.
 * - [RainbowLightColorScheme]: Aydınlık modda kullanılan renk paleti.
 * =====================================================================
 */

/**
 * Karanlık Tema Renk Paleti (Dark Color Scheme)
 * Karanlık ortamlarda ve TV arayüzlerinde göz yormayan neon/canlı renkler.
 */
private val RainbowDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF6B6B),      // [Color] Ana vurgu rengi: Canlı mercan kırmızı (Butonlar, önemli etiketler, odaklar)
    secondary = Color(0xFF4ECDC4),    // [Color] İkincil vurgu rengi: Turkuaz (Başarılar, ikonlar, ikincil öğeler)
    tertiary = Color(0xFFFFE66D),     // [Color] Üçüncül vurgu rengi: Altın sarısı (Yıldızlar, vurgulu metinler, kenarlıklar)
    background = Color(0xFF1A1A2E),   // [Color] Arka plan rengi: Koyu lacivert (Oyun ve menü arka planı)
    surface = Color(0xFF16213E),      // [Color] Yüzey rengi: Koyu mavi (Kartlar, paneller, dialog pencereleri)
    onPrimary = Color.White,          // [Color] Metin rengi: Primary (Ana vurgu) rengi üzerinde okunaklı beyaz metin
    onSecondary = Color.White,        // [Color] Metin rengi: Secondary (İkincil) rengi üzerinde okunaklı beyaz metin
    onBackground = Color(0xFFEAEAEA), // [Color] Metin rengi: Background (Arka plan) üzerinde açık gri genel metin rengi
    onSurface = Color(0xFFEAEAEA)     // [Color] Metin rengi: Surface (Yüzey) üzerinde açık gri genel metin rengi
)

/**
 * Aydınlık Tema Renk Paleti (Light Color Scheme)
 * Aydınlık ortamlarda kullanılan ferah ve canlı renk paleti.
 */
private val RainbowLightColorScheme = lightColorScheme(
    primary = Color(0xFFFF4757),      // [Color] Ana vurgu rengi: Canlı kırmızı
    secondary = Color(0xFF2ED573),    // [Color] İkincil vurgu rengi: Canlı yeşil
    tertiary = Color(0xFFFFA502),     // [Color] Üçüncül vurgu rengi: Turuncu/Sarı
    background = Color(0xFFF1F2F6),   // [Color] Arka plan rengi: Açık gri/mavi
    surface = Color.White,            // [Color] Yüzey rengi: Saf beyaz (Kartlar ve paneller)
    onPrimary = Color.White,          // [Color] Metin rengi: Primary üzerindeki beyaz metin
    onSecondary = Color.White,        // [Color] Metin rengi: Secondary üzerindeki beyaz metin
    onBackground = Color(0xFF2F3542), // [Color] Metin rengi: Background üzerindeki koyu gri metin
    onSurface = Color(0xFF2F3542)     // [Color] Metin rengi: Surface üzerindeki koyu gri metin
)

/**
 * BurcinGameTheme Composable Fonksiyonu
 * Sistem temasını ve dinamik renkleri (Android 12+) uygulayarak MaterialTheme sarmalayıcısı sağlar.
 * 
 * @param darkTheme [Boolean] Karanlık tema aktif mi? (Sistem ayarına göre otomatik belirlenir)
 * @param dynamicColor [Boolean] Android 12+ cihazlarda duvar kağıdı renklerinin temaya uygulanması (Varsayılan: true)
 * @param content [Composable] Tema sarmalayıcısı içinde çalışacak UI içerikleri lambda bloğu
 */
@Composable
fun BurcinGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // [UI Property / Logic] Renk şeması belirleme mantığı: Dinamik renk (Android 12+) veya manuel renk paletleri
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> RainbowDarkColorScheme
        else -> RainbowLightColorScheme
    }

    // MaterialTheme sarmalayıcısı: Seçilen renk şemasını ve içerikleri uygular
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
