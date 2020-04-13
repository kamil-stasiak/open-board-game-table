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
     {:style {:border "1px dotted #a3a3a3"}
      :on-click on-click}
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
      ^{:key index}
      [card-component
       element
       #(println (str "Clicked: " deck-name " " (:id element)))])]])

(defn deck-component [name cards deck-name]
  [:div {:style {:border "1px solid #a3a3a3"}}
   [:h3 (str name " (" (count cards) ")")]
   [:div.deck-cards
    {:style
     {:border "1px solid #a3a3a3"
      :width "200px"
      :height "300px"
      :display "flex"
      :justify-content "space-around"
      :align-items "center"}}
    (when (first cards)
      [card-component
       (first cards)
       #(println (str "Clicked: " deck-name " " (:id (first cards))))])]])

(defn stack-component [cards name deck-name]
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
        [card-component card #(println (str "Clicked: " deck-name " " (:id card)))]])]]])

(defn app []
  [:div.my-app {:style {:display "flex" :flex-wrap "wrap"}}
   (if @(r/subscribe [:initialized?])
     (let [splendor-deck @(r/subscribe [:query-deck-id :splendor-deck-id])]
       [:div {:style {:display "flex" :flex-wrap "wrap"}}
        [field-component "Multi Field" splendor-deck :splendor-deck]
        [field-component "Multi Field reversed" (reverse splendor-deck) :splendor-deck]
        [stack-component splendor-deck "Stack" :splendor-deck]
        [stack-component (reverse splendor-deck) "Stack reversed" :splendor-deck]
        [deck-component "Deck" splendor-deck :splendor-deck]
        [deck-component "Deck reversed" (reverse splendor-deck) :splendor-deck]]))])

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
