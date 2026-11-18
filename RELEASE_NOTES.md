### YounoAds v4.10 - Catatan Rilis & Pembaruan

Pembaruan optimalisasi transisi layar, eliminasi efek ghosting, dan sinkronisasi navigasi pemutar:

1. Perbaikan Transisi Layar Antar Halaman (Pure Fast Fade)
- Menghapus efek skala mengecil (scaleIn dan scaleOut) saat berpindah layar yang sebelumnya menimbulkan efek bayangan tembus pandang (ghosting / double exposure) pada latar belakang transparan.
- Menerapkan transisi pudar cepat (fade in 140 ms, fade out 100 ms) dengan penetapan prioritas lapisan (targetContentZIndex = 1f) sehingga perpindahan antar layar terasa instan, bersih, dan tajam.

2. Sinkronisasi Animasi Keluar Pemutar Video & Bilah Bawah
- Memperbaiki koordinasi animasi saat keluar dari layar pemutar video (PlayerScreen) ke beranda atau daftar putar.
- Menghapus pergeseran vertikal pada bilah bawah serta memberikan jeda halus saat bilah navigasi dan miniplayer muncul kembali, mencegah pemutar video ganda dan bilah bawah saling bertabrakan di layar.

3. Penyempurnaan Transisi Komponen Antarmuka
- Mempercepat transisi pemuatan konten feed dari skeleton shimmer ke kartu video asli agar kerangka pemuatan tidak tertinggal di belakang thumbnail.
- Menyelaraskan kecepatan transisi bilah pencarian di bagian atas beranda.
