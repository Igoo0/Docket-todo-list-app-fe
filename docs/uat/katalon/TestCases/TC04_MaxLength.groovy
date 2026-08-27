import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

// TS-04 — Batas panjang teks 240 karakter (boundary)
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)

String teksPanjang = 'A' * 300

// Langkah 1: kolom hanya menerima 240 karakter
WebUI.setText(DK.field(), teksPanjang)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getFieldValue'().length(), 240)

// Langkah 2: tersimpan dengan 240 karakter
WebUI.sendKeys(DK.field(), Keys.chord(Keys.ENTER))
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(1).length(), 240)

// Langkah 3: coretan menandai seluruh baris yang membungkus
CustomKeywords.'docket.DocketKeywords.clearLine'(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.isStruck'(1), true)

WebUI.takeScreenshot()
WebUI.closeBrowser()
