import { createBrowserRouter } from "react-router";
import Root from "@/routes/root";
import Home from "@/routes/home";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: Root,
    children: [
      {
        index: true,
        Component: Home,
      },
    ],
  },
]);
