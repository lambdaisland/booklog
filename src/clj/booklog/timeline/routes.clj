(ns booklog.timeline.routes
  (:require [booklog.timeline.views :refer [timeline-view]]
            [compojure.core :refer [GET routes]]
            [spicerack.core :as sr]))

(defn render-timeline [books]
  #:render {:view timeline-view
            :data {:timeline/books books}})

(defn timeline-routes [{:keys [spicerack] :as endpoint}]
  (let [books (sr/open-hashmap (:db spicerack) "books")]
    (routes
     (GET "/" _
       (render-timeline (vals books)))

     (GET "/users/:user/books" [user]
       (render-timeline (filter #(= (:user/identity %) user) (vals books)))))))
