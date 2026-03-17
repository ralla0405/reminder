"use client";

import { useState, useEffect, useCallback } from "react";
import { Reminder, ReminderList, Subtask, Priority } from "@/types/reminder";
import { subtaskApi } from "@/lib/subtaskApi";

const PRIORITIES: { value: Priority; label: string; icon: string }[] = [
  { value: "NONE", label: "없음", icon: "" },
  { value: "LOW", label: "낮음", icon: "!" },
  { value: "MEDIUM", label: "보통", icon: "!!" },
  { value: "HIGH", label: "높음", icon: "!!!" },
];

interface Props {
  reminder: Reminder;
  lists: ReminderList[];
  onSave: (id: number, title: string, description?: string, remindAt?: string, priority?: Priority, reminderListId?: number | null) => void;
  onClose: () => void;
}

export default function ReminderDetail({ reminder, lists, onSave, onClose }: Props) {
  const [title, setTitle] = useState(reminder.title);
  const [description, setDescription] = useState(reminder.description || "");
  const [remindAt, setRemindAt] = useState(reminder.remindAt?.slice(0, 16) || "");
  const [priority, setPriority] = useState<Priority>(reminder.priority || "NONE");
  const [listId, setListId] = useState<number | null>(reminder.reminderListId);
  const [subtasks, setSubtasks] = useState<Subtask[]>([]);
  const [newSubtask, setNewSubtask] = useState("");

  const loadSubtasks = useCallback(async () => {
    const data = await subtaskApi.fetchByReminder(reminder.id);
    setSubtasks(data);
  }, [reminder.id]);

  useEffect(() => {
    loadSubtasks();
  }, [loadSubtasks]);

  const handleSave = () => {
    if (!title.trim()) return;
    onSave(
      reminder.id,
      title.trim(),
      description || undefined,
      remindAt || undefined,
      priority,
      listId,
    );
    onClose();
  };

  const handleAddSubtask = async () => {
    if (!newSubtask.trim()) return;
    await subtaskApi.create(reminder.id, newSubtask.trim(), subtasks.length);
    setNewSubtask("");
    loadSubtasks();
  };

  const handleToggleSubtask = async (subtaskId: number) => {
    await subtaskApi.toggleComplete(reminder.id, subtaskId);
    loadSubtasks();
  };

  const handleDeleteSubtask = async (subtaskId: number) => {
    await subtaskApi.delete(reminder.id, subtaskId);
    loadSubtasks();
  };

  const completedCount = subtasks.filter((s) => s.completed).length;

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
          {/* 제목 */}
          <div>
            <label className="text-sm text-gray-500 mb-1 block">제목</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-xl text-[15px] outline-none focus:border-blue-400"
            />
          </div>

          {/* 메모 */}
          <div>
            <label className="text-sm text-gray-500 mb-1 block">메모</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
              className="w-full px-3 py-2 border border-gray-200 rounded-xl text-[15px] outline-none focus:border-blue-400 resize-none"
            />
          </div>

          {/* 날짜/시간 */}
          <div>
            <label className="text-sm text-gray-500 mb-1 block">날짜/시간</label>
            <input
              type="datetime-local"
              value={remindAt}
              onChange={(e) => setRemindAt(e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-xl text-[15px] outline-none focus:border-blue-400"
            />
          </div>

          {/* 우선순위 */}
          <div>
            <label className="text-sm text-gray-500 mb-1 block">우선순위</label>
            <div className="flex gap-2">
              {PRIORITIES.map((p) => (
                <button
                  key={p.value}
                  onClick={() => setPriority(p.value)}
                  className={`flex-1 py-2 text-[13px] font-medium rounded-lg border transition-colors ${
                    priority === p.value
                      ? "bg-blue-500 text-white border-blue-500"
                      : "border-gray-200 text-gray-600 hover:border-blue-300"
                  }`}
                >
                  {p.icon && <span className="text-orange-500 mr-0.5">{p.icon}</span>}
                  {p.label}
                </button>
              ))}
            </div>
          </div>

          {/* 리스트 선택 */}
          <div>
            <label className="text-sm text-gray-500 mb-1 block">목록</label>
            <select
              value={listId ?? ""}
              onChange={(e) => setListId(e.target.value ? Number(e.target.value) : null)}
              className="w-full px-3 py-2 border border-gray-200 rounded-xl text-[15px] outline-none focus:border-blue-400 bg-white"
            >
              <option value="">없음</option>
              {lists.map((l) => (
                <option key={l.id} value={l.id}>{l.name}</option>
              ))}
            </select>
          </div>

          {/* 하위 작업 */}
          <div>
            <label className="text-sm text-gray-500 mb-1 block">
              하위 작업 {subtasks.length > 0 && <span className="text-gray-400">({completedCount}/{subtasks.length})</span>}
            </label>
            <div className="space-y-1">
              {subtasks.map((st) => (
                <div key={st.id} className="flex items-center gap-2 group">
                  <button
                    onClick={() => handleToggleSubtask(st.id)}
                    className={`w-[18px] h-[18px] rounded-full border-2 flex-shrink-0 flex items-center justify-center transition-all duration-200 ${
                      st.completed ? "bg-blue-500 border-blue-500" : "border-gray-300"
                    }`}
                  >
                    {st.completed && (
                      <svg className="w-2.5 h-2.5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                      </svg>
                    )}
                  </button>
                  <span className={`flex-1 text-[14px] ${st.completed ? "line-through text-gray-400" : ""}`}>{st.title}</span>
                  <button
                    onClick={() => handleDeleteSubtask(st.id)}
                    className="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-red-500 text-xs"
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>
            <div className="flex items-center gap-2 mt-2">
              <input
                type="text"
                value={newSubtask}
                onChange={(e) => setNewSubtask(e.target.value)}
                onKeyDown={(e) => { if (e.key === "Enter") handleAddSubtask(); }}
                placeholder="하위 작업 추가"
                className="flex-1 px-2 py-1.5 text-[13px] border border-gray-200 rounded-lg outline-none focus:border-blue-400"
              />
              <button
                onClick={handleAddSubtask}
                className="text-blue-500 hover:text-blue-600 text-sm font-medium"
              >
                +
              </button>
            </div>
          </div>

          {/* 저장 */}
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
