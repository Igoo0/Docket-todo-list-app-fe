import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

// TS-03 — Input kosong ditolak (negatif)
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)

// Langkah 1: Enter pada kolom kosong
WebUI.sendKeys(DK.field(), Keys.chord(Keys.ENTER))
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 0)

// Langkah 2: spasi saja, tombol tetap non-aktif
WebUI.setText(DK.field(), '     ')
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.isAddButtonDisabled'(), true)

// Langkah 3: Enter tetap tidak menambah baris
WebUI.sendKeys(DK.field(), Keys.chord(Keys.ENTER))
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 0)

WebUI.takeScreenshot()
WebUI.closeBrowser()
