import { Reminder, FilterCounts, Priority } from "@/types/reminder";

const BASE_URL = "/api";

async function fetchJSON<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, options);
  if (!res.ok) {
    throw new Error(`API error: ${res.status}`);
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

export const reminderApi = {
  fetchAll(filter?: string, search?: string): Promise<Reminder[]> {
    const params = new URLSearchParams();
    if (search) params.set("search", search);
    else if (filter) params.set("filter", filter);
    const query = params.toString();
    return fetchJSON(`${BASE_URL}/reminders${query ? `?${query}` : ""}`);
  },

  fetchById(id: number): Promise<Reminder> {
    return fetchJSON(`${BASE_URL}/reminders/${id}`);
  },

  fetchCounts(): Promise<FilterCounts> {
    return fetchJSON(`${BASE_URL}/reminders/counts`);
  },

  create(title: string, description?: string, remindAt?: string, priority?: Priority, reminderListId?: number): Promise<Reminder> {
    return fetchJSON(`${BASE_URL}/reminders`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title, description, remindAt, priority, reminderListId }),
    });
  },

  update(id: number, title: string, description?: string, remindAt?: string, priority?: Priority, reminderListId?: number | null): Promise<Reminder> {
    return fetchJSON(`${BASE_URL}/reminders/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title, description, remindAt, priority, reminderListId }),
    });
  },

  toggleComplete(id: number): Promise<Reminder> {
    return fetchJSON(`${BASE_URL}/reminders/${id}/complete`, { method: "PATCH" });
  },

  delete(id: number): Promise<void> {
    return fetchJSON(`${BASE_URL}/reminders/${id}`, { method: "DELETE" });
  },
};
