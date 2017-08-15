(ns booklog.test-helper
  (:require [sparkledriver.core :as sd]
            [booklog.util :as util]
            [booklog.application :as app]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [lambdaisland.uri :as uri]
            [booklog.components.spicerack :as sc]))

(def test-http-port 59800)

(defn test-config []
  {:http-port test-http-port
   :db-path (util/temp-file-name "test" "db")})

(defn wrap-test-system
  "A fixture function which sets up the system before tests and tears it down afterwards."
  [tests]
  (let [config (test-config)
        system (-> config app/app-system component/start)]
    (tests)
    (component/stop system)
    (io/delete-file (:db-path config))))
