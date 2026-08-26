import { useState } from "react";
import type { Filter, Task } from "../types";
import { TaskRow } from "./TaskRow";

type Props = {
  tasks: Task[];
  visible: Task[];
  filter: Filter;
  onToggle: (id: string) => void;
  onRename: (id: string, text: string) => void;
  onRemove: (id: string) => void;
  onMove: (id: string, delta: -1 | 1) => boolean;
  onReorder: (draggedId: string, targetId: string) => void;
};

const EMPTY: Record<Filter, string> = {
  all: "Nothing on today's docket. Write the first line above.",
  open: "Every line is cleared. The docket is empty.",
  done: "No lines cleared yet.",
};

export function TaskList({
  tasks,
  visible,
  filter,
  onToggle,
  onRename,
  onRemove,
  onMove,
  onReorder,
}: Props) {
  const [draggedId, setDraggedId] = useState<string | null>(null);
  const [overId, setOverId] = useState<string | null>(null);

  if (!visible.length) {
    return (
      <p className="px-4 py-14 text-center font-mono text-[13px] text-ink-soft sm:px-6">
        {EMPTY[filter]}
      </p>
    );
  }

  return (
    <ul className="min-h-0">
      {visible.map((task) => (
        <TaskRow
          key={task.id}
          task={task}
          /* The number is the line's place in the full docket, not in the
             filtered view — otherwise it would renumber as you filter. */
          line={tasks.findIndex((t) => t.id === task.id) + 1}
          isDragging={draggedId === task.id}
          isOver={overId === task.id && draggedId !== task.id}
          onToggle={onToggle}
          onRename={onRename}
          onRemove={onRemove}
          onMove={onMove}
          onDragStart={setDraggedId}
          onDragOver={setOverId}
          onDrop={(targetId) => {
            if (draggedId) onReorder(draggedId, targetId);
            setDraggedId(null);
            setOverId(null);
          }}
          onDragEnd={() => {
            setDraggedId(null);
            setOverId(null);
          }}
        />
      ))}
    </ul>
  );
}
