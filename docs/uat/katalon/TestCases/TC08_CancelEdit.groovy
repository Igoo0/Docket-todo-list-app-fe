import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// TS-08 — Membatalkan perubahan dengan Esc
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Kirim laporan harian ke supervisor', 'Rekonsiliasi kas kecil', 'Siram tanaman kantor'])

// Langkah 1: catat teks apa adanya
String sebelum = CustomKeywords.'docket.DocketKeywords.getLineText'(3)

// Langkah 2-3: ubah lalu tekan Esc
CustomKeywords.'docket.DocketKeywords.editLine'(3, 'TEKS SALAH', false)

// Langkah 4: teks lama harus utuh
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(3), sebelum)

// Pastikan juga tidak diam-diam tersimpan lewat blur saat kolom isian ditutup
WebUI.refresh()
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(3), sebelum)

WebUI.takeScreenshot()
WebUI.closeBrowser()
