import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable

// TS-20 — Tampilan di layar ponsel
WebUI.openBrowser('')
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(['Satu pekerjaan', 'Dua pekerjaan'])

// Langkah 1: lebar 390 px
CustomKeywords.'docket.DocketKeywords.setViewport'(390, 800)

// Langkah 2: baris tab penyaring tetap satu baris (kalau membungkus jadi > 60 px)
def h = WebUI.executeJavaScript(
    "return document.querySelector('section > div').getBoundingClientRect().height", null)
double tinggiTab = ((Number) h).doubleValue()

if (tinggiTab < 60) {
    KeywordUtil.markPassed('Baris tab tetap satu baris: ' + tinggiTab + ' px')
} else {
    KeywordUtil.markFailed('Baris tab membungkus: ' + tinggiTab + ' px')
}

// Langkah 3: tombol aksi selalu terlihat tanpa hover
WebUI.verifyElementVisible(DK.delBtn(1))

// Langkah 4: fungsi utama tetap jalan di lebar ponsel
CustomKeywords.'docket.DocketKeywords.addLine'('Tiga pekerjaan')
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 3)
CustomKeywords.'docket.DocketKeywords.clearLine'(3)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getTally'(), '01/03')

WebUI.takeScreenshot()
CustomKeywords.'docket.DocketKeywords.setViewport'(1280, 900)
WebUI.closeBrowser()
