(require '[sparkledriver.core :as sd])
(import 'java.util.regex.Pattern)

(user/go)

(def home-page "http://localhost:1234")

;; [with-browser make-browser fetch! find-by-xpath* text]

(def *browser* (sd/make-browser))
;; (sd/close-browser! *browser*)


(sd/fetch! *browser* home-page)

(sd/current-url *browser*)

(sd/click! (sd/find-by-xpath *browser* "//a[text()='Log in']"))
(sd/click! (sd/find-by-xpath *browser* "//a[text()='Register instead']"))

(sd/send-text! (sd/find-by-css *browser* "form #username") "Arne")
(sd/send-text! (sd/find-by-css *browser* "form #password") "sekrit")

(sd/click! (sd/find-by-css *browser* "input[type=submit]"))

(sd/page-source *browser*)

(defn page-text []
  (sd/text (sd/find-by-css *browser* "html")))

(defn some-text [txt]
  (re-find (Pattern/compile txt Pattern/LITERAL) (page-text)))

(clojure.test/deftest foo-test
  (clojure.test/is (some-text "Thanks for registering! Please log in..")))

(defmethod clojure.test/assert-expr 'some-text [msg form]
  `(if ~form
     (clojure.test/do-report {:type :pass, :message ~msg,
                              :expected '~form, :actual '~form})
     (clojure.test/do-report {:type :fail, :message ~msg,
                              :expected '~(second form), :actual ~(page-text)})))

(foo-test)

(spicerack.core/open-hashmap (:db (:spicerack reloaded.repl/system)) "users")
{"abc" "bcrypt+sha512$35686702206a3e8df6ae67282429d085$12$03e8f9c6038d84ad4f3a76c7b32dfacd1105a07c60022186", "Arne" "bcrypt+sha512$adb09132c4a4549e38ff08f22e549a5b$12$0467a94555d83b2bc322bb83364986b50e372b5ec0f23b39"}


(->
 (sd/find-by-xpath* "//a[text()='Log in'")
 )
