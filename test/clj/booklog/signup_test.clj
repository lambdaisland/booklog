(ns booklog.signup-test
  (:require [clojure.test :refer :all]
            [booklog.test-helper :refer :all]
            [sparkledriver.core :as sd :refer [click! send-text!]]))

(use-fixtures :once (fn [tests] (wrap-browser #(wrap-test-system tests))))
(use-fixtures :each wrap-clear-db)

(defn login! [user password]
  (fetch! (app-url "/login"))
  (send-text! (find-by-css "form #username") user)
  (send-text! (find-by-css "form #password") password)
  (click! (find-by-css "input[type=submit]")))

(deftest sign-up-test
  (fetch! (app-url "/"))
  (is (= (current-path) "/"))

  (click! (find-by-xpath "//a[text()='Log in']"))
  (is (= (current-path) "/login"))

  (click! (find-by-xpath "//a[text()='Register instead']"))
  (is (= (current-path) "/register"))

  (send-text! (find-by-css "form #username") "Arne")
  (send-text! (find-by-css "form #password") "sekrit")
  (click! (find-by-css "input[type=submit]"))

  (is (= (current-path) "/login"))
  (is (some-page-text "Thanks for registering! Please log in."))

  (send-text! (find-by-css "form #username") "Arne")
  (send-text! (find-by-css "form #password") "sekrit")
  (click! (find-by-css "input[type=submit]"))

  (is (= (current-path) "/"))
  (is (some-page-text "Login successful, welcome back!")))

(deftest add-book-test
  (load-db! {"users" {"Arne" "bcrypt+sha512$40cdea8a6b00230d83dc71e9eab25b8c$12$b8d940e1561e8bc273db2dd6a8d11914061315a08be4d5f8"}})
  (login! "Arne" "Sekrit")
  (click! (find-by-xpath "//a[text()='Add book']"))

  (send-text! (find-by-css "form #author") "Michal Zalewski")
  (send-text! (find-by-css "form #title") "The Tangled Web")
  (click! (find-by-css "input[type=submit]"))

  (is (re-find #"/books/[-0-9a-f]+" (current-path)))
  (is (some-page-text "My books"))
  (is (some-page-text "“The Tangled Web” by Michal Zalewski")))

(comment
  (run-tests))
