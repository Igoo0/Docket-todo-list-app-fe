import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable

// TS-12 — Kesempatan undo berakhir setelah 7 detik (boundary)
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(['Satu pekerjaan', 'Dua pekerjaan'])

// Langkah 1: bar undo muncul
CustomKeywords.'docket.DocketKeywords.deleteLine'(1)
WebUI.verifyElementPresent(DK.undoButton(), 5)

// Langkah 2: hilang sendiri. Pakai penantian berbasis kondisi, bukan delay tetap.
WebUI.waitForElementNotPresent(DK.undoButton(), 12)
WebUI.verifyElementNotPresent(DK.undoButton(), 3)

// Langkah 3: penghapusan bersifat final
WebUI.refresh()
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 1)

WebUI.takeScreenshot()
WebUI.closeBrowser()
