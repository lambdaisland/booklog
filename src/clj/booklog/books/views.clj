(ns booklog.books.views
  (:require [booklog.components :refer [form form-field form-button]]
            [booklog.layout :refer [layout]]))

(defn book-form [{:book/keys [id author title]}]
  (form {:method "POST" :action (if id (str "/books/" id) "/books")}
    [:div
     [:div.dt.w-100.bsp1
      (form-field "author" "Author" {:value author})
      (form-field "title" "Title" {:value title})
      (form-button "Submit")]]))

(defn new-book-view [data]
  [:div
   [:h1 "Add new book"]
   (book-form data)])

(defn book-view [{:book/keys [user created-at title author]}]
  [:div
   [:h1 "“" [:span title] "”" " by " [:span.i author]]
   [:div "Read on " created-at " by " user]
   ])
