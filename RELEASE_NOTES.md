### YounoAds v4.19 - Catatan Rilis & Pembaruan

Pembaruan mesin unduhan multi-resolusi dengan penggabungan audio-video native (Hardware MediaMuxer), konfigurasi tema default Matte Black untuk instalasi baru, dan transisi animasi navigasi balik Stretch Left:

1. Unduhan Multi-Resolusi & Native Media Muxing
- Pilihan Resolusi Lengkap: Dialog unduhan kini mendeteksi seluruh resolusi video yang tersedia (1080p, 720p, 480p, 360p, 240p, 144p) serta opsi Audio Saja (M4A).
- Hardware MediaMuxer Integration: Mengunduh aliran video berkualitas tinggi dan aliran audio secara paralel, lalu menggabungkannya menjadi berkas MP4 utuh menggunakan MediaMuxer native Android tanpa aplikasi pihak ketiga.
- Notifikasi Progres Terpadu: Bilah notifikasi unduhan menampilkan tahapan terukur (0-75% unduh video, 75-95% unduh audio, 96-100% penggabungan berkas).
- Pemutaran 100% Offline Andal: Integrasi FileDataSource dan DefaultDataSource pada ExoPlayer memastikan berkas unduhan lokal dapat langsung diputar tanpa jeda loading dan tanpa memakan kuota data.

2. Tema Default Instalasi Baru (Matte Black Stealth AMOLED)
- Menetapkan tema Hitam Doff (Matte Black / Stealth AMOLED) sebagai tema bawaan standar pada saat aplikasi pertama kali dipasang di perangkat baru.
- Menjaga integritas preferensi pengguna lama saat melakukan pembaruan aplikasi tanpa mereset tema yang telah digunakan.

3. Animasi Navigasi Balik Stretch Left (Shrink to Left)
- Menggantikan animasi default kartu mengecil di tengah (scale-out 70%) saat gestur usap kembali (predictive back) dengan efek Stretch Left.
- Layar pemutar video menyusut dan bergeser secara alami ke arah kiri (shrink towards start & slide out to left), menyingkap halaman beranda di latar belakang secara mulus.

---

### YounoAds v4.18 - Catatan Rilis & Pembaruan

Pembaruan fitur Video Chapters (Segmen Bab Video) dan mesin Download Mode Offline mandiri dengan pemutaran 100% lokal tanpa koneksi internet:

1. Video Chapters (Segmen Bab Video Terintegrasi)
- Ekstraksi Otomatis: Mendeteksi segmen bab langsung dari metadata stream YouTube maupun penguraian stempel waktu (timestamps) pada deskripsi video secara akurat.
- Penanda Visual pada Garis Waktu: Garis seekbar menampilkan celah batas bab (tick marks) antar segmen video.
- Badge & Judul Bab Aktif: Saat menggeser (scrubbing), gelembung indikator menampilkan judul bab yang sedang dituju, serta judul bab yang sedang diputar tampil otomatis di bilah durasi kontrol pemutar.

2. Unduhan & Pemutaran Offline Mandiri (Offline Download Mode)
- Mesin Unduhan Latar Belakang OkHttp: Mengunduh konten video dan audio secara streaming di latar belakang dengan notifikasi progres unduhan native Android yang interaktif.
- Pilihan Kualitas Fleksibel: Dialog pemilihan unduhan mendukung Video Kualitas Tinggi (720p MP4), Video Standar (360p MP4), dan Audio Saja (M4A).
- Manajemen Berkas Scoped Storage: Berkas tersimpan rapi pada direktori Film/Musik perangkat dengan proteksi berkas sementara (.tmp) dan sanitasi nama berkas yang aman.
- Layar Media Unduhan: Halaman khusus di menu Pengaturan untuk meninjau seluruh koleksi unduhan lengkap dengan ukuran berkas, progres unduhan, dan dialog konfirmasi hapus.
- Pemutaran 100% Offline: Memutar video dan lagu unduhan langsung dari berkas lokal via ExoPlayer tanpa membutuhkan akses internet, bebas jeda buffering, dan tanpa memakan kuota data.

---

### YounoAds v4.17 - Catatan Rilis & Pembaruan

Pembaruan arsitektur transmisi jaringan dan mesin cache pemutaran video mandiri untuk pengalaman streaming tanpa jeda (Zero-Buffering Playback):

1. Resilient OkHttp Streaming Engine & Mekanisme Fallback
- Mengintegrasikan OkHttpDataSource resmi berbasis protokol HTTP/2 dengan connection pool terpadu (8 concurrent connections, keep-alive 5 menit) untuk transfer data video berkecepatan tinggi.
- Resilient Fallback Otomatis: Menyediakan mekanisme pengalihan instan ke DefaultHttpDataSource (HttpURLConnection) jika terjadi gangguan handshake TLS, pembatasan koneksi ISP, atau error transmisi jaringan tanpa memutus jalannya video.

2. Fast-Start Buffer Tuning
- Mengoptimalkan DefaultLoadControl dengan ambang batas awal (bufferForPlaybackMs) diturunkan ke 600ms dan bufferForPlaybackAfterRebufferMs ke 1200ms.
- Menjadikan video mulai memutar lebih dari 2x lebih cepat segera setelah chunk data pertama tiba di perangkat tanpa menunggu alokasi buffer berlebih.

3. Media Caching Lokal (Zero-Buffering Rewind)
- Mengimplementasikan CacheDataSource berbasis SimpleCache ExoPlayer berkapasitas 200MB dengan eviksi otomatis Least Recently Used (LRU).
- Potongan video dan audio yang sudah dimuat otomatis disimpan di penyimpanan cache lokal berkecepatan tinggi.
- Scrubbing & Rewind Instan: Pengguna dapat melompat mundur (seek back) atau memutar ulang bagian video tanpa buffering dan tanpa menggunakan kuota data internet tambahan.
- Proteksi FLAG_IGNORE_CACHE_ON_ERROR untuk memastikan integritas pemutaran tetap aman jika terjadi kendala pada penyimpanan lokal.

---

### YounoAds v4.16 - Catatan Rilis & Pembaruan

Pembaruan animasi pembuka aplikasi (Splash Screen) adaptif tema dan perombakan antarmuka seekbar pemutar video menempel rapat di bibir bawah layar ala YouTube native:

1. Splash Screen Animasi Sinematik (2.5 Detik)
- Menghadirkan layar pembuka in-app adaptif tema dengan durasi 2.5 detik (2500ms).
- Menampilkan logo ikon launcher aktif pengguna yang berpendar lembut (ambient glow) dengan animasi scale-in dan fade-in.
- Dilengkapi garis indikator pemuatan halus berbasis gradien tema dari 0% hingga 100% serta tipografi elegan JetBrains Mono.
- Pre-loading di Latar Belakang: Konten beranda (home feed) langsung mulai dimuat di latar belakang saat splash screen tampil, sehingga ketika transisi fade-out selesai, halaman utama sudah siap pakai tanpa jeda loading kosong.
- Proteksi Cold Start: Splash screen hanya berjalan saat aplikasi dibuka dari keadaan mati (cold start), dan tidak akan muncul kembali saat rotasi layar atau kembali dari background/PiP.

2. Desain Seekbar Pemutar Video Menempel Tepi Bawah (YouTube Native Style)
- Perombakan urutan tata letak kontrol bawah: baris info waktu dan tombol opsi diposisikan di atas, sedangkan seekbar diposisikan tepat di bibir paling bawah video.
- Garis seekbar membentang penuh edge-to-edge dengan clamping radius thumb scrubber agar lingkaran titik tidak terpotong di sudut layar.
- Transisi tanpa lompat: posisi garis seekbar aktif kini selaras pada baseline Y yang sama dengan progress bar minimalis saat kontrol disembunyikan.
- Penyesuaian Inset Mode Layar Penuh (Landscape): Menambahkan margin adaptif pada mode fullscreen untuk melindungi dari terpicunya gestur navigasi sistem Android dan menghindari kamera punch-hole / sudut melengkung ponsel.

---

### YounoAds v4.15 - Catatan Rilis & Pembaruan

Pembaruan fitur kustomisasi ikon aplikasi launcher mandiri dengan 10 varian estetika, integrasi pengalih komponen Android native, dan penyempurnaan dokumentasi:

1. Kustomisasi Ikon Aplikasi (App Icon Selector)
- Menghadirkan 10 pilihan ikon launcher yang dapat dipilih langsung melalui menu Pengaturan:
  - Original YounoAds: Merah marun klasik dengan aksen play bawaan.
  - Cyberpunk Neo: Latar hitam pekat dengan aksen sian elektrik dan neon magenta.
  - Sakura Pastel: Ungu plum lembut berpadu merah muda kelopak bunga sakura.
  - Stealth AMOLED: Hitam pekat obsidian murni dengan aksen merah minimalis.
  - Clean Light: Putih salju bersih dengan aksen merah cerah kontras tinggi.
  - Youno Mascot: Siluet karakter anime maskot Youno-chan.
  - Retro Pixel: Palet hijau monokrom konsol genggam retro 8-bit.
  - City Pop 80s: Indigo senja Tokyo dengan gradasi jingga keemasan.
  - Mecha Tactical: Abu-abu baja taktis dengan aksen hijau neon asam.
  - Terminal CRT: Teks hijau fosfor matriks di atas layar hitam tabung CRT.

2. Arsitektur AppIconManager Native & Reaktif
- Menggunakan mekanisme pergantian komponen Android native (PackageManager.setComponentEnabledSetting) dengan target activity alias.
- Menerapkan flag DONT_KILL_APP dan strategi aktivasi komponen target sebelum menonaktifkan komponen lama sehingga transisi berjalan mulus tanpa force close atau restart aplikasi.
- Status ikon disimpan secara persisten di penyimpanan preferensi lokal dan dipantau melalui StateFlow reaktif.

3. Pratinjau Visual & Dialog Pemilihan Modern
- Dialog pemilihan Material 3 dilengkapi kartu pratinjau visual tajam berbasis vektor drawable.
- Menampilkan nama tema ikon, ringkasan deskripsi visual, indikator centang pada ikon aktif, serta umpan balik getaran haptik saat opsi dipilih.

4. Panduan Pemasangan & Keamanan
- Penambahan panduan verifikasi Google Play Protect pada berkas dokumentasi bagi pengguna yang memasang atau memperbarui file APK mandiri dari GitHub.

---

### YounoAds v4.14 - Catatan Rilis & Pembaruan

Pembaruan besar sistem tema visual dengan penambahan kategori Dynamic System (Material You) dan Time-Adaptive (Smart Cycle), integrasi mesin deteksi waktu lokal, dan optimalisasi status visual:

1. Kategori Baru: Dynamic System (Material You Monet Engine)
- Menghadirkan 5 tema dinamis baru yang mengekstrak palet warna wallpaper perangkat pengguna secara otomatis pada Android 12+ (API 31+):
  - Dynamic Material Dark: Mode gelap Material 3 yang menyatu dengan wallpaper ponsel.
  - Dynamic AMOLED Black: Latar 100% hitam pekat hemat daya dengan aksen warna wallpaper.
  - Dynamic Material Light: Tampilan terang bersih yang selaras dengan warna perangkat.
  - Dynamic Vibrant Pulse: Kontras gelap dengan aksen warna dinamis bersaturasi tinggi.
  - Dynamic Frosted Glass: Nuansa permukaan akrilik lembut dengan sentuhan warna wallpaper.
- Dilengkapi skema warna fallback Google Material 3 Baseline yang aman untuk perangkat Android di bawah versi 12.

2. Kategori Baru: Time-Adaptive / Smart Cycle (Siklus Waktu Pintar)
- Memperkenalkan 5 tema siklus waktu otomatis yang berevolusi sepanjang hari berdasarkan jam nyata:
  - Siklus Karakter Youno-chan: Sakura Lo-Fi pagi hari, Matcha Zen siang hari, City Pop senja, Cyberpunk Neo-Tokyo malam hari, dan Ocean Abyss tengah malam.
  - Siklus Sirkadian Ramah Mata: Putih Bersih pagi hari, Kontras Tinggi siang hari, Sepia Malam senja, Hitam Doff malam hari, dan AMOLED Merah Redup tengah malam.
  - Siklus Panorama Alam & Musim: Fuyu Yuki fajar salju, Matcha Zen siang, Momiji Autumn senja maple, Sumi-e Ink malam, dan Ocean Abyss larut malam.
  - Siklus Estetika Digital Retro: Retro Pixel Game Boy siang hari, Vaporwave senja 90-an, Dracula Developer malam hari, dan Terminal Hacker CRT tengah malam.
  - Siklus Dinamis Siang & Malam: Dynamic Light siang hari, Dynamic Frosted senja, Dynamic Dark malam hari, dan Dynamic AMOLED tengah malam.

3. Arsitektur SmartTimeEngine Reaktif & Hemat Daya
- Mesin waktu cerdas mendeteksi 5 fase waktu lokal (Pagi, Siang, Senja, Malam, dan Larut Malam) menggunakan siaran OS Android native (ACTION_TIME_TICK) tanpa proses background polling yang menguras baterai.
- Tema aktif dan warna antarmuka bertransisi tepat waktu saat jam berganti.

4. Status Kosong Adaptif Tema (Theme-Adaptive Empty State)
- Menampilkan ilustrasi karakter anime pada tema kategori Anime dan tema siklus berkarakter.
- Menampilkan ikon vektor bertema yang minimalis, rapi, dan bersih pada tema non-anime.

5. Peningkatan UI Dialog Pemilihan Tema
- Baris tab kategori tema kini mendukung scroll horizontal mulus sehingga seluruh kategori (Standar, Anime, Dinamis, Siklus Waktu, Khusus) tertata rapi tanpa terpotong.
- Penambahan badge khusus "Siklus" dan indikator 3 titik warna representasi pergantian waktu pada tema siklus.
