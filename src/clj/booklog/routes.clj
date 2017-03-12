(ns booklog.routes
  (:require [booklog.views :refer [layout login-form register-form]]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]
            [buddy.auth.backends :as buddy-backends]
            [buddy.hashers :as hashers]))

#_
(def buddy-backend
  (buddy-backends/session))

(defn ok-html [h]
  (-> (str (layout h))
      response
      (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

(defn app-routes [{:keys [spicerack] :as endpoint}]
  (routes

   (GET "/" _
     (ok-html (login-form)))

   (GET "/register" _
     (ok-html (register-form)))

   (POST "/login" {:keys [session]
                   {:keys [username password]} :params}

     (ok-html (layout ())))

   (POST "/register" [username password]
     #_(let [users (open-hashmap (:db spicerack) "usaers")]
         (put! users username (hashers/derive password))))

   (resources "/")))
