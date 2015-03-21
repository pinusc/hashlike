(ns clojurelike.entities
  (:require [play-clj.g2d :refer :all]))

(defn create-cowboy
  []
  (assoc (texture "cowboy.png")
    :x 2
    :y 4
    :living? true
    :cowboy? true
    :width 1
    :height 2))

(defn isthereanybody
  [x y entities]
  (some #(and (= x (:x %)) (= y (:y %))) entities))
