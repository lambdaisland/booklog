(ns booklog.auth.routes
  (:require [booklog.auth.views :refer [login-view register-view]]
            [booklog.render :refer [render flash-message]]
            [buddy.hashers :as hashers]
            [compojure.core :refer [GET POST routes]]
            [ring.util.response :refer [redirect]]
            [spicerack.core :as sr]
            [clojure.string :as str]))

(defn auth-routes [endpoint]
  (routes

   (GET "/login" _
     ,,,)

   (POST "/login" _
     ,,,)

   (GET "/register" _
     ,,,)

   (POST "/register" _
     ,,,)

   ))
