(ns booklog.routes
  (:require [booklog.auth.routes :refer [auth-routes]]
            [booklog.books.routes :refer [books-routes]]
            [booklog.timeline.routes :refer [timeline-routes]]
            [compojure.core :refer [routes]]
            [compojure.route :refer [not-found resources]]))

(defn app-routes [endpoint]
  (routes
   (timeline-routes endpoint)
   (books-routes endpoint)
   (auth-routes endpoint)
   (resources "/")
   (not-found "URL not found")))
