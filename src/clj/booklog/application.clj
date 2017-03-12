(ns booklog.application
  (:gen-class)
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [environ.core :refer [env]]
            [com.stuartsierra.component :as component]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.jetty :refer [new-web-server]]
            [booklog.routes :refer [routes]]))

(defn app-system []
  (component/system-map
   :routes (new-endpoint (fn [_] routes))
   :middleware (new-middleware  {:middleware [[wrap-defaults site-defaults]
                                              wrap-with-logger
                                              wrap-gzip]})
   :handler (component/using
             (new-handler)
             [:routes :middleware])
   :http (component/using
          (new-web-server (Integer. (or (env :port) 10555)))
          [:handler])))

(defn -main [& _]
  (component/start (app-system)))
