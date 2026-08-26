type Props = {
  date: Date;
  total: number;
  done: number;
};

/** ISO-8601 week number — the docket's filing reference. */
function isoWeek(date: Date): number {
  const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay() || 7));
  const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
  return Math.ceil(((d.getTime() - yearStart.getTime()) / 86400000 + 1) / 7);
}

const pad = (n: number) => String(n).padStart(2, "0");

export function Masthead({ date, total, done }: Props) {
  const weekday = date.toLocaleDateString(undefined, { weekday: "long" });
  const stamp = date
    .toLocaleDateString(undefined, { day: "numeric", month: "long", year: "numeric" })
    .toUpperCase();

  return (
    <header className="flex flex-wrap items-end justify-between gap-x-8 gap-y-4">
      <div>
        <p className="font-mono text-[11px] tracking-[0.18em] text-paper/45">
          {stamp} <span className="text-paper/25">·</span> WEEK {isoWeek(date)}
        </p>
        <h1 className="mt-2 font-display text-[clamp(2.75rem,10vw,5.5rem)] leading-[0.82] font-extrabold tracking-[-0.04em] text-paper">
          {weekday}
        </h1>
      </div>

      <div className="text-right">
        <p className="font-mono text-[11px] tracking-[0.18em] text-paper/45">CLEARED</p>
        <p className="mt-1 font-mono text-3xl leading-none tabular-nums sm:text-4xl">
          <span className={done ? "text-cobalt" : "text-paper/35"}>{pad(done)}</span>
          <span className="text-paper/25">/</span>
          <span className="text-paper/70">{pad(total)}</span>
        </p>
      </div>
    </header>
  );
}
