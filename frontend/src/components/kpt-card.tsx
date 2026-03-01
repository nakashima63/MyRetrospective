import { useState, useRef, useEffect } from "react";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Trash2 } from "lucide-react";
import type { KptItem } from "@/types/retrospective";

interface KptCardProps {
  item: KptItem;
  readOnly?: boolean;
  onUpdate?: (content: string) => Promise<void>;
  onDelete?: () => Promise<void>;
}

export function KptCard({ item, readOnly, onUpdate, onDelete }: KptCardProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(item.content);
  const [isDeleting, setIsDeleting] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (isEditing && textareaRef.current) {
      textareaRef.current.focus();
      textareaRef.current.selectionStart = textareaRef.current.value.length;
    }
  }, [isEditing]);

  const handleSave = async () => {
    const trimmed = editContent.trim();
    if (trimmed && trimmed !== item.content) {
      await onUpdate?.(trimmed);
    }
    setIsEditing(false);
  };

  const handleCancel = () => {
    setEditContent(item.content);
    setIsEditing(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSave();
    }
    if (e.key === "Escape") {
      handleCancel();
    }
  };

  const handleDelete = async () => {
    setIsDeleting(true);
    try {
      await onDelete?.();
    } catch {
      setIsDeleting(false);
    }
  };

  if (isEditing) {
    return (
      <div className="rounded-md border bg-white p-3 shadow-sm">
        <Textarea
          ref={textareaRef}
          value={editContent}
          onChange={(e) => setEditContent(e.target.value)}
          onKeyDown={handleKeyDown}
          onBlur={handleSave}
          rows={3}
          className="mb-2 resize-none"
        />
        <div className="flex justify-end gap-1">
          <Button
            variant="ghost"
            size="sm"
            onMouseDown={(e) => e.preventDefault()}
            onClick={handleCancel}
          >
            キャンセル
          </Button>
          <Button
            size="sm"
            onMouseDown={(e) => e.preventDefault()}
            onClick={handleSave}
          >
            保存
          </Button>
        </div>
      </div>
    );
  }

  if (readOnly) {
    return (
      <div className="rounded-md border bg-white p-3 shadow-sm">
        <p className="whitespace-pre-wrap text-sm">{item.content}</p>
      </div>
    );
  }

  return (
    <div
      className="group flex cursor-pointer items-start justify-between rounded-md border bg-white p-3 shadow-sm transition-colors hover:bg-gray-50"
      onClick={() => setIsEditing(true)}
    >
      <p className="flex-1 whitespace-pre-wrap text-sm">{item.content}</p>
      <Button
        variant="ghost"
        size="sm"
        className="ml-2 h-6 w-6 shrink-0 p-0 opacity-0 transition-opacity group-hover:opacity-100"
        onClick={(e) => {
          e.stopPropagation();
          handleDelete();
        }}
        disabled={isDeleting}
      >
        <Trash2 className="h-4 w-4 text-destructive" />
      </Button>
    </div>
  );
}
