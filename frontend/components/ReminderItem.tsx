"use client";

import { Reminder } from "@/types/reminder";

interface Props {
  reminder: Reminder;
  onToggleComplete: (id: number) => void;
  onDelete: (id: number) => void;
  onEdit: (reminder: Reminder) => void;
  searchQuery?: string;
}

function HighlightText({ text, query }: { text: string; query?: string }) {
  if (!query || !text) return <>{text}</>;
  const parts = text.split(new RegExp(`(${query.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")})`, "gi"));
  return (
    <>
      {parts.map((part, i) =>
        part.toLowerCase() === query.toLowerCase() ? (
          <mark key={i} className="bg-yellow-200 rounded-sm px-0.5">{part}</mark>
        ) : (
          <span key={i}>{part}</span>
        )
      )}
    </>
  );
}

const PRIORITY_ICONS: Record<string, string> = {
  LOW: "!",
  MEDIUM: "!!",
  HIGH: "!!!",
};

export default function ReminderItem({ reminder, onToggleComplete, onDelete, onEdit, searchQuery }: Props) {
  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return null;
    const date = new Date(dateStr);
    return date.toLocaleDateString("ko-KR", { month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" });
  };

  const priorityIcon = PRIORITY_ICONS[reminder.priority];

  return (
    <div className="group flex items-start gap-3 py-2 px-4">
      {/* 체크박스 */}
      <button
        onClick={() => onToggleComplete(reminder.id)}
        className={`mt-0.5 w-[22px] h-[22px] rounded-full border-2 flex-shrink-0 flex items-center justify-center transition-all duration-300 ease-in-out ${
          reminder.completed
            ? "bg-blue-500 border-blue-500"
            : "border-gray-300 hover:border-blue-400"
        }`}
      >
        {reminder.completed && (
          <svg className="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
          </svg>
        )}
      </button>

      {/* 내용 */}
      <div
        className="flex-1 min-w-0 cursor-pointer"
        onClick={() => onEdit(reminder)}
      >
        <div className="flex items-center gap-1.5">
          {priorityIcon && (
            <span className="text-orange-500 text-[13px] font-bold flex-shrink-0">{priorityIcon}</span>
          )}
          <p className={`text-[15px] font-medium ${reminder.completed ? "line-through opacity-40" : ""}`}>
            <HighlightText text={reminder.title} query={searchQuery} />
          </p>
        </div>
        {reminder.description && (
          <p className="text-[13px] text-gray-500 truncate">
            <HighlightText text={reminder.description} query={searchQuery} />
          </p>
        )}
        {reminder.remindAt && (
          <p className="text-[12px] text-gray-400">{formatDate(reminder.remindAt)}</p>
        )}
      </div>

      {/* 삭제 버튼 */}
      <button
        onClick={() => onDelete(reminder.id)}
        className="opacity-0 group-hover:opacity-100 transition-opacity text-red-500 hover:text-red-600 mt-0.5 flex-shrink-0"
      >
        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  );
}
