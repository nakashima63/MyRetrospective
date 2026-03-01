import { apiFetch } from "@/lib/api";
import type {
  KptItem,
  CreateKptItemRequest,
  UpdateKptItemRequest,
} from "@/types/retrospective";

export async function createKptItem(
  retrospectiveId: number,
  data: CreateKptItemRequest,
): Promise<KptItem> {
  const response = await apiFetch(`/retrospectives/${retrospectiveId}/items`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error("Failed to create KPT item");
  return response.json();
}

export async function updateKptItem(
  retrospectiveId: number,
  itemId: number,
  data: UpdateKptItemRequest,
): Promise<KptItem> {
  const response = await apiFetch(
    `/retrospectives/${retrospectiveId}/items/${itemId}`,
    {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    },
  );
  if (!response.ok) throw new Error("Failed to update KPT item");
  return response.json();
}

export async function deleteKptItem(
  retrospectiveId: number,
  itemId: number,
): Promise<void> {
  const response = await apiFetch(
    `/retrospectives/${retrospectiveId}/items/${itemId}`,
    { method: "DELETE" },
  );
  if (!response.ok) throw new Error("Failed to delete KPT item");
}
