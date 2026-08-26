import { useEffect, useMemo, useRef, useState } from "react";
import { Composer } from "./components/Composer";
import { DayGauge } from "./components/DayGauge";
import { FilterBar } from "./components/FilterBar";
import { Masthead } from "./components/Masthead";
import { TaskList } from "./components/TaskList";
import { UndoToast } from "./components/UndoToast";
import { useTasks } from "./hooks/useTasks";
import type { Filter } from "./types";

function Key({ children }: { children: React.ReactNode }) {
  return (
    <kbd className="rounded-[2px] border border-ground-edge px-1.5 py-0.5 font-mono text-[10px] text-paper/60">
      {children}
    </kbd>
  );
}

export default function App() {
  const docket = useTasks();
  const [filter, setFilter] = useState<Filter>("all");
  const [jumpId, setJumpId] = useState<string | null>(null);
  const composerRef = useRef<HTMLInputElement>(null);
  const today = useMemo(() => new Date(), []);

  const counts = useMemo(() => {
    const done = docket.tasks.filter((task) => task.done).length;
    return { all: docket.tasks.length, open: docket.tasks.length - done, done };
  }, [docket.tasks]);

  const visible = useMemo(() => {
    if (filter === "open") return docket.tasks.filter((task) => !task.done);
    if (filter === "done") return docket.tasks.filter((task) => task.done);
    return docket.tasks;
  }, [docket.tasks, filter]);

  const { dismissUndo } = docket;

  useEffect(() => {
    function onKeyDown(e: KeyboardEvent) {
      const target = e.target as HTMLElement | null;
      const typing = !!target && /^(INPUT|TEXTAREA)$/.test(target.tagName);

      if (e.key === "/" && !typing) {
        e.preventDefault();
        composerRef.current?.focus();
      }
      if (e.key === "Escape") {
        if (target === composerRef.current) composerRef.current?.blur();
        dismissUndo();
      }
    }
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [dismissUndo]);

  /* Runs after the filter change has committed, so the row is on the page. */
  useEffect(() => {
    if (!jumpId) return;
    const row = document.getElementById(`line-${jumpId}`);
    row?.scrollIntoView({ behavior: "smooth", block: "center" });
    row?.querySelector<HTMLInputElement>('input[type="checkbox"]')?.focus();
    setJumpId(null);
  }, [jumpId, visible]);

  return (
    <div className="min-h-dvh px-4 pt-8 pb-28 sm:px-8 lg:pt-14">
      <div className="mx-auto w-full max-w-[46rem]">
        <div className="mb-9 flex items-center gap-2.5">
          <span className="h-2.5 w-2.5 bg-cobalt" aria-hidden="true" />
          <span className="font-mono text-[11px] tracking-[0.3em] text-paper/55">DOCKET</span>
        </div>

        <Masthead date={today} total={counts.all} done={counts.done} />

        <DayGauge
          tasks={docket.tasks}
          onJump={(id) => {
            setFilter("all");
            setJumpId(id);
          }}
        />

        <section className="mt-7 overflow-hidden rounded-[5px] border border-paper-shade bg-paper text-ink shadow-[0_36px_70px_-45px_#000]">
          <Composer inputRef={composerRef} onAdd={docket.add} />
          <FilterBar
            filter={filter}
            counts={counts}
            onFilter={setFilter}
            onClearDone={docket.clearDone}
          />
          <TaskList
            tasks={docket.tasks}
            visible={visible}
            filter={filter}
            onToggle={docket.toggle}
            onRename={docket.rename}
            onRemove={docket.remove}
            onMove={docket.move}
            onReorder={docket.reorder}
          />
        </section>

        <footer className="mt-6 flex flex-wrap items-center gap-x-6 gap-y-2 font-mono text-[11px] tracking-[0.08em] text-paper/35">
          <span>KEPT IN THIS BROWSER</span>
          <span className="flex items-center gap-1.5">
            <Key>/</Key> FIELD
          </span>
          <span className="flex items-center gap-1.5">
            <Key>↵</Key> ADD
          </span>
          <span className="flex items-center gap-1.5">
            <Key>ALT</Key>
            <Key>↑↓</Key> MOVE
          </span>
          <span>DOUBLE-CLICK A LINE TO EDIT</span>
        </footer>
      </div>

      <UndoToast label={docket.undo?.label ?? null} onUndo={docket.applyUndo} />

      <p className="sr-only" role="status" aria-live="polite">
        {docket.status}
      </p>
    </div>
  );
}
