import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import docket.DocketKeywords as DK
import internal.GlobalVariable as GlobalVariable

// TS-15 — Membersihkan pekerjaan selesai dan membatalkannya
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)
CustomKeywords.'docket.DocketKeywords.addLines'(
    ['Satu pekerjaan', 'Dua pekerjaan', 'Tiga pekerjaan'])
CustomKeywords.'docket.DocketKeywords.clearLine'(1)
CustomKeywords.'docket.DocketKeywords.clearLine'(2)

// Langkah 1: tautan muncul hanya ketika ada baris selesai
WebUI.verifyElementPresent(DK.removeCleared(), 5)

// Langkah 2: baris selesai tersapu, baris terbuka utuh
WebUI.click(DK.removeCleared())
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(1), 'Tiga pekerjaan')

// Langkah 3: pesan menyebut jumlahnya
WebUI.verifyMatch(CustomKeywords.'docket.DocketKeywords.getUndoMessage'(),
    '.*2 cleared lines removed.*', true)

// Langkah 4: undo memulihkan urutan DAN status selesainya
WebUI.click(DK.undoButton())
WebUI.delay(1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 3)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineText'(1), 'Satu pekerjaan')
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.isStruck'(1), true)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.isStruck'(2), true)

// Langkah 5: tautan menghilang saat tidak ada lagi baris selesai
CustomKeywords.'docket.DocketKeywords.reopenLine'(1)
CustomKeywords.'docket.DocketKeywords.reopenLine'(2)
WebUI.verifyElementNotPresent(DK.removeCleared(), 5)

WebUI.takeScreenshot()
WebUI.closeBrowser()
