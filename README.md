# YounoAds

Pemutar video Android bebas iklan dengan tampilan segar, beragam tema visual, dan fitur lengkap untuk kenyamanan menonton sehari-hari.

---

## Tentang YounoAds

YounoAds dibuat untuk memberikan pengalaman menonton video yang nyaman, lancar, dan bebas dari gangguan iklan di HP Android. Aplikasi ini bisa langsung kamu gunakan tanpa perlu login akun Google, tanpa biaya langganan, dan seluruh riwayat tontonan tersimpan aman di perangkat kamu sendiri.

Selain pemutaran video yang mulus, YounoAds hadir dengan sentuhan visual bertema anime dan pilihan palet warna yang melimpah, membuat pengalaman menonton terasa lebih personal dan menyenangkan.

---

## Fitur Utama

### Nonton Nyaman Bebas Iklan

Nikmati konten video apa saja dari awal sampai selesai tanpa terpotong jeda iklan di tengah video, baik untuk klip pendek maupun video berdurasi panjang.

### Streaming Cepat & Bebas Buffering (OkHttp HTTP/2 & Smart Media Cache)

Didukung oleh arsitektur jaringan canggih yang memaksimalkan kecepatan pemutaran dan menghemat kuota:
- **Resilient OkHttp Engine**: Menggunakan protokol HTTP/2 dengan connection pool terpadu untuk transfer data streaming berkecepatan tinggi serta mekanisme pengalihan otomatis (fallback) ke DefaultHttpDataSource jika terjadi kendala jaringan.
- **Fast-Start Buffer**: Waktu awal mulai pemutaran (buffer-for-playback) dioptimalkan hanya 600ms sehingga video memutar seketika tanpa waktu tunggu yang lama.
- **Zero-Buffering Rewind (Media Cache 200MB)**: Potongan video yang telah diputar otomatis tersimpan dalam media cache lokal. Saat melompat mundur (seek back) atau mengulang bagian video, pemutaran langsung berjalan seketika dari penyimpanan lokal tanpa perlu buffering ulang dan tanpa memakan kuota internet.

### Segmen Bab Video (Video Chapters)

Menonton video berdurasi panjang, tutorial, podcast, atau album musik kini jauh lebih terarah dan efisien:
- **Pendeteksian Bab Otomatis**: Mendukung penguraian segmen bab baik langsung dari data YouTube resmi maupun stempel waktu deskripsi pengunggah.
- **Pemisah Bab Visual (Tick Marks)**: Bilah garis waktu (seekbar) memiliki celah penanda antar bab sehingga kamu tahu persis pergantian topik video.
- **Badge Navigasi Scrubbing**: Saat menggeser slider, gelembung waktu menampilkan nama bab secara real-time dan judul bab aktif tertera langsung di samping penanda durasi pemutar.

### Unduh & Putar Video Offline (100% Offline Mode & Native Muxer)

Simpan video dan musik favorit langsung ke penyimpanan ponsel untuk ditonton kapan pun tanpa koneksi internet:
- **Download Latar Belakang Mandiri**: Proses unduhan berjalan di latar belakang menggunakan mesin streaming OkHttp terisolasi dengan notifikasi progres interaktif di bilah status ponsel.
- **Pilihan Resolusi Lengkap & Native Muxing**: Pilih resolusi dari 1080p, 720p, 480p, 360p, hingga opsi Audio Saja (M4A). Sistem otomatis menggabungkan aliran video dan audio menggunakan MediaMuxer native Android menghasilkan berkas MP4 utuh.
- **Pemutaran Lokal Tanpa Kuota**: Buka halaman Media Unduhan di menu Pengaturan untuk memutar berkas yang telah diunduh seketika via ExoPlayer lokal tanpa jeda loading dan tanpa memakan kuota data.

### Suara Tetap Jalan di Latar Belakang

Ingin mendengarkan musik, podcast, atau rekaman sambil membalas pesan atau mengunci layar HP? Suara video akan tetap berputar di latar belakang dengan kontrol pemutar praktis di bilah notifikasi HP kamu.

### Video Mengambang Otomatis (Picture-in-Picture)

Saat sedang memutar video, kamu cukup mengusap layar ke atas atau menekan tombol Home untuk kembali ke menu utama ponsel. Video akan otomatis mengecil menjadi jendela mengambang di pojok layar sehingga kamu bisa menonton sambil membuka aplikasi lain. Di jendela mengambang ini, kamu juga bisa langsung menjeda, melompat mundur, atau berganti ke mode suara saja.

### Koleksi 31 Pilihan Tema Menarik

Tampilan aplikasi bisa disesuaikan dengan suasana hatimu. Tersedia 31 tema pilihan yang dikelompokkan dengan rapi ke dalam lima kategori:

- **Standar**: Pilihan Hitam Doff murni yang hemat baterai layar AMOLED dan Putih Bersih yang cerah dan rapi.
- **Anime & Musiman**: 10 tema estetik dengan karakter maskot anime Youno-chan, mulai dari suasana Cyberpunk Neo-Tokyo, Sakura mekar, City Pop 80-an, Mecha futuristik, hingga 4 musim Jepang (Natsu Umi di pantai musim panas, Momiji di musim gugur, Fuyu Yuki di tengah salju, kedai teh Matcha Zen, palung laut Ocean Abyss, dan lukisan tinta Sumi-e).
- **Dinamis (Material You)**: 5 tema dinamis berbasis Monet Engine yang mengekstrak palet warna dari wallpaper ponselmu secara otomatis (Material Gelap Dinamis, AMOLED Hitam Dinamis, Material Terang Dinamis, Dinamis Cerah Menyala, dan Kaca Transparan Dinamis).
- **Siklus Waktu (Smart Cycle)**: 5 tema cerdas yang otomatis berevolusi dan bertransisi mengikuti jam nyata dari pagi, siang, senja, malam, hingga tengah malam (Siklus Karakter Youno-chan, Siklus Sirkadian Ramah Mata, Siklus Panorama Alam & Musim, Siklus Estetika Digital Retro, dan Siklus Dinamis Siang & Malam).
- **Khusus & Malam**: 9 palet warna populer seperti tema Dracula, Nord dingin, Catppuccin pastel, nuansa konsol retro Game Boy, layar terminal hijau klasik, mode sepia malam yang ramah mata, hingga merah redup agar tidak silau di ruangan gelap.

Garis waktu pemutar video (slider) juga otomatis memancarkan gradien warna yang serasi dengan tema yang sedang kamu gunakan. Garis seekbar kini menempel rapat di bibir paling bawah video frame persis seperti YouTube resmi, dilengkapi baris durasi waktu dan tombol opsi di atasnya serta margin proteksi gestur saat mode layar penuh horizontal.

### Layar Pembuka Sinematik (Theme-Adaptive Splash Screen)

Pengalaman visual dimulai sejak detik pertama membuka aplikasi dengan layar pembuka berdurasi 2.5 detik:
- **Adaptif Tema & Ikon**: Logo utama otomatis menyesuaikan ikon launcher yang aktif, dipadukan pendaran cahaya (ambient glow) bersumber dari gradien tema yang kamu pilih.
- **Indikator Progres Mulus**: Dilengkapi garis progres gradien halus selama 2.5 detik dan tipografi khas JetBrains Mono.
- **Pre-loading Cerdas**: Halaman beranda langsung memuat feed video di latar belakang saat splash screen tampil, sehingga saat animasi memudar, video langsung siap ditonton tanpa jeda kosong.
- **Hanya Muncul di Cold Start**: Splash screen hanya aktif saat aplikasi pertama kali dibuka dari keadaan mati dan tidak akan mengganggu saat kembali dari aplikasi lain atau mode Picture-in-Picture.

### Kustomisasi Ikon Aplikasi (10 Pilihan Ikon Launcher)

Tampilan ikon aplikasi di layar utama ponsel dapat disesuaikan langsung dengan selera visual pengguna melalui menu Pengaturan:
- **10 Varian Ikon**: Tersedia pilihan Original YounoAds, Cyberpunk Neo, Sakura Pastel, Stealth AMOLED, Clean Light, Youno Mascot, Retro Pixel, City Pop 80s, Mecha Tactical, dan Terminal CRT.
- **Transisi Mulus Tanpa Gangguan**: Pergantian ikon berlangsung seketika tanpa menutup paksa aplikasi atau memerlukan mulai ulang ponsel berkat pengalih komponen native Android.
- **Pratinjau Visual & Haptic**: Dialog pemilihan dilengkapi pratinjau visual tajam berbasis vektor drawable dan umpan balik getaran haptik saat ikon dipilih.

### Ilustrasi Status Kosong Cerdas (Theme-Adaptive Empty State)

Saat halaman belum memiliki konten (seperti Video Tersimpan, Riwayat Tontonan, Detail Daftar Putar Kustom, atau Hasil Pencarian), tampilan status kosong otomatis menyesuaikan diri:
- **Tema Anime & Siklus Karakter**: Menampilkan artwork karakter anime tema aktif yang serasi.
- **Tema Non-Anime**: Menampilkan ikon vektor bertema yang minimalis, modern, dan bersih tanpa gambar anime.
- Seluruh teks dan tombol aksi otomatis mengikuti palet warna Material 3 tema aktif.

### Bebas Atur Kualitas Video

- **Pilih Langsung**: Saat menonton, kamu bisa memilih resolusi video sesuai selera, mulai dari resolusi hemat kuota (360p, 480p) hingga jernih Full HD (720p, 1080p).
- **Kualitas Default Otomatis**: Di menu Pengaturan, kamu bisa menetapkan resolusi bawaan favoritmu (misalnya selalu otomatis atau selalu 720p), sehingga setiap video baru yang dibuka akan langsung menyesuaikan resolusi tersebut.

### Kontrol Praktis & Gestur Ketuk Cepat

- **Lompatan Waktu Ketuk Ganda (Standar YouTube)**: Cukup ketuk dua kali di sisi kanan layar untuk melompat maju, atau sisi kiri untuk mundur. Dilengkapi jendela waktu responsif 1200ms sesuai standar resmi YouTube, kamu bisa mengetuk berulang kali untuk melompat lebih jauh (10, 15, 20 detik). Interval lompatan bisa diatur sesukamu di menu pengaturan.
- **Kecepatan Putar Fleksibel**: Video bisa diperlambat (0.5x, 0.75x) atau dipercepat (1.25x hingga 2.0x) dengan mudah.

### Lanjut Tonton Otomatis (Auto-Resume)

Tidak perlu khawatir jika tontonanmu terputus di tengah jalan. Saat video dibuka kembali di lain waktu, video akan otomatis melanjutkan pemutaran langsung dari posisi menit dan detik terakhir tanpa perlu konfirmasi berbelit-belit.

### Koleksi Pribadi: Halaman Tersimpan & Playlist Mandiri

- **Halaman Video Tersimpan Mandiri**: Akses langsung seluruh video yang kamu tandai melalui menu Video Tersimpan di pengaturan, lengkap dengan tombol Putar Semua dan opsi pembersihan simpanan.
- **Halaman Playlist Kustom Mandiri**: Buat dan kelola daftar putar buatanmu sendiri secara terpisah tanpa tercampur dengan riwayat tontonan.
- **Konfirmasi Aman Saat Menghapus**: Penghapusan riwayat tontonan kini selalu meminta konfirmasi pop-up, baik untuk menghapus satu video maupun seluruh riwayat, sehingga riwayat aman dari sentuhan tidak sengaja.

### Putar Ulang (Loop) & Lanjut Otomatis (Autoplay)

- Ingin mendengarkan satu video atau lagu secara berulang-ulang tanpa henti? Aktifkan sakelar Loop.
- Ingin santai menonton video berikutnya secara otomatis tanpa menyentuh layar? Nyalakan Autoplay.

### Rekomendasi Video Cerdas

Di bawah video yang sedang kamu putar dan di halaman beranda, tersedia deretan rekomendasi video yang segar dan bervariasi dari berbagai kreator, menyesuaikan topik yang sedang kamu tonton tanpa memonopoli satu saluran saja.

### Dukungan Penuh Dua Bahasa (Indonesia & Inggris)

Seluruh antarmuka aplikasi sudah mendukung penuh Bahasa Indonesia dan Bahasa Inggris secara sinkron, mulai dari menu navigasi, dialog opsi, format waktu tonton, jumlah tayangan dan subscriber, hingga rekomendasi wilayah video. Pilihan bahasa bisa diganti kapan saja dengan mudah melalui menu Pengaturan tanpa perlu memulai ulang aplikasi.

### Animasi Transisi Halus & Selaras

Perpindahan antar halaman dan saat membuka maupun menutup layar pemutar video kini berjalan sangat mulus dan tenang dengan animasi transisi yang seragam, tanpa hentakan maupun kedipan visual.

### Tipografi Khas JetBrains Mono

Seluruh tulisan di dalam aplikasi kini menggunakan font JetBrains Mono yang terpasang langsung di dalam aplikasi (offline). Tampilan judul, angka durasi waktu, penanda kualitas video, dan menu terlihat semakin rapi, presisi, dan berkarakter.

### Bebas Login & Privasi 100% Aman

YounoAds tidak meminta login akun Google, email, ataupun kata sandi. Seluruh data riwayat pencarian, riwayat tontonan, serta daftar simpanan hanya disimpan di memori HP kamu sendiri dan tidak pernah dikirim ke server mana pun.

### Pembaruan Otomatis di Dalam Aplikasi

Saat versi pembaruan aplikasi tersedia di GitHub, aplikasi akan menampilkan notifikasi pembaruan secara otomatis sehingga kamu bisa langsung mengunduh dan memasang versi terbaru dengan mudah.

---

## Persyaratan Sistem

- **Sistem Operasi**: Android 7.0 (Nougat) atau versi yang lebih baru
- **Arsitektur Perangkat**: Mendukung ponsel 32-bit dan 64-bit (ARM & x86)
- **Koneksi Internet**: Diperlukan untuk memutar video daring

---

## Cara Memasang Aplikasi

1. Unduh file rilis terbaru (`app-release.apk`) dari halaman Releases di repositori GitHub ini.
2. Buka file APK yang sudah selesai diunduh di ponsel Android kamu.
3. Jika muncul permintaan izin sistem, aktifkan izin untuk memasang aplikasi dari sumber ini (_Install unknown apps_).
4. Tekan tombol **Pasang** (_Install_) dan tunggu beberapa saat hingga selesai.
5. Buka **YounoAds** dan nikmati tontonanmu dengan nyaman.

---

## Pertanyaan yang Sering Diajukan (FAQ)

**Apakah saya perlu masuk dengan akun Google untuk memakai aplikasi ini?**  
Tidak. YounoAds bisa langsung digunakan sepuasnya tanpa login akun apa pun.

**Apakah riwayat tontonan dan playlist saya aman?**  
Sangat aman. Semua data disimpan secara privat di dalam penyimpanan lokal ponsel kamu dan tidak dibagikan ke pihak mana pun.

**Bagaimana cara memperbarui YounoAds ke versi terbaru?**  
Aplikasi sudah dilengkapi fitur pemeriksa pembaruan otomatis. Saat ada versi baru, notifikasi akan langsung muncul di dalam aplikasi dengan tombol unduh dan pasang.

**Bagaimana jika muncul peringatan Play Protect saat memasang atau memperbarui aplikasi?**  
Karena YounoAds didistribusikan secara mandiri di GitHub (sideloading) tanpa melalui Google Play Store, Play Protect dapat menampilkan dialog verifikasi aplikasi dari sumber luar. Anda cukup memilih opsi "Detail selengkapnya" lalu tekan "Tetap pasang" (Install anyway). Seluruh kode sumber aplikasi bersifat terbuka, bebas iklan, dan tidak memuat pelacak apa pun.

---

## Penafian (Disclaimer)

YounoAds dikembangkan sebagai proyek portofolio pribadi independen dan non-komersial.

- **Non-Komersial**: Aplikasi ini sepenuhnya gratis, tidak memuat biaya langganan, tidak menjual fitur berbayar, dan tidak memonetisasi konten dalam bentuk apa pun.
- **Independen**: Proyek ini tidak memiliki afiliasi resmi, kemitraan, ataupun dukungan dari Google LLC maupun YouTube.
- **Hak Cipta Konten**: Seluruh hak cipta video, merek dagang, nama kreator, dan konten audio-visual yang ditampilkan di dalam aplikasi adalah hak milik sah dari masing-masing kreator konten dan penyedia layanan asal.
