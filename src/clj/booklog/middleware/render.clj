(ns booklog.middleware.render)

(def res-defaults
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}})

(defn deep-merge [& maps]
  (apply merge-with (fn [x y]
                      (if (and (map? x) (map? y))
                        (merge x y)
                        y)) maps))

(defn update-response [res body]
  (-> res
      (deep-merge res-defaults)
      (assoc :body (list body))))

(defn layout-view-data [{:keys [identity] :as req}]
  (if identity
    {:layout/authenticated? true}
    {}))

(defn wrap-render-views [handler]
  (fn [req]
    (let [res (handler req)]
      (if-let [view (:render/view res)]
        (let [view-data (merge (:flash req)
                               (:render/data res)
                               (layout-view-data req))]
          (update-response res (view view-data)))
        res))))
