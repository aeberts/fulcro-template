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

(defsc Root [this {:root/keys [lists items ui]}]
  {:initial-state {:root/lists [{:id 1 :label "Personal"}
                                {:id 2 :label "Work"}]
                   :root/items [{:id 1 :label "Take out the trash" :status :not-done}
                                {:id 2 :label "Paint the deck" :status :not-done}]
                   :root/ui    {:selected-list-id 2}}}
  (let [list-labels (map :label lists)
        todo-labels (map :label items)]
    (div :.ui.container.segment
      (div :.ui.grid
        #_(div :.row                                          ; debug info uncomment form see it in the UI
          (div :.sixteen.wide.mobile.sixteen.wide.computer.column
            (dom/h3 "Debug info:")
            (p ":ui data " (str ui))
            (p ":lists data " (str lists))
            (p ":items data " (str items))
            (p "list-labels: " (str list-labels))))
        (div :.row
          (div :.sixteen.wide.mobile.four.wide.computer.column
            (dom/h2 "Lists ")
            (div :.row
              (ui-menu {:pointing true :secondary true :vertical true}
                (map #(ui-menu-item {:name % :active false}) list-labels))))
          (div :.sixteen.wide.mobile.twelve.wide.computer.column
            (dom/h2 #js {:style #js {:marginBottom "25px"}} "Tasks")
            (div :.row
              (ui-list {:verticalAlign "middle"}
                (map #(ui-list-item {:className "todo-item"}
                        (ui-list-content {:className "todo-content-button" :floated "right" :verticalAlign "middle"}
                          (ui-button {:className "todo-button" :compact true} "Delete"))
                        (ui-list-content {:floated "left"}
                          (ui-checkbox {:className "todo-checkbox"}))
                        (ui-list-content {:verticalAlign "middle"} %)) todo-labels)))))))))