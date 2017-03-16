(ns booklog.styles
  (:require [garden-watcher.def :refer [defstyles]]))

(defstyles style
  ;; .dtc1 .. .dtc10 : table cell that spans x columns
  (for [i (range 10)]
    [(keyword (str ".dtc" i)) {:display "table-cell" :column-span i}])
  [:.bsp1 {:border-spacing "0.25em"}]
  [:.bsp2 {:border-spacing "0.5em"}]
  [:.bsp3 {:border-spacing "1em"}]
  [:.bsp4 {:border-spacing "2em"}]
  [:.bsp5 {:border-spacing "4em"}]
  [:.bsp6 {:border-spacing "8em"}]
  [:.bsp7 {:border-spacing "16em"}]
  [:.form-input:focus {:border-color "#85B7D9"}])
