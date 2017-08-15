(ns booklog.application
  (:gen-class)
  (:require [booklog.components.spicerack :refer [new-spicerack]]
            [booklog.middleware.render :refer [wrap-render-views]]
            [booklog.routes :refer [app-routes]]
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
            [system.components.middleware :refer [new-middleware]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.auth.backends :as backends]))

(defn app-config []
  {:http-port 1234
   :db-path "./booklog.db"})

(defn app-system [config]
  (component/system-map
   :spicerack (new-spicerack (:db-path config))
   :routes (-> (new-endpoint #(fn [req] ((app-routes %) req)))
               (component/using [:spicerack]))
   :middleware (new-middleware  {:middleware [wrap-render-views
                                              [wrap-authentication (backends/session)]
                                              [wrap-defaults site-defaults]
                                              wrap-with-logger
                                              wrap-gzip
                                              prone/wrap-exceptions]})
   :handler (component/using
             (new-handler)
             [:routes :middleware])
   :http (component/using
          (new-web-server (Integer. (or (env :port) (:http-port config))))
          [:handler])))

(defn -main [& _]
  (component/start (app-system (app-config))))
