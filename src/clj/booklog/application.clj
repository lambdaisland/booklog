(ns booklog.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [booklog.components.spicerack :refer [new-spicerack]]
            [booklog.middleware.render :refer [wrap-render-views]]
            [booklog.routes :refer [app-routes]]
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

#_(defn oauth-config []
    {:google
     {:authorize-uri    "https://accounts.google.com/o/oauth2/v2/auth"
      :access-token-uri "https://www.googleapis.com/oauth2/v4/token"
      :client-id        <CLIENT-ID>
      :client-secret    <CLIENT-SECRET>
      :scopes           ["..."]
      :launch-uri       "..."
      :redirect-uri     "..."
      :landing-uri      "..."}})

(defn app-system [config]
  (component/system-map
   :spicerack (new-spicerack (:db-path config))
   :routes (-> (new-endpoint #(fn [req] ((app-routes %) req)))
               (component/using [:spicerack]))
   :middleware (new-middleware {:middleware [wrap-render-views
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
