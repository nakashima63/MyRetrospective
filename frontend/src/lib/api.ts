import {
  getAccessToken,
  getRefreshToken,
  setTokens,
  clearTokens,
} from "@/lib/auth";

const API_BASE = "/api";

async function refreshAccessToken(): Promise<boolean> {
  const refreshToken = getRefreshToken();
  if (!refreshToken) return false;

  try {
    const response = await fetch(`${API_BASE}/auth/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
      clearTokens();
      return false;
    }

    const data = await response.json();
    setTokens(data.accessToken, data.refreshToken);
    return true;
  } catch {
    clearTokens();
    return false;
  }
}

export async function apiFetch(
  path: string,
  options: RequestInit = {},
): Promise<Response> {
  const headers = new Headers(options.headers);
  const accessToken = getAccessToken();
  if (accessToken) {
    headers.set("Authorization", `Bearer ${accessToken}`);
  }

  let response = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (response.status === 401 && accessToken) {
    const refreshed = await refreshAccessToken();
    if (refreshed) {
      headers.set("Authorization", `Bearer ${getAccessToken()}`);
      response = await fetch(`${API_BASE}${path}`, { ...options, headers });
    }
  }

  return response;
}

export interface SignupData {
  email: string;
  password: string;
  username: string;
}

export interface LoginData {
  email: string;
  password: string;
}

export async function signup(data: SignupData): Promise<Response> {
  return fetch(`${API_BASE}/auth/signup`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}

export async function login(data: LoginData): Promise<Response> {
  return fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
}
