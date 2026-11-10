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

### 4. Efek Pencahayaan Ambient (Ambient Glow)
Menghadirkan bias cahaya adaptif di sekeliling pemutar video yang selaras dengan palet warna frame video yang sedang aktif, memberikan pengalaman menonton yang imersif.

### 5. Kontrol Interaktif dan Gestur
- **Double-Tap to Seek**: Ketuk ganda di sisi kanan atau kiri pemutar untuk melompat maju atau mundur 10 detik dengan feedback haptik instan.
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

### 11. Desain Tema Gelap AMOLED
Tampilan gelap murni (Pure Dark AMOLED) dengan aksen merah YouTubeRed standar, hemat konsumsi daya layar OLED dan nyaman di mata pada kondisi pencahayaan rendah.

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

---

## Informasi & Spesifikasi Teknis

### Arsitektur Sistem

Proyek ini menerapkan prinsip Clean Architecture yang dipadukan dengan pola arsitektur **MVVM (Model-View-ViewModel)** dan paradigma **Unidirectional Data Flow (UDF)**:

1. **Presentation Layer (UI)**:
   - Dibangun sepenuhnya secara deklaratif menggunakan **Jetpack Compose**.
   - Mengonsumsi data reaktif melalui `StateFlow` dari ViewModel dengan siklus hidup terkelola (`collectAsStateWithLifecycle`).
   - Menerapkan modularisasi komponen reusable (Pill Buttons, Dialogs, Sliders, Glow Panels).

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

---

## Struktur Direktori Proyek

```
app/src/main/kotlin/com/example/yoouutt/
|-- data/
|   |-- local/           # SQLite Database, Repositori Riwayat Tontonan & Playlist
|   |-- model/           # Data classes & domain models (StreamData, VideoItem, PlaylistData, FeedItem)
|   |-- repository/      # Abstraksi repositori data & integrasi NewPipe (LRU Cache)
|-- playback/            # PlaybackManager, PlaybackService & MediaSession
|-- ui/
|   |-- channel/         # Profil saluran, Tab Video & Tab Playlist (PlaylistCard)
|   |-- common/          # Komponen UI global (AppConfirmDialog, ViewModels)
|   |-- feed/            # Tampilan beranda & daftar video / playlist
|   |-- history/         # Tampilan riwayat tontonan & aksi kelola
|   |-- navigation/      # Navigasi bottom bar & routing Compose
|   |-- player/          # Tampilan utama pemutar video
|   |   |-- components/  # Sub-komponen (ControlsOverlay, Scrubber, AmbientGlow, PlayerPlaylistBanner)
|   |-- playlist/        # Tampilan detail playlist saluran (PlaylistScreen, PlaylistViewModel)
|   |-- search/          # Antarmuka pencarian & saran kata kunci
|   |-- theme/           # Tema Material 3, Warna (YouTubeRed), Tipografi
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
