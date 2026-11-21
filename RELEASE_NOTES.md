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
