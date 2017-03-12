(ns user
  (:require [booklog.application]
            [com.stuartsierra.component :as component]
            [figwheel-sidecar.config :as fw-config]
            [figwheel-sidecar.system :as fw-sys]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]
            [reloaded.repl :refer [system init start stop go reset reset-all]]
            [ring.middleware.reload :refer [wrap-reload]]
            [figwheel-sidecar.repl-api :as figwheel]
            [garden-watcher.core :refer [new-garden-watcher]]))

(defn dev-system []
  (merge
   (booklog.application/app-system)
   (component/system-map
    :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
    :css-watcher (fw-sys/css-watcher {:watch-paths ["resources/public/css"]})
    :garden-watcher (new-garden-watcher ['booklog.styles]))))

(set-refresh-dirs "src" "dev")
(reloaded.repl/set-init! #(dev-system))

(defn run []
  (go))

(defn browser-repl []
  (fw-sys/cljs-repl (:figwheel-system system)))
