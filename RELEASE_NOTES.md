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
