(ns app.ui.root
  (:require
    [app.model.session :as session]
    [app.application :refer [SPA]]
    [clojure.string :as str]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.dom :as dom :refer [div p a]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc get-initial-state get-query]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
    [com.fulcrologic.fulcro.algorithms.normalized-state :refer [swap!-> integrate-ident]]
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

(defmutation update-selected-list [{:list/keys [id] :as list}]
  (action [{:keys [app state] :as env}]
    (swap!-> state
      (assoc-in [:ui/selected-list :list/id] id))))

(defmutation toggle-item-status [{:item/keys [id status] :as params}]
  (action [{:keys [app state] :as env}]
    (let [new-status ((fn [s] (if (= s :done) :not-started :done)) status)]
      (swap! state update-in [:item/id id] assoc :item/status new-status))))

(defsc TodoItem [this {:item/keys [id label status]}]
  {:query         [:item/id :item/label :item/status]
   :ident         [:item/id :item/id]
   :initial-state {:item/id :param/id :item/label :param/label :item/status :param/status}}
  (comp/fragment
    (ui-list-item {:className "todo-item"}
      (ui-list-content {:className "todo-content-button" :floated "right" :verticalAlign "middle"}
        (ui-button {:className "todo-button" :compact true} "Delete"))
      (ui-list-content {:floated "left"}
        (ui-checkbox {:className "todo-checkbox" :checked (= status :done)
                      :onClick   #(comp/transact! this [(toggle-item-status {:item/id id :item/status status})])}))
      (ui-list-content {:className     (str "todo-label " (when (= status :done) "done"))
                        :verticalAlign "middle"}
        label))))

(def ui-todo-item (comp/factory TodoItem {:keyfn :item/id}))

(defsc ListItem [this {:list/keys [id label items]} {:keys [active?]}]
  {:query         [:list/id :list/label {:list/items (comp/get-query TodoItem)}]
   :ident         [:list/id :list/id]
   :initial-state {:list/id :param/id :list/label :param/label :list/items :param/items}}
  (comp/fragment
    (ui-menu-item {:name    label
                   :active  active?
                   :onClick #(comp/transact! this [(update-selected-list {:list/id id})])
                   })))
(def ui-listitem (comp/computed-factory ListItem {:keyfn :list/id}))

;; Listpane is a singleton so we'll store it at [:component :listpane] in the app-cache
(defsc ListPanel [this {:keys [list-item] :as props} {:ui/keys [selected-list]}]
  {:query         [{:list-item (comp/get-query ListItem)}
                   [:ui/selected-list '_]]
   :ident         (fn [] [:component/id :listpanel])
   :initial-state (fn [params]
                    (identity params))
   }
  (let [update-selected #(comp/transact! this [(update-selected-list {:list/id %})])
        _ (log/info "listpane props: " (str lists))
        ]
    (dom/h2 "Lists"
      (div :.row
        (ui-menu {:pointing true :secondary true :vertical true}
          (map #(ui-listitem % {:active? (= (:list/id %) (:list/id selected-list))}) lists))))))

(def ui-listpanel (comp/factory ListPanel))

(defsc Root [this {:root/keys [listpanel]} {:ui/keys [selected-list]}]
  {:query         [{:root/listpanel (comp/get-query ListPanel)}
                   :ui/selected-list]
   :initial-state [{:root/listpanel
                    [{:id    1 :label "Home"
                      :items [{:id 1 :label "Take out the trash" :status :not-started}
                              {:id 2 :label "Paint the deck" :status :done}]}
                     {:id    2 :label "Work"
                      :items [{:id 3 :label "Write TPS report" :status :done}
                              {:id 4 :label "Make copies" :status :not-started}]}
                     {:id    3 :label "Foo"
                      :items [{:id 5 :label "Some Foo Todo 1" :status :done}
                              {:id 6 :label "Foo Todo 1" :status :not-started}]}]}]}
  (let [selected-todos (some :list/items (map #(if (= (:list/id selected-list) (:list/id %)) %) listpanel))
        update-selected #(comp/transact! this [(update-selected-list {:list/id %})])]
    (div :.ui.container.segment
      (div :.ui.grid
        ;; region
        (div :.row                                          ; debug info - uncomment this form see it in the UI
          (div :.sixteen.wide.mobile.sixteen.wide.computer.column
            (dom/h3 "Debug info:")
            (p ":root/listpanel " (str listpanel))
            #_(p ":ui data " (str ui))
            #_(p "lists: " (str lists))
            (p "selected-list: " (str selected-list))
            (p "selected-todos: " (str selected-todos))
            ))
        ;; endregion
        (div :.row
          (div :.sixteen.wide.mobile.four.wide.computer.column
            (ui-listpanel listpanel))
          (div :.sixteen.wide.mobile.twelve.wide.computer.column
            (dom/h2 #js {:style #js {:marginBottom "25px"}} "Tasks")
            (div :.row
              (ui-list {:verticalAlign "middle"}
                (map ui-todo-item selected-todos)))))))))

(comment

  (def s (app/current-state SPA))
  (type s)

  (def ls (->> (app/current-state SPA)
            :list/id))

  ;; create a vector of list ids whose value of :selected? is false
  (->> s
    :list/id
    (filter #(not (= 2 (key %))))
    (map first))

  (update-in s [:list/id 3] assoc :list/selected? false)

  (fdn/db->tree (comp/get-query Root) (comp/get-initial-state Root {}) {})

  (def root-state (comp/get-initial-state Root {}))

  (fdn/db->tree [{:root/lists [:list/label]}] (comp/get-initial-state Root {}) {})

  ;; What we did in this step:
  ;;
  ;; Updated the lists to show the selected list. However the solution is not optimal
  ;; In the next step we'll improve it.

  )