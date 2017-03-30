(ns booklog.timeline.views)

(defn book-view [{:book/keys [id author title]
                  :user/keys [identity]
                  :as book}]
  [:div
   [:a {:href (str "/users/" identity "/books")} identity]
   " read "
   [:a {:href (str "/books/" id)} title]
   " by "
   author])

(defn timeline-view [{:timeline/keys [books]}]
  [:div
   [:h1 "Timeline"]
   (map book-view books)])
