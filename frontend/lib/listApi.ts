import { ReminderList, Reminder } from "@/types/reminder";

const BASE_URL = "/api";

async function fetchJSON<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, options);
  if (!res.ok) throw new Error(`API error: ${res.status}`);
  if (res.status === 204) return undefined as T;
  return res.json();
}

export const listApi = {
  fetchAll(): Promise<ReminderList[]> {
    return fetchJSON(`${BASE_URL}/lists`);
  },

  fetchReminders(listId: number): Promise<Reminder[]> {
    return fetchJSON(`${BASE_URL}/lists/${listId}/reminders`);
  },

  create(name: string, color?: string): Promise<ReminderList> {
    return fetchJSON(`${BASE_URL}/lists`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, color }),
    });
  },

  update(id: number, name: string, color: string): Promise<ReminderList> {
    return fetchJSON(`${BASE_URL}/lists/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, color }),
    });
  },

  delete(id: number): Promise<void> {
    return fetchJSON(`${BASE_URL}/lists/${id}`, { method: "DELETE" });
  },
};
