(ns booklog.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [booklog.core-test]
   [booklog.common-test]))

(enable-console-print!)

(doo-tests 'booklog.core-test
           'booklog.common-test)
