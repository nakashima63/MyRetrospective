import { useState } from "react";
import { useNavigate } from "react-router";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { createRetrospective } from "@/lib/api/retrospective";

export default function NewRetrospective() {
  const navigate = useNavigate();
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    setIsSubmitting(true);
    setError("");
    try {
      const retrospective = await createRetrospective({
        title: title.trim(),
        description: description.trim() || undefined,
      });
      navigate(`/retrospectives/${retrospective.id}/edit`);
    } catch {
      setError("振り返りの作成に失敗しました。");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="mx-auto max-w-2xl p-6">
      <h1 className="mb-6 text-2xl font-bold">新しい振り返りを作成</h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="title">タイトル *</Label>
          <Input
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="例: スプリント #1 振り返り"
            required
            maxLength={255}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="description">説明</Label>
          <Textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="振り返りの目的やスコープを記入（任意）"
            rows={3}
          />
        </div>

        {error && <p className="text-sm text-destructive">{error}</p>}

        <div className="flex gap-2">
          <Button type="submit" disabled={!title.trim() || isSubmitting}>
            {isSubmitting ? "作成中..." : "作成して編集開始"}
          </Button>
          <Button type="button" variant="ghost" onClick={() => navigate("/")}>
            キャンセル
          </Button>
        </div>
      </form>
    </div>
  );
}
