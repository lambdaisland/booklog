(ns booklog.test-helper
  (:require [sparkledriver.core :as sd]
            [booklog.util :as util]
            [booklog.application :as app]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [lambdaisland.uri :as uri]
            [booklog.components.spicerack :as sc]))

(def test-http-port 59800)

(def ^:dynamic *browser* nil)

(def ^:dynamic *system* nil)

(defn test-config []
  {:http-port test-http-port
   :db-path (util/temp-file-name "test" "db")})

(defn wrap-test-system
  "A fixture function which sets up the system before tests and tears it down afterwards."
  [tests]
  (let [config (test-config)
        system (-> config app/app-system component/start)]
    (binding [*system* system]
      (tests))
    (component/stop system)
    (io/delete-file (:db-path config))))

(defn wrap-clear-db
  "Test fixture which clears the database before each test."
  [test]
  (sc/clear-db! (:spicerack *system*))
  (test))

(defn wrap-browser
  "A fixture function which binds *browser* to a new browser instance."
  [tests]
  (sd/with-browser [browser (sd/make-browser)]
    (binding [*browser* browser]
      (tests))))

(defn wrap-clear-cookies [test]
  (sd/delete-all-cookies! *browser*)
  (test))

(defn fetch!
  ([url]
   (sd/fetch! *browser* url))
  ([browser url]
   (sd/fetch! browser url)))

(defn find-by-css
  ([css]
   (sd/find-by-css *browser* css))
  ([browser css]
   (sd/find-by-css browser css)))

(defn find-by-xpath
  ([xpath]
   (sd/find-by-xpath *browser* xpath))
  ([browser xpath]
   (sd/find-by-xpath browser xpath)))

(defn page-text
  ([]
   (sd/page-text *browser*))
  ([browser]
   (sd/page-text)))

(defn app-url [path]
  (assoc (uri/uri "http://localhost")
         :path path
         :port test-http-port))

(defn current-path []
  (:path (uri/uri (sd/current-url *browser*))))

(defn dump-db []
  (sc/dump-db (:spicerack *system*)))

(defn load-db! [data]
  (sc/load-db! (:spicerack *system*) data))
