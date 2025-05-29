import { createRouter,createWebHistory } from "vue-router";

import Home from "@/views/learningviews/HomeView.vue";
import ReadingView from "@/views/learningviews/ReadingView.vue";
import WritingView from "@/views/learningviews/WritingView.vue";
import CollectionView from "@/views/learningviews/CollectionView.vue";
import ChatView from "@/views/learningviews/ChatView.vue";
import LoginView from "@/views/login/LoginView.vue";
import RegisterView from "@/views/login/RegisterView.vue";
import IndexView from "@/views/IndexView.vue";
import ProfileView from "@/views/learningviews/ProfileView.vue";

import VocabularyView from "@/views/learningviews/collection/VocabularyVIew.vue";
import SentenceView from "@/views/learningviews/collection/SentenceView.vue";

const router=createRouter({
    history:createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path:"/login",
            name:"login",
            component:LoginView,
        },
        {
            path:"/register",
            name:"register",
            component:RegisterView,
        },
        {
          path: "/",
          name: "index",
          component: IndexView,
          redirect: "/home", // 默认重定向到 /home
          children: [
            {
                path:"home",
                name:"home",
                component:Home,
            },
            {
              path: "reading", // 实际路径：/reading
              name: "reading",
              component: ReadingView,
            },
            {
              path: "writing", // 实际路径：/writing
              name: "writing",
              component: WritingView,
            },
            {
              path: "collection", // 实际路径：/collection
              name: "collection",
              component: CollectionView,
            },
            {
              path: "chat", // 实际路径：/chat
              name: "chat",
              component: ChatView,
            },
            {
              path: "profile", // 实际路径：/profile
              name: "profile",
              component: ProfileView,
            },
            {
                path:"vocabulary",
                name:"vocabulary",
                component: VocabularyView,
              },
              {
                path:"sentence",
                name:"sentence",
                component: SentenceView,
              }
          ],
        },
        // {
        //   path: "/:pathMatch(.*)*",
        //   name: "home",
        //   component: Home,
        // },
]

})

export default router