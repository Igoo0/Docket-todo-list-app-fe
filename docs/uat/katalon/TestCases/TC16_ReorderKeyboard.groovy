import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// TS-16 — Mengubah urutan prioritas dengan papan ketik
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Satu pekerjaan', 'Dua pekerjaan', 'Tiga pekerjaan'])

String teratas = CustomKeywords.'docket.DocketKeywords.getLineText'(1)

// Langkah 2: turun satu posisi.
// moveLine memasang fokus lewat JavaScript karena checkbox-nya 1x1 px,
// sehingga WebUI.sendKeys biasa akan ditolak Selenium.
CustomKeywords.'docket.DocketKeywords.moveLine'(1, true)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(2), teratas)

// Langkah 3: naik lagi ke posisi semula
CustomKeywords.'docket.DocketKeywords.moveLine'(2, false)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(1), teratas)

// Langkah 4: di baris teratas, panah atas tidak melakukan apa-apa
CustomKeywords.'docket.DocketKeywords.moveLine'(1, false)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(1), teratas)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 3)

WebUI.takeScreenshot()
WebUI.closeBrowser()
