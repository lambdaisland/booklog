(ns booklog.test-helper
  (:require [booklog.application :as app]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [sparkledriver.core :as sd]
            [lambdaisland.uri :as uri]
            [clojure.test :as test]
            [spicerack.core :as sr])
  (:import java.util.regex.Pattern))

(def ^{:dynamic true
       :doc "The SparkleDriver browser instance. You can access this inside your tests."}
  *browser* nil)

(def ^{:dynamic true
       :doc "The test system"}
  *system* nil)

(def test-http-port 59800)

(defn temp-file-name [name ext]
  (str (io/file (System/getProperty "java.io.tmpdir")
                (str name (rand-int 99999) "." ext))))

(defn test-system []
  (-> (app/app-system)
      (assoc-in [:http :options :port] test-http-port)
      (assoc-in [:spicerack :path] (temp-file-name "test" "db"))
      (assoc-in [:spicerack :db-opts] {:transaction-enable? true})))

(defn wrap-test-system [tests]
  (sd/with-browser [browser (sd/make-browser)]
    (binding [*browser* browser
              *system* (component/start (test-system))]
      (tests)
      (component/stop *system*))))

(defn clear-db! [db]
  (doseq [n (.getAllNames db)
          :let [hm (sr/open-hashmap db n)]]
    (run! (partial sr/remove! hm) (keys hm))))

(defn dump-db []
  (let [db (get-in *system* [:spicerack :db])]
    (into {} (map (juxt identity (partial sr/open-hashmap db)) (.getAllNames db)))))

(defn load-db! [data]
  (let [db (get-in *system* [:spicerack :db])]
    (doseq [[name values] data
            :let [hmap (sr/open-hashmap db name)]]
      (doseq [[k v] values]
        (sr/put! hmap k v)))))

(defn wrap-clear-db [tests]
  (clear-db! (get-in *system* [:spicerack :db]))
  (tests))

(defn find-by-class*
  ([arg] (sd/find-by-class* *browser* arg))
  ([browser-or-elem arg] (sd/find-by-class* browser-or-elem arg)))
(defn find-by-css
  ([arg] (sd/find-by-css *browser* arg))
  ([browser-or-elem arg] (sd/find-by-css browser-or-elem arg)))
(defn find-by-css*
  ([arg] (sd/find-by-css* *browser* arg))
  ([browser-or-elem arg] (sd/find-by-css* browser-or-elem arg)))
(defn find-by-class
  ([arg] (sd/find-by-class *browser* arg))
  ([browser-or-elem arg] (sd/find-by-class browser-or-elem arg)))
(defn find-by-id
  ([arg] (sd/find-by-id *browser* arg))
  ([browser-or-elem arg] (sd/find-by-id browser-or-elem arg)))
(defn find-by-xpath*
  ([arg] (sd/find-by-xpath* *browser* arg))
  ([browser-or-elem arg] (sd/find-by-xpath* browser-or-elem arg)))
(defn find-by-tag
  ([arg] (sd/find-by-tag *browser* arg))
  ([browser-or-elem arg] (sd/find-by-tag browser-or-elem arg)))
(defn find-by-xpath
  ([arg] (sd/find-by-xpath *browser* arg))
  ([browser-or-elem arg] (sd/find-by-xpath browser-or-elem arg)))
(defn find-by-tag*
  ([arg] (sd/find-by-tag* *browser* arg))
  ([browser-or-elem arg] (sd/find-by-tag* browser-or-elem arg)))

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

(defn current-path []
  (:path (uri/uri (current-url))))

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
