(ns booklog.example-test
  (:require [booklog.test-helper :refer [wrap-test-system
                                         test-http-port
                                         *browser*]]
            [clojure.test :refer :all]
            [sparkledriver.core :as sd]))

(use-fixtures :once wrap-test-system)

(def home-page (str "http://localhost:" test-http-port))

(deftest example-passing-test
  (-> (sd/fetch! *browser* home-page)
      sd/screenshot
      prn))

(comment
  (run-tests))
