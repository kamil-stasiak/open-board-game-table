(ns bgt.cards)

(def base-card
  {:active :back
   :selected? false
   :front
   {:img "https://upload.wikimedia.org/wikipedia/en/3/3b/Pokemon_Trading_Card_Game_cardback.jpg"}
   :back
   {:img "http://abload.de/img/cardyback1fprd.jpg"}})

(def splendor-1
  {:active :back
   :selected? false
   :front
   {:img "https://upload.wikimedia.org/wikipedia/en/3/3b/Pokemon_Trading_Card_Game_cardback.jpg"}
   :back
   {:img "http://abload.de/img/cardgbacktqjy4.jpg"}})

(def splendor-2
  {:active :back
   :selected? false
   :front
   {:img "https://upload.wikimedia.org/wikipedia/en/3/3b/Pokemon_Trading_Card_Game_cardback.jpg"}
   :back
   {:img "http://abload.de/img/cardyback1fprd.jpg"}})

(def splendor-3
  {:active :back
   :selected? false
   :front
   {:img "https://upload.wikimedia.org/wikipedia/en/3/3b/Pokemon_Trading_Card_Game_cardback.jpg"}
   :back
   {:img "http://abload.de/img/cardbback4tuqz.jpg"}})

(def splendor-deck
  (list splendor-1 splendor-2 splendor-3))
