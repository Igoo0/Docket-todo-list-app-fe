import { useState } from "react";
import type { RefObject } from "react";

type Props = {
  inputRef: RefObject<HTMLInputElement | null>;
  onAdd: (text: string) => void;
};

/** Mirrors TaskRow's grid so the field lines up with the lines below it. */
export function Composer({ inputRef, onAdd }: Props) {
  const [value, setValue] = useState("");
  const ready = value.trim().length > 0;

  return (
    <form
      className="grid grid-cols-[1.75rem_1fr_auto] items-center gap-3 border-b border-rule py-3 pr-4 pl-4 sm:grid-cols-[0.75rem_1.75rem_1fr_auto] sm:gap-4 sm:pr-6 sm:pl-3"
      onSubmit={(e) => {
        e.preventDefault();
        onAdd(value);
        setValue("");
      }}
    >
      <span aria-hidden="true" className="hidden sm:block" />

      <span
        aria-hidden="true"
        className="grid h-7 w-7 place-items-center rounded-[3px] border border-dashed border-ink/25 font-mono text-sm leading-none text-ink-soft"
      >
        +
      </span>

      <input
        id="new-line"
        ref={inputRef}
        value={value}
        onChange={(e) => setValue(e.target.value)}
        maxLength={240}
        autoComplete="off"
        placeholder="What needs doing?"
        aria-label="New line"
        className="min-w-0 bg-transparent text-ink outline-none placeholder:text-ink-soft/70"
      />

      <button
        type="submit"
        disabled={!ready}
        className="shrink-0 rounded-[3px] bg-cobalt px-3.5 py-2 text-sm font-semibold text-white transition-colors hover:bg-cobalt-deep disabled:opacity-30"
      >
        Add line
      </button>
    </form>
  );
}
