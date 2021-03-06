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

(defsc TodoItem [this {:item/keys [id label status]}]
  {:query         [:item/id :item/label :item/status]
   :ident         [:item/id :item/id]
   :initial-state (fn [{:item/keys [id label status]}]
                    {:item/id id :item/label label :item/status status})}
  (comp/fragment
    (ui-list-item {:className "todo-item"}
      (ui-list-content {:className "todo-content-button" :floated "right" :verticalAlign "middle"}
        (ui-button {:className "todo-button" :compact true} "Delete"))
      (ui-list-content {:floated "left"}
        (ui-checkbox {:className "todo-checkbox"}))
      (ui-list-content {:verticalAlign "middle"} label))))

(def ui-todo-item (comp/factory TodoItem {:keyfn :item/id}))

(defsc ListItem [this {:list/keys [id label]}]
  {:query         [:list/id :list/label]
   :ident         [:list/id :list/id]
   :initial-state (fn [{:list/keys [id label]}]
                    {:list/id id :list/label label})}
  (comp/fragment
    (ui-menu-item {:name label :active false})))

(def ui-listitem (comp/factory ListItem {:keyfn :list/id}))

(defsc Root [this {:root/keys [lists items]}]
  {:query         [{:root/lists (comp/get-query ListItem)}
                   {:root/items (comp/get-query TodoItem)}]
   :initial-state (fn [params]
                    {:root/lists [(comp/get-initial-state ListItem {:list/id 1 :list/label "Work"})
                                  (comp/get-initial-state ListItem {:list/id 2 :list/label "Play"})]
                     :root/items [(comp/get-initial-state TodoItem {:item/id 1 :item/label "Take out the trash" :item/status :not-done})
                                  (comp/get-initial-state TodoItem {:item/id 2 :item/label "Paint the deck" :item/status :not-done})
                                  (comp/get-initial-state TodoItem {:item/id 3 :item/label "Write TPS report" :item/status :not-done})
                                  (comp/get-initial-state TodoItem {:item/id 4 :item/label "Make copies" :item/status :not-done})]})}
  (div :.ui.container.segment
    (div :.ui.grid
      ;; region
      #_(div :.row                                          ; debug info uncomment form see it in the UI
          (div :.sixteen.wide.mobile.sixteen.wide.computer.column
            (dom/h3 "Debug info:")
            (p ":lists data " (str lists))
            (p ":items data " (str items))))
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
              (map ui-todo-item items))))))))

(comment

  ;;Notice a couple of things about this step:
  ;;- We're using the "lambda form" of :initial-state here. In the next step we'll use the more concise "template form"
  ;;- In general, you should prefer the "template form" of :initial-state because it will do error checking for you
  ;;- The components in this example are expecting a map of individual elements so we need to call comp/get-initial-state for each component instance
  ;;- :initial-state should be a map of the props that your component expects (i.e. the props in the argument list)
  ;;- For ListItem the props it expects are: :list/id and :list/label because that is what we're destructing in props

  )