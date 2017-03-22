(ns booklog.auth.routes
  (:require [booklog.auth.views :refer [login-view register-view]]
            [buddy.hashers :as hashers]
            [compojure.core :refer [GET POST routes]]
            [medley.core :refer [dissoc-in]]
            [ring.util.response :refer [redirect]]
            [spicerack.core :as sr]))

(defn auth-routes [{:keys [spicerack] :as endpoint}]
  (let [users (sr/open-hashmap (:db spicerack) "users")]
    (routes
     (GET "/login" _
       #:render{:view login-view
                :data {:layout/title "Login"}})

     (POST "/login" {:keys [session params]}
       (let [{:keys [username password]} params]
         (if (hashers/check password (get users username))
           (-> (redirect "/")
               (assoc :session (assoc session :identity username))
               (assoc :flash {:layout/message "Login ok"}))
           #:render{:view login-view
                    :data {:layout/title "Login"
                           :layout/message "Login failed"
                           :user/username username
                           :user/password password}})))

     (GET "/register" _
       #:render{:view register-view
                :data {:layout/title "Register"}})

     (POST "/register" [username password]
       (if (get users username)
         #:render{:view register-view
                  :data {:layout/title "Register"
                         :layout/message "Username is already taken"
                         :user/username username
                         :user/password password}}
         (do
           (sr/put! users username (hashers/derive password))
           (-> (redirect "/login")
               (assoc-in [:flash :layout/message]
                         "Thanks for registering, please log in")))))

     (GET "/logout" {session :session}
       (-> (redirect "/")
           (assoc-in [:session :identity] nil)
           (assoc-in [:flash :layout/message]
                     "You have been logged out"))))))
