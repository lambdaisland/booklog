(ns booklog.render
  (:require [com.rpl.specter :refer [MAP-KEYS NAMESPACE transform]]))

(defn render
  "The render subsystem expects Ring response maps like {:render/view
  foo-view :reder/data foo-data}. This helper makes it easier to write these
  as (render :view foo-view :data foo-data)."
  [& {:as opts}]
  (transform [MAP-KEYS NAMESPACE] (constantly "render") opts))

(defn flash-message [req msg]
  (assoc-in req [:flash :layout/message] msg))
