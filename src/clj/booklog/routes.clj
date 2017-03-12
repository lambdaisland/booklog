(ns booklog.routes
  (:require [booklog.views :refer [layout login-form register-form]]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]
            [buddy.auth.backends :as buddy-backends]))

(defn ok-html [h]
  (-> (str (layout h))
      response
      (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

(defn app-routes [endpoint]
  (routes

   (GET "/" _
     (ok-html (login-form)))

   (GET "/register" _
     (ok-html (register-form)))

   (POST "/login" _
     (ok-html (layout ())))

   (resources "/")))
