import { apiFetch } from "@/lib/api";
import type {
  Retrospective,
  RetrospectiveDetail,
  CreateRetrospectiveRequest,
} from "@/types/retrospective";

export async function listRetrospectives(): Promise<Retrospective[]> {
  const response = await apiFetch("/retrospectives");
  if (!response.ok) throw new Error("Failed to fetch retrospectives");
  return response.json();
}

export async function createRetrospective(
  data: CreateRetrospectiveRequest,
): Promise<Retrospective> {
  const response = await apiFetch("/retrospectives", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!response.ok) throw new Error("Failed to create retrospective");
  return response.json();
}

export async function getRetrospective(
  id: number,
): Promise<RetrospectiveDetail> {
  const response = await apiFetch(`/retrospectives/${id}`);
  if (!response.ok) throw new Error("Failed to fetch retrospective");
  return response.json();
}
