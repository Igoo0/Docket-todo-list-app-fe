import type { Task } from "../types";

type Props = {
  tasks: Task[];
  onJump: (id: string) => void;
};

/**
 * A meter, not a chart. One bar per line in docket order, standing on a
 * baseline that runs the full width so a short day still reads as a scale.
 * Bars grow and turn cobalt as lines clear, and jump you to the line.
 */
export function DayGauge({ tasks, onJump }: Props) {
  return (
    <div className="relative mt-8 h-8">
      <div className="absolute inset-x-0 bottom-0 h-px bg-ground-edge" aria-hidden="true" />

      {tasks.length > 0 && (
        <div
          role="group"
          aria-label="Day gauge — jump to a line"
          className="absolute inset-x-0 bottom-0 flex items-end gap-[3px] overflow-hidden"
        >
          {tasks.map((task, i) => (
            <button
              key={task.id}
              type="button"
              onClick={() => onJump(task.id)}
              title={`${i + 1}. ${task.text}`}
              aria-label={`Line ${i + 1}, ${task.done ? "cleared" : "open"}: ${task.text}`}
              className={[
                "min-w-[3px] max-w-[9px] flex-1 rounded-t-[1px] transition-all duration-300 ease-out",
                task.done
                  ? "h-[26px] bg-cobalt hover:bg-cobalt-deep"
                  : "h-[8px] bg-paper/18 hover:h-[15px] hover:bg-paper/40",
              ].join(" ")}
            />
          ))}
        </div>
      )}
    </div>
  );
}
