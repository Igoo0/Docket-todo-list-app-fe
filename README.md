# Docket

A day's tasks kept on one sheet. React 19, Tailwind v4, Vite, TypeScript. No component
library, no state library, no backend — lines live in `localStorage`, so they stay in the
browser that wrote them.

```
npm install
npm run dev      # http://localhost:5173
npm run build    # type-check, then bundle to dist/
npm run preview  # serve the built bundle
```

## What it does

- Write a line, edit it in place, delete it, or drag it up the order
- Clear a line by clicking its number — the number is the control
- Filter by All / Open / Cleared, with live counts
- Undo a delete or a "remove cleared" sweep for 7 seconds
- Remembers the docket across reloads

| Action | How |
| --- | --- |
| Jump to the field | `/` |
| Add a line, save an edit | `Enter` |
| Cancel an edit, dismiss the undo bar | `Esc` |
| Edit a line | Double-click it, or `EDIT` |
| Move a line | `Alt` + `↑` / `↓`, or drag the grip |

Drag-to-reorder uses HTML5 drag and drop, which desktop browsers support and touch
browsers do not. `Alt` + arrows covers the same ground from the keyboard.

## Design notes

The page is a dark workbench with a warm paper sheet laid on it. Cobalt is the only
saturated colour and it only ever marks state — a cleared line, the active filter, the
day gauge — never decoration. Type is Syne for the masthead, Instrument Sans for reading,
DM Mono for the utility layer: dates, counts, line numbers, controls.

Two ideas carry the interface:

**The line number is the control.** Each line's place in the docket sits in a small square
at its left; clicking that square clears the line and the number becomes a check. Order is
information here — it's the priority order you drag to change — so the numbering earns its
place rather than decorating the rows.

**The day gauge.** One bar per line, in docket order, standing on a baseline rule that runs
the full width. Bars are short and dim while open, tall and cobalt once cleared, so the
day's shape is legible at a glance. Click any bar to jump to that line.

Clearing a line draws a cobalt rule through the words. The text sits in an inline box with
`box-decoration-break: clone`, so on a line that wraps, the rule marks every row of it the
way a pen would — not one bar across the width of the container.

Everything else stays quiet: hairline rules instead of card shadows, 2–4px corners, actions
that surface on hover as small mono labels. Focus is always visible and
`prefers-reduced-motion` turns the motion off.

## Layout

| Path | |
| --- | --- |
| `src/App.tsx` | Page shell, filters, keyboard shortcuts |
| `src/hooks/useTasks.ts` | All task state, persistence, snapshot-based undo |
| `src/components/Masthead.tsx` | Date, weekday, cleared tally |
| `src/components/DayGauge.tsx` | The gauge |
| `src/components/Composer.tsx` | New-line field |
| `src/components/FilterBar.tsx` | Filters and the cleared sweep |
| `src/components/TaskList.tsx` | List, drag state, empty states |
| `src/components/TaskRow.tsx` | One line: toggle, edit, delete, drag |
| `src/components/UndoToast.tsx` | Undo bar |
| `src/index.css` | Design tokens, base layer, the strike |
