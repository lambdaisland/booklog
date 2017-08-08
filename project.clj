(defproject booklog "0.1.0-SNAPSHOT"
  :description "Keep track of the books you read, a sample project to demonstrate Buddy."
  :url "https://github.com/lambdaisland/booklog"

  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.854" :scope "provided"]
                 [com.google.guava/guava "20.0"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [ring "1.6.2"]
                 [ring/ring-defaults "0.3.1"]
                 [bk/ring-gzip "0.2.1"]
                 [lambdaisland/ring.middleware.logger "0.5.1"]
                 [compojure "1.6.0"]
                 [environ "1.1.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [org.danielsz/system "0.4.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [reagent "0.7.0"]
                 [lambdaisland/garden-watcher "0.3.1"]
                 [hiccup "2.0.0-alpha1"]
                 [spicerack "0.1.3"]
                 [prone "1.1.4"]
                 [com.rpl/specter "1.0.2"]

                 [buddy/buddy-auth "1.4.1"]
                 [buddy/buddy-hashers "1.2.0"]
                 ;;[buddy/buddy-sign "1.4.0"]
                 ;;[buddy/buddy-core "1.2.0"]
                 ]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-environ "1.1.0"]]

  :min-lein-version "2.6.1"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :test-paths ["test/clj" "test/cljc"]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js"]

  :uberjar-name "booklog.jar"

  ;; Use `lein run` if you just want to start a HTTP server, without figwheel
  :main booklog.application

  ;; nREPL by default starts in the :main namespace, we want to start in `user`
  ;; because that's where our development helper functions like (run) and
  ;; (browser-repl) live.
  :repl-options {:init-ns user}

  :cljsbuild {:builds
              [{:id "app"
                :source-paths ["src/cljs" "src/cljc"]

                :figwheel true
                ;; Alternatively, you can configure a function to run every time figwheel reloads.
                ;; :figwheel {:on-jsload "booklog.core/on-figwheel-reload"}

                :compiler {:main booklog.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/booklog.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}

               {:id "test"
                :source-paths ["src/cljs" "test/cljs" "src/cljc" "test/cljc"]
                :compiler {:output-to "resources/public/js/compiled/testable.js"
                           :main booklog.test-runner
                           :optimizations :none}}

               {:id "min"
                :source-paths ["src/cljs" "src/cljc"]
                :jar true
                :compiler {:main booklog.core
                           :output-to "resources/public/js/compiled/booklog.js"
                           :output-dir "target"
                           :source-map-timestamp true
                           :optimizations :advanced
                           :pretty-print false}}]}

  ;; When running figwheel from nREPL, figwheel will read this configuration
  ;; stanza, but it will read it without passing through leiningen's profile
  ;; merging. So don't put a :figwheel section under the :dev profile, it will
  ;; not be picked up, instead configure figwheel here on the top level.

  :figwheel {;; :http-server-root "public"       ;; serve static assets from resources/public/
             :server-port 1235                ;; default
             ;; :server-ip "127.0.0.1"           ;; default
             :css-dirs ["resources/public/css"]  ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process. We
             ;; don't do this, instead we do the opposite, running figwheel from
             ;; an nREPL process, see
             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
             ;; :nrepl-port 7888

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             :server-logfile "log/figwheel.log"}

  :doo {:build "test"}

  :profiles {:dev
             {:dependencies [[figwheel "0.5.12"]
                             [figwheel-sidecar "0.5.12"]
                             [com.cemerick/piggieback "0.2.2"]
                             [org.clojure/tools.nrepl "0.2.13"]
                             [lein-doo "0.1.7"]
                             [reloaded.repl "0.2.3"]]

              :plugins [[lein-figwheel "0.5.12"]
                        [lein-doo "0.1.7"]]

              :source-paths ["dev"]
              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}

             :uberjar
             {:source-paths ^:replace ["src/clj" "src/cljc"]
              :prep-tasks ["compile"
                           ["cljsbuild" "once" "min"]
                           ["run" "-m" "garden-watcher.main" "booklog.styles"]]
              :hooks []
              :omit-source true
              :aot :all}})
