(ns app.ui.root
  (:require
    [app.model.session :as session]
    [app.application :as app :refer [SPA]]
    [clojure.string :as str]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.dom :as dom :refer [div p a]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
    [com.fulcrologic.semantic-ui.collections.menu.ui-menu-item :refer [ui-menu-item]]
    [com.fulcrologic.semantic-ui.elements.list.ui-list :refer [ui-list]]
    [com.fulcrologic.semantic-ui.elements.list.ui-list-content :refer [ui-list-content]]
    [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
    [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
    [com.fulcrologic.semantic-ui.modules.checkbox.ui-checkbox :refer [ui-checkbox]]
    [com.fulcrologic.semantic-ui.elements.image.ui-image :refer [ui-image]]
    [com.fulcrologic.semantic-ui.icons :as i]))

(defmutation update-selected-list [{:list/keys [id] :as params}]
  (action [{:keys [app state] :as env}]
    (swap! state assoc-in [:root/ui :selected-list-id] id)))

(defsc TodoItem [this {:item/keys [id label status]}]
  {:query         [:item/id :item/label :item/status]
   :ident         [:item/id :item/id]
   ;; if we use simple keywords in :initial-state of Root we can destructure with :param/property like so:
   :initial-state {:item/id :param/id :item/label :param/label :item/status :param/status}}
  (comp/fragment
    (ui-list-item {:className "todo-item"}
      (ui-list-content {:className "todo-content-button" :floated "right" :verticalAlign "middle"}
        (ui-button {:className "todo-button" :compact true} "Delete"))
      (ui-list-content {:floated "left"}
        (ui-checkbox {:className "todo-checkbox"}))
      (ui-list-content {:verticalAlign "middle"} label))))

(def ui-todo-item (comp/factory TodoItem {:keyfn :item/id}))

(defsc ListItem [this {:list/keys [id label items]}]
  {:query         [:list/id :list/label {:list/items (comp/get-query TodoItem)}]
   :ident         [:list/id :list/id]
   ;; if we use simple keywords in :initial-state of Root we can destructure with :param/property like so:
   :initial-state {:list/id :param/id :list/label :param/label :list/items :param/items}}
  (comp/fragment
    (ui-menu-item {:name label :active false :onClick #(comp/transact! this [(update-selected-list {:list/id id})])})))

(def ui-listitem (comp/factory ListItem {:keyfn :list/id}))

(defsc Root [this {:root/keys [lists ui]}]
  {:query         [{:root/lists (comp/get-query ListItem)}
                   :root/ui]
   :initial-state {:root/lists [{:id    1 :label "Work"
                                 :items [{:id 1 :label "Take out the trash" :status :not-done}
                                         {:id 2 :label "Paint the deck" :status :not-done}]}
                                {:id    2 :label "Play"
                                 :items [{:id 3 :label "Write TPS report" :status :not-done}
                                         {:id 4 :label "Make copies" :status :not-done}]}]
                   :root/ui    {:selected-list-id 1}}}
  (let [selected-list (ui :selected-list-id)
        selected-list-items (-> lists (get (- selected-list 1)) (:list/items))]
    (div :.ui.container.segment
      (div :.ui.grid
        ;; region
        (div :.row                                          ; debug info - uncomment this form see it in the UI
          (div :.sixteen.wide.mobile.sixteen.wide.computer.column
            (dom/h3 "Debug info:")
            #_(p ":lists data " (str lists))
            #_(p ":ui data " (str ui))
            (p "selected-list: " (str selected-list))
            ))
        ;; endregion
        (div :.row
          (div :.sixteen.wide.mobile.four.wide.computer.column
            (dom/h2 "Lists ")
            (div :.row
              (ui-menu {:pointing true :secondary true :vertical true}
                (map ui-listitem lists))))
          (div :.sixteen.wide.mobile.twelve.wide.computer.column
            (dom/h2 #js {:style #js {:marginBottom "25px"}} "Tasks")
            (div :.row
              (ui-list {:verticalAlign "middle"}
                (map ui-todo-item selected-list-items)))))))))



(comment

  ;;Notice a couple of things about this step:
  ;;
  ;;- We're using the more concise "template form" of :initial-state in the Root component
  ;;- Since the template form of :initial-state only supports simple keywords (not namespaced keywords) we need to use the lambda form in ListItem and TodoItem to destructure the incoming params
  ;;- In general, you should prefer the "template form" of :initial-state because it will do error checking for you
  ;;- The template form of :initial-state auto-derives the joins from the query so we don't need (comp/get-initial-state ListItem) etc. here

  )