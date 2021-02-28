(ns app.ui.root
  (:require
    #?@(:cljs [
               ;[com.fulcrologic.semantic-ui.collections.factories :refer [ui-menu ui-menu-item ui-list ui-list-item ui-list-content ui-button ui-checkbox]]
               [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
               [com.fulcrologic.semantic-ui.collections.menu.ui-menu-item :refer [ui-menu-item]]
               [com.fulcrologic.semantic-ui.elements.list.ui-list :refer [ui-list]]
               [com.fulcrologic.semantic-ui.elements.list.ui-list-content :refer [ui-list-content]]
               [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
               [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
               [com.fulcrologic.semantic-ui.modules.checkbox.ui-checkbox :refer [ui-checkbox]]
               [com.fulcrologic.semantic-ui.icons :as i]
               [com.fulcrologic.fulcro.dom.events :as evt]])
    [app.model.session :as session]
    [clojure.string :as str]
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div p a]]
       :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div p a]])
    ;;[com.fulcrologic.fulcro.dom.html-entities :as ent]
    ;;[com.fulcrologic.fulcro.dom.events :as evt]
    ;;[com.fulcrologic.fulcro.application :as app]
    ;;[com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    ;;[com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
    ;;[com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro-css.css :as css]
    [taoensso.timbre :as log]))

(defsc Root [this {:root/keys [todo-page]}]
  {:query         [{:root/todo-page {} }]
   :initial-state {:root/todo-page {}}}
  (p "Hoo hoo"))
