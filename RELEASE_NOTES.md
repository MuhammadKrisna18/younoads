### YounoAds v3.5 - Catatan Rilis & Pembaruan

Pembaruan fitur kenyamanan pemutaran video dengan interval lompatan cepat yang dapat dikonfigurasi serta mekanisme ketukan berulang akumulatif:

1. Konfigurasi Interval Lompatan Cepat (Configurable Seek Intervals)
- Menambahkan opsi konfigurasi interval lompatan seek (5, 10, 15, dan 20 detik) untuk ketuk ganda (double-tap to seek).
- Pengaturan dapat diakses dengan mudah melalui Lembar Pengaturan Pemutar (Player Settings Bottom Sheet) dengan ikon Lewati Cepat.
- Dialog pemilihan terstruktur (SeekIntervalSelectionDialog) beraksen neon Electric Cyan lengkap dengan penanda centang aktif.
- Pilihan interval pengguna disimpan secara persisten di perangkat lokal melalui SharedPreferences, sehingga tetap terjaga saat aplikasi ditutup dan dibuka kembali.

2. Ketukan Berulang Akumulatif (Accumulative Rapid Seeking)
- Ketukan berurutan cepat (double-tap, triple-tap, quad-tap, dst.) pada sisi kanan atau kiri pemutar kini menambah total detik lompatan secara bertahap (misalnya pada interval 5 detik: double-tap = 10 detik, triple-tap = 15 detik, quad-tap = 20 detik).
- Teks visual indikator seek menampilkan durasi akumulasi total secara langsung (+10 detik, +15 detik, +20 detik, dst.).
- Dilengkapi sistem intersepsi ketukan pintar (tap interceptor) yang mencegah ketukan ketiga dan seterusnya memunculkan atau menyembunyikan kontrol pemutar secara tidak sengaja.
- Umpan balik haptik responsif pada setiap ketukan bertahap.

3. Peningkatan Lembar Pengaturan Pemutar (Player Settings Bottom Sheet)
- Penataan opsi pengaturan yang semakin lengkap dan intuitif, menyatukan Kualitas Resolusi, Kecepatan Pemutaran, Interval Lompatan Cepat, Trek Audio, Pengulangan Loop, dan Autoplay dalam satu antarmuka modal terpadu.

4. Peningkatan Efisiensi & Kinerja Gestur
- Manajemen state gestur yang lebih terisolasi pada VideoPlayerSection dan PlayerViewModel guna mencegah rekomposisi yang tidak perlu selama proses seek berlangsung.
