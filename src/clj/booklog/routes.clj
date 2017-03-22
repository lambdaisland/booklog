(ns booklog.routes
  (:require [booklog.auth.routes :refer [auth-routes]]
            [booklog.auth.views :refer [home-view]]
            [compojure.core :refer [GET routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [redirect response]]))

(defn app-routes [endpoint]
  (routes

   (GET "/" {:keys [identity session]}
     (if identity
       #:render {:view home-view
                 :data {:user/username identity}}
       (redirect "/login")))

   (auth-routes endpoint)
   (resources "/")))
