(ns app.ui.root
  (:require
    [app.model.session :as session]
    [app.application :refer [SPA]]
    [clojure.string :as str]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.dom :as dom :refer [div p a]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
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
    (swap! state assoc-in [:ui/selected-list] {:list/id id})))

(defmutation toggle-item-status [{:item/keys [id status] :as params}]
  (action [{:keys [app state] :as env}]
    (let [new-status ((fn [s] (if (= s :done) :not-started :done)) status)]
      (swap! state update-in [:item/id id] assoc :item/status new-status))))

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
        (ui-checkbox {:className "todo-checkbox" :checked (if (= status :done) true false)
                      :onClick   #(comp/transact! this [(toggle-item-status {:item/id id :item/status status})])}))
      (ui-list-content {:className     (str "todo-label " (if (= status :done) "done"))
                        :verticalAlign "middle"}
        label))))

(def ui-todo-item (comp/factory TodoItem {:keyfn :item/id}))

(defsc ListItem [this {:list/keys [id label items]}]
  {:query         [:list/id :list/label {:list/items (comp/get-query TodoItem)}]
   :ident         [:list/id :list/id]
   ;; if we use simple keywords in :initial-state of Root we can destructure with :param/property like so:
   :initial-state {:list/id :param/id :list/label :param/label :list/items :param/items}}
  (comp/fragment
    (ui-menu-item {:name label :active false :onClick #(comp/transact! this [(update-selected-list {:list/id id})])})))

(def ui-listitem (comp/factory ListItem {:keyfn :list/id}))

(defsc Root [this {:root/keys [lists] :ui/keys [selected-list] :as props}]
  {:query         [{:root/lists (comp/get-query ListItem)}
                   :ui/selected-list]
   :initial-state {:root/lists       [{:id    1 :label "Work"
                                       :items [{:id 1 :label "Take out the trash" :status :not-started}
                                               {:id 2 :label "Paint the deck" :status :done}]}
                                      {:id    2 :label "Play"
                                       :items [{:id 3 :label "Write TPS report" :status :done}
                                               {:id 4 :label "Make copies" :status :not-started}]}]
                   :ui/selected-list {:list/id 1}
                   ;:ui/selected-todos {}
                   }}
  (let [selected-todos (some :list/items (map #(if (= (:list/id selected-list) (:list/id %)) %) lists))]
    (div :.ui.container.segment
      (div :.ui.grid
        ;; region
        #_(div :.row                                        ; debug info - uncomment this form see it in the UI
            (div :.sixteen.wide.mobile.sixteen.wide.computer.column
              (dom/h3 "Debug info:")
              #_(p ":lists data " (str lists))
              #_(p ":ui data " (str ui))
              (p "lists: " (str lists))
              (p "selected-list: " (str selected-list))
              (p "selected-todos: " (str selected-todos))
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
                (map ui-todo-item selected-todos)))))))))

(comment

  (def s (app/current-state SPA))

  (fdn/db->tree (comp/get-query Root) (comp/get-initial-state Root {}) {})

  (def root-state (comp/get-initial-state Root {}))

  (fdn/db->tree [{:root/lists [:list/label]}] (comp/get-initial-state Root {}) {})

  ;; What we did in this step:
  ;; Added a mutation to update the item status when the checkbox is clicked
  ;; Added a custom-site.css file and updated shadow-cljs.edn to watch "resources/public" directory
  ;; Updated to the latest version of Fulcro
  ;;
  ;; Next: Make the Lists component show which list is selected based on :ui/selected-list

  )