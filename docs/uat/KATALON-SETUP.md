# Katalon Studio — panduan otomasi Docket

Pendamping teknis untuk `UAT-TEST-SCRIPT.md`. Berisi locator yang sudah diverifikasi
terhadap DOM aplikasi, potongan Groovy untuk operasi yang tidak lazim, dan daftar jebakan
yang akan membuat test gagal kalau diabaikan.

---

## 1. Jebakan yang wajib dibaca duluan

### 1.1 Checkbox berukuran 1x1 piksel — klik `<label>`, bukan `<input>`

Kotak centang aslinya sengaja disembunyikan secara visual (teknik `sr-only`) demi
aksesibilitas; yang terlihat sebagai kotak bernomor adalah `<label>` pembungkusnya.

| Elemen | Ukuran render | Bisa diklik Selenium |
| --- | --- | --- |
| `input[type=checkbox]` | 1 x 1 px, ter-`clip` | Tidak — `ElementNotInteractableException` |
| `label` pembungkus | 28 x 28 px | Ya |

Selalu targetkan `label`. Klik pada `label` tetap mengubah status checkbox karena keduanya
berelasi secara native di HTML.

### 1.2 Status selesai bukan `text-decoration`

Coretan digambar memakai CSS `background-image` + `background-size`, bukan
`text-decoration: line-through`. Jadi jangan pernah assert properti CSS coretan.

Gunakan atribut yang memang disediakan untuk itu:

```
//section//ul/li[1]//span[@data-struck='true']
```

`data-struck` bernilai `"true"` atau `"false"` dan berubah seketika saat status berubah.

### 1.3 Drag and drop tidak bisa diotomasi

Fitur seret memakai **HTML5 Drag and Drop API** (event `dragstart` / `dragover` / `drop`).
Selenium — dan karenanya keyword `WebUI.dragAndDropToObject` di Katalon — mensimulasikan
gerakan mouse, yang **tidak** memicu event HTML5 tersebut. Test akan tampak jalan tetapi
urutan baris tidak berubah.

Pilihan yang tersedia:

- **Disarankan**: jalankan TS-17 secara manual, dan otomasikan TS-16 (`Alt` + panah) yang
  memberi cakupan fungsional setara untuk logika pengurutan.
- Alternatif: suntikkan shim JavaScript pemicu event HTML5 lewat `WebUI.executeJavaScript`.
  Berhasil, tetapi yang diuji jadi shim-nya, bukan interaksi pengguna sebenarnya.

### 1.4 Bar undo hilang otomatis setelah 7 detik

Jangan menaruh `WebUI.delay()` panjang antara aksi hapus dan klik `UNDO`. Untuk TS-12 yang
justru menguji kedaluwarsanya, gunakan `WebUI.waitForElementNotPresent(..., 10)`.

### 1.5 Animasi coretan berdurasi sekitar 340 ms

Jangan assert langsung setelah klik. Pakai penantian berbasis kondisi, bukan `delay` tetap:

```groovy
WebUI.verifyElementAttributeValue(lineText(1), 'data-struck', 'true', 10)
```

### 1.6 Tombol EDIT / DEL transparan sampai di-hover

Pada lebar desktop tombol aksi memakai `opacity: 0` hingga barisnya di-hover. Selenium
tetap bisa mengkliknya (opacity bukan `visibility`), tetapi `verifyElementVisible` akan
lulus meski tombolnya tidak terlihat mata. Kalau TS-10 langkah 1 ingin diuji sungguhan,
lakukan `WebUI.mouseOver` pada barisnya lalu periksa nilai `opacity` yang dihitung.

### 1.7 Nomor baris mengikuti urutan docket, bukan urutan tampilan

Saat filter `OPEN` aktif, baris pertama bisa saja bernomor `02`. Ini perilaku yang
disengaja (lihat TS-13 langkah 3), bukan bug. Karena itu locator berbasis indeks posisi
(`li[1]`) dan nomor yang tampil bisa berbeda — jangan mencampur keduanya.

### 1.8 Uji terhadap build produksi

Jalankan `npm run build` lalu `npm run preview` (port 4173). Dev server pada port 5173
menyajikan modul mentah dan memasang klien hot-reload yang tidak ada di produksi.
Karena `localStorage` terikat pada origin, berganti port juga berarti data mulai bersih.

---

## 2. Struktur project yang disarankan

```
Object Repository/
  Docket/
    Page/            inp_NewLine, btn_AddLine, lbl_Tally, txt_EmptyState
    Filter/          tab_All, tab_Open, tab_Cleared, lnk_RemoveCleared
    Undo/            btn_Undo, txt_UndoMessage
    Line/            (dibuat dinamis lewat helper, lihat bagian 4)

Test Cases/
  Common/            TC_Reset, TC_SeedFiveLines
  Functional/        TC01_EmptyState ... TC20_MobileLayout

Test Suites/
  TS_UAT_Docket_Full          (TC01-TC20, tanpa TC17)
  TS_UAT_Docket_Smoke         (TC01, TC02, TC05, TC10, TC18)

Profiles/
  default            baseUrl = http://localhost:4173/
```

Simpan `baseUrl` sebagai `GlobalVariable` supaya suite yang sama bisa diarahkan ke
lingkungan lain tanpa mengubah test case.

---

## 3. Object Repository

> Script siap pakai di `katalon/` TIDAK memerlukan bagian ini — TestObject dibangun
> langsung di Groovy. Tabel di bawah disediakan kalau timmu mewajibkan Object Repository.

Semua locator di bawah sudah diverifikasi terhadap DOM yang berjalan.

### Statis

| Nama objek | Strategi | Locator |
| --- | --- | --- |
| `Page/inp_NewLine` | CSS | `#new-line` |
| `Page/btn_AddLine` | CSS | `form button[type="submit"]` |
| `Page/lbl_Tally` | XPath | `(//header//p)[last()]` |
| `Page/txt_EmptyState` | XPath | `//section//p` |
| `Page/lst_Lines` | XPath | `//section//ul/li` |
| `Filter/tab_All` | XPath | `//button[@aria-pressed][contains(., 'ALL')]` |
| `Filter/tab_Open` | XPath | `//button[@aria-pressed][contains(., 'OPEN')]` |
| `Filter/tab_Cleared` | XPath | `//button[@aria-pressed][contains(., 'CLEARED')]` |
| `Filter/lnk_RemoveCleared` | XPath | `//button[normalize-space()='REMOVE CLEARED']` |
| `Undo/btn_Undo` | XPath | `//button[normalize-space()='UNDO']` |
| `Undo/txt_UndoMessage` | XPath | `//button[normalize-space()='UNDO']/preceding-sibling::span` |
| `Page/lst_GaugeBars` | XPath | `//div[@role='group']//button` |
| `Page/inp_EditLine` | XPath | `//input[@aria-label='Edit line']` |

Tab penyaring aktif ditandai `aria-pressed="true"` — pakai itu untuk TS-13 langkah 1:

```groovy
WebUI.verifyElementAttributeValue(findTestObject('Docket/Filter/tab_Open'), 'aria-pressed', 'true', 10)
```

### Dinamis (per baris)

| Kegunaan | XPath (`${n}` = nomor urut baris tampil, mulai 1) |
| --- | --- |
| Kotak centang / nomor | `(//section//ul/li)[${n}]//label` |
| Teks pekerjaan | `(//section//ul/li)[${n}]//span[@data-struck]` |
| Nomor baris tampil | `(//section//ul/li)[${n}]//label/span[1]` |
| Gagang seret | `(//section//ul/li)[${n}]//span[@title='Drag to reorder']` |
| Tombol EDIT | `(//section//ul/li)[${n}]//button[starts-with(@aria-label,'Edit line')]` |
| Tombol DEL | `(//section//ul/li)[${n}]//button[starts-with(@aria-label,'Delete line')]` |

Kalau lebih suka menyasar berdasarkan **nomor docket** ketimbang posisi tampil, gunakan
`aria-label` yang memang memuat nomornya:

```
//button[starts-with(@aria-label,'Delete line 3:')]
```

---

## 4. Keyword kustom

Simpan sebagai `Keywords/docket/DocketKeywords.groovy`.

```groovy
package docket

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.driver.DriverFactory
import org.openqa.selenium.Keys
import org.openqa.selenium.interactions.Actions

class DocketKeywords {

    /** Membangun TestObject dari XPath tanpa perlu entri Object Repository. */
    private static TestObject byXpath(String name, String xpath) {
        TestObject to = new TestObject(name)
        to.addProperty('xpath', ConditionType.EQUALS, xpath)
        return to
    }

    private static String row(int n) { return "(//section//ul/li)[${n}]" }

    static TestObject toggle(int n)   { byXpath("toggle${n}",  row(n) + "//label") }
    static TestObject lineText(int n) { byXpath("text${n}",    row(n) + "//span[@data-struck]") }
    static TestObject lineNo(int n)   { byXpath("no${n}",      row(n) + "//label/span[1]") }

    /**
     * Mengosongkan data aplikasi. WAJIB dipanggil di awal setiap test case —
     * aplikasi menyimpan state di localStorage sehingga test saling mencemari.
     */
    @Keyword
    def resetDocket(String baseUrl) {
        WebUI.navigateToUrl(baseUrl)
        WebUI.executeJavaScript('window.localStorage.clear()', null)
        WebUI.refresh()
        WebUI.waitForElementPresent(byXpath('field', "//input[@id='new-line']"), 10)
    }

    @Keyword
    def addLine(String text) {
        TestObject field = byXpath('field', "//input[@id='new-line']")
        WebUI.setText(field, text)
        WebUI.sendKeys(field, Keys.chord(Keys.ENTER))
    }

    /** Klik LABEL, bukan input — input-nya hanya 1x1 px dan tidak interactable. */
    @Keyword
    def clearLine(int n) {
        WebUI.click(toggle(n))
        WebUI.verifyElementAttributeValue(lineText(n), 'data-struck', 'true', 10)
    }

    @Keyword
    def verifyStruck(int n, boolean expected) {
        WebUI.verifyElementAttributeValue(lineText(n), 'data-struck', String.valueOf(expected), 10)
    }

    @Keyword
    def getLineText(int n) {
        return WebUI.getText(lineText(n)).trim()
    }

    /**
     * Memindahkan baris dengan Alt + panah. Fokus dipasang lewat JavaScript karena
     * checkbox-nya tersembunyi secara visual sehingga sendKeys biasa akan ditolak.
     */
    @Keyword
    def moveLine(int n, boolean down) {
        WebUI.executeJavaScript(
            "document.querySelectorAll('section ul li')[${n - 1}]" +
            ".querySelector('input[type=checkbox]').focus()", null)
        Actions act = new Actions(DriverFactory.getWebDriver())
        act.keyDown(Keys.ALT)
           .sendKeys(down ? Keys.ARROW_DOWN : Keys.ARROW_UP)
           .keyUp(Keys.ALT)
           .perform()
        WebUI.delay(1)
    }

    /** Menekan "/" pada dokumen untuk memindahkan fokus ke kolom input. */
    @Keyword
    def pressSlashShortcut() {
        new Actions(DriverFactory.getWebDriver()).sendKeys('/').perform()
    }

    @Keyword
    def getFocusedElementId() {
        return WebUI.executeJavaScript('return document.activeElement.id', null)
    }

    @Keyword
    def countLines() {
        return WebUI.executeJavaScript(
            "return document.querySelectorAll('section ul li').length", null)
    }
}
```

---

## 5. Contoh test case

### TC05 — Menandai pekerjaan selesai

```groovy
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)

CustomKeywords.'docket.DocketKeywords.addLine'('Kirim laporan harian ke supervisor')
CustomKeywords.'docket.DocketKeywords.addLine'('Rekonsiliasi kas kecil')

// Langkah 1-2: selesaikan baris pertama
CustomKeywords.'docket.DocketKeywords.clearLine'(1)

// Langkah 3: penghitung bertambah
WebUI.verifyElementText(findTestObject('Docket/Page/lbl_Tally'), '01/02')

// Langkah 4: batang gauge memanjang
def h = WebUI.executeJavaScript(
    "return document.querySelector(\"div[role='group'] button\").getBoundingClientRect().height", null)
WebUI.verifyGreaterThan(h as double, 20d)

WebUI.closeBrowser()
```

### TC11 — Undo mengembalikan baris ke posisi semula

```groovy
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
['Satu', 'Dua', 'Tiga'].each { CustomKeywords.'docket.DocketKeywords.addLine'(it) }

String before = CustomKeywords.'docket.DocketKeywords.getLineText'(2)

WebUI.click(findTestObject('Docket/Line/btn_Del_Line2'))
WebUI.verifyElementPresent(findTestObject('Docket/Undo/btn_Undo'), 5)

// harus dalam 7 detik
WebUI.click(findTestObject('Docket/Undo/btn_Undo'))
WebUI.delay(1)

// yang diuji bukan sekadar "kembali", tapi "kembali ke posisi yang sama"
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(2), before)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 3)
```

### TC12 — Undo kedaluwarsa

```groovy
WebUI.click(findTestObject('Docket/Line/btn_Del_Line1'))
WebUI.verifyElementPresent(findTestObject('Docket/Undo/btn_Undo'), 5)
WebUI.waitForElementNotPresent(findTestObject('Docket/Undo/btn_Undo'), 10)
WebUI.refresh()
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 2)
```

---

## 6. Data-driven untuk TS-02 dan TS-04

Katalon kuat di sisi ini. Buat `Data Files/lines.xlsx`:

| text | expectedLength | shouldAppear |
| --- | --- | --- |
| Kirim laporan harian ke supervisor | 34 | yes |
| (240 karakter) | 240 | yes |
| (300 karakter) | 240 | yes |
| (kosong) | 0 | no |
| (5 spasi) | 0 | no |

Ikat ke satu test case, lalu jalankan sebagai Test Suite dengan data binding. Lima baris
pengujian batas tanpa lima test case terpisah.

---

## 7. Bukti eksekusi untuk lampiran UAT

- Aktifkan screenshot pada kegagalan: **Project Settings > Execution > Take screenshot when
  execution fails**
- Tambahkan `WebUI.takeScreenshot()` di akhir tiap test case, bukan hanya saat gagal —
  lampiran UAT butuh bukti langkah berhasil juga, bukan cuma jejak error
- Simpan report HTML/PDF per eksekusi, beri nama sesuai ID skenario supaya kolom **Bukti**
  di `UAT-TEST-SCRIPT.md` bisa merujuk berkasnya langsung
- Untuk eksekusi di CI tanpa Katalon Studio, gunakan Katalon Runtime Engine (berlisensi
  terpisah) — perhitungkan ini kalau UAT harus jalan otomatis di pipeline
