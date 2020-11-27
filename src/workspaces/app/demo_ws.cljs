(ns app.demo-ws
  (:require [com.fulcrologic.fulcro.components :as comp]
            [nubank.workspaces.core :as ws]
            [nubank.workspaces.card-types.fulcro3 :as ct.fulcro]
            [com.fulcrologic.fulcro.mutations :as fm]
            [com.fulcrologic.fulcro.dom :as dom]))

(comp/defsc FulcroDemo [this {:keys [counter]}]
  {:initial-state (fn [_] {:counter 0})
   :ident         (fn [] [::id "singleton"])
   :query         [:counter]}
  (dom/div
    (str "Fulcro counter demo [" counter "]")
    (dom/button {:onClick #(fm/set-value! this :counter (inc counter))} "+")))

(comp/defsc TodoItem [this {:keys [todo/id todo/label todo/status] :as props}]
  {:query [:todo/id :todo/label :todo/status]
   :ident (fn [] [:todo/id (:todo/id props)])
   :initial-state (fn [_]
                    [{:todo/id 1 :todo/label "Get Milk" :todo/status :notstarted}
                     {:todo/id 2 :todo/label "Get Cheese" :todo/status :notstarted}])
   }
  (dom/div
   (dom/li label)))

(def ui-todo-item (comp/factory TodoItem))

(comp/defsc TodoList [this {:keys [list/id list/label list/items] :as props}]
  {:query         [:list/id :list/label {:list/items (comp/get-query TodoItem)}]
   :ident         (fn [] [:list/id (:list/id props)])
   :initial-state (fn [_]
                    {:list/id    1
                     :list/label "Work"
                     :list/items (comp/get-initial-state TodoItem)})}
  (dom/div
    (dom/h3 label ":")
    (dom/div
     (map ui-todo-item items))))

(def ui-todo-list (comp/factory TodoList))

(ws/defcard fulcro-demo-card
  (ct.fulcro/fulcro-card
    {::ct.fulcro/root       TodoList
     ::ct.fulcro/wrap-root? true}))


;;(fp/defsc TodoList [this {:keys [id label items]}]
;;  {:initial-state (fn [props]  )
;;   })