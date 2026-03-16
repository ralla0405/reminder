"use client";

import { FilterCounts } from "@/types/reminder";

interface Props {
  counts: FilterCounts;
  activeFilter: string | null;
  onFilterChange: (filter: string | null) => void;
}

const filters = [
  { key: "today", label: "오늘", color: "bg-blue-500", icon: "📅" },
  { key: "scheduled", label: "예정", color: "bg-red-500", icon: "📋" },
  { key: null, label: "전체", color: "bg-gray-800", icon: "📁" },
  { key: "completed", label: "완료됨", color: "bg-gray-400", icon: "✓" },
] as const;

export default function Sidebar({ counts, activeFilter, onFilterChange }: Props) {
  const getCount = (key: string | null) => {
    if (key === null) return counts.all;
    return counts[key as keyof FilterCounts] ?? 0;
  };

  return (
    <aside className="w-72 h-screen bg-[var(--sidebar-bg)] p-4 flex flex-col overflow-y-auto">
      <div className="grid grid-cols-2 gap-3 mb-6">
        {filters.map((f) => (
          <button
            key={f.label}
            onClick={() => onFilterChange(f.key)}
            className={`flex flex-col items-start p-3 rounded-xl transition-colors ${
              activeFilter === f.key
                ? "bg-white shadow-sm"
                : "hover:bg-white/60"
            }`}
          >
            <div className="flex items-center justify-between w-full mb-1">
              <span className={`w-7 h-7 ${f.color} rounded-full flex items-center justify-center text-white text-xs`}>
                {f.icon}
              </span>
              <span className="text-xl font-bold">{getCount(f.key)}</span>
            </div>
            <span className="text-[13px] text-gray-500 font-medium">{f.label}</span>
          </button>
        ))}
      </div>

      <div className="border-t border-gray-300/50 pt-4">
        <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2 px-2">
          나의 목록
        </h3>
      </div>
    </aside>
  );
}
