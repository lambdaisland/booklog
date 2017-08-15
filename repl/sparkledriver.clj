(ns repl.sparkledriver
  (:require [sparkledriver.core :as sd]
            [clojure.java.browse :refer [browse-url]]))

(def browser (sd/make-browser))
(sd/fetch! browser "http://localhost:1234")

(sd/click! (sd/find-by-css browser "a.log-in"))
(sd/current-url browser)                       ;;=> "http://localhost:1234/login"

(sd/click! (sd/find-by-xpath browser "//a[text()='Log in']"))
(sd/current-url browser)                       ;;=> "http://localhost:1234/login"
