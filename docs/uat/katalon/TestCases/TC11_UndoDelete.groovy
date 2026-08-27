import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable

// TS-11 — Membatalkan penghapusan
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Satu pekerjaan', 'Dua pekerjaan', 'Tiga pekerjaan'])

// Hapus baris TENGAH — ini yang membuktikan pemulihan posisi, bukan sekadar
// pemulihan data. Menghapus baris terakhir tidak akan menangkap regresi
// "dikembalikan ke ujung daftar".
String sebelum = CustomKeywords.'docket.DocketKeywords.getLineText'(2)
CustomKeywords.'docket.DocketKeywords.deleteLine'(2)
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 2)

// Langkah 1: klik UNDO sebelum 7 detik. Jangan menyisipkan delay panjang di sini.
WebUI.verifyElementPresent(DK.undoButton(), 5)
WebUI.click(DK.undoButton())
WebUI.delay(1)

// Langkah 2-3: kembali ke posisi semula, bukan ke ujung daftar
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 3)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(2), sebelum)

WebUI.takeScreenshot()
WebUI.closeBrowser()
