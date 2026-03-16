"use client";

import { useState } from "react";
import { Reminder } from "@/types/reminder";

interface Props {
  reminder: Reminder;
  onSave: (id: number, title: string, description?: string, remindAt?: string) => void;
  onClose: () => void;
}

export default function ReminderDetail({ reminder, onSave, onClose }: Props) {
  const [title, setTitle] = useState(reminder.title);
  const [description, setDescription] = useState(reminder.description || "");
  const [remindAt, setRemindAt] = useState(reminder.remindAt?.slice(0, 16) || "");

  const handleSave = () => {
    if (!title.trim()) return;
    onSave(reminder.id, title.trim(), description || undefined, remindAt || undefined);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex justify-end">
      <div className="absolute inset-0 bg-black/20" onClick={onClose} />
      <div className="relative w-full max-w-sm bg-white h-full shadow-xl p-6 overflow-y-auto animate-slide-in">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold">상세 정보</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div className="space-y-4">
          <div>
            <label className="text-sm text-gray-500 mb-1 block">제목</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-xl text-[15px] outline-none focus:border-blue-400"
            />
          </div>

          <div>
            <label className="text-sm text-gray-500 mb-1 block">메모</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
              className="w-full px-3 py-2 border border-gray-200 rounded-xl text-[15px] outline-none focus:border-blue-400 resize-none"
            />
          </div>

          <div>
            <label className="text-sm text-gray-500 mb-1 block">날짜/시간</label>
            <input
              type="datetime-local"
              value={remindAt}
              onChange={(e) => setRemindAt(e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-xl text-[15px] outline-none focus:border-blue-400"
            />
          </div>

          <button
            onClick={handleSave}
            className="w-full py-2.5 bg-blue-500 text-white rounded-xl text-[15px] font-medium hover:bg-blue-600 transition-colors"
          >
            저장
          </button>
        </div>
      </div>
    </div>
  );
}
