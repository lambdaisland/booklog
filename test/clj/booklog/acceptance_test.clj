(ns booklog.acceptance-test
  (:require [clojure.test :refer :all]
            [booklog.test-helper :refer :all]
            [sparkledriver.core :as sd :refer [click! send-text!]]))
