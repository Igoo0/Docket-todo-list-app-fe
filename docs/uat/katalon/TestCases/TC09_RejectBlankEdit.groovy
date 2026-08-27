import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// TS-09 — Menyimpan perubahan kosong ditolak (negatif)
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Kirim laporan harian ke supervisor', 'Rekonsiliasi kas kecil', 'Siram tanaman kantor'])

String sebelum = CustomKeywords.'docket.DocketKeywords.getLineText'(3)

// Langkah 1-2: kosongkan isian lalu tekan Enter
CustomKeywords.'docket.DocketKeywords.editLine'(3, '   ', true)

// Teks lama dipertahankan, baris tidak menjadi kosong
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(3), sebelum)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 3)

WebUI.takeScreenshot()
WebUI.closeBrowser()
