"use client";

import { useState, useRef, useEffect } from "react";

interface Props {
  onSubmit: (title: string) => void;
  onCancel: () => void;
}

export default function ReminderForm({ onSubmit, onCancel }: Props) {
  const [title, setTitle] = useState("");
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && title.trim()) {
      onSubmit(title.trim());
      setTitle("");
    } else if (e.key === "Escape") {
      onCancel();
    }
  };

  return (
    <div className="flex items-center gap-3 py-2 px-4">
      <div className="w-[22px] h-[22px] rounded-full border-2 border-gray-300 flex-shrink-0" />
      <input
        ref={inputRef}
        type="text"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        onKeyDown={handleKeyDown}
        onBlur={() => { if (!title.trim()) onCancel(); }}
        placeholder="새로운 미리 알림"
        className="flex-1 text-[15px] outline-none bg-transparent"
      />
    </div>
  );
}
