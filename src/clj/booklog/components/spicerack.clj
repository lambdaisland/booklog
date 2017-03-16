(ns booklog.components.spicerack
  (:require [com.stuartsierra.component :as component]
            [spicerack.core :as sr]))

(defn add-shutdown-hook [^Thread thread]
  (.addShutdownHook (Runtime/getRuntime) thread))

(defn remove-shutdown-hook [^Thread thread]
  (.removeShutdownHook (Runtime/getRuntime) thread))

(defrecord SpicerackComponent [path db hook]
  component/Lifecycle
  (start [this]
    (if (:db this)
      this ;; idempotent
      (let [^org.mapdb.DB db (sr/open-database path)
            hook (Thread. (fn [] (.close db)))]
        ;; prevent corruption when the JVM is killed
        ;; without first stopping the system
        (add-shutdown-hook hook)
        (assoc this :db db :hook hook))))

  (stop [{:keys [^org.mapdb.DB db ^Thread hook] :as this}]
    (when db
      (.close db))
    (when hook
      (remove-shutdown-hook hook))
    (dissoc this :db :hook)))

(defn new-spicerack [path]
  (map->SpicerackComponent {:path path}))
