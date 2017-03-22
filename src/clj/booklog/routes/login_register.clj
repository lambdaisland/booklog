(ns booklog.routes.login-register
  (:require [booklog.auth.views :refer [login-view register-view]]
            [buddy.hashers :as hashers]
            [compojure.core :refer [GET POST routes]]
            [ring.util.response :refer [redirect]]
            [spicerack.core :as sr]))

(defn auth-routes [{:keys [spicerack] :as endpoint}]
  (let [users (sr/open-hashmap (:db spicerack) "users")]
    (routes
     (GET "/login" {flash :flash}
       #:render{:view login-view})

     (POST "/login" {:keys [session params]}
       (let [{:keys [username password]} params]
         (if (hashers/check password (get users username))
           (-> (redirect "/")
               (assoc :session (assoc session :identity username)
                      :flash {:layout/message "Login ok"}))
           #:render{:view login-view
                    :data {:layout/message "Login failed"
                           :user/username username
                           :user/password password}})))

     (GET "/register" _
       #:render{:view register-view})

     (POST "/register" [username password]
       (if (get users username)
         #:render {:view register-view
                   :data {:layout/message "Username is already taken"
                          :user/username username
                          :user/password password}}
         (do
           (sr/put! users username (hashers/derive password))
           (-> (redirect "/login")
               (assoc :flash {:layout/message "Thanks for registering, please log in"}))))))))
