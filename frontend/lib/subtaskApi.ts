import { Subtask } from "@/types/reminder";

const BASE_URL = "/api";

async function fetchJSON<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, options);
  if (!res.ok) throw new Error(`API error: ${res.status}`);
  if (res.status === 204) return undefined as T;
  return res.json();
}

export const subtaskApi = {
  fetchByReminder(reminderId: number): Promise<Subtask[]> {
    return fetchJSON(`${BASE_URL}/reminders/${reminderId}/subtasks`);
  },

  create(reminderId: number, title: string, sortOrder?: number): Promise<Subtask> {
    return fetchJSON(`${BASE_URL}/reminders/${reminderId}/subtasks`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title, sortOrder }),
    });
  },

  update(reminderId: number, subtaskId: number, title: string, sortOrder?: number): Promise<Subtask> {
    return fetchJSON(`${BASE_URL}/reminders/${reminderId}/subtasks/${subtaskId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title, sortOrder }),
    });
  },

  toggleComplete(reminderId: number, subtaskId: number): Promise<Subtask> {
    return fetchJSON(`${BASE_URL}/reminders/${reminderId}/subtasks/${subtaskId}/complete`, {
      method: "PATCH",
    });
  },

  delete(reminderId: number, subtaskId: number): Promise<void> {
    return fetchJSON(`${BASE_URL}/reminders/${reminderId}/subtasks/${subtaskId}`, {
      method: "DELETE",
    });
  },
};
