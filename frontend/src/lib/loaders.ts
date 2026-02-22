import { redirect } from "react-router";
import { isAuthenticated } from "@/lib/auth";

export function requireAuth() {
  if (!isAuthenticated()) {
    return redirect("/login");
  }
  return null;
}
