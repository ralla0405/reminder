"use client";

import { useState, useEffect, useCallback } from "react";
import { Reminder, ReminderList, FilterCounts } from "@/types/reminder";
import { reminderApi } from "@/lib/api";
import { listApi } from "@/lib/listApi";
import Sidebar from "@/components/Sidebar";
import ReminderItem from "@/components/ReminderItem";
import ReminderForm from "@/components/ReminderForm";
import ReminderDetail from "@/components/ReminderDetail";

export default function Home() {
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [counts, setCounts] = useState<FilterCounts>({ today: 0, scheduled: 0, all: 0, completed: 0 });
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [activeFilter, setActiveFilter] = useState<string | null>(null);
  const [activeListId, setActiveListId] = useState<number | null>(null);
  const [isAdding, setIsAdding] = useState(false);
  const [editingReminder, setEditingReminder] = useState<Reminder | null>(null);

  const loadReminders = useCallback(async () => {
    if (activeListId !== null) {
      const data = await listApi.fetchReminders(activeListId);
      setReminders(data);
    } else {
      const data = await reminderApi.fetchAll(activeFilter ?? undefined);
      setReminders(data);
    }
  }, [activeFilter, activeListId]);

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

  const handleFilterChange = (filter: string | null) => {
    setActiveFilter(filter);
    setActiveListId(null);
  };

  const handleListSelect = (listId: number) => {
    setActiveListId(listId);
    setActiveFilter(null);
  };

  const handleCreate = async (title: string) => {
    await reminderApi.create(title);
    setIsAdding(false);
    loadReminders();
    loadCounts();
  };

  const handleToggleComplete = async (id: number) => {
    await reminderApi.toggleComplete(id);
    loadReminders();
    loadCounts();
  };

  const handleDelete = async (id: number) => {
    await reminderApi.delete(id);
    loadReminders();
    loadCounts();
  };

  const handleUpdate = async (id: number, title: string, description?: string, remindAt?: string) => {
    await reminderApi.update(id, title, description, remindAt);
    loadReminders();
    loadCounts();
  };

  const handleListCreate = async (name: string, color: string) => {
    await listApi.create(name, color);
    loadLists();
  };

  const handleListDelete = async (id: number) => {
    await listApi.delete(id);
    if (activeListId === id) {
      setActiveListId(null);
      setActiveFilter(null);
    }
    loadLists();
    loadReminders();
    loadCounts();
  };

  const activeList = lists.find((l) => l.id === activeListId);
  const headerLabel = activeListId
    ? activeList?.name ?? ""
    : activeFilter === "today" ? "오늘"
    : activeFilter === "scheduled" ? "예정"
    : activeFilter === "completed" ? "완료됨"
    : "전체";
  const headerColor = activeList?.color;

  return (
    <div className="flex h-screen">
      <Sidebar
        counts={counts}
        activeFilter={activeFilter}
        activeListId={activeListId}
        lists={lists}
        onFilterChange={handleFilterChange}
        onListSelect={handleListSelect}
        onListCreate={handleListCreate}
        onListDelete={handleListDelete}
      />

      <main className="flex-1 flex flex-col overflow-hidden">
        <header className="px-6 pt-8 pb-2">
          <h1 className="text-3xl font-bold" style={headerColor ? { color: headerColor } : undefined}>
            {headerLabel}
          </h1>
        </header>

        <div className="flex-1 overflow-y-auto">
          <div className="divide-y divide-gray-100">
            {reminders.map((r) => (
              <div key={r.id} className="ml-10">
                <ReminderItem
                  reminder={r}
                  onToggleComplete={handleToggleComplete}
                  onDelete={handleDelete}
                  onEdit={setEditingReminder}
                />
              </div>
            ))}
          </div>

          {isAdding ? (
            <ReminderForm
              onSubmit={handleCreate}
              onCancel={() => setIsAdding(false)}
            />
          ) : (
            activeFilter !== "completed" && (
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
          onSave={handleUpdate}
          onClose={() => setEditingReminder(null)}
        />
      )}
    </div>
  );
}
