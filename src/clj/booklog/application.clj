(ns booklog.application
  (:gen-class)
  (:require [booklog.components.spicerack :refer [new-spicerack]]
            [booklog.middleware.render :refer [wrap-render-views]]
            [booklog.routes :refer [app-routes]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [buddy.auth.backends :as buddy-backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [prone.middleware :as prone]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [ring.util.response :refer [redirect]]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.jetty :refer [new-web-server]]
            [system.components.middleware :refer [new-middleware]]))

(def auth-rules {:rules [{:pattern #"^/books/new"
                          :handler :identity
                          :error/message "Only registered users can add books"}
                         ]
                 :on-error (fn [req]
                             (-> (redirect "/login")
                                 (assoc :flash {:layout/message (:error/message req)})))})

(defn app-system []
  (component/system-map
   :spicerack (new-spicerack "./booklog.db")
   :routes (-> (new-endpoint #(fn [req] ((app-routes %) req)))
               (component/using [:spicerack]))
   :middleware (new-middleware  {:middleware [wrap-render-views
                                              [wrap-access-rules auth-rules]
                                              [wrap-authentication (buddy-backends/session)]
                                              [wrap-defaults site-defaults]
                                              wrap-with-logger
                                              wrap-gzip
                                              prone/wrap-exceptions]})
   :handler (component/using
             (new-handler)
             [:routes :middleware])
   :http (component/using
          (new-web-server (Integer. (or (env :port) 1234)))
          [:handler])))

(defn -main [& _]
  (component/start (app-system)))
