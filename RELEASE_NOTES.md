### YounoAds v4.11 - Catatan Rilis & Pembaruan

Restrukturisasi arsitektur latar belakang solid, backdrop hero beranda, dan transisi pemutar video Hybrid YouTube:

1. Restrukturisasi Latar Belakang Solid Mengikuti Tema
- Menghapus pembungkus global transparan pada level aplikasi dan menggantinya dengan kanvas solid dinamis yang mengikuti warna tema aktif (MaterialTheme.colorScheme.background).
- Menjadikan layar pemutar video (PlayerScreen) 100% solid (warna tema saat potret dan hitam pekat saat layar penuh), sehingga elemen beranda tidak akan pernah lagi tembus ke badan pemutar video.
- Menjadikan seluruh sub-halaman (Riwayat, Pengaturan, Koleksi, Daftar Putar, Saluran) memiliki latar belakang solid yang padat dan tajam.

2. Header Hero Backdrop Karakter Anime di Beranda
- Menghadirkan artwork karakter anime tema aktif sebagai banner hero di puncak beranda (FeedHeroBackdrop).
- Dilengkapi gradasi vertikal halus yang memudar lembut menyatu ke dalam warna solid tema di bawahnya, menjaga kontras logo, bilah pencarian, dan tombol filter tetap tajam dan nyaman dibaca.

3. Transisi Cerdas Hybrid YouTube
- Membuka video kini meluncur naik dari bawah (slideInVertically) menutupi layar dengan mulus layaknya aplikasi YouTube resmi.
- Menutup video meluncur turun ke bawah (slideOutVertically) menyatu kembali ke bilah miniplayer secara presisi tanpa ada tabrakan animasi maupun pemutar video ganda.
- Menambahkan popTransitionSpec pada navigasi dua arah sehingga tombol Back dan gestur Predictive Back berjalan rapi dan konsisten.
