import { useState, useCallback } from "react";
import { useLoaderData, useNavigate } from "react-router";
import { Button } from "@/components/ui/button";
import { ArrowLeft } from "lucide-react";
import { KptColumn } from "@/components/kpt-column";
import { createKptItem, updateKptItem, deleteKptItem } from "@/lib/api";
import type {
  RetrospectiveDetail,
  KptItem,
  KptType,
} from "@/types/retrospective";

export default function EditRetrospective() {
  const { retrospective: initial } = useLoaderData<{
    retrospective: RetrospectiveDetail;
  }>();
  const navigate = useNavigate();
  const [kptItems, setKptItems] = useState<KptItem[]>(initial.kptItems);

  const itemsByType = useCallback(
    (type: KptType) =>
      kptItems
        .filter((item) => item.type === type)
        .sort((a, b) => a.sortOrder - b.sortOrder),
    [kptItems],
  );

  const handleAddItem = useCallback(
    async (type: KptType, content: string) => {
      const sameTypeItems = kptItems.filter((item) => item.type === type);
      const sortOrder = sameTypeItems.length;
      const newItem = await createKptItem(initial.id, {
        type,
        content,
        sortOrder,
      });
      setKptItems((prev) => [...prev, newItem]);
    },
    [initial.id, kptItems],
  );

  const handleUpdateItem = useCallback(
    async (itemId: number, content: string) => {
      const item = kptItems.find((i) => i.id === itemId);
      if (!item) return;
      const updated = await updateKptItem(initial.id, itemId, {
        type: item.type,
        content,
        sortOrder: item.sortOrder,
      });
      setKptItems((prev) => prev.map((i) => (i.id === itemId ? updated : i)));
    },
    [initial.id, kptItems],
  );

  const handleDeleteItem = useCallback(
    async (itemId: number) => {
      await deleteKptItem(initial.id, itemId);
      setKptItems((prev) => prev.filter((i) => i.id !== itemId));
    },
    [initial.id],
  );

  return (
    <div className="flex min-h-screen flex-col">
      <header className="border-b bg-white px-6 py-4">
        <div className="mx-auto flex max-w-7xl items-center gap-4">
          <Button variant="ghost" size="sm" onClick={() => navigate("/")}>
            <ArrowLeft className="mr-1 h-4 w-4" />
            戻る
          </Button>
          <div className="flex-1">
            <h1 className="text-xl font-bold">{initial.title}</h1>
            {initial.description && (
              <p className="text-sm text-muted-foreground">
                {initial.description}
              </p>
            )}
          </div>
        </div>
      </header>

      <main className="flex-1 p-6">
        <div className="mx-auto grid max-w-7xl grid-cols-1 items-start gap-4 md:grid-cols-3">
          <KptColumn
            type="KEEP"
            items={itemsByType("KEEP")}
            onAddItem={(content) => handleAddItem("KEEP", content)}
            onUpdateItem={handleUpdateItem}
            onDeleteItem={handleDeleteItem}
          />
          <KptColumn
            type="PROBLEM"
            items={itemsByType("PROBLEM")}
            onAddItem={(content) => handleAddItem("PROBLEM", content)}
            onUpdateItem={handleUpdateItem}
            onDeleteItem={handleDeleteItem}
          />
          <KptColumn
            type="TRY"
            items={itemsByType("TRY")}
            onAddItem={(content) => handleAddItem("TRY", content)}
            onUpdateItem={handleUpdateItem}
            onDeleteItem={handleDeleteItem}
          />
        </div>
      </main>
    </div>
  );
}
