(ns booklog.acceptance-test
  (:require [clojure.test :refer :all]
            [booklog.test-helper :refer :all]
            [sparkledriver.core :as sd :refer
             [click! send-text!]]))

(use-fixtures :once wrap-test-system wrap-browser)
(use-fixtures :each wrap-clear-db wrap-clear-cookies)

(deftest sign-up-test
  (fetch! (app-url "/"))
  (click! (find-by-css "a.log-in"))
  (is (= (current-path) "/login"))

  (click! (find-by-xpath "//a[text()='Register instead']"))
  (is (= (current-path) "/register"))

  (send-text! (find-by-css "#username") "Arne")
  (send-text! (find-by-css "#password") "sekrit")
  (click! (find-by-css "input[type=submit]"))

  (is (re-find #"Thanks for registering! Please log in" (page-text))))

(deftest login-test
  (load-db! {"users" {"Arne" "bcrypt+sha512$e0cf0035cd4f616d180119d2c3cd4c12$12$a841ed436714247677f0342268ede7aa9e681b71d6060b46"}})

  (fetch! (app-url "/login"))
  (send-text! (find-by-css "#username") "Arne")
  (send-text! (find-by-css "#password") "sekrit")
  (click! (find-by-css "input[type=submit]"))

  (is (re-find #"Login successful, welcome back!" (page-text))))

(comment
  (run-tests))
