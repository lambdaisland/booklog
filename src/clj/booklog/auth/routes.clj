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

     (POST "/login" {{username :username password :password} :params
                     session :session }
       (if (hashers/check password (get users username))
         (let [next-session (assoc session :identity username)]
           (-> (redirect "/")
               (assoc :session next-session)
               (flash-message "Login successful, welcome back!")))
         (render :view login-view
                 :data {:layout/title "Login"
                        :layout/message "Login failed"
                        :user/username username
                        :user/password password})))

     (GET "/register" _
       (render :view register-view
               :data {:layout/title "Register"}))

     (POST "/register" [username password]
       (if (get users username)
         (render :view register-view
                 :data {:layout/title "Register"
                        :layout/message "Username is already taken"
                        :user/username username
                        :user/password password})
         (do
           (sr/put! users username (hashers/derive password))
           (-> (redirect "/login")
               (flash-message "Thanks for registering, please log in")))))

     (GET "/logout" {session :session}
       (let [next-session (assoc session :identity nil)]
         (-> (redirect "/")
             (assoc :session next-session)
             (assoc-in [:flash :layout/message]
                       "You have been logged out")))))))

(hashers/derive "trustno1")
;;=> "bcrypt+sha512$02f85c2ded57bb4d46b6547a268f2693$12$9a52bb16806562b9b0450659331c7da80731e1441f218451"

;;=>
["bcrypt+sha512"                                    ; encryption
 "5f8dc902b11de2aa17e67ecb0f76713f"                 ; salt
 "12"                                               ; iterations
 "622891a3aed90cea118b3ed791b96e27818389bddf9e5a9b" ; hashed password
 ]
