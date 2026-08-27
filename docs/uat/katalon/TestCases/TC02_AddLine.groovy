import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

// TS-02 — Menambahkan pekerjaan baru
WebUI.openBrowser('')
WebUI.maximizeWindow()
CustomKeywords.'docket.DocketKeywords.resetDocket'(GlobalVariable.baseUrl)

// Langkah 1-3: satu pekerjaan pertama
CustomKeywords.'docket.DocketKeywords.addLine'('Kirim laporan harian ke supervisor')
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 1)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineNumber'(1), '01')

// Langkah 4: kolom input kembali kosong
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getFieldValue'(), '')

// Langkah 5-6: penghitung dan day gauge menyesuaikan
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getTally'(), '00/01')
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countGaugeBars'(), 1)

// Langkah 7: empat pekerjaan berikutnya, baris baru masuk di BAWAH
CustomKeywords.'docket.DocketKeywords.addLines'([
    'Rekonsiliasi kas kecil',
    'Tindak lanjut tagihan Kepler',
    'Siram tanaman kantor',
    'Baca catatan rapat sebelum kunjungan'])

WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.countLines'(), 5)
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getLineNumber'(5), '05')
WebUI.verifyEqual(CustomKeywords.'docket.DocketKeywords.getTally'(), '00/05')

WebUI.takeScreenshot()
WebUI.closeBrowser()
