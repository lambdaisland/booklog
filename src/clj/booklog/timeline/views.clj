(ns booklog.timeline.views)

(defn book-view [{:book/keys [id author title user]}]
  [:div
   user
   " read "
   title
   " by "
   author])

(defn timeline-view [{:timeline/keys [books]}]
  [:div
   [:h1 "Timeline"]
   (map book-view books)])
