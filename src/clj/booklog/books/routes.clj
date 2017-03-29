(ns booklog.books.routes
  (:require [booklog.books.views :refer [book-view new-book-view]]
            [clj-time.core :refer [now]]
            [compojure.core :refer [context DELETE GET POST PUT routes]]
            [medley.core :refer [random-uuid]]
            [ring.util.response :refer [redirect]]
            [spicerack.core :as sr]))

(defn save-book [books user author title]
  (let [id (str (random-uuid))]
    (sr/put! books id
             {:book/id id
              :book/author author
              :book/title title
              :book/created-at (now)
              :user/identity user})
    id))

(defn show [books id]
  #:render{:view book-view
           :data (get books id)})

(defn create [books author title identity]
  (let [id (save-book books identity author title)]
    (redirect (str "/books/" id))))

(defn new* []
  #:render{:view new-book-view
           :data {:layout/title "Add new book"}})

(defn books-routes [{:keys [spicerack] :as endpoint}]
  (let [books (sr/open-hashmap (:db spicerack) "books")]
    (context "/books" []
      (routes
       ;; index
       (GET "/" _)

       ;; create
       (POST "/" [author title :as req]
         (create books author title (:identity req)))

       ;; new
       (GET "/new" _
         (new*))

       ;; edit
       (context "/:id" [id]
         (GET "/edit" _)

         ;; show
         (GET "/" _
           (show books id))

         ;; update
         (PUT "/" _)

         ;; delete
         (DELETE "/" _))
       ))))
