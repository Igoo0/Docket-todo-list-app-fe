# Script Katalon siap pakai

Isi folder ini bisa langsung ditempel ke Katalon Studio. Tidak memerlukan Object
Repository — semua TestObject dibangun di dalam `DocketKeywords.groovy`.

```
Keywords/DocketKeywords.groovy      -> Keywords/docket/DocketKeywords.groovy
TestCases/TC**.groovy               -> isi tab Script tiap Test Case
```

## Kenapa tanpa Object Repository

Berkas `.rs` adalah XML yang skemanya berbeda antar versi Katalon, dan locator baris di
aplikasi ini bersifat dinamis (bergantung nomor urut). Membangun TestObject di Groovy
menghilangkan dua masalah itu sekaligus dan tetap sah dipakai di proyek Katalon.

Kalau timmu mewajibkan Object Repository, tabel locator lengkapnya ada di
`../KATALON-SETUP.md` bagian 3 — tinggal dibuat manual lewat **New > Test Object**.

## Langkah pemasangan

1. **Buat project baru**: `File > New > Project`, tipe **Web**
2. **Pasang keyword**
   - Klik kanan folder `Keywords` > `New > Package`, beri nama `docket`
   - Klik kanan package `docket` > `New > Keyword`, beri nama `DocketKeywords`
   - Timpa seluruh isinya dengan `Keywords/DocketKeywords.groovy`
3. **Buat GlobalVariable**
   - Buka `Profiles > default`
   - Tambah variabel `baseUrl` dengan nilai `http://localhost:4173/`
4. **Buat Test Case** untuk tiap berkas di `TestCases/`
   - `File > New > Test Case`, beri nama sesuai nama berkas (mis. `TC01_EmptyState`)
   - Buka tab **Script**, timpa isinya dengan isi berkas
5. **Buat Test Suite**
   - `TS_UAT_Docket_Full` — seluruh TC kecuali TS-17
   - `TS_UAT_Docket_Smoke` — TC01, TC02, TC05, TC10, TC18
6. **Jalankan aplikasi lebih dulu**: `npm run build` lalu `npm run preview`

## Yang tercakup

| Berkas | Skenario | Menguji |
| --- | --- | --- |
| `TC01_EmptyState` | TS-01 | Penghitung `00/00`, pesan docket kosong, tombol non-aktif |
| `TC02_AddLine` | TS-02 | Penambahan, penomoran, reset kolom, day gauge |
| `TC03_RejectEmptyInput` | TS-03 | Input kosong dan spasi ditolak |
| `TC04_MaxLength` | TS-04 | Batas 240 karakter |
| `TC05_ClearLine` | TS-05 | Penandaan selesai, penghitung, tinggi batang gauge |
| `TC06_ReopenLine` | TS-06 | Pembatalan tanda selesai |
| `TC07_EditLine` | TS-07 | Ubah teks dan ketahanannya setelah muat ulang |
| `TC08_CancelEdit` | TS-08 | Esc membatalkan, termasuk cek anti-tersimpan-lewat-blur |
| `TC09_RejectBlankEdit` | TS-09 | Perubahan kosong ditolak |
| `TC10_DeleteLine` | TS-10 | Penghapusan dan pesan undo |
| `TC11_UndoDelete` | TS-11 | Pemulihan **di posisi semula** |
| `TC12_UndoExpires` | TS-12 | Jendela undo 7 detik berakhir |
| `TC13_FilterOpen` | TS-13 | Penyaringan dan penomoran docket |
| `TC14_FilterCleared` | TS-14 | Penyaringan dan pesan kosong |
| `TC15_RemoveClearedAndUndo` | TS-15 | Sapu bersih dan pemulihannya |
| `TC16_ReorderKeyboard` | TS-16 | Alt + panah, termasuk batas atas |
| `TC18_Persistence` | TS-18 | Data bertahan setelah browser ditutup |
| `TC19_QuickNavigation` | TS-19 | Shortcut `/` dan lompatan day gauge |
| `TC20_MobileLayout` | TS-20 | Tata letak 390 px |

**TS-17 (drag) tidak dibuatkan script** — Selenium tidak dapat memicu HTML5 Drag and Drop.
Otomasinya akan lulus tanpa memindahkan apa pun. Jalankan manual.

## Status verifikasi

Seluruh locator dan **52 expected value** di dalam script ini sudah diadu langsung dengan
aplikasi yang berjalan, dan semuanya cocok. Yang **belum** bisa diverifikasi dari sini:

- Kompilasi Groovy di dalam Katalon Studio
- Nama keyword `WebUI.*` pada versi Katalon yang kamu pakai

Kalau run pertama gagal, kemungkinan besar penyebabnya salah satu dari dua hal itu, bukan
selector atau nilai yang diharapkan. Keyword yang paling mungkin berbeda antar versi:
`WebUI.verifyMatch`, `WebUI.verifyNotEqual`, dan `WebUI.getAttribute`.

## Tiga hal yang membuat script ini beda dari hasil Recorder

1. **`resetDocket()` di awal setiap test case.** Aplikasi menyimpan state di
   `localStorage`. Tanpa reset, test kedua dan seterusnya membaca sisa data test
   sebelumnya. Recorder tidak akan pernah menambahkan ini.
2. **Klik `<label>`, bukan `<input>`.** Checkbox-nya berukuran 1x1 px demi aksesibilitas.
   Recorder merekam klik pada koordinat dan menghasilkan XPath absolut yang rapuh.
3. **Assert lewat `data-struck`, bukan gaya CSS.** Coretan digambar dengan
   `background-size`, jadi assert `text-decoration` akan selalu gagal.
