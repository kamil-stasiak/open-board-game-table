(ns bgt.store
  (:require [re-frame.core :as r]
            [bgt.utils :refer [drop-nth add-nth]]
            [bgt.cards :refer [splendor-deck] ]))

(def element
  {:active :back
   :selected? false
   :front
   {:img "https://upload.wikimedia.org/wikipedia/en/3/3b/Pokemon_Trading_Card_Game_cardback.jpg"}
   :back
   {:img "https://upload.wikimedia.org/wikipedia/en/2/2b/Yugioh_Card_Back.jpg"}})

(defn toggle-side [{:keys [active]}]
  (case active
    :front :back
    :back :front))

(r/reg-event-db
 :initialize
 (fn [_ _]
   (js/console.log "Initialize fired!")
   {:selected nil
    :message "init"
    :decks {:player1 (list element element element element)
            :player2 (list element)
            :splendor-deck splendor-deck}}))


(r/reg-sub
 :query-selected
 (fn [db [_]]
   (:selected db)))

(r/reg-sub
 :query-deck
 (fn [db [_ player]]
   (get-in db [:decks player])))
(r/reg-sub :query-items (fn [db v] (:message db)))
(r/reg-sub :query-mode (fn [db v] (:mode db)))

(r/reg-event-fx
 :set-mode
 (fn [{:keys [db]} [_ mode]]
   (let [new-db (assoc db :mode mode)]
     {:db new-db}))
 )
(defn flip-card-handler [{:keys [db]} [_ [deck item-id]]]
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

