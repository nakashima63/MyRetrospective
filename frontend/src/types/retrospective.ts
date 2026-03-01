export type KptType = "KEEP" | "PROBLEM" | "TRY";

export interface KptItem {
  id: number;
  type: KptType;
  content: string;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface Retrospective {
  id: number;
  title: string;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface RetrospectiveDetail extends Retrospective {
  kptItems: KptItem[];
}

export interface CreateRetrospectiveRequest {
  title: string;
  description?: string;
}

export interface CreateKptItemRequest {
  type: KptType;
  content: string;
  sortOrder?: number;
}

export interface UpdateKptItemRequest {
  type: KptType;
  content: string;
  sortOrder?: number;
}
