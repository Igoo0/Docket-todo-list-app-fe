package docket

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import org.openqa.selenium.Dimension
import org.openqa.selenium.Keys
import org.openqa.selenium.interactions.Actions

/**
 * Keyword kustom untuk aplikasi Docket.
 *
 * Semua TestObject dibangun di sini, bukan di Object Repository, karena locator
 * baris bersifat dinamis (bergantung nomor urut) dan format berkas .rs berbeda
 * antar versi Katalon.
 *
 * Simpan sebagai: Keywords/docket/DocketKeywords.groovy
 */
public class DocketKeywords {

    // ------------------------------------------------------------------ //
    // Pembangun locator                                                   //
    // ------------------------------------------------------------------ //

    private static TestObject xp(String name, String xpath) {
        TestObject to = new TestObject(name)
        to.addProperty('xpath', ConditionType.EQUALS, xpath)
        return to
    }

    private static String rowXp(int n) {
        return '(//section//ul/li)[' + n + ']'
    }

    static TestObject field()        { return xp('field', "//input[@id='new-line']") }
    static TestObject addButton()    { return xp('addButton', "//form//button[@type='submit']") }
    static TestObject tallyLabel()   { return xp('tally', '(//header//p)[last()]') }
    static TestObject emptyState()   { return xp('emptyState', '//section//p') }
    static TestObject editField()    { return xp('editField', "//input[@aria-label='Edit line']") }
    static TestObject removeCleared(){ return xp('removeCleared', "//button[normalize-space()='REMOVE CLEARED']") }
    static TestObject undoButton()   { return xp('undoButton', "//button[normalize-space()='UNDO']") }
    static TestObject undoMessage()  { return xp('undoMessage', "//button[normalize-space()='UNDO']/preceding-sibling::span") }

    static TestObject tab(String label) {
        return xp('tab_' + label, "//button[@aria-pressed][contains(., '" + label + "')]")
    }

    static TestObject gaugeBar(int n) {
        return xp('gauge' + n, "(//div[@role='group']//button)[" + n + ']')
    }

    /** PENTING: yang diklik adalah label, bukan input. Input-nya hanya 1x1 px. */
    static TestObject toggle(int n)   { return xp('toggle' + n, rowXp(n) + '//label') }
    static TestObject lineText(int n) { return xp('text' + n, rowXp(n) + '//span[@data-struck]') }
    static TestObject lineNo(int n)   { return xp('no' + n, rowXp(n) + '//label/span[1]') }
    static TestObject grip(int n)     { return xp('grip' + n, rowXp(n) + "//span[@title='Drag to reorder']") }

    static TestObject editBtn(int n) {
        return xp('edit' + n, rowXp(n) + "//button[starts-with(@aria-label,'Edit line')]")
    }

    static TestObject delBtn(int n) {
        return xp('del' + n, rowXp(n) + "//button[starts-with(@aria-label,'Delete line')]")
    }

    // ------------------------------------------------------------------ //
    // Persiapan                                                           //
    // ------------------------------------------------------------------ //

    /**
     * Mengosongkan data aplikasi dan memuat ulang halaman.
     *
     * WAJIB dipanggil di awal SETIAP test case. Docket menyimpan state di
     * localStorage, jadi tanpa ini test case kedua dan seterusnya akan membaca
     * sisa data test sebelumnya.
     */
    @Keyword
    def resetDocket(String baseUrl) {
        WebUI.navigateToUrl(baseUrl)
        WebUI.executeJavaScript('window.localStorage.clear()', null)
        WebUI.refresh()
        WebUI.waitForElementPresent(field(), 10)
    }

    // ------------------------------------------------------------------ //
    // Aksi                                                                //
    // ------------------------------------------------------------------ //

    @Keyword
    def addLine(String text) {
        WebUI.setText(field(), text)
        WebUI.sendKeys(field(), Keys.chord(Keys.ENTER))
        WebUI.delay(1)
    }

    @Keyword
    def addLines(List<String> texts) {
        for (String t : texts) {
            addLine(t)
        }
    }

    /** Menandai baris selesai dan menunggu animasi coretan tuntas. */
    @Keyword
    def clearLine(int n) {
        WebUI.click(toggle(n))
        WebUI.verifyElementAttributeValue(lineText(n), 'data-struck', 'true', 10)
    }

    @Keyword
    def reopenLine(int n) {
        WebUI.click(toggle(n))
        WebUI.verifyElementAttributeValue(lineText(n), 'data-struck', 'false', 10)
    }

    /**
     * Mengubah teks baris.
     *
     * @param save true untuk menyimpan dengan Enter, false untuk membatalkan
     *             dengan Esc.
     */
    @Keyword
    def editLine(int n, String newText, boolean save) {
        WebUI.doubleClick(lineText(n))
        WebUI.waitForElementPresent(editField(), 10)
        WebUI.setText(editField(), newText)
        WebUI.sendKeys(editField(), Keys.chord(save ? Keys.ENTER : Keys.ESCAPE))
        WebUI.waitForElementNotPresent(editField(), 10)
    }

    @Keyword
    def deleteLine(int n) {
        WebUI.mouseOver(lineText(n))
        WebUI.click(delBtn(n))
    }

    /**
     * Memindahkan baris dengan Alt + panah.
     *
     * Fokus dipasang lewat JavaScript: checkbox-nya tersembunyi secara visual
     * (1x1 px) sehingga WebUI.sendKeys akan ditolak Selenium.
     */
    @Keyword
    def moveLine(int n, boolean down) {
        WebUI.executeJavaScript(
            "document.querySelectorAll('section ul li')[" + (n - 1) + "]" +
            ".querySelector('input[type=checkbox]').focus()", null)

        Actions act = new Actions(DriverFactory.getWebDriver())
        act.keyDown(Keys.ALT)
           .sendKeys(down ? Keys.ARROW_DOWN : Keys.ARROW_UP)
           .keyUp(Keys.ALT)
           .perform()
        WebUI.delay(1)
    }

    /** Menekan "/" pada dokumen, bukan pada elemen tertentu. */
    @Keyword
    def pressSlash() {
        new Actions(DriverFactory.getWebDriver()).sendKeys('/').perform()
        WebUI.delay(1)
    }

    @Keyword
    def clickBody() {
        WebUI.executeJavaScript("document.querySelector('h1').click()", null)
    }

    @Keyword
    def setViewport(int width, int height) {
        DriverFactory.getWebDriver().manage().window().setSize(new Dimension(width, height))
        WebUI.delay(1)
    }

    // ------------------------------------------------------------------ //
    // Pembacaan                                                           //
    // ------------------------------------------------------------------ //

    @Keyword
    def getLineText(int n) {
        return WebUI.getText(lineText(n)).trim()
    }

    @Keyword
    def getLineNumber(int n) {
        return WebUI.getText(lineNo(n)).trim()
    }

    @Keyword
    def countLines() {
        def n = WebUI.executeJavaScript(
            "return document.querySelectorAll('section ul li').length", null)
        return ((Number) n).intValue()
    }

    @Keyword
    def countGaugeBars() {
        def n = WebUI.executeJavaScript(
            "return document.querySelectorAll(\"div[role='group'] button\").length", null)
        return ((Number) n).intValue()
    }

    /** Mengembalikan penghitung dalam bentuk "01/05" tanpa spasi. */
    @Keyword
    def getTally() {
        return WebUI.getText(tallyLabel()).replaceAll('\\s', '')
    }

    @Keyword
    def isStruck(int n) {
        return 'true'.equals(WebUI.getAttribute(lineText(n), 'data-struck'))
    }

    /** Tombol Add line memakai atribut disabled, bukan kelas CSS. */
    @Keyword
    def isAddButtonDisabled() {
        def v = WebUI.executeJavaScript(
            "return document.querySelector('form button[type=submit]').disabled", null)
        return Boolean.TRUE.equals(v)
    }

    @Keyword
    def getFieldValue() {
        def v = WebUI.executeJavaScript(
            "return document.getElementById('new-line').value", null)
        return v == null ? '' : v.toString()
    }

    @Keyword
    def getFocusedElementId() {
        def v = WebUI.executeJavaScript('return document.activeElement.id', null)
        return v == null ? '' : v.toString()
    }

    @Keyword
    def getFocusedAriaLabel() {
        def v = WebUI.executeJavaScript(
            "return document.activeElement.getAttribute('aria-label')", null)
        return v == null ? '' : v.toString()
    }

    @Keyword
    def getGaugeBarHeight(int n) {
        def v = WebUI.executeJavaScript(
            "return document.querySelectorAll(\"div[role='group'] button\")[" + (n - 1) +
            "].getBoundingClientRect().height", null)
        return ((Number) v).doubleValue()
    }

    @Keyword
    def getEmptyStateText() {
        return WebUI.getText(emptyState()).trim()
    }

    @Keyword
    def getUndoMessage() {
        return WebUI.getText(undoMessage()).trim()
    }

    /** Mengembalikan seluruh teks baris sesuai urutan tampil. */
    @Keyword
    def getAllLineTexts() {
        def v = WebUI.executeJavaScript(
            "return Array.from(document.querySelectorAll('section ul li [data-struck]'))" +
            ".map(function (e) { return e.textContent.trim() })", null)
        return v as List
    }
}
