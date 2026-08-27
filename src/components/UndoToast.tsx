type Props = {
  label: string | null;
  onUndo: () => void;
};

export function UndoToast({ label, onUndo }: Props) {
  if (!label) return null;

  return (
    <div className="fixed inset-x-4 bottom-5 z-20 flex justify-center motion-safe:animate-[rise_180ms_cubic-bezier(0.2,0.8,0.2,1)]">
      <div className="flex max-w-full items-center gap-5 rounded-[4px] border border-ground-edge bg-ground-lift py-2.5 pr-2.5 pl-4 shadow-[0_18px_40px_-24px_#000]">
        <span className="truncate font-mono text-[12px] text-paper/70">{label}</span>
        <button
          type="button"
          onClick={onUndo}
          className="shrink-0 rounded-[3px] bg-cobalt px-2.5 py-1 font-mono text-[11px] tracking-[0.14em] text-white transition-colors hover:bg-cobalt-deep"
        >
          UNDO
        </button>
      </div>
    </div>
  );
}
