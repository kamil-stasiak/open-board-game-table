(ns open-board-game-table.core
  (:require [reagent.dom :as d] [re-frame.core :as r]))

(enable-console-print!)

(println "-> Reloaded")

(defn drop-nth [n coll]
  (keep-indexed #(if (not= %1 n) %2) coll))

(defn add-nth [n element coll]
  (concat (subvec coll 0 n) [element] (subvec coll n)))

(add-nth 0 "x" [{:a "aaa"}])

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
    :decks {:player1 (list element element element)
            :player2 (list element)}}))

(r/reg-sub
 :query-selected
 (fn [db [_]]
   (:selected db)))

(defn action-component [selected]
  (println selected)
  (when selected
    [:div
     [:div "selected: " (str selected)]
     [:h4
      {:on-click #(r/dispatch [:move-card selected :player1])}
      "Move to player1"]
     [:h4
      {:on-click #(r/dispatch [:move-card selected :player2])}
      "Move to player2"]]))

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
   (let [deck1 (get-in db [:decks deck])
         card (nth deck1 idx)
         toggled-card (assoc card :selected? (not (:selected? card)))
         new-db (assoc db :selected [deck idx])
         new-deck (assoc (vec deck1) idx toggled-card)
         new-db2 (assoc-in new-db [:decks deck] new-deck)
         ]
     ; (println "--->>> " new-db2)
     {:db new-db2})))

(r/reg-event-fx
 :move-card
 (fn [{:keys [db]} [_ [player idx] to]]
   (println (str "Move from " [player idx] " to " to))
   (let [
         from-deck (get-in db [:decks player])
         element (nth from-deck idx)
         new-from-deck (drop-nth idx from-deck)
         to-deck (get-in db [:decks to])
         new-to-deck (add-nth 0 element (vec to-deck))
         db1 (assoc-in db [:decks player] new-from-deck)
         db2 (assoc-in db1 [:decks to] new-to-deck)
         ]
     (println db2) {:db db2})))

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
                     (fn []
                       (js/console.log (str "Card clicked!" index) )
                       (r/dispatch [:select-card deck-name index]))])]])

(defn app []
  [:div.my-app {:on-click #(js/console.log "background clicked")}
   (when-let [deck @(r/subscribe [:query-deck :player1])]
     [deck-component "First player" deck :player1])
   (when-let [deck @(r/subscribe [:query-deck :player2])]
     [deck-component "Second player" deck :player2])
   (when-let [first-card (first @(r/subscribe [:query-deck]))]
     [card-component first-card])
   [action-component @(r/subscribe [:query-selected])]])

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
