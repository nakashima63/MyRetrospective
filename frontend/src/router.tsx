import { createBrowserRouter } from "react-router";
import Root from "@/routes/root";
import Home from "@/routes/home";
import Login from "@/routes/login";
import Signup from "@/routes/signup";
import { requireAuth } from "@/lib/loaders";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: Root,
    children: [
      {
        index: true,
        loader: requireAuth,
        Component: Home,
      },
    ],
  },
  {
    path: "/login",
    Component: Login,
  },
  {
    path: "/signup",
    Component: Signup,
  },
]);
