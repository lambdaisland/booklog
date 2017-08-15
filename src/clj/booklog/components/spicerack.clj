(ns booklog.components.spicerack
  (:require [com.stuartsierra.component :as component]
            [medley.core :refer [mapply]]
            [spicerack.core :as sr]))

(defn add-shutdown-hook [^Thread thread]
  (.addShutdownHook (Runtime/getRuntime) thread))

(defn remove-shutdown-hook [^Thread thread]
  (.removeShutdownHook (Runtime/getRuntime) thread))

(defrecord SpicerackComponent [path db hook db-opts]
  component/Lifecycle
  (start [this]
    (if (:db this)
      this ;; idempotent
      (let [^org.mapdb.DB db (mapply sr/open-database path db-opts)
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

(defn new-spicerack [path & {:as db-opts}]
  (map->SpicerackComponent {:path path :db-opts db-opts}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helpers

(defn dump-db
  "Return all data in the database as one big hash-map."
  [db]
  (into {} (map (juxt identity (partial sr/open-hashmap db)) (.getAllNames db))))

(defn load-db!
  "Populate the database with data, as returned by `load-db!`"
  [db data]
  (doseq [[name values] data
          :let [hmap (sr/open-hashmap db name)]]
    (doseq [[k v] values]
      (sr/put! hmap k v))))

(defn clear-db!
  "Delete all data in the database, making it empty again."
  [db]
  (doseq [n (.getAllNames db)
          :let [hm (sr/open-hashmap db n)]]
    (run! (partial sr/remove! hm) (keys hm))))
