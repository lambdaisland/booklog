(ns booklog.layout
  (:require [booklog.components :refer [center-box]]
            [hiccup.page :refer [doctype]]
            [hiccup2.core :refer [html]]
            [clojure.string :as str]))

(defn menu-item [href caption]
  [:a.pr4.white.link {:href href :class (str/replace (str/lower-case caption) #"\s+" "-")} caption])

(defn menu [{:user/keys [authenticated?
                         identity]}]
  (center-box
   [:nav#menu.f3.flex
    (menu-item "/" "Timeline")

    (if authenticated?
      (menu-item "/books/new" "Add book"))
    (if authenticated?
      (menu-item (str "/users/" identity "/books") "My books"))
    (if authenticated?
      (menu-item "/logout" "Log out")
      [:div
       (menu-item "/login" "Log in")
       (menu-item "/log-in-with-google" "Log in with Google")])]))

(defn layout [{:layout/keys [message title authenticated?] :as data} content]
  (html
   {:mode :html}
   (doctype :html5)
   [:html
    [:head
     [:meta {:name "viewport", :content "width=device-width, initial-scale=1"}]
     [:link {:href "/css/tachyons.min.css", :rel "stylesheet", :type "text/css"}]
     [:link {:href "/css/style.css", :rel "stylesheet", :type "text/css"}]
     [:title (str/join " | " (remove nil? [title "Booklog"]))]]

    [:body
     [:header#header.w-100.bg-yellow.pv3
      (center-box
       [:h1 [:a.link.dark-gray {:href "/"} "Booklog"]])]

     [:section.w-100.bg-dark-gray.pv3
      (menu data)]

     [:div#app

      (if message
        [:div.w-100.bg-washed-yellow.bb.b--gold.b
         (center-box
          [:div.pv3.br2 message])])
      (center-box
       (if title [:h2 title]))
      (center-box
       [:div.container.pv2.pt2 content])]]
    #_[:script {:src "js/compiled/booklog.js", :type "text/javascript"}]]))
