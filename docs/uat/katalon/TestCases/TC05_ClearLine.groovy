import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// TS-05 — Menandai pekerjaan selesai
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Kirim laporan harian ke supervisor', 'Rekonsiliasi kas kecil'])

// Langkah 1-2: klik kotak bernomor, teks tercoret
// clearLine mengklik LABEL, bukan input (input hanya 1x1 px, tidak interactable)
CustomKeywords.'docket.DocketKeywords.clearLine'(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.isStruck'(1), true)

// Langkah 3: penghitung bertambah
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getTally'(), '01/02')

// Langkah 4: batang gauge memanjang (26 px saat selesai, 8 px saat terbuka)
double tinggi = CustomKeywords.'docket.DocketKeywords.getGaugeBarHeight'(1)
if (tinggi > 20) {
    KeywordUtil.markPassed('Batang gauge memanjang: ' + tinggi + ' px')
} else {
    KeywordUtil.markFailed('Batang gauge tidak memanjang: ' + tinggi + ' px')
}

WebUI.takeScreenshot()
WebUI.closeBrowser()
