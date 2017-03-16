(ns booklog.views
  (:require [hiccup.page :refer [doctype]]
            [hiccup2.core :refer [html]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))

(defn center-box [& cs]
  `[:div.center.w-100.w-80-ns.w-70-m.w-60-l
    ~@cs])

(defn layout [{:layout/keys [message title]} content]
  (html
   {:mode :html}
   (doctype :html5)
   [:html
    [:head
     [:meta {:name "viewport", :content "width=device-width, initial-scale=1"}]
     [:link {:href "css/tachyons.min.css", :rel "stylesheet", :type "text/css"}]
     [:link {:href "css/style.css", :rel "stylesheet", :type "text/css"}]
     [:title (str title " | Booklog")]]

    [:body
     [:header#header.w-100.bg-yellow.pv3
      (center-box
       [:h1 "Booklog"])]
     [:div#app
      (if message
        [:div.w-100.bg-washed-yellow.dark-red.bb.b--gold.b
         (center-box
          [:div.pv3.br2 message])])
      (center-box
       (if title [:h2 title]))
      (center-box
       [:div.container.pv2 content])]]
    #_[:script {:src "js/compiled/booklog.js", :type "text/javascript"}]]))

(defn ^{:style/indent [1]} form [& contents]
  `[:form
    ~@contents
    [:input {:type "hidden" :name "__anti-forgery-token" :value ~*anti-forgery-token*}]])

(defn form-field [id label & [{:keys [type value] :or {type "text" value ""}}]]
  [:div.pv1
   [:label.db.b. {:for id} label]
   [:div.pv1
    [:input.form-input.w-100.ba.b--black-10.br2.pa2 {:type type :id id :name id :value value :placeholder label}]]])

(defn form-button [caption]
  [:input.b--none.br2.pv2.ph3.bg-black-30.hover-black-70.black-90.b {:type "submit" :value caption}])

(defn user-form [{:user/keys [username password]
                  :userform/keys [action caption alt-action alt-caption]}]
  (form {:method "POST" :action action}
    [:div.measure-narrow
     [:div.dt.w-100.bsp1
      (form-field "username" "Username" {:value username})
      (form-field "password" "Password" {:type "password" :value password})
      [:div.dt-row
       [:div.dtc.tl.pv1
        (form-button caption)
        [:div.pv1 [:a {:href alt-action} alt-caption " instead"]]]
       [:div.dtc]]]]))

(defn login-view [vs]
  (let [vs (merge vs
                  #:layout{:title "Login"}
                  #:userform{:action "/login"
                             :caption "Login"
                             :alt-action "/register"
                             :alt-caption "Register"})]
    (layout vs (user-form vs))))

(defn register-view [vs]
  (let [vs (merge vs
                  #:layout{:title "Register"}
                  #:userform{:action "/register"
                             :caption "Register"
                             :alt-action "/login"
                             :alt-caption "Login"})]
    (layout vs (user-form vs))))


(defn home-view [vs]
  (layout vs [:p "Hello, " (:user/username vs)]))
