import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// TS-18 — Data bertahan setelah browser ditutup
// Langkah 3 (jendela Incognito) dijalankan manual, tidak termasuk di sini.
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Satu pekerjaan', 'Dua pekerjaan', 'Tiga pekerjaan'])
CustomKeywords.'docket.DocketKeywords.clearLine'(2)
CustomKeywords.'docket.DocketKeywords.moveLine'(1, true)

// Langkah 1: catat kondisi
List sebelum = CustomKeywords.'docket.DocketKeywords.getAllLineTexts'()
String tallySebelum = CustomKeywords.'docket.DocketKeywords.getTally'()

// Langkah 2: tutup lalu buka lagi alamat yang sama
WebUI.closeBrowser()
WebUI.openBrowser('')
WebUI.maximizeWindow()
WebUI.navigateToUrl(GlobalVariable.baseUrl)
WebUI.delay(2)

WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getAllLineTexts'(), sebelum)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getTally'(), tallySebelum)

// Status selesai ikut bertahan, bukan hanya teksnya
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.isStruck'(2), true)

WebUI.takeScreenshot()
WebUI.closeBrowser()
