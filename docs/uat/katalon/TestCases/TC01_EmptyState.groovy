import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// TS-01 — Membuka docket kosong
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)

// Langkah 2: penghitung menampilkan 00/00
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getTally'(), '00/00')

// Langkah 3: pesan docket kosong
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getEmptyStateText'(),
    "Nothing on today's docket. Write the first line above.")

// Langkah 4: tombol Add line non-aktif
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.isAddButtonDisabled'(), true)

WebUI.takeScreenshot()
WebUI.closeBrowser()
