(ns booklog.views
  (:require [hiccup.page :refer [doctype]]
            [hiccup2.core :refer [html]]))

(defn layout [contents]
  (html
   {:mode :html}
   (doctype :html5)
   [:html
    [:meta {:name "viewport", :content "width=device-width, initial-scale=1"}]
    [:link {:href "css/tachyons.min.css", :rel "stylesheet", :type "text/css"}]
    [:link {:href "css/style.css", :rel "stylesheet", :type "text/css"}]
    [:div#app contents]
    #_[:script {:src "js/compiled/booklog.js", :type "text/javascript"}]]))

(defn form-field [id label & [{:keys [type] :or {type "text"}}]]
  [:div
   [:label {:for id} label]
   [:div
    [:input {:type type :id id}]]])

(defn login-form []
  [:form {:method "POST" :action "/login"}
   (form-field "username" "Username")
   (form-field "password" "Password" {:type "password"})
   [:input {:type "submit" :value "Login"}]
   [:div.text-center
    [:a {:href "/register"} "register instead"]]])

(defn register-form []
  [:form {:method "POST" :action "/register"}
   (form-field "username" "Username")
   (form-field "password" "Password" {:type "password"})
   [:input {:type "submit" :value "Register"}]
   [:div.text-center
    [:a {:href "/"} "log in instead"]]])
