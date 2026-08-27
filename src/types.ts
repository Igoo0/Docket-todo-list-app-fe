export type Task = {
  id: string;
  text: string;
  done: boolean;
};

export type Filter = "all" | "open" | "done";
