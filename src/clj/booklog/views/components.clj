(ns booklog.views.components
  (:require [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))

(defn center-box [& cs]
  `[:div.center.w-100.w-80-ns.w-70-m.w-60-l
    ~@cs])

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
