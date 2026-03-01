import { redirect } from "react-router";
import type { LoaderFunctionArgs } from "react-router";
import { isAuthenticated } from "@/lib/auth";
import { listRetrospectives, getRetrospective } from "@/lib/api/retrospective";

export function requireAuth() {
  if (!isAuthenticated()) {
    return redirect("/login");
  }
  return null;
}

export async function retrospectivesListLoader() {
  if (!isAuthenticated()) {
    return redirect("/login");
  }
  const retrospectives = await listRetrospectives();
  return { retrospectives };
}

export async function retrospectiveDetailLoader({
  params,
}: LoaderFunctionArgs) {
  if (!isAuthenticated()) {
    return redirect("/login");
  }
  const id = Number(params.id);
  if (isNaN(id)) {
    throw new Response("Invalid retrospective ID", { status: 400 });
  }
  const retrospective = await getRetrospective(id);
  return { retrospective };
}
