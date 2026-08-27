import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable

// TS-13 — Menyaring pekerjaan yang belum selesai
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Satu pekerjaan', 'Dua pekerjaan', 'Tiga pekerjaan'])
CustomKeywords.'docket.DocketKeywords.clearLine'(1)

// Langkah 1: tab OPEN aktif
WebUI.click(DK.tab('OPEN'))
WebUI.delay(1)
WebUI.verifyElementAttributeValue(DK.tab('OPEN'), 'aria-pressed', 'true', 10)

// Langkah 2: hanya baris terbuka
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 2)

// Langkah 3: penomoran mengikuti urutan docket, BUKAN dinomori ulang jadi 01.
// Ini perilaku yang disengaja — kalau berubah jadi '01', itu regresi.
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineNumber'(1), '02')

// Langkah 4: kembali ke ALL
WebUI.click(DK.tab('ALL'))
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 3)

WebUI.takeScreenshot()
WebUI.closeBrowser()
