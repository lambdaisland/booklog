(ns booklog.test-helper
  (:require [booklog.application :as app]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [sparkledriver.core :as sd]))

(def ^{:dynamic true
       :doc "The SparkleDriver browser instance. You can access this inside your tests."}
  *browser* nil)

(def test-http-port 59800)

(defn temp-file-name [name ext]
  (str (io/file (System/getProperty "java.io.tmpdir")
                (str name (rand-int 99999) "." ext))))

(defn test-system []
  (-> (app/app-system)
      (assoc-in [:http :options :port] test-http-port)
      (assoc-in [:spicerack :path] (temp-file-name "test" "db"))))

(defn wrap-test-system [tests]
  (let [sys (component/start (test-system))]
    (sd/with-browser [browser (sd/make-browser)]
      (binding [*browser* browser]
        (tests)))
    (component/stop sys)))
