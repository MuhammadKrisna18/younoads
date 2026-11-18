### YounoAds v4.13 - Catatan Rilis & Pembaruan

Pembaruan transisi navigasi global Seamless Crossfade, integrasi tema dinamis pada pemutar video dan layar status kosong, serta peningkatan kenyamanan pemutaran:

1. Transisi Navigasi Global Seamless Crossfade
- Menyeragamkan seluruh transisi halaman menggunakan animasi Seamless Crossfade (fadeIn 160ms dan fadeOut 130ms) yang tersentralisasi pada NavigationTransitions.
- Menghadirkan ilusi visual yang memukau di mana wallpaper karakter tema tetap stabil dan diam sempurna saat berpindah halaman tanpa ada robekan maupun tabrakan animasi.

2. Wallpaper Karakter Tema pada Layar Pemutar Video
- Menghadirkan artwork karakter tema aktif pada layar pemutar video (PlayerScreen) saat mode potret dengan gradasi scrim gelap yang elegan di balik detail dan rekomendasi video.
- Menjaga area video player (16:9) serta mode layar penuh (landscape) tetap 100% hitam pekat untuk memaksimalkan fokus menonton.

3. Ilustrasi Status Kosong Dinamis Mengikuti Tema (Theme-Adaptive Empty State)
- Halaman tanpa konten (Video Tersimpan, Riwayat Tontonan, Detail Daftar Putar Kustom, dan Hasil Pencarian) kini secara cerdas menyesuaikan tampilannya dengan tema aktif.
- Pada tema anime: Menampilkan karakter anime dari tema aktif yang sedang dipilih (Cyberpunk, Sakura Lo-Fi, City Pop, Mecha, Natsu Umi, Momiji, Fuyu Yuki, Matcha Zen, Ocean Abyss, Sumi-e).
- Pada tema non-anime: Menampilkan ikon vektor minimalis modern (Bookmark, History, Playlist, Search) di dalam lencana bertema tanpa menampilkan ilustrasi anime.
- Seluruh teks dan tombol pada status kosong kini dinamis mengikuti palet warna Material 3 tema aktif.

4. Penyesuaian Durasi Double Tap to Seek (Standar YouTube)
- Memperpanjang toleransi waktu ketukan beruntun dan tampilan indikator visual lompat waktu dari 800ms menjadi 1200ms mengikuti standar resmi YouTube.
- Memberikan kenyamanan lebih bagi jari pengguna untuk menambah lompatan durasi video tanpa terburu-buru.

5. Perapihan Komponen Pemutar Video
- Menghapus tombol duplikat Salin Tautan pada baris aksi PlayerScreen karena fungsi salin tautan (dengan atau tanpa stempel waktu) sudah terintegrasi secara lengkap di dalam panel Berbagi (Share).
- Membersihkan dead code dan parameter tak terpakai pada komponen PlayerActionPillRow dan PlayerDetailContent.
