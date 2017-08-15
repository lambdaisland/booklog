(ns booklog.acceptance-test
  (:require [clojure.test :refer :all]
            [booklog.test-helper :refer :all]
            [sparkledriver.core :as sd :refer [click! send-text!]]))

(use-fixtures :once wrap-test-system wrap-browser)
(use-fixtures :each wrap-clear-db wrap-clear-cookies)

(deftest sign-up-test
  (fetch! (app-url "/"))
  (click! (find-by-css "a.log-in"))
  (is (= (current-path) "/login"))

  (click! (find-by-xpath "//a[text()='Register instead']"))
  (is (= (current-path) "/register"))

  (send-text! (find-by-css "form #username") "Arne")
  (send-text! (find-by-css "form #password") "sekrit")
  (click! (find-by-css "input[type=submit]"))

  (is (re-find #"Thanks for registering! Please log in" (page-text))))

(deftest login-test
  (load-db! {"users"
             {"Arne"
              "bcrypt+sha512$445d2014419411cd0cb8f86ecf7b9f46$12$a47115cbae94fc575ebfac0762ee2cf338876c81a8fb6af1"}})

  (fetch! (app-url "/"))
  (click! (find-by-css "a.log-in"))
  (is (= (current-path) "/login"))

  (send-text! (find-by-css "form #username") "Arne")
  (send-text! (find-by-css "form #password") "sekrit")
  (click! (find-by-css "input[type=submit]"))

  (is (re-find #"Login successful, welcome back!" (page-text)))
  (is (= (current-path) "/")))

(run-tests)
