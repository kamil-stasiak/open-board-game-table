(ns open-board-game-table.core
  (:require [reagent.dom :as d] [re-frame.core :as r]))

(enable-console-print!)

(println "-> Reloaded")

(def element
  {:active :back
   :selected? false
   :front
   {:img "https://upload.wikimedia.org/wikipedia/en/3/3b/Pokemon_Trading_Card_Game_cardback.jpg"}
   :back
   {:img "https://upload.wikimedia.org/wikipedia/en/2/2b/Yugioh_Card_Back.jpg"}})

(r/reg-event-db
 :initialize
 (fn [_ _]
   (js/console.log "Initialize fired!")
   {:selected nil
    :message "init"
    :decks {:player1 [element element element]
            :player2 [element]}}))

(r/reg-sub
 :query-selected
 (fn [db [_]]
   (:selected db)))

(defn action-component [selected]
  (println selected)
  [:div "hello world" (str selected)])

(r/reg-sub
 :query-deck
 (fn [db [_ player]]
   (get-in db [:decks player])))
(r/reg-sub :query-items (fn [db v] (:message db)))

(defn toggle-side [{:keys [active]}]
  (case active
    :front :back
    :back :front))

(defn flip-card-handler [{:keys [db]} [_ deck item-id]]
  (let [card (get-in db [:decks deck item-id])
        flipped-card (assoc card :active (toggle-side card))]
    {:db (assoc-in db [:decks deck item-id] flipped-card)}))

(r/reg-event-fx :flip-card flip-card-handler)
(r/reg-event-fx
 :select-card
 (fn [{:keys [db]} [_ deck idx]]
   (let [card (get-in db [:decks deck idx])
         toggled-card (assoc card :selected? (not (:selected? card)))
         new-db (assoc db :selected [deck idx])
         new-db2 (assoc-in new-db [:decks deck idx] toggled-card)]
     {:db new-db2})
   ))

(defn card-component [{:keys [active selected?] :as e} on-click]
  (let [image (:img (active e))]
    [:div {:style {:width "140px" :height "200px"
                   :background-color (if selected? "blue" "red")}
           :on-click on-click}
     [:img
      {:src image
       :height "198px" :width "136px"}]]))

(defn deck-component [name cards deck-name]
  [:div.deck-component
   [:h3 name]
   [:div.deck-cards
    {:style {:width "500px" :height "200px" :display "flex" :justify-content "space-around"}}
    (for [[index element] (map-indexed vector cards)]
      ^{:key index} [card-component element
                     #(r/dispatch [:select-card deck-name index])]
      )]])

(defn app []
  [:div.my-app {:on-click #(js/console.log "background clicked")}
   (when-let [deck @(r/subscribe [:query-deck :player1])]
    [deck-component "First player" deck :player1])
   (when-let [deck @(r/subscribe [:query-deck :player2])]
     [deck-component "Second player" deck :player2])
   (when-let [first-card (first @(r/subscribe [:query-deck]))]
     [card-component first-card])
   [action-component @(r/subscribe [:query-selected])]
   ])

;; rendering

(defn render []
  (d/render [app] (js/document.getElementById "app")))

(render)

(defn init []
  (r/dispatch [:initialize])
  (render))

(init)
(defn on-js-reload []
  (init)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
