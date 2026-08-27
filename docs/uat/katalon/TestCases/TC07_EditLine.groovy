import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// TS-07 — Mengubah teks pekerjaan
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Kirim laporan harian ke supervisor', 'Rekonsiliasi kas kecil'])

// Langkah 1-3: klik dua kali, ganti teks, simpan dengan Enter
CustomKeywords.'docket.DocketKeywords.editLine'(2, 'Revisi anggaran triwulan', true)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(2), 'Revisi anggaran triwulan')

// Langkah 4: bertahan setelah muat ulang
WebUI.refresh()
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(2), 'Revisi anggaran triwulan')

WebUI.takeScreenshot()
WebUI.closeBrowser()
