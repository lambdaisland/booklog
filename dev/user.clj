(ns user
  (:require [booklog.application]
            [com.stuartsierra.component :as component]
            [figwheel-sidecar.config :as fw-config]
            [figwheel-sidecar.system :as fw-sys]
            [clojure.tools.namespace.repl :as ctnr]
            [reloaded.repl :as reloaded]
            [ring.middleware.reload :refer [wrap-reload]]
            [figwheel-sidecar.repl-api :as figwheel]
            [garden-watcher.core :refer [new-garden-watcher]]))

(def init reloaded/init)
(def start reloaded/start)
(def stop reloaded/stop)
(def go reloaded/go)
(def reset reloaded/reset)
(def reset-all reloaded/reset-all)

(defn dev-system []
  (merge
   (booklog.application/app-system)
   (component/system-map
    :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
    :css-watcher (fw-sys/css-watcher {:watch-paths ["resources/public/css"]})
    :garden-watcher (new-garden-watcher ['booklog.styles]))))

(ctnr/set-refresh-dirs "src" "dev")
(reloaded/set-init! #(dev-system))

(defn browser-repl []
  (fw-sys/cljs-repl (:figwheel-system reloaded/system)))
