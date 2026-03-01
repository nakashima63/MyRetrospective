import { useState, useCallback } from "react";
import { useLoaderData, useNavigate } from "react-router";
import { Button } from "@/components/ui/button";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { ArrowLeft, Pencil, Trash2 } from "lucide-react";
import { KptColumn } from "@/components/kpt-column";
import { deleteRetrospective } from "@/lib/api/retrospective";
import type {
  RetrospectiveDetail as RetrospectiveDetailType,
  KptItem,
  KptType,
} from "@/types/retrospective";

export default function RetrospectiveDetail() {
  const { retrospective } = useLoaderData<{
    retrospective: RetrospectiveDetailType;
  }>();
  const navigate = useNavigate();
  const [isDeleting, setIsDeleting] = useState(false);

  const itemsByType = useCallback(
    (type: KptType): KptItem[] =>
      retrospective.kptItems
        .filter((item) => item.type === type)
        .sort((a, b) => a.sortOrder - b.sortOrder),
    [retrospective.kptItems],
  );

  const handleDelete = async () => {
    setIsDeleting(true);
    try {
      await deleteRetrospective(retrospective.id);
      navigate("/");
    } catch {
      setIsDeleting(false);
    }
  };

  return (
    <div className="flex min-h-screen flex-col">
      <header className="border-b bg-white px-6 py-4">
        <div className="mx-auto flex max-w-7xl items-center gap-4">
          <Button variant="ghost" size="sm" onClick={() => navigate("/")}>
            <ArrowLeft className="mr-1 h-4 w-4" />
            戻る
          </Button>
          <div className="flex-1">
            <h1 className="text-xl font-bold">{retrospective.title}</h1>
            {retrospective.description && (
              <p className="text-sm text-muted-foreground">
                {retrospective.description}
              </p>
            )}
          </div>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() =>
                navigate(`/retrospectives/${retrospective.id}/edit`)
              }
            >
              <Pencil className="mr-1 h-4 w-4" />
              編集
            </Button>
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button variant="destructive" size="sm" disabled={isDeleting}>
                  <Trash2 className="mr-1 h-4 w-4" />
                  削除
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>振り返りを削除しますか？</AlertDialogTitle>
                  <AlertDialogDescription>
                    「{retrospective.title}
                    」を削除します。この操作は取り消せません。
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>キャンセル</AlertDialogCancel>
                  <AlertDialogAction
                    onClick={handleDelete}
                    disabled={isDeleting}
                  >
                    削除する
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </div>
        </div>
      </header>

      <main className="flex-1 p-6">
        <div className="mx-auto grid max-w-7xl grid-cols-1 items-start gap-4 md:grid-cols-3">
          <KptColumn type="KEEP" items={itemsByType("KEEP")} readOnly />
          <KptColumn type="PROBLEM" items={itemsByType("PROBLEM")} readOnly />
          <KptColumn type="TRY" items={itemsByType("TRY")} readOnly />
        </div>
      </main>
    </div>
  );
}
