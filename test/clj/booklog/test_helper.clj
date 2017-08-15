(ns booklog.test-helper
  (:require [booklog.application :as app]
            [booklog.util :as util]
            [booklog.components.spicerack :as sc]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [sparkledriver.core :as sd]
            [lambdaisland.uri :as uri]
            [clojure.test :as test]
            [spicerack.core :as sr])
  (:import java.util.regex.Pattern
           org.openqa.selenium.NoSuchElementException))

(def ^{:dynamic true
       :doc "The SparkleDriver browser instance. You can access this inside your tests."}
  *browser* nil)

(def ^{:dynamic true
       :doc "The test system"}
  *system* nil)

(def test-http-port 59800)

(defn test-config []
  (assoc
   (app/app-config)
   :http-port test-http-port
   :db-path  (util/temp-file-name "test" "db")))

(defn wrap-test-system [tests]
  (let [config (test-config)]
    (binding [*system* (-> config app/app-system component/start)]
      (tests)
      (component/stop *system*)
      (io/delete-file (:db-path config)))))

(defn wrap-browser [tests]
  (sd/with-browser [browser (sd/make-browser)]
    (binding [*browser* browser]
      (tests))))

(defn wrap-clear-cookies [test]
  (.deleteAllCookies (.manage *browser*))
  (test))

(defn app-url [path]
  (str "http://localhost:" test-http-port path))

(defn current-path []
  (:path (uri/uri (sd/current-url *browser*))))

(defn dump-db
  "Return all data in the database as one big hash-map."
  []
  (sc/dump-db (get-in *system* [:spicerack :db])))

(defn load-db!
  "Populate the database with data, as returned by `load-db!`"
  [data]
  (sc/load-db! (get-in *system* [:spicerack :db]) data))

(defn clear-db!
  "Delete all data in the database, making it empty again."
  [db]
  (sc/clear-db! db))

(defn wrap-clear-db
  "Test fixture which clears the database before each test."
  [test]
  (clear-db! (get-in *system* [:spicerack :db]))
  (test))

;; (defn find-by-class*
;;   ([arg] (sd/find-by-class* *browser* arg))
;;   ([browser-or-elem arg] (sd/find-by-class* browser-or-elem arg)))
;; (defn find-by-css
;;   ([arg] (sd/find-by-css *browser* arg))
;;   ([browser-or-elem arg] (sd/find-by-css browser-or-elem arg)))
;; (defn find-by-css*
;;   ([arg] (sd/find-by-css* *browser* arg))
;;   ([browser-or-elem arg] (sd/find-by-css* browser-or-elem arg)))
;; (defn find-by-class
;;   ([arg] (sd/find-by-class *browser* arg))
;;   ([browser-or-elem arg] (sd/find-by-class browser-or-elem arg)))
;; (defn find-by-id
;;   ([arg] (sd/find-by-id *browser* arg))
;;   ([browser-or-elem arg] (sd/find-by-id browser-or-elem arg)))
;; (defn find-by-xpath*
;;   ([arg] (sd/find-by-xpath* *browser* arg))
;;   ([browser-or-elem arg] (sd/find-by-xpath* browser-or-elem arg)))
;; (defn find-by-tag
;;   ([arg] (sd/find-by-tag *browser* arg))
;;   ([browser-or-elem arg] (sd/find-by-tag browser-or-elem arg)))
;; (defn find-by-xpath
;;   ([arg] (sd/find-by-xpath *browser* arg))
;;   ([browser-or-elem arg] (sd/find-by-xpath browser-or-elem arg)))
;; (defn find-by-tag*
;;   ([arg] (sd/find-by-tag* *browser* arg))
;;   ([browser-or-elem arg] (sd/find-by-tag* browser-or-elem arg)))

(defn page-source [& args] (apply sd/page-source *browser* args))
(defn current-url [& args] (apply sd/current-url *browser* args))
(defn all-windows [& args] (apply sd/all-windows *browser* args))
(defn maximize-window [& args] (apply sd/maximize-window *browser* args))
(defn available-log-types [& args] (apply sd/available-log-types *browser* args))
(defn current-window [& args] (apply sd/current-window *browser* args))
(defn close-browser! [& args] (apply sd/close-browser! *browser* args))
(defn logs [& args] (apply sd/logs *browser* args))
(defn execute-script [& args] (apply sd/execute-script *browser* args))
(defn fetch! [& args] (apply sd/fetch! *browser* args))
(defn status-code [& args] (apply sd/status-code *browser* args))
(defn page-wait [& args] (apply sd/page-wait *browser* args))
(defn media-dir [& args] (apply sd/media-dir *browser* args))
(defn switch-to-window [& args] (apply sd/switch-to-window *browser* args))
(defn attachments-dir [& args] (apply sd/attachments-dir *browser* args))
(defn execute-script-async [& args] (apply sd/execute-script-async *browser* args))
(defn switch-to-alert [& args] (apply sd/switch-to-alert *browser* args))
(defn browser-cookies->map [& args] (apply sd/browser-cookies->map *browser* args))
(defn cache-dir [& args] (apply sd/cache-dir *browser* args))

(defn page-text []
  (sd/text (find-by-css "html")))

(defn some-page-text [txt]
  (if (string? txt)
    (re-find (Pattern/compile txt Pattern/LITERAL) (page-text))
    (re-find txt (page-text))))

(defmethod test/assert-expr 'some-page-text [msg form]
  `(if ~form
     (test/do-report {:type :pass, :message ~msg,
                      :expected '~form, :actual '~form})
     (test/do-report {:type :fail, :message ~msg,
                      :expected '~form, :actual (page-text)})))

;; Copy over docstrings
(doseq [[n v] (ns-publics *ns*)]
  (if-let [orig (resolve (symbol (str "sd/" n)))]
    (alter-meta! v (partial merge (meta orig)))))

(defn find-with-timeout [{:keys [finder browser selector timeout-millis] :or {timeout-millis 5000}}]
  (let [start-time (System/currentTimeMillis)
        timeout?   #(> (- (System/currentTimeMillis) start-time) timeout-millis)]
    (loop []
      (let [element (try
                      (finder browser selector)
                      (catch NoSuchElementException e
                        (if (timeout?)
                          (let [expected (list (-> finder meta :name) selector)
                                actual   (list 'not expected)
                                message  (str "No such element: " selector)]
                            (test/report {:type :error :message message :expected expected :actual actual})
                            :timeout)
                          :not-found)))]
        (if (= element :not-found)
          (recur)
          element)))))

(defn find-by-css [selector]
  (find-with-timeout {:finder #'sd/find-by-css :browser *browser* :selector selector}))

(defn find-by-css* [selector]
  (find-with-timeout {:finder #'sd/find-by-css* :browser *browser* :selector selector}))

(defn find-by-xpath [selector]
  (find-with-timeout {:finder #'sd/find-by-xpath :browser *browser* :selector selector}))

(defn find-by-xpath* [selector]
  (find-with-timeout {:finder #'sd/find-by-xpath :browser *browser* :selector selector}))
