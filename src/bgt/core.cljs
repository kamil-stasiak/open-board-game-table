(ns bgt.core
  (:require [reagent.dom :as d]
            [re-frame.core :as r]
            [bgt.utils :refer [drop-nth add-nth]]
            [bgt.store] [bgt.cards :refer [splendor-1]]))

(enable-console-print!)

(println "-> Reloaded")

(defn action-component [selected]
  (println selected)
  (when selected
    [:div
     [:div "selected: " (str selected)]
     [:h4
      {:on-click #(r/dispatch [:set-mode :move])}
      "Move MODE"]
     [:h4
      {:on-click #(r/dispatch [:move-card selected :player1])}
      "Move to player1"]
     [:h4
      {:on-click #(r/dispatch [:move-card selected :player2])}
      "Move to player2"]
     [:h4
      {:on-click #(r/dispatch [:flip-card selected])}
      "Flip"]]))

(defn card-component [{:keys [active selected?] :as card} on-click]
  (let [image (:img (active card))]
    [:div.card
     {:on-click on-click}
     [:img {:src image :height "198px" :width "136px"}]]))

(defn field-component [name cards deck-name]
  [:div {:style {:border "1px solid #a3a3a3"}}
   [:h3 (str name " (" (count cards) ")")]
   [:div
    {:style
     {:border "1px dotted #a3a3a3"
      :width "600px"
      :height "250px"
      :display "flex"
      :justify-content "space-evenly"
      :align-items "center"}}
    (for [[index element] (map-indexed vector cards)]
      ^{:key index} [card-component element #()])]])

(defn deck-component [name cards deck-name]
  [:div
   [:h3 (str name " (" (count cards) ")")]
   [:div.deck-cards
    {:style
     {:border "1px solid #a3a3a3"
      :width "200px"
      :height "250px"
      :display "flex"
      :justify-content "space-around"
      :align-items "center"}}
    (when (first cards)
      [card-component (first cards)
       #(println (str "Clicked: " deck-name " " 0))])]])

(defn stack-component [name cards deck-name]
  [:div
   {:style
    {:border "1px solid #a3a3a3"
     :width "200px"}}
   [:h3 (str name " (" (count cards) ")")]
   [:div.stack-cards
    {:style
     {:border "1px dotted #a3a3a3"
      :width "200px"
      :height "300px"
      :display "flex"
      :justify-content "space-around"
      :align-items "center"}}
    [:div.test
     {:style
      {:position "relative"
       :width "140px"
       :height (str (+ 170 (* (count cards) 30)) "px")}}
     (for [[index card] (map-indexed vector cards)]
       ^{:key index}
       [:div.card-on-stack
        {:style
         {:position "absolute"
          :top (str (* index 30) "px")}}
        ;; FIX ME broken index when reversed
        [card-component card #(println (str "Clicked: " deck-name " " index))]])]]])

(defn app []
  [:div.my-app {:style {:display "flex" :flex-wrap "wrap"}}
   (when-let [deck @(r/subscribe [:query-deck :splendor-deck])]
     [field-component "Field" deck :splendor-deck])
   (when-let [stack @(r/subscribe [:query-deck :splendor-deck])]
     [stack-component "Stack" stack :player1])
   (when-let [stack (reverse @(r/subscribe [:query-deck :splendor-deck]))]
     [stack-component "Stack reverse" stack :player1])
   (when-let [card (first @(r/subscribe [:query-deck :splendor-deck]))]
     [card-component card #(println "Single card clicked")])
   (when-let [deck @(r/subscribe [:query-deck :splendor-deck])]
     [deck-component "Deck" deck :player1])
   ;; - player1
   (when-let [deck @(r/subscribe [:query-deck :player1])]
     [field-component "Field" deck :splendor-deck])
   (when-let [stack @(r/subscribe [:query-deck :player1])]
     [stack-component "Stack" stack :player1])
   (when-let [card (first @(r/subscribe [:query-deck :player1]))]
     [card-component card #(println "Single card clicked")])
   (when-let [deck @(r/subscribe [:query-deck :player1])]
     [deck-component "Deck" deck :player1])
   ;[action-component @(r/subscribe [:query-selected])]
   ])

;; rendering


(defn render []
  (d/render [app] (js/document.getElementById "app")))

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (r/clear-subscription-cache!)
  (render))

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
