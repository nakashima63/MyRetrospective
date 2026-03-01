import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Plus } from "lucide-react";
import { KptCard } from "@/components/kpt-card";
import type { KptItem, KptType } from "@/types/retrospective";

const COLUMN_CONFIG: Record<
  KptType,
  { label: string; color: string; bgColor: string }
> = {
  KEEP: {
    label: "Keep",
    color: "text-emerald-700",
    bgColor: "bg-emerald-50",
  },
  PROBLEM: {
    label: "Problem",
    color: "text-red-700",
    bgColor: "bg-red-50",
  },
  TRY: {
    label: "Try",
    color: "text-amber-700",
    bgColor: "bg-amber-50",
  },
};

interface KptColumnProps {
  type: KptType;
  items: KptItem[];
  onAddItem: (content: string) => Promise<void>;
  onUpdateItem: (itemId: number, content: string) => Promise<void>;
  onDeleteItem: (itemId: number) => Promise<void>;
}

export function KptColumn({
  type,
  items,
  onAddItem,
  onUpdateItem,
  onDeleteItem,
}: KptColumnProps) {
  const [isAdding, setIsAdding] = useState(false);
  const [newContent, setNewContent] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const config = COLUMN_CONFIG[type];

  const handleAdd = async () => {
    const trimmed = newContent.trim();
    if (!trimmed) return;
    setIsSubmitting(true);
    try {
      await onAddItem(trimmed);
      setNewContent("");
      setIsAdding(false);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleAdd();
    }
    if (e.key === "Escape") {
      setNewContent("");
      setIsAdding(false);
    }
  };

  return (
    <div className={`flex flex-col rounded-lg ${config.bgColor} p-4`}>
      <div className="mb-3 flex items-center justify-between">
        <h3 className={`text-lg font-semibold ${config.color}`}>
          {config.label}
        </h3>
        <span className={`text-sm ${config.color}`}>{items.length}</span>
      </div>

      <div className="flex flex-1 flex-col gap-2">
        {items.map((item) => (
          <KptCard
            key={item.id}
            item={item}
            onUpdate={(content) => onUpdateItem(item.id, content)}
            onDelete={() => onDeleteItem(item.id)}
          />
        ))}
      </div>

      {isAdding ? (
        <div className="mt-3">
          <Textarea
            value={newContent}
            onChange={(e) => setNewContent(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder={`${config.label} を入力...`}
            rows={3}
            className="mb-2 resize-none bg-white"
            autoFocus
          />
          <div className="flex justify-end gap-1">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => {
                setNewContent("");
                setIsAdding(false);
              }}
              disabled={isSubmitting}
            >
              キャンセル
            </Button>
            <Button
              size="sm"
              onClick={handleAdd}
              disabled={!newContent.trim() || isSubmitting}
            >
              追加
            </Button>
          </div>
        </div>
      ) : (
        <Button
          variant="ghost"
          className={`mt-3 w-full justify-start ${config.color}`}
          onClick={() => setIsAdding(true)}
        >
          <Plus className="mr-1 h-4 w-4" />
          カードを追加
        </Button>
      )}
    </div>
  );
}
