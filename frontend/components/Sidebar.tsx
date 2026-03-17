"use client";

import { useState } from "react";
import { FilterCounts, ReminderList } from "@/types/reminder";

const APPLE_COLORS = [
  "#FF3B30", "#FF9500", "#FFCC00", "#34C759",
  "#00C7BE", "#30B0C7", "#007AFF", "#5856D6",
  "#AF52DE", "#FF2D55", "#A2845E", "#8E8E93",
];

interface Props {
  counts: FilterCounts;
  activeFilter: string | null;
  activeListId: number | null;
  lists: ReminderList[];
  onFilterChange: (filter: string | null) => void;
  onListSelect: (listId: number) => void;
  onListCreate: (name: string, color: string) => void;
  onListDelete: (id: number) => void;
  searchQuery: string;
  onSearchChange: (query: string) => void;
}

const filters = [
  { key: "today", label: "오늘", color: "bg-blue-500", icon: "📅" },
  { key: "scheduled", label: "예정", color: "bg-red-500", icon: "📋" },
  { key: null, label: "전체", color: "bg-gray-800", icon: "📁" },
  { key: "completed", label: "완료됨", color: "bg-gray-400", icon: "✓" },
] as const;

export default function Sidebar({
  counts, activeFilter, activeListId, lists,
  onFilterChange, onListSelect, onListCreate, onListDelete,
  searchQuery, onSearchChange,
}: Props) {
  const [isCreating, setIsCreating] = useState(false);
  const [newName, setNewName] = useState("");
  const [newColor, setNewColor] = useState("#007AFF");

  const getCount = (key: string | null) => {
    if (key === null) return counts.all;
    return counts[key as keyof FilterCounts] ?? 0;
  };

  const handleCreate = () => {
    if (!newName.trim()) return;
    onListCreate(newName.trim(), newColor);
    setNewName("");
    setNewColor("#007AFF");
    setIsCreating(false);
  };

  return (
    <aside className="w-72 h-screen bg-[var(--sidebar-bg)] p-4 flex flex-col overflow-y-auto">
      {/* 검색 */}
      <div className="mb-4">
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
          placeholder="검색"
          className="w-full px-3 py-1.5 text-sm bg-white/70 border border-gray-200 rounded-lg outline-none focus:border-blue-400 placeholder-gray-400"
        />
      </div>

      {/* 필터 카드 */}
      <div className="grid grid-cols-2 gap-3 mb-6">
        {filters.map((f) => (
          <button
            key={f.label}
            onClick={() => onFilterChange(f.key)}
            className={`flex flex-col items-start p-3 rounded-xl transition-colors ${
              activeFilter === f.key && activeListId === null
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

      {/* 리스트 목록 */}
      <div className="border-t border-gray-300/50 pt-4 flex-1">
        <div className="flex items-center justify-between mb-2 px-2">
          <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider">나의 목록</h3>
          <button
            onClick={() => setIsCreating(true)}
            className="text-blue-500 hover:text-blue-600 text-sm font-medium"
          >
            +
          </button>
        </div>

        {lists.map((list) => (
          <div
            key={list.id}
            className={`group flex items-center gap-3 px-2 py-2 rounded-lg cursor-pointer transition-colors ${
              activeListId === list.id ? "bg-white shadow-sm" : "hover:bg-white/60"
            }`}
            onClick={() => onListSelect(list.id)}
          >
            <span
              className="w-7 h-7 rounded-full flex items-center justify-center text-white text-xs flex-shrink-0"
              style={{ backgroundColor: list.color }}
            >
              ●
            </span>
            <span className="flex-1 text-[14px] font-medium truncate">{list.name}</span>
            <button
              onClick={(e) => { e.stopPropagation(); onListDelete(list.id); }}
              className="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-red-500 text-xs"
            >
              ✕
            </button>
          </div>
        ))}

        {isCreating && (
          <div className="px-2 py-2 space-y-2">
            <input
              type="text"
              value={newName}
              onChange={(e) => setNewName(e.target.value)}
              onKeyDown={(e) => { if (e.key === "Enter") handleCreate(); if (e.key === "Escape") setIsCreating(false); }}
              placeholder="리스트 이름"
              className="w-full px-2 py-1 text-sm border border-gray-200 rounded-lg outline-none focus:border-blue-400"
              autoFocus
            />
            <div className="flex gap-1 flex-wrap">
              {APPLE_COLORS.map((c) => (
                <button
                  key={c}
                  onClick={() => setNewColor(c)}
                  className={`w-5 h-5 rounded-full ${newColor === c ? "ring-2 ring-offset-1 ring-blue-500" : ""}`}
                  style={{ backgroundColor: c }}
                />
              ))}
            </div>
            <div className="flex gap-2">
              <button onClick={handleCreate} className="text-xs text-blue-500 font-medium">추가</button>
              <button onClick={() => setIsCreating(false)} className="text-xs text-gray-400">취소</button>
            </div>
          </div>
        )}
      </div>
    </aside>
  );
}
