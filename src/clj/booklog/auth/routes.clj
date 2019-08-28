(ns booklog.auth.routes
  (:require [booklog.auth.views :refer [login-view register-view]]
            [booklog.render :refer [render flash-message]]
            [buddy.hashers :as hashers]
            [compojure.core :refer [GET POST routes]]
            [ring.util.response :refer [redirect]]
            [spicerack.core :as sr]
            [clojure.string :as str]))


(defn auth-routes [{:keys [spicerack] :as endpoint}]
  (let [users (sr/open-hashmap (:db spicerack) "users")]
    (routes

     (GET "/login" _
       (render :view login-view
               :data {:layout/title "Login"}))

     (POST "/login" [username password :as req]
       (if (hashers/check password (get users username))
         (let [next-session (-> (assoc (:session req) :identity username)
                                (with-meta {:recreate true}))]
           (-> (redirect "/")
               (assoc :session next-session)
               (flash-message "Login successful, welcome back!")))
         (render :view login-view
                 :data {:layout/title "Login"
                        :layout/message "Login failed, please check your password."
                        :user/username username})))

     (GET "/register" _
       (render :view register-view
               :data {:layout/title "Register"}))

     (POST "/register" [username password]
       (if (get users username)
         (render :view register-view
                 :data {:layout/title "Register"
                        :layout/message "Username is already taken"
                        :user/username username})
         (do
           (sr/put! users username (hashers/derive password))
           (-> (redirect "/login")
               (flash-message "Thanks for registering! Please log in.")))))

     (GET "/logout" _
       (-> (redirect "/")
           (assoc :session nil)
           (flash-message "You have been logged out"))))))
