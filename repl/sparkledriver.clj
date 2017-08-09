(require '[sparkledriver.core :as sd])

#_
(with-browser [browser (sd/make-browser)]
  ,,,)

(def browser (sd/make-browser))
;; (sd/close-browser! browser)

(sd/fetch! browser "http://localhost:1234")
;;=> #object[com.machinepublishers.jbrowserdriver.JBrowserDriver 0x392d747e "com.machinepublishers.jbrowserdriver.JBrowserDriver@392d747e"]

(sd/current-url browser)
;;=> "http://localhost:1234/"
(sd/page-source browser)
;;=> "<html><head><meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">..."
(sd/screenshot browser)
;;=> "/tmp/screenshot7524621746679777593.png"

(require '[clojure.java.shell :refer [sh]])
(sh "xdg-open" (sd/screenshot browser))
