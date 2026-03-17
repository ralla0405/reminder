"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { Reminder, ReminderList, FilterCounts, Priority } from "@/types/reminder";
import { reminderApi } from "@/lib/api";
import { listApi } from "@/lib/listApi";
import Sidebar from "@/components/Sidebar";
import ReminderItem from "@/components/ReminderItem";
import ReminderForm from "@/components/ReminderForm";
import ReminderDetail from "@/components/ReminderDetail";

function groupByDate(reminders: Reminder[]) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);
  const endOfWeek = new Date(today);
  endOfWeek.setDate(endOfWeek.getDate() + (7 - endOfWeek.getDay()));

  const groups: { label: string; items: Reminder[] }[] = [
    { label: "오늘", items: [] },
    { label: "내일", items: [] },
    { label: "이번 주", items: [] },
    { label: "이후", items: [] },
  ];

  for (const r of reminders) {
    if (!r.remindAt) { groups[3].items.push(r); continue; }
    const d = new Date(r.remindAt);
    d.setHours(0, 0, 0, 0);
    if (d.getTime() === today.getTime()) groups[0].items.push(r);
    else if (d.getTime() === tomorrow.getTime()) groups[1].items.push(r);
    else if (d < endOfWeek) groups[2].items.push(r);
    else groups[3].items.push(r);
  }

  return groups.filter((g) => g.items.length > 0);
}

export default function Home() {
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [counts, setCounts] = useState<FilterCounts>({ today: 0, scheduled: 0, all: 0, completed: 0 });
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [activeFilter, setActiveFilter] = useState<string | null>(null);
  const [activeListId, setActiveListId] = useState<number | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [isAdding, setIsAdding] = useState(false);
  const [editingReminder, setEditingReminder] = useState<Reminder | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const debounceTimer = useRef<ReturnType<typeof setTimeout>>(null);

  // 검색 debounce
  useEffect(() => {
    debounceTimer.current = setTimeout(() => {
      setDebouncedSearch(searchQuery);
    }, 300);
    return () => { if (debounceTimer.current) clearTimeout(debounceTimer.current); };
  }, [searchQuery]);

  const loadReminders = useCallback(async () => {
    if (debouncedSearch) {
      const data = await reminderApi.fetchAll(undefined, debouncedSearch);
      setReminders(data);
    } else if (activeListId !== null) {
      const data = await listApi.fetchReminders(activeListId);
      setReminders(data);
    } else {
      const data = await reminderApi.fetchAll(activeFilter ?? undefined);
      setReminders(data);
    }
  }, [activeFilter, activeListId, debouncedSearch]);

  const loadCounts = async () => {
    setCounts(await reminderApi.fetchCounts());
  };

  const loadLists = async () => {
    setLists(await listApi.fetchAll());
  };

  useEffect(() => {
    loadReminders();
    loadCounts();
    loadLists();
  }, [loadReminders]);

  const handleSearchChange = (query: string) => {
    setSearchQuery(query);
    if (query) {
      setActiveFilter(null);
      setActiveListId(null);
    }
  };

  const handleFilterChange = (filter: string | null) => {
    setActiveFilter(filter);
    setActiveListId(null);
    setSearchQuery("");
    setSidebarOpen(false);
  };

  const handleListSelect = (listId: number) => {
    setActiveListId(listId);
    setActiveFilter(null);
    setSearchQuery("");
    setSidebarOpen(false);
  };

  const refresh = () => { loadReminders(); loadCounts(); loadLists(); };

  const handleCreate = async (title: string) => {
    await reminderApi.create(title, undefined, undefined, undefined, activeListId ?? undefined);
    setIsAdding(false);
    refresh();
  };

  const handleToggleComplete = async (id: number) => {
    await reminderApi.toggleComplete(id);
    refresh();
  };

  const handleDelete = async (id: number) => {
    setDeletingId(id);
    setTimeout(async () => {
      await reminderApi.delete(id);
      setDeletingId(null);
      refresh();
    }, 300);
  };

  const handleUpdate = async (id: number, title: string, description?: string, remindAt?: string, priority?: Priority, reminderListId?: number | null) => {
    await reminderApi.update(id, title, description, remindAt, priority, reminderListId ?? undefined);
    refresh();
  };

  const handleListCreate = async (name: string, color: string) => {
    await listApi.create(name, color);
    loadLists();
  };

  const handleListDelete = async (id: number) => {
    await listApi.delete(id);
    if (activeListId === id) { setActiveListId(null); setActiveFilter(null); }
    loadLists();
    refresh();
  };

  const activeList = lists.find((l) => l.id === activeListId);
  const headerLabel = debouncedSearch ? `"${debouncedSearch}" 검색 결과`
    : activeListId ? (activeList?.name ?? "")
    : activeFilter === "today" ? "오늘"
    : activeFilter === "scheduled" ? "예정"
    : activeFilter === "completed" ? "완료됨"
    : "전체";
  const headerColor = activeList?.color;

  const isScheduledView = activeFilter === "scheduled" && !debouncedSearch && !activeListId;
  const dateGroups = isScheduledView ? groupByDate(reminders) : null;

  const renderReminderItem = (r: Reminder) => (
    <div
      key={r.id}
      className={`ml-10 transition-all duration-300 ${deletingId === r.id ? "max-h-0 opacity-0 overflow-hidden" : "max-h-40"}`}
    >
      <ReminderItem
        reminder={r}
        onToggleComplete={handleToggleComplete}
        onDelete={handleDelete}
        onEdit={setEditingReminder}
        searchQuery={debouncedSearch || undefined}
      />
    </div>
  );

  return (
    <div className="flex h-screen">
      {/* 모바일 오버레이 */}
      {sidebarOpen && (
        <div className="fixed inset-0 bg-black/30 z-30 md:hidden" onClick={() => setSidebarOpen(false)} />
      )}

      {/* 사이드바 */}
      <div className={`
        fixed inset-y-0 left-0 z-40 transform transition-transform duration-300 md:relative md:translate-x-0
        ${sidebarOpen ? "translate-x-0" : "-translate-x-full"}
      `}>
        <Sidebar
          counts={counts}
          activeFilter={activeFilter}
          activeListId={activeListId}
          lists={lists}
          onFilterChange={handleFilterChange}
          onListSelect={handleListSelect}
          onListCreate={handleListCreate}
          onListDelete={handleListDelete}
          searchQuery={searchQuery}
          onSearchChange={handleSearchChange}
        />
      </div>

      <main className="flex-1 flex flex-col overflow-hidden">
        <header className="px-6 pt-8 pb-2 flex items-center gap-3">
          {/* 모바일 햄버거 */}
          <button
            onClick={() => setSidebarOpen(true)}
            className="md:hidden text-gray-600"
          >
            <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          <h1 className="text-3xl font-bold" style={headerColor ? { color: headerColor } : undefined}>
            {headerLabel}
          </h1>
        </header>

        <div className="flex-1 overflow-y-auto">
          {dateGroups ? (
            // 예정 뷰: 날짜별 그룹핑
            dateGroups.map((group) => (
              <div key={group.label} className="mb-4">
                <h3 className="px-6 py-2 text-sm font-semibold text-gray-500 uppercase tracking-wide">
                  {group.label}
                </h3>
                <div className="divide-y divide-gray-100">
                  {group.items.map(renderReminderItem)}
                </div>
              </div>
            ))
          ) : (
            <div className="divide-y divide-gray-100">
              {reminders.map(renderReminderItem)}
            </div>
          )}

          {!debouncedSearch && isAdding ? (
            <ReminderForm
              onSubmit={handleCreate}
              onCancel={() => setIsAdding(false)}
            />
          ) : (
            !debouncedSearch && activeFilter !== "completed" && (
              <button
                onClick={() => setIsAdding(true)}
                className="flex items-center gap-2 px-4 py-3 text-blue-500 hover:text-blue-600 text-[15px] font-medium"
              >
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
                </svg>
                새로운 미리 알림
              </button>
            )
          )}
        </div>
      </main>

      {editingReminder && (
        <ReminderDetail
          reminder={editingReminder}
          lists={lists}
          onSave={handleUpdate}
          onClose={() => setEditingReminder(null)}
        />
      )}
    </div>
  );
}
