export interface Reminder {
  id: number;
  title: string;
  description: string | null;
  remindAt: string | null;
  completed: boolean;
  createdAt: string;
}

export interface ReminderList {
  id: number;
  name: string;
  color: string;
  createdAt: string;
}

export interface Subtask {
  id: number;
  title: string;
  completed: boolean;
  sortOrder: number;
}

export type Priority = "NONE" | "LOW" | "MEDIUM" | "HIGH";

export interface FilterCounts {
  today: number;
  scheduled: number;
  all: number;
  completed: number;
}
