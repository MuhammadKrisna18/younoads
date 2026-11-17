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
