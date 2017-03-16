(ns booklog.routes
  (:require [booklog.views :refer [home-view layout login-view register-view]]
            [buddy.hashers :as hashers]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [redirect response]]
            [spicerack.core :as sr]))

(defn ok-html [h]
  (-> (str h)
      response
      (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))

(defn app-routes [{:keys [spicerack] :as endpoint}]
  (let [users (sr/open-hashmap (:db spicerack) "users")]
    (routes

     (GET "/" {:keys [flash identity session]}
       (if identity
         (ok-html (home-view (merge {:user/username identity} flash)))
         (redirect "/login")))

     (GET "/login" {flash :flash}
       (ok-html (login-view (or flash {}))))

     (POST "/login" {:keys [session params]}
       (let [{:keys [username password]} params]
         (if (hashers/check password (get users username))
           (-> (redirect "/")
               (assoc :session (assoc session :identity username))
               (assoc :flash {:layout/message "Login ok"}))
           (ok-html (login-view {:layout/message "Login failed"
                                 :user/username username
                                 :user/password password})))))

     (GET "/register" _
       (ok-html (register-view {})))

     (POST "/register" [username password]
       (if (get users username)
         (ok-html (register-view {:layout/message "Username is already taken"
                                  :user/username username
                                  :user/password password}))
         (do
           (sr/put! users username (hashers/derive password))
           (-> (redirect "/login")
               (assoc :flash {:layout/message "Thanks for registering, please log in"})))))

     (GET "/users" _
       (ok-html (layout {} [:ul (for [[username hash] users]
                                  [:li username " -> " hash])])))

     (resources "/"))))
