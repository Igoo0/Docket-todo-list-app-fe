import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable

// TS-10 — Menghapus pekerjaan
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Satu pekerjaan', 'Dua pekerjaan', 'Tiga pekerjaan'])

String dihapus = CustomKeywords.'docket.DocketKeywords.getLineText'(3)

// Langkah 1-2: hover baris lalu klik DEL
CustomKeywords.'docket.DocketKeywords.deleteLine'(3)
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 2)

// Langkah 3: gauge ikut berkurang
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countGaugeBars'(), 2)

// Langkah 4: bar undo memuat teks baris yang dihapus
// CATATAN: pesan aplikasi memakai tanda kutip melengkung (U+201C/U+201D),
// jadi dicocokkan dengan regex, bukan perbandingan string persis.
WebUI.verifyElementPresent(DK.undoButton(), 5)
String pesan = CustomKeywords.'docket.DocketKeywords.getUndoMessage'()
WebUI.verifyMatch(pesan, '.*Deleted.*Tiga pekerjaan.*', true)

WebUI.takeScreenshot()
WebUI.closeBrowser()
