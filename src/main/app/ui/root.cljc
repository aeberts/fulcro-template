(ns app.ui.root
  (:require
    #?@(:cljs [[com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
               [com.fulcrologic.semantic-ui.collections.menu.ui-menu-item :refer [ui-menu-item]]
               [com.fulcrologic.semantic-ui.elements.list.ui-list :refer [ui-list]]
               [com.fulcrologic.semantic-ui.elements.list.ui-list-content :refer [ui-list-content]]
               [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
               [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
               [com.fulcrologic.semantic-ui.modules.checkbox.ui-checkbox :refer [ui-checkbox]]
               [com.fulcrologic.semantic-ui.elements.image.ui-image :refer [ui-image]]
               [com.fulcrologic.semantic-ui.icons :as i]
               [com.fulcrologic.fulcro.dom.events :as evt]])
    [app.model.session :as session]
    [clojure.string :as str]
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div p a]]
       :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div p a]])
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro-css.css :as css]
    [taoensso.timbre :as log]))

(defsc Root [this {:root/keys [lists items] :ui/keys [selected-list-id]}]
  (div :.ui.container.segment
    (div :.ui.grid
      (div :.row
        (div :.sixteen.wide.mobile.four.wide.computer.column
          (dom/h2 "Projects")
          (div :.row
            (ui-menu {:pointing true :secondary true :vertical true}
              (ui-menu-item {:name "Home" :active true})
              (ui-menu-item {:name "Work" :active false}))))
        (div :.sixteen.wide.mobile.twelve.wide.computer.column
          (dom/h2 #js {:style #js {:marginBottom "25px"}} "Tasks")
          (div :.row
            (ui-list {:verticalAlign "middle"}
              (ui-list-item {:className "todo-item"}
                (ui-list-content {:className "todo-content-button" :floated "right" :verticalAlign "middle"}
                  (ui-button {:className "todo-button" :compact true} "Delete"))
                (ui-list-content {:floated "left"}
                  (ui-checkbox {:className "todo-checkbox"}))
                (ui-list-content {:verticalAlign "middle"} "Take out the Trash"))
              (ui-list-item {:className "todo-item"}
                (ui-list-content {:className "todo-content-button" :floated "right" :verticalAlign "middle"}
                  (ui-button {:className "todo-button" :compact true} "Delete"))
                (ui-list-content {:floated "left"}
                  (ui-checkbox {:className "todo-checkbox"}))
                (ui-list-content {:verticalAlign "middle"} "Paint the Deck")))))))))
