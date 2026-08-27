import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// TS-06 — Membatalkan tanda selesai
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Kirim laporan harian ke supervisor', 'Rekonsiliasi kas kecil'])
CustomKeywords.'docket.DocketKeywords.clearLine'(1)

// Langkah 1-2: klik ulang, centang hilang dan coretan lenyap
CustomKeywords.'docket.DocketKeywords.reopenLine'(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.isStruck'(1), false)

// Nomor baris muncul kembali di dalam kotak
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineNumber'(1), '01')

// Langkah 3: penghitung kembali
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getTally'(), '00/02')

WebUI.takeScreenshot()
WebUI.closeBrowser()
