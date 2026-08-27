# UAT Test Script — Docket

Dokumen ini dipakai tester bisnis. Setiap skenario ditulis dalam bahasa pengguna, bukan
bahasa implementasi. Kolom **Hasil**, **Status**, dan **Bukti** diisi saat eksekusi.

- **Aplikasi**: Docket — daftar pekerjaan harian
- **Versi yang diuji**: _isi saat eksekusi_
- **Tanggal eksekusi**: _isi saat eksekusi_
- **Tester**: _isi saat eksekusi_

---

## 1. Lingkungan pengujian

| Item | Nilai |
| --- | --- |
| URL | `http://localhost:4173/` |
| Cara menjalankan | `npm run build` lalu `npm run preview` |
| Browser | Chrome / Edge versi terkini |
| Resolusi desktop | 1280 x 900 |
| Resolusi mobile | 390 x 800 |
| Penyimpanan data | `localStorage`, key `docket.tasks.v1` (lokal di browser, tanpa server) |

> Jalankan UAT terhadap **hasil build** (`npm run preview`), bukan dev server (`npm run dev`).
> Dev server memuat modul mentah dan punya hot-reload, jadi bukan representasi produksi.

### Prosedur reset (WAJIB sebelum tiap skenario)

Aplikasi menyimpan data di browser, jadi sisa data skenario sebelumnya akan mencemari
hasil. Sebelum setiap skenario:

1. Buka DevTools (`F12`) lalu pilih tab **Console**
2. Jalankan `localStorage.clear()`
3. Tekan `F5`

Di Katalon langkah ini diotomatiskan — lihat `KATALON-SETUP.md`.

### Istilah

| Istilah di UI | Arti |
| --- | --- |
| **Line** | Satu baris pekerjaan |
| **Docket** | Keseluruhan daftar hari itu |
| **Clear** | Menandai pekerjaan selesai |
| **Open** | Pekerjaan yang belum selesai |
| **Day gauge** | Batang indikator di bawah judul hari; satu batang mewakili satu line |

---

## 2. Skenario pengujian

Legenda kolom **Otomasi**: `A` = bisa diotomasi penuh di Katalon, `M` = wajib manual.

### TS-01 — Membuka docket kosong `A`

**Tujuan bisnis**: Pengguna baru langsung paham apa yang harus dilakukan.
**Prasyarat**: Reset sudah dijalankan.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Buka URL aplikasi | Halaman tampil dengan judul nama hari ini (mis. "Monday") | | | |
| 2 | Perhatikan bagian atas kanan | Penghitung menampilkan `00/00` dengan label `CLEARED` | | | |
| 3 | Perhatikan area daftar | Tampil pesan "Nothing on today's docket. Write the first line above." | | | |
| 4 | Perhatikan tombol `Add line` | Tombol dalam keadaan non-aktif (disabled) | | | |

---

### TS-02 — Menambahkan pekerjaan baru `A`

**Tujuan bisnis**: Pengguna dapat mencatat pekerjaan secepat mengetik.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Klik kolom "What needs doing?" | Kursor masuk ke kolom | | | |
| 2 | Ketik `Kirim laporan harian ke supervisor` | Teks tampil, tombol `Add line` menjadi aktif | | | |
| 3 | Tekan `Enter` | Baris baru muncul di **bawah** daftar dengan nomor `01` | | | |
| 4 | Perhatikan kolom input | Kolom kembali kosong dan siap menerima entri berikutnya | | | |
| 5 | Perhatikan penghitung | Berubah menjadi `00/01` | | | |
| 6 | Perhatikan day gauge | Bertambah satu batang pendek | | | |
| 7 | Tambahkan 4 pekerjaan lain dengan cara yang sama | Nomor baris berurutan `01` sampai `05`, penghitung `00/05` | | | |

---

### TS-03 — Input kosong ditolak `A` (negatif)

**Tujuan bisnis**: Daftar tidak boleh terisi baris kosong.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Kosongkan kolom input, tekan `Enter` | Tidak ada baris baru dibuat | | | |
| 2 | Isi kolom dengan 5 spasi | Tombol `Add line` tetap non-aktif | | | |
| 3 | Tekan `Enter` | Tidak ada baris baru dibuat, jumlah baris tidak berubah | | | |

---

### TS-04 — Batas panjang teks 240 karakter `A` (boundary)

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Tempel teks sepanjang 300 karakter ke kolom input | Kolom hanya menerima 240 karakter pertama | | | |
| 2 | Tekan `Enter` | Baris tersimpan dengan 240 karakter, teks membungkus ke beberapa baris | | | |
| 3 | Selesaikan baris tersebut | Garis coret menandai **setiap** baris teks yang membungkus, bukan hanya baris pertama | | | |

---

### TS-05 — Menandai pekerjaan selesai `A`

**Tujuan bisnis**: Menyelesaikan pekerjaan harus terasa satu klik dan terlihat jelas.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Klik kotak bernomor `01` di kiri baris pertama | Kotak menjadi biru dan menampilkan tanda centang | | | |
| 2 | Perhatikan teks pekerjaan | Tercoret garis biru dari kiri ke kanan | | | |
| 3 | Perhatikan penghitung | Angka kiri bertambah satu (mis. `01/05`) | | | |
| 4 | Perhatikan day gauge | Batang untuk baris tersebut memanjang dan berwarna biru | | | |

---

### TS-06 — Membatalkan tanda selesai `A`

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Klik kembali kotak centang baris yang sama | Centang hilang, nomor baris muncul lagi | | | |
| 2 | Perhatikan teks | Garis coret hilang | | | |
| 3 | Perhatikan penghitung | Kembali ke `00/05` | | | |

---

### TS-07 — Mengubah teks pekerjaan `A`

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Klik dua kali pada teks baris ke-2 | Teks berubah menjadi kolom isian dengan garis bawah biru | | | |
| 2 | Hapus isinya, ketik `Revisi anggaran triwulan` | Teks baru tampil di kolom isian | | | |
| 3 | Tekan `Enter` | Kolom isian tertutup, baris menampilkan teks baru | | | |
| 4 | Muat ulang halaman (`F5`) | Teks hasil perubahan tetap tersimpan | | | |

---

### TS-08 — Membatalkan perubahan `A`

**Tujuan bisnis**: Salah ketik saat mengedit tidak boleh merusak data lama.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Catat teks baris ke-3 apa adanya | — | | | |
| 2 | Klik dua kali baris ke-3, ganti isinya dengan `TEKS SALAH` | Kolom isian berisi `TEKS SALAH` | | | |
| 3 | Tekan `Esc` | Kolom isian tertutup | | | |
| 4 | Bandingkan dengan langkah 1 | Teks kembali persis seperti semula, `TEKS SALAH` **tidak** tersimpan | | | |

---

### TS-09 — Menyimpan perubahan kosong ditolak `A` (negatif)

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Klik dua kali baris ke-3, hapus seluruh isinya | Kolom isian kosong | | | |
| 2 | Tekan `Enter` | Kolom tertutup dan teks lama dipertahankan (baris tidak menjadi kosong) | | | |

---

### TS-10 — Menghapus pekerjaan `A`

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Arahkan kursor ke baris ke-5 | Muncul tombol `EDIT` dan `DEL` di sisi kanan baris | | | |
| 2 | Klik `DEL` | Baris hilang dari daftar | | | |
| 3 | Perhatikan penghitung dan day gauge | Jumlah total berkurang satu, satu batang gauge hilang | | | |
| 4 | Perhatikan bagian bawah layar | Muncul bar berisi `Deleted "<teks baris>"` dan tombol `UNDO` | | | |

---

### TS-11 — Membatalkan penghapusan `A`

**Tujuan bisnis**: Salah hapus harus bisa dipulihkan, termasuk posisinya.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Lanjutkan dari TS-10, klik `UNDO` **sebelum 7 detik** | Baris kembali muncul | | | |
| 2 | Perhatikan posisi baris | Kembali ke urutan semula, bukan di akhir daftar | | | |
| 3 | Perhatikan penghitung | Kembali ke jumlah sebelum penghapusan | | | |

---

### TS-12 — Kesempatan undo berakhir setelah 7 detik `A` (boundary)

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Hapus satu baris dengan `DEL` | Bar undo muncul | | | |
| 2 | Tunggu 8 detik tanpa menyentuh apa pun | Bar undo hilang dengan sendirinya | | | |
| 3 | Muat ulang halaman | Baris yang dihapus tidak kembali | | | |

---

### TS-13 — Menyaring pekerjaan yang belum selesai `A`

**Prasyarat**: Ada minimal 1 baris selesai dan 2 baris belum selesai.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Klik tab `OPEN` | Tab `OPEN` bergaris bawah biru | | | |
| 2 | Perhatikan daftar | Hanya baris belum selesai yang tampil | | | |
| 3 | Perhatikan nomor baris pertama | Nomor tetap mengikuti urutan docket asli (mis. `02`), **bukan** dinomori ulang jadi `01` | | | |
| 4 | Klik tab `ALL` | Seluruh baris tampil kembali | | | |

---

### TS-14 — Menyaring pekerjaan yang sudah selesai `A`

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Klik tab `CLEARED` | Hanya baris selesai yang tampil, semuanya tercoret | | | |
| 2 | Batalkan semua tanda selesai, lalu klik `CLEARED` lagi | Tampil pesan "No lines cleared yet." | | | |

---

### TS-15 — Membersihkan pekerjaan selesai dan membatalkannya `A`

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Pastikan ada minimal 2 baris selesai | Tautan `REMOVE CLEARED` tampil di kanan baris tab | | | |
| 2 | Klik `REMOVE CLEARED` | Seluruh baris selesai hilang; baris belum selesai tetap utuh | | | |
| 3 | Perhatikan bar bawah | Muncul `N cleared lines removed` dengan tombol `UNDO` | | | |
| 4 | Klik `UNDO` | Seluruh baris yang tersapu kembali, lengkap dengan urutan dan status selesainya | | | |
| 5 | Batalkan semua tanda selesai | Tautan `REMOVE CLEARED` menghilang dari layar | | | |

---

### TS-16 — Mengubah urutan prioritas dengan papan ketik `A`

**Tujuan bisnis**: Urutan baris adalah urutan prioritas, jadi harus bisa diatur.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Klik kotak nomor baris `01` dua kali (menandai lalu membatalkan) agar fokus berada di baris itu | Status baris kembali seperti semula | | | |
| 2 | Tekan `Alt` + `panah bawah` | Baris turun satu posisi; penomoran baris menyesuaikan | | | |
| 3 | Tekan `Alt` + `panah atas` | Baris kembali ke posisi semula | | | |
| 4 | Tekan `Alt` + `panah atas` sekali lagi di baris teratas | Tidak terjadi apa-apa (tidak error, tidak berpindah) | | | |

---

### TS-17 — Mengubah urutan prioritas dengan drag `M` (manual)

> Tidak dapat diotomasi oleh Katalon/Selenium — lihat catatan di `KATALON-SETUP.md`.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Arahkan kursor ke baris ke-1 | Muncul gagang titik-titik di ujung kiri baris | | | |
| 2 | Tekan dan tahan gagang tersebut | Kursor berubah menjadi tanda genggam | | | |
| 3 | Seret ke posisi baris ke-3, lalu lepaskan | Baris pindah ke posisi ke-3, penomoran menyesuaikan | | | |
| 4 | Muat ulang halaman | Urutan hasil seret tetap tersimpan | | | |

---

### TS-18 — Data bertahan setelah browser ditutup `A`

**Tujuan bisnis**: Pekerjaan tidak boleh hilang saat browser ditutup.

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Catat isi dan urutan seluruh baris | — | | | |
| 2 | Tutup tab browser, buka kembali URL aplikasi | Seluruh baris tampil dengan isi, urutan, dan status selesai yang sama | | | |
| 3 | Buka URL di jendela **Incognito** | Docket kosong (data tidak dibagi antar profil browser) | | | |

---

### TS-19 — Navigasi cepat `A`

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Klik area kosong halaman, tekan tombol `/` | Kursor melompat ke kolom "What needs doing?" | | | |
| 2 | Perhatikan isi kolom | Kolom tetap kosong — karakter `/` tidak ikut terketik | | | |
| 3 | Tekan `Esc` | Fokus keluar dari kolom | | | |
| 4 | Klik salah satu batang pada day gauge | Halaman menggulir ke baris terkait dan baris tersebut mendapat fokus | | | |

---

### TS-20 — Tampilan di layar ponsel `A`

| # | Langkah | Expected Result | Hasil | Status | Bukti |
| --- | --- | --- | --- | --- | --- |
| 1 | Ubah lebar layar menjadi 390 px | Tata letak menyesuaikan satu kolom | | | |
| 2 | Perhatikan baris tab penyaring | `ALL` / `OPEN` / `CLEARED` tetap dalam satu baris, tidak terpotong | | | |
| 3 | Perhatikan baris pekerjaan | Tombol `EDIT` dan `DEL` selalu terlihat tanpa perlu hover | | | |
| 4 | Tambah dan selesaikan satu pekerjaan | Berfungsi sama seperti pada layar desktop | | | |

---

## 3. Matriks keterlacakan (RTM)

| Fitur | Skenario |
| --- | --- |
| Mencatat pekerjaan | TS-02, TS-03, TS-04 |
| Menyelesaikan pekerjaan | TS-05, TS-06 |
| Mengubah pekerjaan | TS-07, TS-08, TS-09 |
| Menghapus dan pemulihan | TS-10, TS-11, TS-12, TS-15 |
| Menyaring daftar | TS-13, TS-14 |
| Mengatur prioritas | TS-16, TS-17 |
| Ketahanan data | TS-18 |
| Kemudahan penggunaan | TS-01, TS-19, TS-20 |

## 4. Ringkasan eksekusi

| Total skenario | Lulus | Gagal | Tidak diuji |
| --- | --- | --- | --- |
| 20 | | | |

**Catatan penguji**:

**Kesimpulan** (diterima / diterima dengan catatan / ditolak):
