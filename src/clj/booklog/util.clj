(ns booklog.util
  (:require [clojure.java.io :as io]))

(defn temp-file-name [name ext]
  (str (io/file (System/getProperty "java.io.tmpdir")
                (str name (rand-int 99999) "." ext))))
