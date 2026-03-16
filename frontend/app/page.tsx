"use client";

import { useState, useEffect, useCallback } from "react";
import { Reminder, FilterCounts } from "@/types/reminder";
import { reminderApi } from "@/lib/api";
import Sidebar from "@/components/Sidebar";
import ReminderItem from "@/components/ReminderItem";
import ReminderForm from "@/components/ReminderForm";
import ReminderDetail from "@/components/ReminderDetail";

export default function Home() {
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [counts, setCounts] = useState<FilterCounts>({ today: 0, scheduled: 0, all: 0, completed: 0 });
  const [activeFilter, setActiveFilter] = useState<string | null>(null);
  const [isAdding, setIsAdding] = useState(false);
  const [editingReminder, setEditingReminder] = useState<Reminder | null>(null);

  const loadReminders = useCallback(async () => {
    const data = await reminderApi.fetchAll(activeFilter ?? undefined);
    setReminders(data);
  }, [activeFilter]);

  const loadCounts = async () => {
    const data = await reminderApi.fetchCounts();
    setCounts(data);
  };

  useEffect(() => {
    loadReminders();
    loadCounts();
  }, [loadReminders]);

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

  const filterLabel = activeFilter === "today" ? "오늘"
    : activeFilter === "scheduled" ? "예정"
    : activeFilter === "completed" ? "완료됨"
    : "전체";

  return (
    <div className="flex h-screen">
      <Sidebar
        counts={counts}
        activeFilter={activeFilter}
        onFilterChange={setActiveFilter}
      />

      <main className="flex-1 flex flex-col overflow-hidden">
        <header className="px-6 pt-8 pb-2">
          <h1 className="text-3xl font-bold">{filterLabel}</h1>
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
