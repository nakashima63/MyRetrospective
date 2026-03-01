import { useLoaderData, useNavigate } from "react-router";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card";
import { Plus } from "lucide-react";
import type { Retrospective } from "@/types/retrospective";

export default function Home() {
  const { retrospectives } = useLoaderData<{
    retrospectives: Retrospective[];
  }>();
  const navigate = useNavigate();

  return (
    <div className="mx-auto max-w-4xl p-6">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">振り返り一覧</h1>
          <p className="text-sm text-muted-foreground">
            KPT 振り返りを管理します
          </p>
        </div>
        <Button onClick={() => navigate("/retrospectives/new")}>
          <Plus className="mr-1 h-4 w-4" />
          新規作成
        </Button>
      </div>

      {retrospectives.length === 0 ? (
        <div className="flex flex-col items-center justify-center rounded-lg border border-dashed p-12 text-center">
          <p className="mb-4 text-muted-foreground">まだ振り返りがありません</p>
          <Button onClick={() => navigate("/retrospectives/new")}>
            <Plus className="mr-1 h-4 w-4" />
            最初の振り返りを作成
          </Button>
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {retrospectives.map((retro) => (
            <Card
              key={retro.id}
              className="cursor-pointer transition-shadow hover:shadow-md"
              onClick={() => navigate(`/retrospectives/${retro.id}/edit`)}
            >
              <CardHeader>
                <CardTitle className="text-lg">{retro.title}</CardTitle>
                {retro.description && (
                  <CardDescription>{retro.description}</CardDescription>
                )}
                <p className="text-xs text-muted-foreground">
                  {new Date(retro.createdAt).toLocaleDateString("ja-JP")}
                </p>
              </CardHeader>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
