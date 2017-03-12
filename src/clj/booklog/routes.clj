(ns booklog.routes
  (:require [booklog.views :refer [layout login-form register-form]]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :refer [resources]]
            [ring.util.response :refer [response]]
            [buddy.auth.backends :as buddy-backends]
            [buddy.hashers :as hashers]))

(defn login-authenticate
  "Check request username and password against authdata
  username and passwords.
  On successful authentication, set appropriate user
  into the session and redirect to the value of
  (:next (:query-params request)). On failed
  authentication, renders the login page."
  [request]
  (let [username (get-in request [:form-params "username"])
        password (get-in request [:form-params "password"])
        session (:session request)
        found-password (get authdata (keyword username))]
    (if (and found-password (= found-password password))
      (let [next-url (get-in request [:query-params :next] "/")
            updated-session (assoc session :identity (keyword username))]
        (-> (redirect next-url)
            (assoc :session updated-session)))
      (let [content (slurp (io/resource "login.html"))]
        (render content request)))))

(defn ok-html [h]
  (-> (str (layout h))
      response
      (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))



#_
(with-open [db (open-database "./baking-db")]
  (let [ingredients (open-hashmap db "ingredient-hashmap")]
    (put! ingredients :apple-pie [:flour :butter :sugar :apples])
    ;;=> [:flour :butter :sugar :apples]
    (update! ingredients :apple-pie #(conj % :cinnamon))))
;;=> [:flour :butter :sugar :apples :cinnamon]

(defn app-routes [{:keys [spicerack] :as endpoint}]
  (routes

   (GET "/" _
     (ok-html (login-form)))

   (GET "/register" [username password]
     (let [users (open-hashmap (:db spicerack) "users")]
       (put! users username (hashers/derive password))
       (ok-html (register-form))))

   (POST "/login" {:keys [session]
                   {:keys [username password]} :params}

     (ok-html (layout ())))

   (resources "/")))

(buddy-backends/session)
