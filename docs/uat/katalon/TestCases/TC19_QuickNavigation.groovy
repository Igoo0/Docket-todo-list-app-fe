import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

// TS-19 — Navigasi cepat
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Satu pekerjaan', 'Dua pekerjaan', 'Tiga pekerjaan'])

// Langkah 1: tekan "/" dari luar kolom input
CustomKeywords.'docket.DocketKeywords.clickBody'()
CustomKeywords.'docket.DocketKeywords.pressSlash'()
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getFocusedElementId'(), 'new-line')

// Langkah 2: karakter "/" tidak ikut terketik
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getFieldValue'(), '')

// Langkah 3: Esc melepas fokus
WebUI.sendKeys(DK.field(), Keys.chord(Keys.ESCAPE))
WebUI.delay(1)
WebUI.verifyNotEqual(CustomKeywords.'docket.DocketKeywords.getFocusedElementId'(), 'new-line')

// Langkah 4: klik batang gauge memindahkan fokus ke baris terkait
WebUI.click(DK.gaugeBar(3))
WebUI.delay(1)
WebUI.verifyMatch(CustomKeywords.'docket.DocketKeywords.getFocusedAriaLabel'(),
    '(?i).*line 3.*', true)

WebUI.takeScreenshot()
WebUI.closeBrowser()
