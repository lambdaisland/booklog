(ns booklog.books.routes
  (:require [compojure.core :refer [DELETE GET POST PUT routes]]
            [spicerack.core :as sr]))

(defn books-routes [{:keys [spicerack] :as endpoint}]
  (let [books (sr/open-hashmap (:db spicerack) "books")]
    (routes
     ;; index
     (GET "/books" _)

     ;; create
     (POST "/books" _)

     ;; new
     (GET "/books/new" _)

     ;; edit
     (GET "/books/:id/edit" _)

     ;; show
     (GET "/books/:id" _)

     ;; update
     (PUT "/books/:id" _)

     ;; delete
     (DELETE "/books/:id" _)
     )))
