/**
 * Headed Playwright walkthrough of every feature in Docket.
 *
 *   npm run dev          # in one terminal
 *   node scripts/demo.mjs
 *
 * Opens a real Chromium window, captions each step on screen, asserts the
 * result, and prints a PASS/FAIL table at the end.
 */
import { chromium } from "playwright";

const URL = process.env.DEMO_URL ?? "http://localhost:5173/";
const SLOW = Number(process.env.DEMO_SLOWMO ?? 350);

const results = [];
const problems = [];

function check(name, ok, detail = "") {
  results.push({ name, ok, detail });
  const mark = ok ? "\x1b[32mPASS\x1b[0m" : "\x1b[31mFAIL\x1b[0m";
  console.log(`  ${mark}  ${name}${detail ? `  — ${detail}` : ""}`);
}

const browser = await chromium.launch({
  headless: false,
  slowMo: SLOW,
  args: ["--window-position=60,40"],
});
const context = await browser.newContext({ viewport: { width: 1280, height: 900 } });
const page = await context.newPage();

page.on("pageerror", (e) => problems.push(`pageerror: ${e}`));
page.on("console", (m) => {
  if (m.type() === "error") problems.push(`console.error: ${m.text()}`);
});

/** Paints a caption bar so the run is watchable. */
async function step(label) {
  console.log(`\n\x1b[36m${label}\x1b[0m`);
  await page.evaluate((text) => {
    let el = document.getElementById("__demo_caption");
    if (!el) {
      el = document.createElement("div");
      el.id = "__demo_caption";
      el.style.cssText =
        "position:fixed;left:0;right:0;top:0;z-index:9999;padding:9px 18px;" +
        "background:#3b5bff;color:#fff;pointer-events:none;" +
        "font:600 13px/1.35 ui-sans-serif,system-ui;letter-spacing:.03em";
      document.body.appendChild(el);
    }
    el.textContent = text;
  }, label);
  await page.waitForTimeout(450);
}

const tally = async () =>
  (await page.locator("header p").last().innerText()).replace(/\s+/g, "");
const rows = () => page.locator("li");
const rowText = async (i) => (await rows().nth(i).locator("[data-struck]").innerText()).trim();
const isStruck = async (i) =>
  (await rows().nth(i).locator("[data-struck]").getAttribute("data-struck")) === "true";
const lineNumber = async (i) => (await rows().nth(i).locator("label span").first().innerText()).trim();
const filterBtn = (label) => page.locator("button[aria-pressed]", { hasText: label });
const gaugeBars = () => page.locator('button[aria-label^="Line "]');

const SEED = [
  "Draft the quarterly notes for the Hallam account",
  "Call the framer about the print that came back warped",
  "Send the revised budget to the studio, with the Tuesday walkthrough dates attached",
  "Water the fig tree",
  "Chase the Kepler invoice",
];

try {
  await page.goto(URL);
  await page.evaluate(() => localStorage.clear());
  await page.reload();
  await page.waitForSelector("#new-line");

  // ── 01 ────────────────────────────────────────────────────────────────────
  await step("01 · Empty state");
  check(
    "empty-state copy is shown",
    (await page.locator("section p").innerText()).includes("Nothing on today's docket"),
  );
  check("tally reads 00/00", (await tally()) === "00/00", await tally());
  check("Add button is disabled on an empty field", await page.locator("button[type=submit]").isDisabled());

  // ── 02 ────────────────────────────────────────────────────────────────────
  await step("02 · Add five lines");
  for (const text of SEED) {
    await page.fill("#new-line", text);
    await page.press("#new-line", "Enter");
  }
  check("five rows on the sheet", (await rows().count()) === 5, `${await rows().count()}`);
  check("gauge grew five bars", (await gaugeBars().count()) === 5);
  check("field cleared after submit", (await page.inputValue("#new-line")) === "");
  check("tally reads 00/05", (await tally()) === "00/05", await tally());

  // ── 03 ────────────────────────────────────────────────────────────────────
  await step("03 · Clear a line by clicking its number");
  await rows().nth(0).locator("label").click();
  await page.waitForTimeout(700);
  check("line 1 is struck", await isStruck(0));
  check("tally reads 01/05", (await tally()) === "01/05", await tally());
  const barHeight = (await gaugeBars().first().boundingBox())?.height ?? 0;
  check("its gauge bar grew to full height", barHeight > 20, `${Math.round(barHeight)}px`);

  // ── 04 ────────────────────────────────────────────────────────────────────
  await step("04 · Reopen the same line");
  await rows().nth(0).locator("label").click();
  await page.waitForTimeout(600);
  check("line 1 is open again", !(await isStruck(0)));
  check("tally back to 00/05", (await tally()) === "00/05", await tally());
  await rows().nth(0).locator("label").click(); // clear it again for later steps
  await page.waitForTimeout(600);

  // ── 05 ────────────────────────────────────────────────────────────────────
  await step("05 · Edit a line — double-click, retype, Enter");
  await rows().nth(1).locator("[data-struck]").dblclick();
  check("an edit field opened", (await page.locator('input[aria-label="Edit line"]').count()) === 1);
  await page.fill('input[aria-label="Edit line"]', "Call the framer — the print came back warped");
  await page.press('input[aria-label="Edit line"]', "Enter");
  await page.waitForTimeout(300);
  check("the new text was saved", (await rowText(1)).includes("— the print came back warped"));

  // ── 06 ────────────────────────────────────────────────────────────────────
  await step("06 · Cancel an edit with Esc");
  const before = await rowText(3);
  await rows().nth(3).locator("[data-struck]").dblclick();
  await page.fill('input[aria-label="Edit line"]', "THIS SHOULD NEVER BE SAVED");
  await page.press('input[aria-label="Edit line"]', "Escape");
  await page.waitForTimeout(400);
  check("the original text survived", (await rowText(3)) === before, await rowText(3));

  // ── 07 ────────────────────────────────────────────────────────────────────
  await step("07 · Filter to OPEN");
  await filterBtn("OPEN").click();
  await page.waitForTimeout(400);
  check("only open lines are listed", (await rows().count()) === 4, `${await rows().count()}`);
  check(
    "numbering keeps docket order (first shown is 02)",
    (await lineNumber(0)) === "02",
    await lineNumber(0),
  );

  await step("08 · Filter to CLEARED");
  await filterBtn("CLEARED").click();
  await page.waitForTimeout(400);
  check("only cleared lines are listed", (await rows().count()) === 1, `${await rows().count()}`);
  check("and that line is struck", await isStruck(0));

  await filterBtn("ALL").click();
  await page.waitForTimeout(300);

  // ── 09 ────────────────────────────────────────────────────────────────────
  await step("09 · Delete a line, then undo it");
  const doomed = await rowText(4);
  await page.locator('button[aria-label^="Delete line 5"]').click();
  await page.waitForTimeout(400);
  check("row is gone", (await rows().count()) === 4);
  check("undo bar appeared", await page.locator("button:has-text('UNDO')").isVisible());
  await page.locator("button:has-text('UNDO')").click();
  await page.waitForTimeout(400);
  check("row came back", (await rows().count()) === 5);
  check("and came back in its old place", (await rowText(4)) === doomed);

  // ── 10 ────────────────────────────────────────────────────────────────────
  await step("10 · Remove cleared lines, then undo that too");
  await page.locator("button:has-text('REMOVE CLEARED')").click();
  await page.waitForTimeout(400);
  check("cleared line was swept", (await rows().count()) === 4);
  await page.locator("button:has-text('UNDO')").click();
  await page.waitForTimeout(400);
  check("sweep was undone", (await rows().count()) === 5);

  // ── 11 ────────────────────────────────────────────────────────────────────
  await step("11 · Reorder with Alt + ArrowDown");
  const wasFirst = await rowText(0);
  await rows().nth(0).locator('input[type="checkbox"]').focus();
  await page.keyboard.press("Alt+ArrowDown");
  await page.waitForTimeout(400);
  check("the line moved down one place", (await rowText(1)) === wasFirst);
  await page.keyboard.press("Alt+ArrowUp");
  await page.waitForTimeout(400);
  check("and back up again", (await rowText(0)) === wasFirst);

  // ── 12 ────────────────────────────────────────────────────────────────────
  await step("12 · Drag a line by its grip");
  const dragged = await rowText(0);
  await rows().nth(0).hover();
  await rows()
    .nth(0)
    .locator('[title="Drag to reorder"]')
    .dragTo(rows().nth(2), { force: true });
  await page.waitForTimeout(500);
  const movedByDrag = (await rowText(2)) === dragged || (await rowText(1)) === dragged;
  check("drag moved the line down the docket", movedByDrag, `now at index ${(await page.locator("li [data-struck]").allInnerTexts()).findIndex((t) => t.trim() === dragged)}`);

  // ── 13 ────────────────────────────────────────────────────────────────────
  await step("13 · Press / to jump to the field");
  await page.locator("body").click({ position: { x: 5, y: 400 } });
  await page.keyboard.press("/");
  check(
    "focus landed on the new-line field",
    (await page.evaluate(() => document.activeElement?.id)) === "new-line",
  );
  check("the slash itself was not typed", (await page.inputValue("#new-line")) === "");
  await page.keyboard.press("Escape");

  // ── 14 ────────────────────────────────────────────────────────────────────
  await step("14 · Click a gauge bar to jump to its line");
  await gaugeBars().last().click();
  await page.waitForTimeout(500);
  const focusedLabel = await page.evaluate(() =>
    document.activeElement?.getAttribute("aria-label"),
  );
  check("focus moved to that line's toggle", /line 5/i.test(focusedLabel ?? ""), focusedLabel ?? "none");

  // ── 15 ────────────────────────────────────────────────────────────────────
  await step("15 · Reload — the docket persists");
  const beforeReload = await page.locator("li [data-struck]").allInnerTexts();
  await page.reload();
  await page.waitForSelector("li");
  const afterReload = await page.locator("li [data-struck]").allInnerTexts();
  check(
    "same lines, same order, after a reload",
    JSON.stringify(beforeReload) === JSON.stringify(afterReload),
  );

  // ── 16 ────────────────────────────────────────────────────────────────────
  await step("16 · Mobile layout at 390px");
  await page.setViewportSize({ width: 390, height: 800 });
  await page.waitForTimeout(900);
  /* The filter bar is the sheet's only direct <div> child — the composer is a
     <form> and the list is a <ul>. */
  const barBox = await page.locator("section > div").first().boundingBox();
  check("filter bar stays on one row", (barBox?.height ?? 99) < 60, `${Math.round(barBox?.height ?? 0)}px`);
  check("actions are always visible on touch widths", await page.locator('button[aria-label^="Delete line 1"]').isVisible());
  await page.waitForTimeout(1200);
  await page.setViewportSize({ width: 1280, height: 900 });
  await page.waitForTimeout(700);

  // ── 17 ────────────────────────────────────────────────────────────────────
  await step("17 · Console check");
  check("no page errors during the whole run", problems.length === 0, problems.join(" | "));

  // ── summary ───────────────────────────────────────────────────────────────
  const passed = results.filter((r) => r.ok).length;
  const failed = results.filter((r) => !r.ok);
  console.log(`\n${"─".repeat(64)}`);
  console.log(`${passed}/${results.length} checks passed`);
  if (failed.length) {
    console.log("\nFailures:");
    for (const f of failed) console.log(`  · ${f.name}${f.detail ? ` — ${f.detail}` : ""}`);
  }
  console.log(`${"─".repeat(64)}\n`);

  await step(
    failed.length
      ? `Done — ${passed}/${results.length} passed, ${failed.length} failed. Close the window when you're finished.`
      : `Done — all ${passed} checks passed. Close the window when you're finished.`,
  );

  // Leave the window up so it can be looked at; give up after five minutes.
  await Promise.race([
    page.waitForEvent("close", { timeout: 0 }).catch(() => {}),
    page.waitForTimeout(300_000),
  ]);
} catch (err) {
  console.error("\nRun aborted:", err);
  process.exitCode = 1;
} finally {
  await browser.close().catch(() => {});
}
