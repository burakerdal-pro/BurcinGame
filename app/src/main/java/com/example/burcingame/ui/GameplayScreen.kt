package com.example.burcingame.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.burcingame.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * =====================================================================
 * OYUN MOTORU VE FİZİK YÖNETİCİSİ (GameEngine)
 * =====================================================================
 * Bu sınıf, oyunun fizik motorunu, karakter hareketlerini, zıplama mekaniğini,
 * yerçekimi hesaplamalarını, platform çarpışmalarını ve toplanabilir objeleri yönetir.
 * [Large-Screen Scaling] Expanded world for Android TV (3000f x 1080f).
 * [Camera Translation] Smooth camera tracking for large 16:9 displays.
 * =====================================================================
 */
class GameEngine(
    private val onGameOverCallback: () -> Unit, // [Parameter] Oyun bittiğinde çağrılacak callback fonksiyonu
    private val onVictoryCallback: () -> Unit   // [Parameter] Bölüm kazanıldığında çağrılacak callback fonksiyonu
) {
    // [Large-Screen Scaling] Expanded world width (3000f) for large Android TV screens (Mi Box / 16:9 TV)
    val worldWidth: Float = 3000f

    // [Large-Screen Scaling] Expanded world height (1080f) for 1080p Android TV viewports
    val worldHeight: Float = 1080f

    // [State] Oyuncunun başlangıç pozisyonu ve durumu (X: 150f, Y: 700f on large ground)
    private val _playerState = MutableStateFlow(PlayerState(x = 150f, y = 700f))
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    // [Camera Translation] Smooth camera X position for tracking the player across the large landscape
    private val _cameraX = MutableStateFlow(0f)
    val cameraX: StateFlow<Float> = _cameraX.asStateFlow()

    // [Camera Translation] Smooth camera Y position for vertical camera tracking
    private val _cameraY = MutableStateFlow(0f)
    val cameraY: StateFlow<Float> = _cameraY.asStateFlow()

    // [State] Toplam skor state'i (Başlangıç: 0 puan)
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    // [State] Oyuncunun kalan can (lives) sayısı (Başlangıç: 3 can)
    private val _lives = MutableStateFlow(3)
    val lives: StateFlow<Int> = _lives.asStateFlow()

    // [State] Ekran üzerinde geçici olarak gösterilen bilgi mesajları state'i (Başlangıç: null)
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    // [Large-Screen Scaling] Expanded collectibles distributed across the 3000f large landscape
    private val _collectibles = MutableStateFlow(
        listOf(
            Collectible(1, 420f, 730f, CollectibleType.RAINBOW_STAR),   // [Layered Level] Floating Cloud 1 üstünde yıldız
            Collectible(2, 880f, 650f, CollectibleType.MAGIC_DIAMOND),  // [Layered Level] Candy Ledge 2 üstünde elmas
            Collectible(3, 1110f, 570f, CollectibleType.UNICORN_CRYSTAL),// [Layered Level] High Cloud 3 üstünde kristal
            Collectible(4, 1340f, 650f, CollectibleType.RAINBOW_STAR),   // [Layered Level] Golden Bridge 4 üstünde yıldız
            Collectible(5, 1800f, 670f, CollectibleType.MAGIC_DIAMOND),  // [Layered Level] Candy Ledge 5 üstünde elmas
            Collectible(6, 2260f, 670f, CollectibleType.UNICORN_CRYSTAL),// [Layered Level] Golden Bridge 7 üstünde kristal
            Collectible(7, 2810f, 600f, CollectibleType.RAINBOW_STAR)    // [Layered Level] Candy Ledge 9 üstünde yıldız
        )
    )
    val collectibles: StateFlow<List<Collectible>> = _collectibles.asStateFlow()

    // [Large-Screen Scaling] Expanded helper entities (cats and owls) across the 3000f large landscape
    private val _entities = MutableStateFlow(
        listOf(
            GameEntity(1, 650f, 820f, EntityType.CAT), // [Layered Level] Zemin Katmanı 2 üzerinde kedi
            GameEntity(2, 1080f, 570f, EntityType.OWL),// [Layered Level] Yüksek bulut üzerinde bilge baykuş
            GameEntity(3, 2020f, 770f, EntityType.CAT) // [Layered Level] Zemin Katmanı 5 üzerinde sihirli kedi
        )
    )
    val entities: StateFlow<List<GameEntity>> = _entities.asStateFlow()

    /**
     * [Multi-Tier Ground & Lava Gaps] Farklı katmanlarda zemin blokları ve aralarındaki lav çukurları.
     * Zemin tek parça upuzun değildir; farklı yüksekliklerde basamaklı katmanlardan ve aralarındaki tehlikeli lavlardan oluşur.
     */
    val platforms = listOf(
        // === 1. ZEMİN KATMANLARI (Farklı Yükseklik ve Seviyelerdeki Zemin Parçaları) ===
        // Katman 1 (Başlangıç Platosu - Y: 940f)
        Platform(1, 0f, 940f, 360f, 140f, PlatformType.GOLDEN_BLOCK),
        // Boşluk 1: Lav Çukuru 1 (X: 360f - 500f)
        Platform(2, 360f, 970f, 140f, 110f, PlatformType.LAVA_PIT),

        // Katman 2 (1. Yükselti Basamağı - Y: 870f, 70f daha yukarıda)
        Platform(3, 500f, 870f, 320f, 210f, PlatformType.GOLDEN_BLOCK),
        // Boşluk 2: Lav Çukuru 2 (X: 820f - 960f)
        Platform(4, 820f, 970f, 140f, 110f, PlatformType.LAVA_PIT),

        // Katman 3 (Yüksek Zemin Sırtı - Y: 790f, 80f daha yukarıda zirve teras)
        Platform(5, 960f, 790f, 320f, 290f, PlatformType.GOLDEN_BLOCK),
        // Boşluk 3: Lav Çukuru 3 (X: 1280f - 1420f)
        Platform(6, 1280f, 970f, 140f, 110f, PlatformType.LAVA_PIT),

        // Katman 4 (Vadi Tabanı / Orta Zemin - Y: 890f, 100f alçalış)
        Platform(7, 1420f, 890f, 320f, 190f, PlatformType.GOLDEN_BLOCK),
        // Boşluk 4: Lav Çukuru 4 (X: 1740f - 1880f)
        Platform(8, 1740f, 970f, 140f, 110f, PlatformType.LAVA_PIT),

        // Katman 5 (Üst Teras Zemin - Y: 820f, 70f yükseliş)
        Platform(9, 1880f, 820f, 320f, 260f, PlatformType.GOLDEN_BLOCK),
        // Boşluk 5: Lav Çukuru 5 (X: 2200f - 2340f)
        Platform(10, 2200f, 970f, 140f, 110f, PlatformType.LAVA_PIT),

        // Katman 6 (Alçak Plato - Y: 910f, 90f alçalış)
        Platform(11, 2340f, 910f, 280f, 170f, PlatformType.GOLDEN_BLOCK),
        // Boşluk 6: Lav Çukuru 6 (X: 2620f - 2760f)
        Platform(12, 2620f, 970f, 140f, 110f, PlatformType.LAVA_PIT),

        // Katman 7 (Zafer Zirvesi Zemin - Y: 830f, 80f yükseliş)
        Platform(13, 2760f, 830f, 240f, 250f, PlatformType.GOLDEN_BLOCK),

        // === 2. HAVADA ASILI BULUT, ŞEKER VE KÖPRÜ PLATFORMLARI (Lavların ve Katmanların Üzerinden Kolay Geçiş) ===
        // Lav 1 üzerinde rahatça çıkılabilen gökkuşağı bulutu (Y: 800f, 1. zeminden sadece 140f yukarıda)
        Platform(14, 320f, 800f, 220f, 30f, PlatformType.RAINBOW_CLOUD),
        // Lav 2 üzerinde şeker platformu (Y: 720f)
        Platform(15, 780f, 720f, 220f, 30f, PlatformType.CANDY_LEDGE),
        // Katman 3 üzerinde yüksek gökkuşağı bulutu (Y: 640f)
        Platform(16, 1020f, 640f, 200f, 30f, PlatformType.RAINBOW_CLOUD),
        // Lav 3 üzerinde altın asılı köprü (Y: 720f)
        Platform(17, 1240f, 720f, 220f, 30f, PlatformType.GOLDEN_BLOCK),
        // Lav 4 üzerinde şeker platformu (Y: 740f)
        Platform(18, 1700f, 740f, 220f, 30f, PlatformType.CANDY_LEDGE),
        // Katman 5 üzerinde yüksek gökkuşağı bulutu (Y: 670f)
        Platform(19, 1940f, 670f, 200f, 30f, PlatformType.RAINBOW_CLOUD),
        // Lav 5 üzerinde altın asılı köprü (Y: 740f)
        Platform(20, 2160f, 740f, 220f, 30f, PlatformType.GOLDEN_BLOCK),
        // Lav 6 üzerinde gökkuşağı bulutu (Y: 760f)
        Platform(21, 2580f, 760f, 220f, 30f, PlatformType.RAINBOW_CLOUD),
        // Zafer alanı üzerinde yüksek şeker terası (Y: 670f)
        Platform(22, 2720f, 670f, 200f, 30f, PlatformType.CANDY_LEDGE)
    )

    // [Optimization] 60 FPS döngüsünde liste tahsisi yapmamak için önceden filtrelenmiş katı platformlar ve lavlar
    private val solidPlatforms = platforms.filter { it.type != PlatformType.LAVA_PIT }.sortedBy { it.y }
    private val lavaPits = platforms.filter { it.type == PlatformType.LAVA_PIT }

    private var leftPressed = false  // [Flag] Sol tuş basılı mı?
    private var rightPressed = false // [Flag] Sağ tuş basılı mı?
    private var gameLoopJob: Job? = null // [Coroutine Job] Oyun döngüsü arka plan işi

    /**
     * Oyun Döngüsünü (Game Loop) Başlatır.
     */
    fun start(coroutineScope: CoroutineScope) {
        if (gameLoopJob?.isActive == true) return
        gameLoopJob = coroutineScope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(16L) // [Timing] ~60 FPS oyun döngüsü kare gecikmesi (16 milisaniye)
                updatePhysics()
            }
        }
    }

    /**
     * Oyun Döngüsünü Durdurur.
     */
    fun stop() {
        gameLoopJob?.cancel()
        gameLoopJob = null
    }

    /**
     * Klavye ve TV Kumandası Tuş Olaylarını İşler.
     */
    fun handleKeyEvent(event: KeyEvent): Boolean {
        val key = event.key
        val isDown = event.type == KeyEventType.KeyDown

        when (key) {
            Key.DirectionLeft, Key.NumPad4, Key.A -> {
                leftPressed = isDown
                updateHorizontalVelocity()
                return true
            }
            Key.DirectionRight, Key.NumPad6, Key.D -> {
                rightPressed = isDown
                updateHorizontalVelocity()
                return true
            }
            Key.DirectionUp, Key.DirectionCenter, Key.Enter, Key.Spacebar, Key.NumPad5 -> {
                if (isDown) {
                    jump()
                }
                return true
            }
        }
        return false
    }

    /**
     * Yatay Hızı (Velocity X) ve Karakterin Yönünü Günceller.
     */
    private fun updateHorizontalVelocity() {
        val speed = 7f // [Large-Screen Scaling] Slightly increased speed (7f) for large TV landscape traversal
        val vx = when {
            leftPressed && !rightPressed -> -speed
            rightPressed && !leftPressed -> speed
            else -> 0f
        }
        val facing = if (vx > 0) true else if (vx < 0) false else _playerState.value.facingRight
        _playerState.value = _playerState.value.copy(vx = vx, facingRight = facing)
    }

    /**
     * Zıplama (Jump) Eylemini Gerçekleştirir.
     */
    private fun jump() {
        val p = _playerState.value
        if (!p.isJumping) {
            _playerState.value = p.copy(vy = -16.5f, isJumping = true) // [Jump Height Tuning] Yükseltilmiş zıplama kuvveti (-16.5f) ile platformlara rahatça çıkış
        }
    }

    /**
     * Oyun Fizik Motoru Güncellemesi
     * Yerçekimi, çarpışmalar, düşme kontrolü, koleksiyon, varlık etkileşimleri ve kamera takibi.
     */
    private fun updatePhysics() {
        val p = _playerState.value
        val gravity = 0.6f // [Physics Parameter] Yerçekimi kuvveti sabiti (0.6f)

        var vy = p.vy + gravity
        var vx = p.vx
        var x = p.x + vx
        var y = p.y + vy
        var isJumping = p.isJumping

        // [Character & Object Scaling] Larger player dimensions for 16:9 Android TV viewing distances
        val playerWidth = 60f  // [Character & Object Scaling] Increased player width (60f)
        val playerHeight = 75f // [Character & Object Scaling] Increased player height (75f)

        // [Large-Screen Scaling] Expanded world boundary checks for 3000f width
        if (x < 0f) { x = 0f; vx = 0f }
        if (x > worldWidth - playerWidth) { x = worldWidth - playerWidth; vx = 0f }

        // [Collision Detection & Hazards] Platform Çarpışma ve Lav Tehlike Algılama Mantığı
        var hitLava = false
        var landedOnSolid = false

        // Katı platformları en üsttekinden başlayarak kontrol et
        for (plat in solidPlatforms) {
            val prevBottom = p.y + playerHeight
            val currentBottom = y + playerHeight
            val horizontalOverlap = x + playerWidth > plat.x && x < plat.x + plat.width

            if (horizontalOverlap) {
                val tolerance = maxOf(20f, vy + 4f)
                if (prevBottom <= plat.y + tolerance && currentBottom >= plat.y && vy >= 0f) {
                    y = plat.y - playerHeight
                    vy = 0f
                    isJumping = false
                    landedOnSolid = true
                    break
                }
            }
        }

        // Lav çukuru kontrolü: Oyuncu katı bir platforma basmadıysa ve lav seviyesine indiyse tehlike tetiklenir
        if (!landedOnSolid) {
            val currentBottom = y + playerHeight
            for (lava in lavaPits) {
                val horizontalOverlap = x + playerWidth > lava.x && x < lava.x + lava.width
                if (horizontalOverlap && currentBottom >= lava.y + 10f) {
                    hitLava = true
                    break
                }
            }
        }

        // [Lava Hazard & Fall Logic] Lav Çukuruna Düşme veya Boşluktan Aşağı Düşme Durumu
        if (hitLava || y > worldHeight + 50f) {
            val currentLives = _lives.value - 1
            _lives.value = currentLives
            _message.value = "Burçin lav çukuruna düştü! Can kaybedildi! 🔥"
            if (currentLives <= 0) {
                stop()
                onGameOverCallback()
                return
            } else {
                // [Respawn & Bouncy Jump] Can kaybettikten sonra güvenli başlangıç konumuna dönme, can azaltma ve elastik zıplama
                x = 150f
                y = 700f
                vx = 0f
                vy = -10f
                isJumping = true
            }
        }

        _playerState.value = p.copy(x = x, y = y, vx = vx, vy = vy, isJumping = isJumping)

        // [Camera Translation] Smooth camera tracking centering around player across 3000f landscape (assuming 1920x1080 viewport)
        val viewportWidth = 1920f
        val viewportHeight = 1080f
        val targetCamX = (x + playerWidth / 2f - viewportWidth / 2f).coerceIn(0f, maxOf(0f, worldWidth - viewportWidth))
        val targetCamY = (y + playerHeight / 2f - viewportHeight / 2f).coerceIn(0f, maxOf(0f, worldHeight - viewportHeight))

        // [Camera Translation] Linear interpolation (lerp) for smooth camera following without jitter
        _cameraX.value += (targetCamX - _cameraX.value) * 0.15f
        _cameraY.value += (targetCamY - _cameraY.value) * 0.15f

        // [Logic] Toplanabilir Objeler (Collectibles) Çarpışma ve Puan Mantığı
        val currentCollectibles = _collectibles.value
        var scoreDelta = 0
        val updatedCollectibles = currentCollectibles.map { c ->
            if (!c.collected) {
                val dist = kotlin.math.hypot((x + playerWidth/2) - (c.x + 20f), (y + playerHeight/2) - (c.y + 20f))
                if (dist < 55f) { // [Character & Object Scaling] Increased collection threshold (55f) for larger collectibles
                    scoreDelta += 150
                    c.copy(collected = true)
                } else c
            } else c
        }

        if (scoreDelta > 0) {
            _collectibles.value = updatedCollectibles
            _score.value = _score.value + scoreDelta

            // [Condition] Tüm objeler toplandıysa zafer (victory) durumuna geçiş
            if (updatedCollectibles.all { it.collected }) {
                stop()
                onVictoryCallback()
                return
            }
        }

        // [Logic] Yardımcı Varlıklar (Kedi ve Baykuş) Etkileşim Mantığı
        val currentEntities = _entities.value
        for (entity in currentEntities) {
            if (entity.isAlive) {
                val dist = kotlin.math.hypot((x + playerWidth/2) - (entity.x + 25f), (y + playerHeight/2) - (entity.y + 25f))
                if (dist < 60f) { // [Character & Object Scaling] Increased entity interaction threshold (60f)
                    val bonus = 300
                    _score.value = _score.value + bonus
                    val entityName = if (entity.type == EntityType.CAT) "Sihirli Kedi 🐾" else "Bilge Baykuş 🦉"
                    _message.value = "$entityName Burçin'e 300 Bonus Puan Hediye Etti! ✨"
                    _entities.value = currentEntities.map { if (it.id == entity.id) it.copy(isAlive = false) else it }
                }
            }
        }
    }
}

/**
 * =====================================================================
 * OYUN EKRANI (GameplayScreen.kt)
 * =====================================================================
 * Bu Composable fonksiyonu, oyun içi HUD ve Canvas üzerinde çizilen zengin
 * büyük ekran TV (16:9) görsel öğelerini ve kamera çevirisini barındırır.
 * =====================================================================
 */
@Composable
fun GameplayScreen(
    level: Int,                     // [Parameter] Oynanan mevcut bölüm numarası
    onGameOver: () -> Unit,         // [Parameter] Game over callback fonksiyonu
    onVictory: () -> Unit,          // [Parameter] Zafer (Victory) callback fonksiyonu
    onBackToMenu: () -> Unit        // [Parameter] Menüye dönme callback fonksiyonu
) {
    val coroutineScope = rememberCoroutineScope()
    val gameEngine = remember {
        GameEngine(
            onGameOverCallback = onGameOver,
            onVictoryCallback = onVictory
        )
    }

    val playerState by gameEngine.playerState.collectAsState()
    val cameraX by gameEngine.cameraX.collectAsState()
    val cameraY by gameEngine.cameraY.collectAsState()
    val score by gameEngine.score.collectAsState()
    val lives by gameEngine.lives.collectAsState()
    val message by gameEngine.message.collectAsState()
    val collectibles by gameEngine.collectibles.collectAsState()
    val entities by gameEngine.entities.collectAsState()

    val focusRequesterClean = remember { FocusRequester() }

    // [Animation] Canvas grafikleri için sonsuz animasyon zamanlayıcısı
    val infiniteTransition = rememberInfiniteTransition(label = "game_canvas_anim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // [Math] 2 * PI (Tam daire radyan döngüsü)
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animTime"
    )

    LaunchedEffect(Unit) {
        gameEngine.start(coroutineScope)
        focusRequesterClean.requestFocus()
    }

    DisposableEffect(Unit) {
        onDispose {
            gameEngine.stop()
        }
    }

    // [UI Layout] Kök Box container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)) // [Color] Oyun ana arkaplan rengi (Koyu lacivert)
            .focusable()
            .focusRequester(focusRequesterClean)
            .onKeyEvent { event ->
                gameEngine.handleKeyEvent(event)
            }
            .padding(24.dp) // [UI Property / Spacing] İç kenar boşlukları (24 dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // [UI Layout] TV-Optimize Edilmiş HUD (Üst Bilgi Çubuğu)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // [UI Component] Bölüm ve Can Gösterge Paneli Surface
                Surface(
                    color = Color(0xFF1F4068).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFFFE66D))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🌈 Bölüm $level",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFE66D)
                        )
                        Text(
                            text = "Can: ${"❤️".repeat(lives)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // [UI Component] Aktif Bilgi Mesajı Banner'ı
                AnimatedVisibility(
                    visible = message != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (message != null) {
                        Surface(
                            color = Color(0xFFFFE66D),
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 8.dp
                        ) {
                            Text(
                                text = message!!,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A2E)
                            )
                        }
                    }
                }

                // [UI Component] Skor Gösterge Paneli Surface
                Surface(
                    color = Color(0xFF1F4068).copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFFFE66D))
                ) {
                    Text(
                        text = "Skor: $score ✨",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFE66D)
                    )
                }
            }

            // [UI Component] Zengin Canvas Oyun Tahtası (Camera Translated Viewport)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFFFE66D).copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = gameEngine.worldWidth
                    val height = gameEngine.worldHeight

                    // [Camera Translation] Apply camera translation (-cameraX, -cameraY) to track the player across the large 3000f landscape
                    translate(left = -cameraX, top = -cameraY) {

                        // 1. Çok Duraklı Gökkuşağı Degrade Gökyüzü (Sky Brush spanning 3000f world width)
                        val skyBrush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF240046), // [Color] Koyu İndigo
                                Color(0xFF3C096C), // [Color] Mor
                                Color(0xFF560BAD), // [Color] Menekşe
                                Color(0xFF480CA8), // [Color] Mavi-Mor
                                Color(0xFF4CC9F0)  // [Color] Gökyüzü Camgöbeği
                            )
                        )
                        drawRect(brush = skyBrush, topLeft = Offset(0f, 0f), size = Size(width, height)) // [Large-Screen Scaling] Span entire 3000f x 1080f world

                        // 2. Parıldayan Yıldızlar (Twinkling Stars distributed across 3000f width)
                        val starPoints = listOf(
                            Offset(width * 0.05f, height * 0.12f),
                            Offset(width * 0.12f, height * 0.06f),
                            Offset(width * 0.20f, height * 0.18f),
                            Offset(width * 0.30f, height * 0.10f),
                            Offset(width * 0.40f, height * 0.15f),
                            Offset(width * 0.50f, height * 0.08f),
                            Offset(width * 0.60f, height * 0.22f),
                            Offset(width * 0.70f, height * 0.12f),
                            Offset(width * 0.80f, height * 0.16f),
                            Offset(width * 0.90f, height * 0.09f),
                            Offset(width * 0.96f, height * 0.20f)
                        )
                        starPoints.forEachIndexed { index, starPos ->
                            val alpha = (0.4f + 0.6f * kotlin.math.sin(animTime * 3f + index)).coerceIn(0.1f, 1f)
                            drawCircle(
                                color = Color(0xFFFFE66D).copy(alpha = alpha),
                                radius = 4f + (index % 2), // [Character & Object Scaling] Slightly larger stars for TV visibility
                                center = starPos
                            )
                        }

                        // 3. Paralaks Kayan Bulutlar (Parallax Floating Clouds spanning 3000f width)
                        val cloud1X = (animTime * 22f) % (width + 400f) - 200f
                        val cloud2X = (animTime * 14f + 600f) % (width + 500f) - 250f
                        val cloud3X = (animTime * 18f + 1400f) % (width + 450f) - 200f
                        drawCloud(cloud1X, height * 0.12f, 220f, 75f, Color.White.copy(alpha = 0.25f)) // [Large-Screen Scaling] Expanded cloud 1 across large world
                        drawCloud(cloud2X, height * 0.28f, 260f, 90f, Color.White.copy(alpha = 0.20f)) // [Large-Screen Scaling] Expanded cloud 2 across large world
                        drawCloud(cloud3X, height * 0.18f, 240f, 80f, Color.White.copy(alpha = 0.22f)) // [Large-Screen Scaling] Expanded cloud 3 across large world

                        // 4. Gökyüzündeki Animasyonlu Gökkuşağı Kemerleri (Multiple Rainbow Arcs across the landscape)
                        val rainbowColors = listOf(
                            Color(0xFFEF233C).copy(alpha = 0.35f),
                            Color(0xFFFB8500).copy(alpha = 0.35f),
                            Color(0xFFFFB703).copy(alpha = 0.35f),
                            Color(0xFF52B788).copy(alpha = 0.35f),
                            Color(0xFF4CC9F0).copy(alpha = 0.35f),
                            Color(0xFF4361EE).copy(alpha = 0.35f),
                            Color(0xFF7209B7).copy(alpha = 0.35f)
                        )
                        val arcCenters = listOf(width * 0.25f, width * 0.60f, width * 0.88f) // [Large-Screen Scaling] Multiple rainbow arc positions across 3000f width
                        arcCenters.forEach { arcCenterX ->
                            val arcRadiusBase = 450f
                            rainbowColors.forEachIndexed { index, color ->
                                val radius = arcRadiusBase + index * 18f + kotlin.math.sin(animTime * 0.5f + index * 0.3f) * 10f
                                drawArc(
                                    color = color,
                                    startAngle = 180f,
                                    sweepAngle = 180f,
                                    useCenter = false,
                                    topLeft = Offset(arcCenterX - radius, height * 0.1f - radius),
                                    size = Size(radius * 2f, radius * 2f),
                                    style = Stroke(width = 14f, cap = StrokeCap.Round) // [Large-Screen Scaling] Thicker rainbow stroke for large TV viewing
                                )
                            }
                        }

                        // 5. Farklı Katmanlardaki Zeminleri, Lav Çukurlarını ve Asılı Platformları Çizme
                        gameEngine.platforms.forEach { plat ->
                            if (plat.type == PlatformType.LAVA_PIT) {
                                // [Lava Pit Rendering] Gaps arasındaki parlak ateş ve lav çukurları
                                // 1. Lav çukurunun üstündeki boşluğa vuran sıcak parıltı (Heat Glow)
                                val glowGradient = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFFFF5400).copy(alpha = 0.35f)),
                                    startY = plat.y - 60f,
                                    endY = plat.y
                                )
                                drawRect(
                                    brush = glowGradient,
                                    topLeft = Offset(plat.x, plat.y - 60f),
                                    size = Size(plat.width, 60f)
                                )

                                // 2. Lav gövdesi (Çok katmanlı akkor magma vertical gradient)
                                val lavaGradient = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFF07C), // Sarı akkor yüzey (Sıcak ısı)
                                        Color(0xFFFF5400), // Canlı turuncu magma
                                        Color(0xFFD90429), // Derin kırmızı lav
                                        Color(0xFF4A0008)  // Derin volkanik taban
                                    ),
                                    startY = plat.y,
                                    endY = plat.y + plat.height
                                )
                                drawRoundRect(
                                    brush = lavaGradient,
                                    topLeft = Offset(plat.x, plat.y),
                                    size = Size(plat.width, plat.height),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                                )

                                // 3. Dalgalanan lav yüzeyi (Animasyonlu magma köpüğü çizgisi)
                                val waveOffset = kotlin.math.sin(animTime * 6f + plat.id * 1.5f) * 4f
                                drawRoundRect(
                                    color = Color(0xFFFFE66D).copy(alpha = 0.85f),
                                    topLeft = Offset(plat.x, plat.y + waveOffset),
                                    size = Size(plat.width, 8f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                                )

                                // 4. Lav kabarcıkları (Molten Bubbles)
                                for (i in 0 until 3) {
                                    val bubbleX = plat.x + (plat.width / 4f) * (i + 1) + kotlin.math.sin(animTime * 3.5f + i * 2f) * 12f
                                    val bubbleY = plat.y + 18f + ((animTime * 25f + i * 28f) % 45f)
                                    drawCircle(
                                        color = Color(0xFFFFF3B0).copy(alpha = 0.85f),
                                        radius = 4f + (i % 2) * 2f,
                                        center = Offset(bubbleX, bubbleY)
                                    )
                                }

                                // 5. Havaya yükselen lav kıvılcımları (Embers / Sparks)
                                for (i in 0 until 2) {
                                    val sparkX = plat.x + 20f + ((i * 55f + animTime * 30f) % plat.width)
                                    val sparkY = plat.y - 6f - ((animTime * 25f + i * 25f) % 40f)
                                    drawCircle(
                                        color = Color(0xFFFFB703).copy(alpha = 0.75f),
                                        radius = 2.5f,
                                        center = Offset(sparkX, sparkY)
                                    )
                                }
                            } else {
                                // Katı Platformlar: Zemin Katmanları veya Havada Asılı Platformlar
                                if (plat.height > 60f) {
                                    // [Multi-Layer Ground Tier] Farklı katmanlardaki derin zemin blokları
                                    // Katman C: En alt derin bazalt/kaya katmanı (Bedrock)
                                    val bedrockGradient = Brush.verticalGradient(
                                        colors = listOf(Color(0xFF281E3B), Color(0xFF140D22)),
                                        startY = plat.y + 70f,
                                        endY = plat.y + plat.height
                                    )
                                    drawRoundRect(
                                        brush = bedrockGradient,
                                        topLeft = Offset(plat.x, plat.y + 70f),
                                        size = Size(plat.width, plat.height - 70f),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                                    )

                                    // Katman B: Orta kristal/toprak tabakası (Strata)
                                    val strataGradient = Brush.verticalGradient(
                                        colors = listOf(Color(0xFF5A189A), Color(0xFF3C096C)),
                                        startY = plat.y + 24f,
                                        endY = plat.y + 70f
                                    )
                                    drawRect(
                                        brush = strataGradient,
                                        topLeft = Offset(plat.x, plat.y + 24f),
                                        size = Size(plat.width, 46f)
                                    )
                                    drawLine(
                                        color = Color(0xFF7B2CBF).copy(alpha = 0.7f),
                                        start = Offset(plat.x, plat.y + 46f),
                                        end = Offset(plat.x + plat.width, plat.y + 46f),
                                        strokeWidth = 3f
                                    )

                                    // Katman A: Üst altın zemin yüzeyi (Top Golden Crust)
                                    val capGradient = Brush.verticalGradient(
                                        colors = listOf(Color(0xFFFFE066), Color(0xFFFFB703)),
                                        startY = plat.y,
                                        endY = plat.y + 24f
                                    )
                                    drawRoundRect(
                                        brush = capGradient,
                                        topLeft = Offset(plat.x, plat.y),
                                        size = Size(plat.width, 24f),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                                    )
                                    // Üst parlama çizgisi
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.8f),
                                        start = Offset(plat.x + 8f, plat.y + 3f),
                                        end = Offset(plat.x + plat.width - 8f, plat.y + 3f),
                                        strokeWidth = 3f,
                                        cap = StrokeCap.Round
                                    )
                                    // Uçurum yan gölgeleri (Lava boşluğuna bakan dikey kenarlar)
                                    drawLine(
                                        color = Color.Black.copy(alpha = 0.45f),
                                        start = Offset(plat.x + 2f, plat.y),
                                        end = Offset(plat.x + 2f, plat.y + plat.height),
                                        strokeWidth = 4f
                                    )
                                    drawLine(
                                        color = Color.Black.copy(alpha = 0.45f),
                                        start = Offset(plat.x + plat.width - 2f, plat.y),
                                        end = Offset(plat.x + plat.width - 2f, plat.y + plat.height),
                                        strokeWidth = 4f
                                    )
                                } else {
                                    // [Floating Platforms] Havada asılı bulut, şeker veya altın köprüler
                                    when (plat.type) {
                                        PlatformType.RAINBOW_CLOUD -> {
                                            val cloudBrush = Brush.verticalGradient(
                                                colors = listOf(Color.White, Color(0xFFE2EAFc)),
                                                startY = plat.y,
                                                endY = plat.y + plat.height
                                            )
                                            drawRoundRect(
                                                brush = cloudBrush,
                                                topLeft = Offset(plat.x, plat.y),
                                                size = Size(plat.width, plat.height),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f, 15f)
                                            )
                                            // Bulut altı gökkuşağı parıltısı
                                            drawRoundRect(
                                                color = Color(0xFFC77DFF).copy(alpha = 0.4f),
                                                topLeft = Offset(plat.x + 10f, plat.y + plat.height - 6f),
                                                size = Size(plat.width - 20f, 4f),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                                            )
                                        }
                                        PlatformType.CANDY_LEDGE -> {
                                            val candyBrush = Brush.horizontalGradient(
                                                colors = listOf(Color(0xFFFF758F), Color(0xFFFF4D6D), Color(0xFFFF758F)),
                                                startX = plat.x,
                                                endX = plat.x + plat.width
                                            )
                                            drawRoundRect(
                                                brush = candyBrush,
                                                topLeft = Offset(plat.x, plat.y),
                                                size = Size(plat.width, plat.height),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                                            )
                                            // Şeker süsleme çizgisi
                                            drawLine(
                                                color = Color.White.copy(alpha = 0.7f),
                                                start = Offset(plat.x + 10f, plat.y + 5f),
                                                end = Offset(plat.x + plat.width - 10f, plat.y + 5f),
                                                strokeWidth = 3f,
                                                cap = StrokeCap.Round
                                            )
                                        }
                                        else -> {
                                            val blockBrush = Brush.verticalGradient(
                                                colors = listOf(Color(0xFFFFD166), Color(0xFFF77F00)),
                                                startY = plat.y,
                                                endY = plat.y + plat.height
                                            )
                                            drawRoundRect(
                                                brush = blockBrush,
                                                topLeft = Offset(plat.x, plat.y),
                                                size = Size(plat.width, plat.height),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 6. Toplanabilir Objeleri Çizme (Render Collectibles: Yıldız, Elmas, Kristal)
                        collectibles.forEach { c ->
                            if (!c.collected) {
                                val cx = c.x + 22f
                                val cy = c.y + 22f + kotlin.math.sin(animTime * 4f + c.id) * 6f
                                when (c.type) {
                                    CollectibleType.RAINBOW_STAR -> {
                                        drawCircle(Color(0xFFFFE66D), radius = 20f, center = Offset(cx, cy)) // [Character & Object Scaling] Larger star radius (20f) for TV
                                        drawCircle(Color.White, radius = 10f, center = Offset(cx, cy))       // [Character & Object Scaling] Larger inner glow (10f)
                                    }
                                    CollectibleType.MAGIC_DIAMOND -> {
                                        drawPath(Path().apply {
                                            moveTo(cx, cy - 20f)
                                            lineTo(cx + 18f, cy)
                                            lineTo(cx, cy + 20f)
                                            lineTo(cx - 18f, cy)
                                            close()
                                        }, color = Color(0xFF4CC9F0)) // [Character & Object Scaling] Larger diamond size for TV
                                    }
                                    CollectibleType.UNICORN_CRYSTAL -> {
                                        drawPath(Path().apply {
                                            moveTo(cx, cy - 22f)
                                            lineTo(cx + 15f, cy - 5f)
                                            lineTo(cx + 10f, cy + 20f)
                                            lineTo(cx - 10f, cy + 20f)
                                            lineTo(cx - 15f, cy - 5f)
                                            close()
                                        }, color = Color(0xFFB5179E)) // [Character & Object Scaling] Larger crystal size for TV
                                    }
                                }
                            }
                        }

                        // 7. Yardımcı Varlıkları Çizme (Kedi ve Baykuş)
                        entities.forEach { e ->
                            if (e.isAlive) {
                                if (e.type == EntityType.CAT) {
                                    drawCat(e.x, e.y, animTime)
                                } else {
                                    drawOwl(e.x, e.y, animTime)
                                }
                            }
                        }

                        // 8. Özel Karakter Avatarını Çizme (Burçin ve Sihirli Asası - Scaled up for TV)
                        drawBurcin(playerState.x, playerState.y, playerState.facingRight, animTime)
                    }
                }
            }

            // [UI Layout] TV Kumandası Tuş Rehberi & Hızlı Aksiyon Butonları Alt Paneli
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎮 Yön Tuşları: Büyük Dünyada Gezinme | OK / Boşluk: Zıpla",
                    fontSize = 14.sp,
                    color = Color(0xFFEAEAEA)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TvMenuButtonSmall(text = "❌ Game Over", onClick = onGameOver)
                    TvMenuButtonSmall(text = "🏆 Victory", onClick = onVictory)
                    TvMenuButtonSmall(text = "⬅️ Menü", onClick = onBackToMenu)
                }
            }
        }
    }
}

/**
 * Bulut Çizim Yardımcı Fonksiyonu (Canvas) - [Large-Screen Scaling]
 */
private fun DrawScope.drawCloud(x: Float, y: Float, width: Float, height: Float, color: Color) {
    drawOval(color = color, topLeft = Offset(x, y + height * 0.3f), size = Size(width * 0.6f, height * 0.7f))
    drawOval(color = color, topLeft = Offset(x + width * 0.3f, y), size = Size(width * 0.5f, height))
    drawOval(color = color, topLeft = Offset(x + width * 0.6f, y + height * 0.2f), size = Size(width * 0.4f, height * 0.8f))
}

/**
 * Burçin Karakteri Yön Yönetimi (Sağa/Sola Bakış) - [Character & Object Scaling]
 */
private fun DrawScope.drawBurcin(x: Float, y: Float, facingRight: Boolean, animTime: Float) {
    translate(x, y) {
        if (!facingRight) {
            scale(-1f, 1f, pivot = Offset(30f, 35f)) { // [Character & Object Scaling] Adjusted pivot for larger sprite (30f, 35f)
                drawBurcinBody()
            }
        } else {
            drawBurcinBody()
        }
    }
}

/**
 * Burçin Karakter Detaylı Çizimi - [Character & Object Scaling] Larger proportions for TV viewing
 */
private fun DrawScope.drawBurcinBody() {
    // Kafa (Gülümseyen yüz) - [Character & Object Scaling] Radius 20f (increased from 15f)
    drawCircle(color = Color(0xFFFFD166), radius = 20f, center = Offset(30f, 18f))

    // Saç (Kahverengi lüleler) - [Character & Object Scaling] Size 44f x 34f
    drawArc(
        color = Color(0xFF6B4226),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(8f, 2f),
        size = Size(44f, 34f)
    )

    // Gözler - [Character & Object Scaling] Radius 2.5f
    drawCircle(color = Color(0xFF1A1A2E), radius = 2.5f, center = Offset(24f, 17f))
    drawCircle(color = Color(0xFF1A1A2E), radius = 2.5f, center = Offset(36f, 17f))

    // Gülümseme (Ağız)
    drawArc(
        color = Color(0xFFD90429),
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(26f, 21f),
        size = Size(10f, 8f),
        style = Stroke(width = 2f)
    )

    // Sevimli Beyaz Tişört - [Character & Object Scaling] Width 22f, Height 18f
    drawRect(
        color = Color.White,
        topLeft = Offset(19f, 36f),
        size = Size(22f, 18f)
    )

    // Canlı Kırmızı Etek - [Character & Object Scaling] Scaled path for TV
    drawPath(
        path = Path().apply {
            moveTo(16f, 54f)
            lineTo(44f, 54f)
            lineTo(52f, 70f)
            lineTo(8f, 70f)
            close()
        },
        color = Color(0xFFEF233C)
    )

    // Ayaklar / Bacaklar - [Character & Object Scaling] Size 7f x 8f
    drawRect(Color(0xFF1A1A2E), topLeft = Offset(21f, 70f), size = Size(7f, 8f))
    drawRect(Color(0xFF1A1A2E), topLeft = Offset(32f, 70f), size = Size(7f, 8f))

    // Elde Tutulan Parıltılı Sihirli Asa (Wand) - [Character & Object Scaling] Scaled wand
    drawLine(
        color = Color(0xFF8D5B4C),
        start = Offset(41f, 40f),
        end = Offset(58f, 20f),
        strokeWidth = 3.5f
    )
    // Asanın Ucundaki Parlayan Yıldız
    drawCircle(
        color = Color(0xFFFFE66D),
        radius = 8f,
        center = Offset(58f, 18f)
    )
    drawCircle(
        color = Color.White,
        radius = 3.5f,
        center = Offset(58f, 18f)
    )
}

/**
 * Yardımcı Kedi (Cat) Çizimi (Canvas) - [Character & Object Scaling]
 */
private fun DrawScope.drawCat(x: Float, y: Float, animTime: Float) {
    translate(x, y) {
        drawOval(color = Color(0xFFFFB703), topLeft = Offset(4f, 18f), size = Size(44f, 32f))
        drawCircle(color = Color(0xFFFFB703), radius = 17f, center = Offset(26f, 14f))
        drawPath(Path().apply {
            moveTo(13f, 7f); lineTo(8f, -3f); lineTo(20f, 3f); close()
        }, color = Color(0xFFFFB703))
        drawPath(Path().apply {
            moveTo(33f, 3f); lineTo(45f, -3f); lineTo(40f, 7f); close()
        }, color = Color(0xFFFFB703))
        drawCircle(Color(0xFF1A1A2E), 2.5f, Offset(21f, 12f))
        drawCircle(Color(0xFF1A1A2E), 2.5f, Offset(31f, 12f))
    }
}

/**
 * Bilge Baykuş (Owl) Çizimi (Canvas) - [Character & Object Scaling]
 */
private fun DrawScope.drawOwl(x: Float, y: Float, animTime: Float) {
    translate(x, y) {
        drawOval(color = Color(0xFF6B705C), topLeft = Offset(6f, 12f), size = Size(38f, 40f))
        drawCircle(color = Color(0xFF4A4E46), radius = 19f, center = Offset(25f, 16f))
        drawCircle(color = Color(0xFFFFE66D), radius = 8f, center = Offset(19f, 15f))
        drawCircle(color = Color(0xFFFFE66D), radius = 8f, center = Offset(31f, 15f))
        drawCircle(Color(0xFF1A1A2E), 3.5f, Offset(19f, 15f))
        drawCircle(Color(0xFF1A1A2E), 3.5f, Offset(31f, 15f))
        drawPath(Path().apply {
            moveTo(25f, 19f); lineTo(21f, 25f); lineTo(29f, 25f); close()
        }, color = Color(0xFFFB8500))
    }
}

/**
 * Küçük Menü Butonu Composable (Game Screen alt paneli için)
 */
@Composable
fun TvMenuButtonSmall(
    text: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale = if (isFocused) 1.05f else 1.0f
    val backgroundColor = if (isFocused) Color(0xFFFFE66D) else Color(0xFF1F4068)
    val contentColor = if (isFocused) Color(0xFF1A1A2E) else Color.White

    Button(
        onClick = onClick,
        modifier = Modifier
            .height(44.dp)
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isFocused) 8.dp else 2.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
