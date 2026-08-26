import type { Filter } from "../types";

type Props = {
  filter: Filter;
  counts: Record<Filter, number>;
  onFilter: (filter: Filter) => void;
  onClearDone: () => void;
};

const TABS: { key: Filter; label: string }[] = [
  { key: "all", label: "ALL" },
  { key: "open", label: "OPEN" },
  { key: "done", label: "CLEARED" },
];

export function FilterBar({ filter, counts, onFilter, onClearDone }: Props) {
  return (
    <div className="flex items-center justify-between gap-4 border-b border-rule bg-paper-shade/50 px-4 py-2 sm:px-6">
      <div className="flex gap-1">
        {TABS.map((tab) => {
          const active = filter === tab.key;
          return (
            <button
              key={tab.key}
              type="button"
              aria-pressed={active}
              onClick={() => onFilter(tab.key)}
              className={[
                "rounded-[2px] px-2 py-1 font-mono text-[11px] tracking-[0.1em] whitespace-nowrap transition-colors sm:tracking-[0.14em]",
                "border-b-2",
                active
                  ? "border-cobalt text-ink"
                  : "border-transparent text-ink-soft hover:text-ink",
              ].join(" ")}
            >
              {tab.label} <span className="opacity-50 tabular-nums">{counts[tab.key]}</span>
            </button>
          );
        })}
      </div>

      {counts.done > 0 && (
        <button
          type="button"
          onClick={onClearDone}
          className="font-mono text-[11px] tracking-[0.1em] whitespace-nowrap text-ink-soft underline underline-offset-4 transition-colors hover:text-brick"
        >
          REMOVE CLEARED
        </button>
      )}
    </div>
  );
}
