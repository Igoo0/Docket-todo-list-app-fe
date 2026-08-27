import { useCallback, useEffect, useState } from "react";
import type { Task } from "../types";

const STORE_KEY = "docket.tasks.v1";
const UNDO_MS = 7000;

type UndoEntry = { label: string; snapshot: Task[] };

const uid = () => Math.random().toString(36).slice(2, 10);

function load(): Task[] {
  try {
    const raw = window.localStorage.getItem(STORE_KEY);
    const parsed: unknown = raw ? JSON.parse(raw) : [];
    if (!Array.isArray(parsed)) return [];
    return parsed
      .filter(
        (t): t is Partial<Task> =>
          typeof t === "object" && t !== null && typeof (t as Task).text === "string",
      )
      .map((t) => ({ id: String(t.id ?? uid()), text: String(t.text), done: !!t.done }));
  } catch {
    return [];
  }
}

/**
 * All task state in one place. Undo keeps a snapshot of the whole list rather
 * than a diff — a delete and a "clear cleared" sweep then restore identically.
 */
export function useTasks() {
  const [tasks, setTasks] = useState<Task[]>(load);
  const [undo, setUndo] = useState<UndoEntry | null>(null);
  const [status, setStatus] = useState("");

  useEffect(() => {
    try {
      window.localStorage.setItem(STORE_KEY, JSON.stringify(tasks));
    } catch {
      /* Private browsing or a full quota: the list still works this session. */
    }
  }, [tasks]);

  useEffect(() => {
    if (!undo) return;
    const timer = window.setTimeout(() => setUndo(null), UNDO_MS);
    return () => window.clearTimeout(timer);
  }, [undo]);

  const add = useCallback((text: string) => {
    const value = text.trim();
    if (!value) return;
    setTasks((prev) => [...prev, { id: uid(), text: value, done: false }]);
    setStatus(`Added: ${value}`);
  }, []);

  const toggle = useCallback(
    (id: string) => {
      const target = tasks.find((task) => task.id === id);
      if (!target) return;
      setTasks(tasks.map((task) => (task.id === id ? { ...task, done: !task.done } : task)));
      setStatus(`${target.done ? "Reopened" : "Cleared"}: ${target.text}`);
    },
    [tasks],
  );

  const rename = useCallback((id: string, text: string) => {
    const value = text.trim();
    if (!value) return;
    setTasks((prev) => prev.map((task) => (task.id === id ? { ...task, text: value } : task)));
    setStatus(`Saved: ${value}`);
  }, []);

  const remove = useCallback(
    (id: string) => {
      const target = tasks.find((task) => task.id === id);
      if (!target) return;
      setUndo({ label: `Deleted “${target.text}”`, snapshot: tasks });
      setTasks(tasks.filter((task) => task.id !== id));
      setStatus(`Deleted: ${target.text}`);
    },
    [tasks],
  );

  const clearDone = useCallback(() => {
    const cleared = tasks.filter((task) => task.done).length;
    if (!cleared) return;
    const label = cleared === 1 ? "1 cleared line removed" : `${cleared} cleared lines removed`;
    setUndo({ label, snapshot: tasks });
    setTasks(tasks.filter((task) => !task.done));
    setStatus(label);
  }, [tasks]);

  /** Moves a line by one place. Returns false at the ends so the caller can
      leave the keystroke alone. */
  const move = useCallback(
    (id: string, delta: -1 | 1) => {
      const from = tasks.findIndex((task) => task.id === id);
      const to = from + delta;
      if (from < 0 || to < 0 || to >= tasks.length) return false;
      const next = tasks.slice();
      const [moved] = next.splice(from, 1);
      next.splice(to, 0, moved);
      setTasks(next);
      setStatus(`Moved to line ${to + 1}`);
      return true;
    },
    [tasks],
  );

  const reorder = useCallback(
    (draggedId: string, targetId: string) => {
      const from = tasks.findIndex((task) => task.id === draggedId);
      const to = tasks.findIndex((task) => task.id === targetId);
      if (from < 0 || to < 0 || from === to) return;
      const next = tasks.slice();
      const [moved] = next.splice(from, 1);
      next.splice(to, 0, moved);
      setTasks(next);
      setStatus(`Moved to line ${to + 1}`);
    },
    [tasks],
  );

  const applyUndo = useCallback(() => {
    if (!undo) return;
    setTasks(undo.snapshot);
    setUndo(null);
    setStatus("Restored");
  }, [undo]);

  const dismissUndo = useCallback(() => setUndo(null), []);

  return {
    tasks,
    status,
    undo,
    add,
    toggle,
    rename,
    remove,
    clearDone,
    move,
    reorder,
    applyUndo,
    dismissUndo,
  };
}
