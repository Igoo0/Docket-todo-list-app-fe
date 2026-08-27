import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable

// TS-14 — Menyaring pekerjaan yang sudah selesai
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Satu pekerjaan', 'Dua pekerjaan', 'Tiga pekerjaan'])
CustomKeywords.'docket.DocketKeywords.clearLine'(2)

// Langkah 1: hanya baris selesai yang tampil
WebUI.click(DK.tab('CLEARED'))
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.isStruck'(1), true)

// Langkah 2: tanpa baris selesai, tampil pesan kosong khusus
WebUI.click(DK.tab('ALL'))
WebUI.delay(1)
CustomKeywords.'docket.DocketKeywords.reopenLine'(2)
WebUI.click(DK.tab('CLEARED'))
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getEmptyStateText'(),
    'No lines cleared yet.')

WebUI.takeScreenshot()
WebUI.closeBrowser()
