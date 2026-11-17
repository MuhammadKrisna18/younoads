### YounoAds v4.3 - Catatan Rilis & Pembaruan

Pembaruan preferensi pemutar video dan otomatisasi kualitas tayangan (Level Medium - Bug 5):

1. Fitur Preferensi Default Kualitas Pemutaran Video (Default Video Quality Engine)
- Menambahkan model preferensi kualitas DefaultVideoQuality yang tersimpan secara persisten di penyimpanan lokal (PlayerPreferences):
  * Otomatis (Optimal): Memilih resolusi terbaik dan paling stabil sesuai ketersediaan stream extractor.
  * 1080p (Full HD): Mengutamakan ketajaman maksimal untuk jaringan Wi-Fi kecepatan tinggi.
  * 720p (HD): Keseimbangan ideal antara kejernihan gambar dan efisiensi konsumsi kuota.
  * 480p (Standar): Pilihan ideal untuk pemutaran cepat dan koneksi seluler hemat daya.
  * 360p (Hemat Data): Meminimalkan konsumsi kuota data secara optimal.
- Mengintegrasikan algoritma pencocokan resolusi otomatis (resolveInitialResolution) pada PlaybackManager. Setiap kali video baru diputar, sistem otomatis mencocokkan katalog resolusi video dengan preferensi target pengguna tanpa perlu pergantian resolusi manual setiap saat.

2. Dialog Pemilih Kualitas Default Interaktif (DefaultQualitySelectionDialog)
- Menambahkan tombol aksi Kualitas (HighQuality icon) pada bilah atas Layar Koleksi (LibraryScreen).
- Menyajikan dialog modal interaktif dengan indikator centang aktif dan deskripsi pemakaian kuota yang informatif dalam Bahasa Indonesia dan English.

---

### YounoAds v4.2 - Catatan Rilis & Pembaruan

Pembaruan perbaikan kritis pemutar video (Level Medium - Bug 7 & 8):

1. Pemulihan Fitur Picture-in-Picture (Gesture Auto-Enter PIP Fix)
- Menyelesaikan anomali unwrap Activity pada rantai konteks localized configuration (ContextImpl) dengan membungkus baseContext menggunakan ContextWrapper yang mendelegasikan resource terjemahan secara transparan.
- Menghadirkan CompositionLocal baru LocalActivity di tingkat root MainActivity, menjamin referensi Activity pada PlayerScreen selalu valid dan terhindar dari nilai null.
- Fungsi registrasi pemutar aktif (setPlayerActive) dan parameter auto-enter PIP (Android 12+) serta transisi gesture onUserLeaveHint (Android 8-11) kini berfungsi normal kembali saat pengguna keluar dari aplikasi.

2. Pemulihan Rotasi Layar Penuh Horizontal (Landscape Fullscreen Fix)
- Mengembalikan eksekusi orientasi otomatis SCREEN_ORIENTATION_SENSOR_LANDSCAPE saat tombol toggle layar penuh ditekan.
- Menghilangkan bug tampilan fullscreen yang sebelumnya terkunci vertikal (potret), kini video langsung berputar memenuhi layar secara horizontal dan menyembunyikan status bar serta navigation bar secara imersif.
- Mengembalikan orientasi layar ke potret secara mulus saat keluar dari mode layar penuh atau saat menekan tombol kembali (BackHandler).

---

### YounoAds v4.1 - Catatan Rilis & Pembaruan

Pembaruan optimalisasi dan perbaikan pengalaman pemutaran video (Level Easy Fixes):

1. Pembersihan Lembar Pengaturan Pemutar (Streamlined Playback Settings)
- Menghapus opsi tema aplikasi, bahasa, dan tambah ke playlist dari lembar pengaturan pemutar (PlayerSettingsBottomSheet).
- Lembar pengaturan pemutar kini terfokus murni pada parameter teknis pemutaran video aktif (Kualitas, Kecepatan, Lompatan Ketuk Ganda, Trek Audio, Pengulangan Loop, dan Autoplay).
- Menghilangkan kode mati (dead code) dan redundansi tombol dengan memanfaatkan tombol pil "Playlist" yang telah tersedia di baris aksi utama.

2. Logika Saling Kunci Looping vs Autoplay (Mutually Exclusive Logic)
- Mengaktifkan pengulangan video tunggal (Loop) kini otomatis mematikan pemutaran otomatis (Autoplay).
- Sebaliknya, mengaktifkan Autoplay kini otomatis mematikan fitur Loop.
- Menghilangkan kebingungan status ganda aktif dan menjamin perilaku akhir pemutaran video selalu dapat diprediksi dengan tepat.

3. Transisi Autoplay Instan Mulus Tanpa Konfirmasi/Countdown
- Mengeliminasi jeda banner hitung mundur 3 detik yang sebelumnya menahan transisi video.
- Saat pemutaran video selesai (STATE_ENDED), pemutar langsung berpindah dan memutar video berikutnya (baik trek playlist maupun rekomendasi teratas) secara instan tanpa memerlukan intervensi tombol atau konfirmasi manual pengguna.
- Menghapus komponen mati AutoplayCountdownBanner dari pohon rendering visual.

---

### YounoAds v4.0 - Catatan Rilis & Pembaruan

Pembaruan tonggak sejarah (Major Milestone Release) menghadirkan 4 pilar fitur inti baru: Manajemen Playlist Kustom Lokal, Sistem Markah Simpan Video Terpadu, Engine 6 Tema Dinamis Adaptif, serta Lokalisasi Dwi-Bahasa (English dan Bahasa Indonesia) dengan peralihan langsung:

1. Pembuatan & Manajemen Playlist Kustom Lokal (Custom Playlists Engine)
- Menambahkan kapabilitas pembuatan playlist kustom lokal tanpa batas dengan judul dan deskripsi mandiri.
- Mengintegrasikan dialog penambahan playlist (AddToPlaylistBottomSheet) langsung dari bilah aksi pemutar video, memungkinkan penyimpanan video aktif ke satu atau beberapa playlist sekaligus secara efisien.
- Tampilan detail playlist kustom lengkap dengan informasi total video, thumbnail cover resolusi tinggi, opsi hapus item, serta tombol "Putar Semua" dan "Acak" yang langsung memuat trek ke antrean pemutar video latar belakang (PlaybackManager).
- Dukungan penghapusan playlist dan pengelolaan urutan video secara instan dan reaktif.

2. Sistem Simpan Video / Markah Cepat (Persistent Video Bookmarking)
- Tombol aksi "Simpan" pada bilah pil pemutar video kini terhubung langsung dengan basis data lokal tersimpan (saved_videos).
- Indikator status visual aktif/non-aktif pada tombol pil pemutar dengan sinkronisasi status bookmark otomatis saat video dimuat.
- Tab khusus "Tersimpan" di Layar Koleksi (Library Hub) menyajikan seluruh katalog video yang disimpan lengkap dengan thumbnail, durasi, nama uploader, serta opsi hapus cepat.

3. Hub Koleksi Terpadu (Unified Library Hub)
- Merombak navigasi bawah dengan mengganti tab "Riwayat" menjadi tab terpadu "Koleksi" (Library), menyatukan 3 pilar konten lokal pengguna dalam 3 tab geser: "Playlist", "Tersimpan", dan "Riwayat".
- Dilengkapi bilah aksi atas (TopAppBar) dengan akses instan ke pembuatan playlist baru, dialog pemilih tema, dan dialog pemilih bahasa.
- Mendukung pembersihan seluruh riwayat tontonan dengan dialog konfirmasi keamanan (AppConfirmDialog).

4. Engine 6 Tema Dinamis (Adaptive 6-Theme Architecture)
- Menghadirkan 6 preset palet tema estetika tinggi yang dapat berganti secara langsung dan persisten tanpa perlu memulai ulang aplikasi:
  * Cyberpunk Neon: Estetika orisinal Neo-Tokyo, Pure Void Black (#08080C), aksen Electric Cyan dan Neon Magenta dengan latar belakang hologram anime maskot Youno-chan.
  * Hitam Doff AMOLED: Gaya stealth ultra-minimalis, latar hitam pekat murni (#000000), tanpa watermark karakter, kontras aksen putih dan cyan bersih yang hemat baterai layar OLED.
  * Putih Bersih Light: Mode terang modern dengan latar putih lembut (#F8F9FA) dan aksen Sapphire Blue dan Indigo untuk keterbacaan optimal di lingkungan luar ruangan.
  * Violet Midnight: Palet nuansa malam galaksi (#0D0B18) dengan aksen pendaran Deep Purple (#9D4EDD) dan Lilac Glow (#C77DFF).
  * Sakura Pastel: Estetika anime pastel lembut (#1A1116) dengan aksen Rose Pink (#FF758F) dan Coral (#FFB3C1).
  * Mecha Blue: Palet sci-fi militer navy (#0A1118) dengan aksen Cobalt Blue (#0077B6) dan Ice Cyan (#90E0EF).
- Latar belakang CyberpunkAppBackground secara cerdas menyembunyikan watermark hologram pada mode Hitam Doff AMOLED dan Putih Bersih Light agar tampilan tetap minimalis dan bersih.
- Dialog pemilihan tema (ThemeSelectionDialog) terintegrasi rapi di Layar Koleksi dan Lembar Pengaturan Pemutar.

5. Lokalisasi Dwi-Bahasa Penuh (Bilingual Localization: English & Bahasa Indonesia)
- Mengimplementasikan sistem pelokalan bahasa menyeluruh mencakup seluruh antarmuka aplikasi dalam Bahasa Indonesia dan English.
- Menyediakan opsi bahasa: Ikuti Sistem (System Default), Bahasa Indonesia, dan English melalui modal dialog LanguageSelectionDialog.
- Pergantian bahasa berlangsung secara instan dan reaktif di seluruh komponen Jetpack Compose menggunakan CompositionLocalProvider tanpa merusak status pemutaran video aktif.
- Menyelaraskan pengaturan bahasa pengguna ke NewPipe extractor untuk memastikan data pencarian dan metadata video YouTube tersaji dalam bahasa yang sesuai.

---

### YounoAds v3.10 - Catatan Rilis & Pembaruan

Pembaruan besar untuk kecerdasan personalisasi beranda (Living Home Feed Engine) dan rangkaian peningkatan fluiditas visual sistem antarmuka (Ultra-Smooth Animation Suite):

1. Mesin Rekomendasi Beranda Dinamis (Living Home Feed Engine)
- Menghadirkan algoritma kurasi beranda adaptif yang menggabungkan kreator paling sering ditonton (top 3) dengan sampling acak dari jejak kreator historis (hingga 4 kreator tambahan), memastikan beranda selalu menyajikan sudut pandang baru pada setiap refresh.
- Menambahkan ekstraksi kata kunci topik dinamis dari judul video yang baru saja ditonton dengan pembersihan stop words otomatis untuk menarik video bertopik relevan dari berbagai kreator eksternal.
- Menyaring video yang sudah ditonton (unwatched filter) agar pengguna tidak disajikan ulang konten yang baru saja selesai ditonton.
- Menerapkan pembatasan kuota anti-clustering (maksimal 2 video per kreator) dan pengocokan acak menyeluruh (`.shuffled()`) dengan penyisipan berkala playlist resmi setiap 4 video.

2. Animasi Garis Waktu Hitung Mundur Autoplay 3000ms Mulus
- Merombak total animasi banner hitung mundur autoplay dari yang sebelumnya berbasis lompatan interval detik diskret menjadi animasi linier kontinu 3000ms penuh menggunakan `Animatable`. Garis progres gradien cyberpunk kini menyusut secara presisi dari 100% hingga 0% tanpa terpotong di angka 33%.
- Menambahkan transisi flip-roll vertikal (`AnimatedContent`) pada angka detik hitung mundur sehingga pergantian angka terasa alami dan berkarakter.

3. Transisi Layar Fluiditas Tinggi (Cubic Bezier Easing)
- Menggantikan kurva transisi layar kaku dengan Material 3 Emphasized Decelerate (`CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)`) dan Emphasized Accelerate (`CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)`).
- Menghilangkan jeda visual atau micro-stutter saat membuka dan menutup layar pemutar video serta transisi geser horizontal pada halaman saluran dan riwayat tontonan.

4. Dinamika Fisika Pegas In-App Mini-Player & Animasi Bilah Progres
- Menggantikan durasi geser kaku saat pelepasan drag horizontal mini-player dengan fisika pegas (`Spring.DampingRatioNoBouncy` dan `Spring.DampingRatioLowBouncy`), memberikan respons sentuhan yang membal alami dan taktil layaknya aplikasi native papan atas.
- Mengintegrasikan interpolasi halus (`animateFloatAsState`) pada bilah kemajuan pemutaran mini-player sehingga garis waktu tidak lagi melompat-lompat setiap interval 500ms.

5. Transisi Crossfade Status Beranda (Pull-to-Refresh Crossfade)
- Membungkus pergantian status beranda (loading skeleton, konten video, error state, dan empty state) dengan `AnimatedContent` berdurasi 280ms/200ms, menghilangkan efek visual pop mendadak saat pengguna melakukan tarik untuk menyegarkan (pull-to-refresh).

---

### YounoAds v3.9 - Catatan Rilis & Pembaruan

Pembaruan besar untuk kecerdasan rekomendasi video dan kesinambungan tontonan yang luwes, segar, dan tidak monoton:

1. Mesin Rekomendasi Dinamis & Shuffling Pool (Dynamic Smart Recommendation Engine)
- Menghilangkan kekakuan rekomendasi video dengan menerapkan sistem pengocokan acak terkontrol (`.shuffled()`) pada kumpulan kandidat relevan.
- Setiap kali Anda membuka video (atau saat menonton ulang video yang sama di waktu berbeda), daftar rekomendasi dan video teratas (Index 0) akan selalu berganti dan bervariasi secara dinamis, menyajikan alternatif tontonan baru tanpa kejenuhan.
- Menjamin stabilitas tampilan: daftar rekomendasi tetap tenang dan stabil selama video sedang diputar sehingga pengguna tidak kehilangan video yang sedang diincar.

2. Eliminasi Monopoli Channel & Pencarian Lintas Kreator
- Membatasi kuota video dari channel kreator aktif menjadi maksimal 3 video, mencegah seluruh feed rekomendasi didominasi oleh satu kreator saja.
- Menambahkan algoritma pembersih kata kunci topik (`extractTopicQuery`) yang membuang stop words umum dari judul video dan melakukan pencarian cerdas untuk menarik video serupa dari berbagai kreator lain di ekosistem YouTube.

3. Arsitektur Multi-Pilar Paralel (Non-Blocking Multi-Source Curation)
- Mengambil kandidat rekomendasi dari 4 pilar secara bersamaan menggunakan eksekusi paralel coroutine (`async`):
  * Pilar 1: Video terkait langsung dari YouTube jika tersedia.
  * Pilar 2: Video topik serupa dari berbagai kreator lain berdasarkan kata kunci esensial judul.
  * Pilar 3: Video rekomendasi terpersonalisasi dari histori tontonan lokal pengguna.
  * Pilar 4: Video pilihan terbaru dari channel kreator aktif.
- Menyelaraskan seluruh rekomendasi segar ini ke antrean pemutaran otomatis (`Autoplay`), menjamin alur tontonan berikutnya selalu mengalir alami ke kreator dan topik yang relevan.

---

### YounoAds v3.8 - Catatan Rilis & Pembaruan

Pembaruan menghadirkan kesinambungan pemutaran video otomatis tanpa henti dengan dukungan rekomendasi cerdas teratas, alur playlist standar YouTube, serta penyempurnaan animasi banner hitung mundur:

1. Autoplay Video Rekomendasi Teratas (Index 0 Fallback)
- Fitur Autoplay kini aktif secara menyeluruh untuk video reguler di luar playlist; ketika video selesai, pemutar otomatis melanjutkan ke video rekomendasi nomor 1 (posisi teratas) dari feed rekomendasi cerdas.
- Menghubungkan kurasi rekomendasi bertingkat (channel, pencarian serupa, dan histori personalisasi) langsung ke engine pemutar latar belakang (`PlaybackManager`).

2. Alur Transisi Playlist Standar YouTube
- Mempertahankan integritas urutan pemutaran playlist resmi YouTube: selama masih ada video berikutnya dalam daftar putar, pemutar akan melanjutkan ke trek berikutnya.
- Saat antrean playlist telah mencapai video terakhir, sistem secara cerdas beralih memutar video rekomendasi teratas dan membersihkan antrean playlist lama tanpa memutus aliran tontonan.

3. Animasi Visual Banner Hitung Mundur 3 Detik
- Banner hitung mundur transisi (`AutoplayCountdownBanner`) kini dipercantik dengan animasi kemunculan halus kombinasi `scaleIn` (skala 0.85) dan `fadeIn`, serta penutupan `scaleOut` dan `fadeOut`.
- Dilengkapi indikator titik status neon Electric Cyan dan garis waktu progres horizontal (`CyberpunkGradient`) yang menyusut secara dinamis selama 3 detik sebelum pemutaran video berikutnya dimulai.
- Pengguna tetap memegang kendali penuh dengan tombol "Putar Sekarang" untuk transisi instan atau tombol "Batal" untuk menghentikan pemutaran otomatis.

---

### YounoAds v3.7 - Catatan Rilis & Pembaruan

Pembaruan fokus pada kenyamanan pemutaran video tanpa distraksi, pemulihan posisi otomatis tanpa konfirmasi, antarmuka pemutar yang bersih dan minimalis, serta sistem kurasi rekomendasi video cerdas bertingkat:

1. Pemulihan Posisi Tonton Otomatis dan Senyap (Silent Auto-Resume)
- Menghapus banner interaktif "Lanjutkan dari mm:ss? Ya Batal" dari viewport pemutar video.
- Sistem secara otomatis membaca posisi tontonan terakhir dari riwayat lokal dan langsung melanjutkan pemutaran di posisi menit dan detik tersebut saat video dibuka, memberikan alur menonton instan tanpa perlu konfirmasi manual.

2. Viewport Pemutar Minimalis & Bersih (Clean Minimalist Viewport)
- Menghapus kontrol overlay yang menumpuk di pojok kanan atas video (toggle Autoplay dan ikon Pengaturan).
- Seluruh preferensi pemutaran (Autoplay, Loop, Resolusi, Kecepatan, Trek Audio, dan Interval Seek) terpusat dengan rapi di Player Settings Bottom Sheet dan tombol Pengaturan di bilah aksi pemutar.
- Tampilan video kini lebih lapang, bersih, dan bebas dari clutter tombol yang redundan.

3. Perampingan Bilah Aksi Pemutar (Streamlined Action Bar)
- Menghapus tombol pil "Layar Penuh" dari deretan aksi di bawah video, menyisakan 3 aksi esensial: Salin, Simpan, dan Pengaturan.
- Transisi layar penuh tetap dapat dipicu dengan mudah melalui ikon rotasi di pojok kanan bawah kontrol video atau sensor auto-rotate perangkat.

4. Rekomendasi Video Pintar Bertingkat (Multi-Tier Smart Fallback Recommendations)
- Area di bawah kartu deskripsi kini dilengkapi feed rekomendasi video dinamis yang terkurasi otomatis, memastikan halaman pemutar tidak pernah kosong.
- Mengadopsi arsitektur rekomendasi 4 tingkatan:
  * Tingkat 1: Menggunakan video terkait bawaan stream jika tersedia minimal 4 item.
  * Tingkat 2: Mengambil katalog video terbaru dari channel kreator yang bersangkutan.
  * Tingkat 3: Mencari video serupa berdasarkan kata kunci judul dan kreator.
  * Tingkat 4: Mengompilasi rekomendasi terpersonalisasi dari histori tontonan lokal pengguna.
- Otomatis menyaring duplikasi item serta mengecualikan video yang sedang aktif diputar.

---

### YounoAds v3.6 - Catatan Rilis & Pembaruan

Pembaruan besar untuk kenyamanan multitasking dengan transisi Picture-in-Picture otomatis, kontrol aksi jendela PiP terpadu, dan opsi beralih ke audio latar belakang:

1. Transisi Otomatis Picture-in-Picture (Gesture Auto-PiP)
- Video yang sedang diputar di layar pemutar kini otomatis beralih menjadi jendela mengambang (Picture-in-Picture) ketika pengguna menekan tombol Home, melakukan gestur swipe-up, atau beralih ke aplikasi lain.
- Mengeliminasi kebutuhan menekan tombol PiP secara manual, memberikan pengalaman multitasking yang alami seperti YouTube native.
- Didukung animasi transisi pengecilan mulus (sourceRectHint) yang presisi mengikuti posisi video di layar.
- Auto-PiP hanya aktif saat video berputar; saat video dijeda (paused), aplikasi tidak akan membuka jendela PiP yang diam.

2. Aksi Ikon Headset pada Jendela PiP (Background Audio Switch)
- Menambahkan tombol aksi dengan ikon Headset langsung di dalam jendela floating PiP.
- Mengklik tombol Headset akan menutup jendela video PiP namun tetap melanjutkan pemutaran audio di latar belakang tanpa jeda.
- Pemutaran audio di latar belakang tetap dapat dikendalikan, dijeda, atau dihentikan kapan saja melalui notifikasi media sistem.

3. Kontrol Aksi Jendela PiP Lengkap (Interactive RemoteActions)
- Menyematkan 3 tombol kontrol interaktif di jendela PiP:
  * Mundur 10 detik (Seek Backward) untuk mengulang bagian penting video secara instan.
  * Putar / Jeda (Play/Pause Toggle) dengan ikon yang berganti secara dinamis sesuai status video.
  * Tombol Headset untuk beralih ke mode audio latar belakang.
- Penutupan jendela PiP melalui tombol silang (X) akan menghentikan pemutaran secara tuntas dan menyimpan posisi tontonan terakhir ke riwayat.

4. Perampingan Baris Aksi Pemutar (Clean Action Bar)
- Menghapus tombol PiP manual dari deretan pil aksi di bawah video, menyisakan 5 tombol esensial yang ringkas dan simetris: Bagikan, Salin, Simpan, Pengaturan, dan Layar Penuh.
- Tampilan bebas dari gulir horizontal berlebih dan lebih nyaman dioperasikan satu tangan.
