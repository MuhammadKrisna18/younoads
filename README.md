# YounoAds

> Pemutar Video Android Modern Bebas Iklan dengan Efisiensi Rekomposisi Tinggi dan Pemutaran Latar Belakang.

---

## Ringkasan Proyek

YounoAds adalah aplikasi pemutar video native Android yang dikembangkan sebagai proyek portofolio rekayasa perangkat lunak mobile. Aplikasi ini berfokus pada performa tinggi, efisiensi sumber daya, antarmuka modern berbasis Jetpack Compose, serta kenyamanan menonton tanpa jeda iklan video.

Aplikasi dirancang agar dapat langsung beroperasi tanpa mewajibkan autentikasi akun Google atau langganan berbayar, menjaga privasi pengguna tetap berada di level perangkat lokal (on-device).

---

## Fitur Pengguna

### 1. Pemutaran Video Bebas Iklan
Menyajikan konten video secara langsung dari awal hingga akhir tanpa interupsi jeda iklan video, baik untuk video berdurasi pendek maupun panjang.

### 2. Pemutaran Latar Belakang (Background Playback)
Audio video tetap berjalan secara berkelanjutan ketika layar ponsel dimatikan atau saat pengguna beralih ke aplikasi lain (seperti aplikasi perpesanan atau peramban). Terintegrasi dengan kontrol media di notification shade dan lockscreen.

### 3. Jendela Mengambang (In-App & System Picture-in-Picture)
- **In-App Floating Player**: Mini player mengambang di dalam aplikasi dengan kontrol gestur halus, memungkinkan navigasi feed sambil tetap menonton video.
- **System PiP**: Mendukung mode Picture-in-Picture native Android saat keluar dari aplikasi.

### 4. Ekstraksi Fallback Otomatis untuk Video Terbatas (Age-Restricted 18+ Bypass)
Mengintegrasikan resolver sekunder berbasis public Piped & Invidious API ketika video terkena batasan usia (Age-Restricted / 18+) atau pembatasan autentikasi YouTube. Alur fallback berjalan secara otomatis di latar belakang tanpa mengharuskan pengguna melakukan login atau registrasi akun.

### 5. Kontrol Interaktif dan Gestur
- **Double-Tap to Seek & Ketukan Akumulatif Cepat**: Ketukan ganda di sisi kanan atau kiri pemutar untuk melompat maju atau mundur sesuai interval yang dikonfigurasi (5, 10, 15, atau 20 detik). Dilengkapi mekanisme ketukan berulang akumulatif (misalnya interval 5 detik: double-tap = 10 detik, triple-tap = 15 detik, quad-tap = 20 detik) dengan teks umpan balik visual dinamis dan feedback haptik instan.
- **Interactive Scrubber**: Kontrol garis waktu video presisi dengan visual thumb yang responsif.
- **Feedback Haptik & Animasi Membal**: Responsivitas taktil pada tombol aksi, navigasi, dan kontrol.

### 6. Pencarian Cerdas & Riwayat Lokal
Pencarian video dan saluran dengan riwayat kata kunci lokal. Riwayat pencarian dapat dikelola atau dihapus kapan saja secara mandiri oleh pengguna.

### 7. Riwayat Tontonan & Auto Resume
Menyimpan riwayat video yang ditonton ke dalam basis data lokal dengan posisi detik terakhir, memungkinkan pengguna melanjutkan tontonan kapan saja dengan satu ketukan.

### 8. Halaman Saluran Kreator
Menampilkan profil kreator, banner saluran, serta daftar video yang dapat diurutkan berdasarkan tanggal terbaru atau popularitas.

### 9. Pembagian Tautan di Menit Tertentu
Mendukung penyalinan dan pembagian tautan video dengan parameter stempel waktu (timestamp) spesifik (`?t=XXs`).

### 10. Konfigurasi Resolusi dan Kecepatan Pemutaran
Pilihan resolusi adaptif (mulai dari resolusi hemat kuota hingga Full HD) dan pengatur kecepatan pemutaran (0.25x hingga 2.0x).

### 11. Desain Tema Cyberpunk Neo-Tokyo & Maskot Anime ("Youno-chan")
- **Estetika Neo-Tokyo AMOLED**: Latar belakang hitam pekat murni (Pure Void Black `#08080C`) dengan aksen pendaran Electric Cyan (`#00F0FF`) dan Neon Magenta (`#FF2A85`) yang hemat daya OLED dan futuristik.
- **Holographic Watermark Backdrop di Semua Halaman**: Latar belakang karakter anime maskot "Youno-chan" dalam format 9:16 vertikal (`youno_backdrop.webp`) dengan opasitas hologram jernih (~35%) dipadukan dengan gradien vignette gelap di seluruh halaman (Beranda, Riwayat, Saluran, Playlist, dan Pemutar Video).
- **Maskot Resmi Bebas Lisensi (Youno-chan)**: Kehadiran karakter anime cewek bertema cyberpunk dengan headphone audio futuristik pada header brand dan indikator status layar.
- **Empty States Berkarakter**: Halaman riwayat tontonan kosong dan pencarian tidak ditemukan diperkaya ilustrasi ekspresif Youno-chan dalam format WebP teroptimasi.
- **Scrubber Spektral**: Garis waktu video menggunakan gradien horizontal neon yang presisi dan responsif terhadap interaksi pengguna.

### 12. Pembaruan Aplikasi Mandiri (In-App Updater)
Pendeteksian rilis versi baru secara otomatis melalui GitHub Releases dengan antarmuka unduh dan pasang langsung.

### 13. Playlist Resmi Saluran & Antrean Pemutaran (Official Playlists & Auto-Next)
- **Tab Playlist Saluran**: Halaman saluran kreator menyediakan tab khusus untuk menjelajahi seluruh daftar putar (playlist) resmi yang dipublikasikan oleh kreator terkait.
- **Tampilan Detail Playlist**: Menampilkan visual cover thumbnail resolusi tinggi, informasi total video, tombol aksi "Putar Semua" dan "Acak", serta daftar trek berurutan.
- **Penemuan di Hasil Pencarian (Search Discovery & Filter)**: Playlist resmi YouTube tampil berdampingan dengan video pada hasil pencarian, lengkap dengan filter chip interaktif ("Semua", "Video", "Playlist") untuk penelusuran terfokus.
- **Rekomendasi Cerdas di Beranda (Personalized Home Feed)**: Beranda ("Semua") secara adaptif menampilkan kartu playlist resmi dari saluran kreator yang sering ditonton atau playlist yang pernah ditonton sebelumnya, diselipkan secara harmonis di antara video rekomendasi.
- **Antrean Pemutar Interaktif (In-Player Queue Banner)**: Komponen banner expandable di bawah layar pemutar video yang menampilkan posisi video aktif, judul playlist, serta daftar trek yang dapat dipilih secara instan.
- **Pemutaran Berkelanjutan (Auto-Next)**: Video berikutnya dalam daftar putar akan otomatis diputar saat video saat ini selesai diputar.
- **Kontrol Navigasi Trek**: Tombol Lewati Berikutnya (Next) dan Lewati Sebelumnya (Previous) terintegrasi pada kontrol pemutar video saat playlist aktif.

### 14. Pemilih Trek Audio & Sulih Suara (Multi-Language Audio Track Selector)
- **Prioritas Cerdas Suara Asli (Original Audio Priority)**: Sistem secara cerdas memprioritaskan trek audio asli (`ORIGINAL`) saat video diputar, mencegah video kreator Indonesia tertukar secara otomatis dengan dubbing AI/Bahasa Inggris.
- **Dukungan Multi-Language Track YouTube**: Mengekstraksi seluruh varian audio (asli, dubbing sulih suara, deskriptif) dari YouTube backend dan mengelompokkannya secara terstruktur.
- **Dialog Pemilih Trek Cyberpunk**: Antarmuka dialog interaktif untuk beralih bahasa audio yang dapat diakses langsung dari kontrol pemutar video dan tombol aksi cepat (*action pills*).
- **Transisi Audio Mulus**: Beralih antar bahasa audio secara instan di latar belakang tanpa me-reset posisi pemutaran video aktif.

### 15. Pemutaran Otomatis (Autoplay) & Pengulangan Video Tunggal (Loop)
- **Autoplay dengan Transisi Hitung Mundur 3 Detik**: Saat video yang sedang diputar selesai (`STATE_ENDED`), aplikasi memulai hitung mundur 3 detik dengan banner transisi mengambang (*floating banner*) yang menampilkan judul video berikutnya, tombol "Putar Sekarang", dan tombol "Batal". Video berikutnya diambil secara otomatis dari playlist aktif atau video terkait pertama (*related video*).
- **Kontrol Cepat di Overlay & Bottom Sheet**: Status Autoplay dapat dinyalakan atau dimatikan secara instan melalui ikon *quick toggle* di sudut kanan atas pemutar video (`PlayerControlsOverlay`) maupun sakelar toggle switch di dalam `PlayerSettingsBottomSheet`.
- **Fitur Loop Video Tunggal**: Memungkinkan pemutaran ulang video yang sama secara terus-menerus tanpa henti. Fitur Loop memiliki prioritas lebih tinggi daripada Autoplay; ketika Loop aktif, video langsung diputar ulang dari detik ke-0 tanpa memicu hitung mundur transisi ke video lain.
- **Penyimpanan Preferensi Lokal (SharedPreferences)**: Preferensi status Autoplay dan Loop disimpan secara persisten di penyimpanan lokal perangkat melalui `PlayerPreferences`, sehingga konfigurasi pengguna tetap terjaga saat aplikasi ditutup dan dibuka kembali.

### 16. Lembar Pengaturan Pemutar Terpadu ala YouTube (Player Settings Bottom Sheet)
- **Konsolidasi Kontrol Pemutar**: Mengelompokkan seluruh opsi konfigurasi video (Kualitas Resolusi, Kecepatan Pemutaran, Interval Lompatan Cepat, Trek Audio, Pengulangan Loop, dan Autoplay) ke dalam satu lembar modal (*Modal Bottom Sheet*) terpadu bergaya YouTube native.
- **Akses Cepat Ganda**: Dapat diakses melalui ikon roda gigi (Settings) di sudut kanan atas pemutar video maupun melalui pil aksi "Pengaturan" di bawah judul video.
- **Konfigurasi Interval Lompatan Seek (5, 10, 15, 20 detik)**: Pilihan interval lompatan cepat ketuk ganda langsung dari lembar pengaturan melalui modal `SeekIntervalSelectionDialog` dengan penanda centang aktif, disimpan secara persisten di penyimpanan lokal perangkat.
- **Komponen Switch Interaktif**: Fitur Loop video dan Autoplay disematkan dengan sakelar toggle `Switch` Material 3 beraksen neon Electric Cyan yang dapat dinyalakan/dimatikan secara instan dengan satu sentuhan.
- **Dialog Pemilih Kecepatan Eksplisit**: Menggantikan siklus kecepatan manual dengan dialog `PlaybackSpeedSelectionDialog` yang menyajikan seluruh opsi (0.5x, 0.75x, Normal 1.0x, 1.25x, 1.5x, 2.0x) lengkap dengan penanda centang aktif.
- **Perampingan Baris Aksi Bebas Bloatware**: Membersihkan tombol "Suka" dan "Subscribe" semu untuk menjaga arsitektur lokal murni tanpa akun, menyisakan baris pil aksi yang esensial, ringkas, dan bebas gulir panjang (`Bagikan`, `Salin`, `Simpan`, `Pengaturan`, `PiP`, `Layar Penuh`).

---

## Informasi & Spesifikasi Teknis

### Arsitektur Sistem

Proyek ini menerapkan prinsip Clean Architecture yang dipadukan dengan pola arsitektur **MVVM (Model-View-ViewModel)** dan paradigma **Unidirectional Data Flow (UDF)**:

1. **Presentation Layer (UI)**:
   - Dibangun sepenuhnya secara deklaratif menggunakan **Jetpack Compose**.
   - Mengonsumsi data reaktif melalui `StateFlow` dari ViewModel dengan siklus hidup terkelola (`collectAsStateWithLifecycle`).
   - Menerapkan modularisasi komponen reusable (Pill Buttons, Dialogs, Sliders).

2. **Domain & ViewModel Layer**:
   - Menangani logika bisnis, manajemen status pemutaran ExoPlayer, navigasi state, dan sinkronisasi durasi/posisi tontonan.
   - Mengisolasi penanganan mutasi data dari representasi visual.

3. **Data Layer**:
   - **Local Storage**: Basis data SQLite lokal menggunakan **Room Database** untuk persistence riwayat tontonan, video tersimpan, dan riwayat pencarian.
   - **Extractor Engine**: Menggunakan **NewPipeExtractor** untuk parsing metadata video, aliran stream audio/video, feed saluran, dan saran pencarian tanpa API keys eksternal.

---

### Tech Stack & Dependensi Utama

| Kategori | Teknologi / Pustaka |
| :--- | :--- |
| Bahasa Pemrograman | Kotlin 2.0+ (JVM 17 Target) |
| UI Framework | Jetpack Compose, Material 3, Accompanist System UI Controller |
| Player Engine | AndroidX Media3 (ExoPlayer 1.3.1) & MediaSession |
| Concurrency | Kotlin Coroutines & Asynchronous Flow (`StateFlow`, `SharedFlow`) |
| Local Database | AndroidX Room 2.6.1 (SQLite ORM) |
| Image Loading | Coil Compose 2.6.0 |
| Metadata Extraction | NewPipeExtractor 0.27.1 |
| Dependency Management | Gradle Kotlin DSL dengan Version Catalog / Plugins Modern |
| Build Tooling | Android Gradle Plugin 8.3+, R8 / ProGuard Optimization |

---

### Sorotan Implementasi Rekayasa (Engineering Highlights)

#### 1. Isolasi Rekomposisi & Lazy Virtualization
- **State Isolation**: Pembaruan posisi waktu pemutar (`currentPositionMs`) yang berdetak dengan interval 100-200 milidetik diisolasi secara ketat ke dalam sub-komponen `VideoPlayerSection`. Hal ini mencegah rekomposisi massal yang tidak perlu pada metadata video, tombol aksi, dan daftar video terkait.
- **Virtualisasi LazyColumn**: Daftar rekomendasi video terkait menggunakan `LazyColumn` dengan kunci stabil (`key = item.url`), memangkas alokasi memori dan menjaga rendering tetap mulus pada 60/120 FPS.

#### 2. In-App Floating PiP dengan Animasi Fisik
Mini-player di dalam aplikasi memanfaatkan `pointerInput` untuk mendeteksi gestur drag horizontal dan vertikal. Nilai perpindahan dianimasikan secara mulus menggunakan `androidx.compose.animation.core.Animatable`. Ditambahkan kurva fading transparansi (`graphicsLayer { alpha }`) berbasis jarak geser untuk memberikan efek pelepasan (dismiss) yang natural.

#### 3. Auto-Fullscreen & Sensor Immersive Mode
Deteksi orientasi perangkat fisik secara dinamis melalui `LocalConfiguration.current.orientation`. Saat perangkat diputar ke mode landscape, tampilan video otomatis meluas ke seluruh layar (`Modifier.fillMaxSize()`) dan mengaktifkan mode imersif (`WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`). Handler tombol kembali dan tombol keluar layar penuh mengembalikan orientasi ke mode potret tanpa mengunci sensor fisik sistem.

#### 4. Background Playback via MediaSessionService
Playback audio di latar belakang dikelola menggunakan service foreground yang terikat pada `MediaSessionService`. Layanan ini menangani kontrol hardware (tombol headset, Bluetooth audio, keyboard media keys) dan mempertahankan sesi saat aplikasi berada dalam keadaan paused atau stopped.

#### 5. Keamanan Data & Manajemen Duplikasi
Input pencarian dan caching saran pencarian difilter secara ketat dengan operator deduplikasi (`.distinct()`), mencegah exception kunci duplikat pada komponen Compose (`IllegalArgumentException: Key already exists`).

#### 6. Pipeline CI/CD Otomatis
- **Release Automation** (`build-release.yml`): Setiap push tag versi (`v*`) secara otomatis memicu kompilasi release APK ter-sign di runner Ubuntu GitHub Actions dan mempublikasikan file binary APK ke GitHub Releases.
- **Upstream Engine Checker** (`auto-check-newpipe.yml`): Pengecekan otomatis berkala terhadap update NewPipeExtractor di upstream repository untuk menjaga kompatibilitas parsing terhadap perubahan YouTube backend.

#### 7. Ekstraksi Playlist Saluran, Caching LRU & Manajemen Antrean ExoPlayer
- **Ekstraksi Tab Saluran**: Menggunakan `ChannelTabExtractor` dengan parameter `ChannelTabs.PLAYLISTS` dari NewPipeExtractor untuk menarik katalog playlist resmi dari halaman saluran kreator secara mandiri.
- **Pencarian Terpadu & Filter Konten**: Mengintegrasikan `YoutubeSearchQueryHandlerFactory.PLAYLISTS` dan `YoutubeSearchQueryHandlerFactory.VIDEOS` sehingga hasil pencarian dapat menyajikan video dan playlist dalam hierarki polimorfik `FeedItem` dengan filter chip dinamis.
- **Rekomendasi Beranda Adaptif**: Menghubungkan frekuensi riwayat tontonan kreator dan tabel `watched_playlists` SQLite ke algoritma `getPersonalizedFeed`, secara cerdas menyisipkan kartu playlist relevan ke dalam antrean beranda pengguna.
- **Two-Tier LRU Caching**: Implementasi cache berbasis `LruCache` pada lapisan repositori (`channelPlaylistsCache` dan `playlistDetailsCache`) guna memangkas latensi jaringan dan waktu respon saat berpindah antar tab saluran atau membuka kembali playlist yang sama.
- **PlaylistContext & State Transition**: Status antrean dikelola dalam immutable data model `PlaylistContext` pada `PlaybackManager`. Transisi trek otomatis diintegrasikan langsung ke lifecycle listener ExoPlayer (`onPlaybackStateChanged`), memicu `playNextInPlaylist()` tanpa beban memori berlebih saat pemutaran mencapai `Player.STATE_ENDED`.
- **Dukungan Pengacakan Trek (Shuffle)**: Mekanisme pengacakan urutan trek saat aksi "Acak" dijalankan dengan tetap mempertahankan konsistensi referensi URL dan metadata orisinal.

#### 8. Sistem Desain Cyberpunk Neon & Manajemen Aset Maskot WebP
- **Palet Warna Spektral Berkontras Tinggi**: Menerapkan arsitektur tema Material 3 terpersonalisasi dengan token warna `NeonMagenta` (`#FF2A85`), `ElectricCyan` (`#00F0FF`), dan `CyberViolet` (`#9D4EDD`) di atas kanvas hitam pekat (`#08080C`), memaksimalkan efisiensi rasio kontras visual dan masa pakai baterai layar OLED/AMOLED.
- **Integrasi Maskot Bebas Lisensi ("Youno-chan")**: Menyematkan ilustrasi maskot dalam format WebP dengan kompresi multi-level ke dalam komponen `BrandLogo`, status pencarian kosong (`youno_search_empty`), status riwayat kosong (`youno_empty_history`), dan latar belakang aplikasi global (`youno_backdrop.webp`) tanpa memperbesar ukuran binary APK.
- **Aksen Neon Spektral Real-Time**: Merombak kalkulasi gradient pada `InteractiveSeekBar` sehingga transisi seekbar memancarkan aksen gradien neon cyberpunk yang halus.

#### 9. Arsitektur CyberpunkAppBackground & Holographic Backdrop Layering
- **Centralized Wrapper Layering**: Mengimplementasikan komponen pembungkus terpusat `CyberpunkAppBackground` pada hierarki root aplikasi di `MainActivity`. Komponen ini menyatukan kanvas dasar `DarkBackground`, layer gambar vektor/bitmap WebP dengan opasitas hologram jernih (~35%), gradien vertikal quad-stop untuk proteksi kontras status bar dan bottom bar, serta vignette radial halus.
- **Transparansi Selektif pada Scaffolding**: Seluruh kontainer halaman (`FeedScreen`, `HistoryScreen`, `ChannelScreen`, `PlaylistScreen`, dan bagian bawah `PlayerScreen`) dikonfigurasi menggunakan `containerColor = Color.Transparent`, memungkinkan efek parallax halus saat daftar video digulir di atas latar belakang watermark yang statis tanpa penalti beban rendering grafis tambahan.
- **Isolasi Pemutar Video**: Area pemutaran video (`VideoPlayerSection`) tetap mempertahankan latar belakang hitam absolut (`Color.Black`) guna menjamin rasio kontras pemutaran video 16:9 dan mode layar penuh (fullscreen) tanpa distorsi visual.

#### 10. Manajemen Multi-Track Audio & MergingMediaSource Dynamic Swapping
- **Ekstraksi & Deduplikasi AudioStream**: Mengelompokkan aliran audio berdasarkan kombinasi `audioTrackId`, `audioTrackName`, dan `audioLocale`. Menyeleksi bitrate dan format M4A terbaik untuk tiap bahasa tanpa duplikasi itag.
- **Smart Priority Resolution**: Mengimplementasikan algoritma komparasi `compareByDescending { it.isOriginal }.thenByDescending { it.languageCode == defaultLocale }` untuk memastikan trek audio asli selalu menjadi default pertama.
- **Dynamic MergingMediaSource Injection**: Memodifikasi pembuat media source pada `PlaybackManager` sehingga saat pengguna memilih trek audio baru, ExoPlayer me-rebuild `MergingMediaSource` dengan aliran audio yang baru dan melakukan seek instan ke posisi detik terakhir tanpa interupsi visual.

#### 11. Modular Clean Architecture Refactoring (Single Responsibility Principle)
- **Modularisasi Playback Layer**: Pemecahan `PlaybackManager` dengan mendelegasikan perakitan sumber media adaptif (HLS, Progressive, dan dynamic audio track injection) ke `MediaSourceBuilder`, serta pengelolaan antrean daftar putar (playlist queue traversal & cursor state) ke `PlaybackQueueManager`. `PlaybackManager` berperan murni sebagai orkestrator kontrol pemutar dan penyedia `StateFlow` reaktif ke UI.
- **Dekomposisi God-Class Repository**: Dekomposisi `NewPipeVideoRepository` dari 695 baris menjadi 58 baris dengan memisahkan 5 domain ekstraksi ke dalam paket `extractor/` (`StreamExtractionHelper`, `SearchExtractionHelper`, `ChannelExtractionHelper`, `PlaylistExtractionHelper`, `FeedExtractionHelper`, dan `ExtractorMappers`). `NewPipeVideoRepository` bertindak sebagai Clean Facade yang mempertahankan backward compatibility 100% tanpa mengubah kontrak pemanggil pada seluruh ViewModel.
- **Pemisahan Database Infrastructure**: Pemisahan infrastruktur DDL SQLite dan manajemen skema versi (`WatchHistoryDbHelper`) dari operasi repositori bisnis (`SqliteWatchHistoryRepository`), memperjelas batas tanggung jawab antara layer database engine dengan layer logika repositori aplikasi.
- **Dekomposisi Monolithic Compose Screens (Frontend Modularization)**: Memecah seluruh screen monolitik yang membengkak (500-700 baris) menjadi sub-komponen terisolasi dalam sub-paket `components/` untuk setiap domain layar:
  - `FeedScreen`: Didekomposisi dari 706 baris menjadi sub-komponen `FeedTopBar`, `FeedFilterRow`, `FeedSearchSuggestions`, `FeedStateViews`, dan `FeedContentList`.
  - `ChannelScreen`: Didekomposisi dari 621 baris menjadi sub-komponen `ChannelTopBar`, `ChannelHeaderSection`, `ChannelTabNavigation`, `ChannelStateViews`, dan `ChannelContent`.
  - `PlaylistScreen`: Didekomposisi dari 469 baris menjadi `PlaylistHeaderSection`, `PlaylistItemRow`, `PlaylistStateViews`, dan `PlaylistContent`.
  - `HistoryScreen`: Didekomposisi dari 314 baris menjadi `HistoryItemRow` dan `HistoryEmptyState`.
  - `PlayerScreen`: Didekomposisi dari 610 baris menjadi `VideoPlayerSection` dan `PlayerDetailContent`.
  Arsitektur ini memaksimalkan kemampuan smart skipping pada Compose compiler, membatasi lingkup rekomposisi hanya pada node UI yang mengalami mutasi state, serta meningkatkan keterbacaan kode secara drastis.
- **Zero Comments & Pure Code Readability**: Seluruh basis kode Kotlin ditulis dengan pendekatan self-documenting code yang rapi, deklaratif, dan 100% bebas dari baris komentar.

#### 12. Fallback Multi-Tier Resolver untuk Konten Terbatas (Age-Restricted Fallback Engine)
- **Seamless Fallback**: Mengimplementasikan `PipedFallbackExtractor` di dalam paket `extractor/` yang secara cerdas menangkap kegagalan ekstraksi (seperti `AgeRestrictedContentException` atau `ContentNotAvailableException` dari NewPipeExtractor).
- **Multi-Instance Redundancy**: Mendukung failover dinamis ke multi-instance publik Piped dan Invidious (`api.piped.private.coffee`, `pipedapi.ducks.party`, `yt.chocolatemoo53.com`) untuk menjamin ketersediaan video 18+ tanpa memerlukan login, kredensial pengguna, atau autentikasi OAuth pada perangkat.

#### 13. State Engine Pemutaran Berkelanjutan: Autoplay Countdown & Loop Priority Scheduling
- **Logika Prioritas State**: Mengatur penjadwalan saat siklus hidup pemutaran ExoPlayer mencapai status `Player.STATE_ENDED`. Evaluasi dilakukan dengan prioritas ketat: jika `isLoopEnabled` bernilai true, pemutar langsung me-replay video aktif dari detik ke-0 (`seekTo(0)` dan `play()`). Jika Loop nonaktif dan `isAutoplayEnabled` bernilai true, coroutine job hitung mundur 3 detik diaktifkan untuk mengeksekusi transisi video berikutnya.
- **Non-Destructive Queue Peeking**: Metode `peekNextTarget()` pada `PlaybackQueueManager` membaca kandidat video berikutnya dari antrean playlist tanpa memajukan kursor antrean sebelum hitung mundur selesai atau dikonfirmasi oleh pengguna.
- **Reactive Cancellation Flow**: Menjamin pembatalan hitung mundur secara reaktif saat terjadi interaksi pengguna (seek, pause, pemilihan video manual, atau penekanan tombol "Batal") untuk mencegah pergantian video yang tidak diinginkan.
- **Modular UI Decoupling**: Menghadirkan komponen UI mandiri `AutoplayCountdownBanner` yang terisolasi dari kontrol scrubber dan overlay, mematuhi prinsip Single Responsibility Principle (SRP) serta memaksimalkan efisiensi kompilasi Compose.
- **Persistensi Preferensi Tanpa Overhead**: Menggunakan `PlayerPreferences` berbasis `SharedPreferences` terisolasi untuk menyimpan status sakelar Autoplay dan Loop secara asinkron, memastikan state konsisten di seluruh sesi penggunaan aplikasi.

#### 14. Konsolidasi Arsitektur Pengaturan Pemutar (YouTube-Style Settings Modal Bottom Sheet)
- **Desain Terpadu & Eliminasi Bloatware Baris Aksi**: Mengelompokkan seluruh opsi pengaturan video ke dalam `PlayerSettingsBottomSheet` dan dialog pemilih kecepatan terpisah `PlaybackSpeedSelectionDialog`. Langkah ini mengeliminasi baris pil aksi yang sebelumnya penuh sesak dan panjang menjadi ringkas dan esensial (`Bagikan`, `Salin`, `Simpan`, `Pengaturan`, `PiP`, `Layar Penuh`).
- **Pembersihan Fitur Pseudo-Account**: Menghilangkan state in-memory `isLiked` dan `isSubscribed` dari `PlayerViewModel` serta komponen visual `PlayerChannelHeader` dan `PlayerActionPillRow`. Refactoring ini menegaskan arsitektur aplikasi sebagai klien lokal murni tanpa akun (no-login) yang hanya mengandalkan fitur persistensi nyata (penyimpanan riwayat dan bookmark SQLite).
- **Alur Interaksi Berjenjang (Sheet-to-Dialog Navigation)**: Menghubungkan transisi mulus dari lembar modal pengaturan ke dialog resolusi, kecepatan, dan trek audio tanpa konflik state atau kebocoran memori.

#### 15. Engine Seek Akumulatif & Intersepsi Gestur Ketukan Berulang (Accumulative Rapid Seeking Engine)
- **Mekanisme Akumulasi Cepat (Rapid Chaining)**: Menghubungkan gestur ketuk ganda dan ketukan berulang (triple-tap, quad-tap, dst.) dengan jendela waktu toleransi 800 milidetik. Setiap ketukan berturut-turut pada arah yang sama mengakumulasikan durasi seek secara dinamis berbasis preferensi pengguna (`seekIntervalSeconds`).
- **Intersepsi Gestur Tanpa Konflik Kontrol (Tap Interceptor)**: Mengintegrasikan fungsi penangkap ketukan `handleTap(isForward)` pada `onTap` di dalam `detectTapGestures`. Jika terdeteksi ketukan berulang setelah double-tap aktif, ketukan diterjemahkan sebagai penambahan seek dan memicu umpan balik haptik tanpa memicu pergantian visibilitas overlay kontrol pemutar (`toggleControls`).
- **Umpan Balik Visual Dinamis**: Teks overlay indikator seek menampilkan akumulasi total detik secara real-time (`+15 detik`, `+20 detik`, dst.) dan otomatis mereset akumulator saat batas waktu berakhir atau pemutaran berpindah.
- **Penyimpanan Preferensi Terisolasi**: Nilai interval seek tersimpan secara persisten pada `PlayerPreferences` via SharedPreferences, sehingga preferensi pengguna tetap dipertahankan saat aplikasi dimulai ulang.

---

## Struktur Direktori Proyek

```
app/src/main/kotlin/com/example/yoouutt/
|-- data/
|   |-- local/           # SQLite Database, PlayerPreferences (Autoplay, Loop, Seek Interval), Repositori Riwayat & Pencarian
|   |-- model/           # Data classes & domain models (StreamData, VideoItem, PlaylistData, FeedItem)
|   |-- repository/      # Abstraksi repositori data (VideoRepository, NewPipeVideoRepository Facade)
|   |   |-- extractor/   # Modular extraction helpers (Stream, Search, Channel, Playlist, Feed, Mappers, PipedFallback)
|-- di/                  # Dependency Injection container (AppContainer, DefaultAppContainer)
|-- playback/            # PlaybackManager, MediaSourceBuilder, PlaybackQueueManager, PlaybackService
|-- ui/
|   |-- channel/         # Profil saluran & routing ChannelScreen
|   |   |-- components/  # ChannelTopBar, ChannelHeaderSection, ChannelTabNavigation, ChannelStateViews, ChannelContent
|   |-- common/          # Komponen UI global (AppConfirmDialog, ViewModels)
|   |-- feed/            # Beranda utama & routing FeedScreen
|   |   |-- components/  # FeedTopBar, FeedFilterRow, FeedSearchSuggestions, FeedStateViews, FeedContentList
|   |-- history/         # Riwayat tontonan & routing HistoryScreen
|   |   |-- components/  # HistoryItemRow, HistoryEmptyState
|   |-- navigation/      # Navigasi bottom bar & routing Compose
|   |-- player/          # Tampilan utama pemutar video & routing PlayerScreen
|   |   |-- components/  # VideoPlayerSection, PlayerDetailContent, ControlsOverlay, SettingsBottomSheet, SpeedDialog, SeekIntervalSelectionDialog, Banner
|   |-- playlist/        # Tampilan detail playlist & routing PlaylistScreen
|   |   |-- components/  # PlaylistHeaderSection, PlaylistItemRow, PlaylistStateViews, PlaylistContent
|   |-- search/          # Antarmuka pencarian & saran kata kunci
|   |-- theme/           # Tema Material 3, Cyberpunk Neo-Tokyo Palette, Tipografi
|-- util/                # Format teks, helper orientasi, parser URL
```

---

## Persyaratan Sistem

- **Sistem Operasi Minimum**: Android 7.0 (API Level 24)
- **Target SDK**: Android 14+ (API Level 37)
- **Arsitektur CPU**: armeabi-v7a, arm64-v8a, x86, x86_64
- **Koneksi Jaringan**: Akses internet untuk streaming video

---

## Cara Memasang Aplikasi

1. Unduh file rilis APK terbaru (`app-release.apk`) dari tab Releases di repositori GitHub ini.
2. Buka file APK yang telah diunduh pada perangkat Android Anda.
3. Berikan izin pemasangan dari sumber tidak dikenal (*Install unknown apps*) jika diminta oleh sistem.
4. Pilih **Pasang** (*Install*) dan tunggu proses pemasangan hingga selesai.
5. Buka **YounoAds** untuk mulai menggunakan aplikasi.

---

## Pertanyaan yang Sering Diajukan (FAQ)

**Apakah aplikasi ini memerlukan login akun Google?**  
Tidak. YounoAds dapat digunakan langsung tanpa login akun apa pun. Tidak ada data akun atau kredensial yang diminta atau disimpan.

**Di mana riwayat dan preferensi pengguna disimpan?**  
Seluruh data (riwayat tontonan, riwayat pencarian, dan video tersimpan) disimpan secara lokal di perangkat Anda menggunakan Room Database. Data tidak pernah dikirimkan ke server eksternal.

**Bagaimana cara memperbarui aplikasi?**  
Aplikasi memiliki fitur pengecekan versi otomatis. Ketika versi baru dirilis, dialog notifikasi pembaruan akan muncul di dalam aplikasi dengan opsi unduh dan pasang langsung.

---

## Penafian (Disclaimer)

YounoAds dikembangkan semata-mata sebagai proyek portofolio pribadi, media riset rekayasa sistem Android native, dan eksplorasi implementasi arsitektur modern (*non-commercial portfolio project*).

- **Non-Komersial**: Aplikasi ini sepenuhnya gratis, tidak memuat sistem langganan berbayar, tidak menjual produk, tidak memungut biaya, dan tidak memonetisasi konten dalam bentuk apa pun.
- **Independen**: Proyek ini bersifat independen dan tidak memiliki keterikatan resmi, afiliasi, kemitraan, atau dukungan dari Google LLC maupun YouTube.
- **Hak Cipta**: Seluruh hak cipta, merek dagang, konten audio-visual, dan metadata yang ditampilkan adalah hak milik sah dari masing-masing kreator konten dan penyedia platform asal.
- **Tujuan Penggunaan**: Diperuntukkan secara terbatas untuk evaluasi teknis, portofolio pribadi pengembang, dan penggunaan oleh lingkaran keluarga serta rekan terdekat.
