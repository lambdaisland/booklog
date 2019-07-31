(ns user
  (:require [booklog.application :as app]
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
(def run reloaded/go)
(def reset reloaded/reset)
(def reset-all reloaded/reset-all)

(defn dev-config []
  (assoc
   (app/app-config)
   :css-watch-paths ["resources/public/css"]
   :garden-watch-nss '[booklog.styles]))

(defn dev-system [config]
  (assoc
   (app/app-system config)
   :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
   :css-watcher     (fw-sys/css-watcher {:watch-paths (:css-watch-paths config)})
   :garden-watcher  (new-garden-watcher (:garden-watch-nss config))))

(ctnr/set-refresh-dirs "src" "dev")

(reloaded/set-init! #(do
                       (println (str "Visit the website at http://localhost:" (:http-port (app/app-config)) ))
                       (dev-system (dev-config))))

(defn browser-repl []
  (fw-sys/cljs-repl (:figwheel-system reloaded/system)))
