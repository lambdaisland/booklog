(ns booklog.auth.views
  (:require [booklog.components :refer [form form-button form-field]]
            [booklog.layout :refer [layout]]))

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
    (user-form vs)))

(defn register-view [vs]
  (let [vs (merge vs
                  #:layout{:title "Register"}
                  #:userform{:action "/register"
                             :caption "Register"
                             :alt-action "/login"
                             :alt-caption "Login"})]
    (user-form vs)))
