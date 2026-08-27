import { useEffect, useRef, useState } from "react";
import type { DragEvent, KeyboardEvent } from "react";
import type { Task } from "../types";

type Props = {
  task: Task;
  line: number;
  isOver: boolean;
  isDragging: boolean;
  onToggle: (id: string) => void;
  onRename: (id: string, text: string) => void;
  onRemove: (id: string) => void;
  onMove: (id: string, delta: -1 | 1) => boolean;
  onDragStart: (id: string) => void;
  onDragOver: (id: string) => void;
  onDrop: (id: string) => void;
  onDragEnd: () => void;
};

const pad = (n: number) => String(n).padStart(2, "0");

export function TaskRow({
  task,
  line,
  isOver,
  isDragging,
  onToggle,
  onRename,
  onRemove,
  onMove,
  onDragStart,
  onDragOver,
  onDrop,
  onDragEnd,
}: Props) {
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState(task.text);
  const [dragReady, setDragReady] = useState(false);
  const fieldRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (!editing) return;
    const field = fieldRef.current;
    if (!field) return;
    field.focus();
    field.setSelectionRange(field.value.length, field.value.length);
  }, [editing]);

  function startEdit() {
    setDraft(task.text);
    setEditing(true);
  }

  function commit() {
    if (!editing) return;
    setEditing(false);
    onRename(task.id, draft);
  }

  function onRowKeyDown(e: KeyboardEvent<HTMLLIElement>) {
    if (editing || !e.altKey) return;
    if (e.key === "ArrowUp" && onMove(task.id, -1)) e.preventDefault();
    if (e.key === "ArrowDown" && onMove(task.id, 1)) e.preventDefault();
  }

  function allowDrop(e: DragEvent<HTMLLIElement>) {
    e.preventDefault();
    e.dataTransfer.dropEffect = "move";
    onDragOver(task.id);
  }

  return (
    <li
      id={`line-${task.id}`}
      draggable={dragReady}
      onKeyDown={onRowKeyDown}
      onDragStart={() => onDragStart(task.id)}
      onDragOver={allowDrop}
      onDrop={(e) => {
        e.preventDefault();
        onDrop(task.id);
      }}
      onDragEnd={() => {
        setDragReady(false);
        onDragEnd();
      }}
      className={[
        "group grid grid-cols-[1.75rem_1fr_auto] items-start gap-3 border-b border-rule/60 py-3 pr-4 pl-4 transition-colors last:border-b-0 sm:grid-cols-[0.75rem_1.75rem_1fr_auto] sm:gap-4 sm:pr-6 sm:pl-3",
        isDragging ? "opacity-35" : "hover:bg-paper-shade/70",
        isOver ? "shadow-[inset_0_2px_0_0_var(--color-cobalt)]" : "",
      ].join(" ")}
    >
      {/* Grip. Pointer-down arms the drag so the row's text stays selectable. */}
      <span
        aria-hidden="true"
        title="Drag to reorder"
        onPointerDown={() => setDragReady(true)}
        onPointerUp={() => setDragReady(false)}
        className="mt-2 hidden h-4 w-3 cursor-grab bg-[radial-gradient(circle,var(--color-ink-soft)_1px,transparent_1.2px)] bg-[length:4px_4px] opacity-0 transition-opacity group-hover:opacity-60 active:cursor-grabbing sm:block"
      />

      {/* The line number is the control: click it to clear the line. */}
      <label
        title={task.done ? "Reopen this line" : "Clear this line"}
        className={[
          "relative mt-px grid h-7 w-7 shrink-0 cursor-pointer place-items-center rounded-[3px] border transition-colors",
          "has-[:focus-visible]:outline-2 has-[:focus-visible]:outline-offset-2 has-[:focus-visible]:outline-cobalt",
          task.done
            ? "border-cobalt bg-cobalt"
            : "border-ink/20 group-hover:border-ink/45 group-hover:bg-paper",
        ].join(" ")}
      >
        <input
          type="checkbox"
          className="sr-only"
          checked={task.done}
          onChange={() => onToggle(task.id)}
          aria-label={`${task.done ? "Reopen" : "Clear"} line ${line}: ${task.text}`}
        />
        <span
          aria-hidden="true"
          className={[
            "pointer-events-none font-mono text-[11px] tabular-nums transition-opacity",
            task.done ? "opacity-0" : "text-ink-soft group-hover:opacity-0",
          ].join(" ")}
        >
          {pad(line)}
        </span>
        <svg
          aria-hidden="true"
          viewBox="0 0 16 16"
          fill="none"
          stroke={task.done ? "#fff" : "var(--color-ink)"}
          strokeWidth="2.25"
          strokeLinecap="square"
          className={[
            "pointer-events-none absolute h-3.5 w-3.5 transition-all duration-200",
            task.done
              ? "scale-100 opacity-100"
              : "scale-75 opacity-0 group-hover:scale-100 group-hover:opacity-35",
          ].join(" ")}
        >
          <path d="M3 8.5 6.5 12 13 4.5" />
        </svg>
      </label>

      {editing ? (
        <input
          ref={fieldRef}
          value={draft}
          maxLength={240}
          aria-label="Edit line"
          onChange={(e) => setDraft(e.target.value)}
          onBlur={commit}
          onKeyDown={(e) => {
            if (e.key === "Enter") commit();
            if (e.key === "Escape") setEditing(false);
          }}
          className="w-full border-b-2 border-cobalt bg-transparent pb-px text-ink outline-none"
        />
      ) : (
        <span onDoubleClick={startEdit} title="Double-click to edit" className="self-center">
          {/* The inner span stays inline — a grid item would be blockified and
              the rule would run the width of the row instead of the words. */}
          <span
            data-struck={task.done}
            className={["line-ink break-words", task.done ? "text-ink-soft" : "text-ink"].join(" ")}
          >
            {task.text}
          </span>
        </span>
      )}

      <div className="flex shrink-0 items-center gap-3 self-center font-mono text-[10px] tracking-[0.14em] opacity-100 transition-opacity sm:opacity-0 sm:group-focus-within:opacity-100 sm:group-hover:opacity-100">
        <button
          type="button"
          onClick={startEdit}
          aria-label={`Edit line ${line}: ${task.text}`}
          className="text-ink-soft transition-colors hover:text-ink"
        >
          EDIT
        </button>
        <button
          type="button"
          onClick={() => onRemove(task.id)}
          aria-label={`Delete line ${line}: ${task.text}`}
          className="text-ink-soft transition-colors hover:text-brick"
        >
          DEL
        </button>
      </div>
    </li>
  );
}
