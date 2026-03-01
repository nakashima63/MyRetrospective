import { createBrowserRouter } from "react-router";
import Root from "@/routes/root";
import Home from "@/routes/home";
import Login from "@/routes/login";
import Signup from "@/routes/signup";
import NewRetrospective from "@/routes/retrospectives/new";
import RetrospectiveDetail from "@/routes/retrospectives/detail";
import EditRetrospective from "@/routes/retrospectives/edit";
import {
  requireAuth,
  retrospectivesListLoader,
  retrospectiveDetailLoader,
} from "@/lib/loaders";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: Root,
    children: [
      {
        index: true,
        loader: retrospectivesListLoader,
        Component: Home,
      },
      {
        path: "retrospectives/new",
        loader: requireAuth,
        Component: NewRetrospective,
      },
      {
        path: "retrospectives/:id",
        loader: retrospectiveDetailLoader,
        Component: RetrospectiveDetail,
      },
      {
        path: "retrospectives/:id/edit",
        loader: retrospectiveDetailLoader,
        Component: EditRetrospective,
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
