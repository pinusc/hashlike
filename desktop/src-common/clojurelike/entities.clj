(ns clojurelike.entities
  (:require [play-clj.g2d :refer :all]
            [play-clj.core :refer :all]))
(def speed 1)
(def mode :move)
(defn isthereanybody
  [x y entities]
  (some #(and (= x (:x %)) (= y (:y %))) entities))

(defn update-position
  "Moves the player by checking get-direction and then modifying x or y"
  [screen entities {:keys [player? camx camy x y] :as entity} direction]
  (if player?
    (let [new-x (case direction
                  :right (+ x speed)
                  :left (- x speed)
                  x)
          new-y (case direction
                  :up (+ y speed)
                  :down (- y speed)
                  y)
          new-camx (case direction
                     :right (+ camx speed)
                     :left (- camx speed)
                     camx)
          new-camy (case direction
                     :up (+ camy speed)
                     :down (- camy speed)
                     camy)]
      (if (= mode :move)
        (if (and (not (tiled-map-layer! (tiled-map-layer screen "casa") :get-cell new-x new-y))
                 (not (isthereanybody new-x, new-y entities)))
          (when-let [anim (get entity direction)]
            (merge entity
                   anim
                   {:x new-x :y new-y :camx new-x :camy new-y}))
          (merge entity
                 (get entity direction)))
        (merge entity
               {:camx new-camx :camy new-camy})))
    (if (:cowboy? entity)
      (do (let [direction (rand-nth [:down :up :left :right])
                new-x (case direction
                        :right (+ x speed)
                        :left (- x speed)
                        x)
                new-y (case direction
                        :up (+ y speed)
                        :down (- y speed)
                        y)]
            (if (and (not (tiled-map-layer! (tiled-map-layer screen "casa") :get-cell new-x new-y))
                     (not (isthereanybody new-x, new-y entities)))
              (merge entity
                     {:x new-x :y new-y})
              entity)))
      entity)))

(defn move-all
  [screen entities direction]
  (map #(update-position screen entities % direction) entities))

(defn create-player
  []
  (let [right (texture "greenbot.png")
        up (texture "greenbot2.png")
        left (texture "greenbot.png" :flip true false)
        down (texture "greenbot2.png" :flip false true)]
    (assoc (texture "greenbot.png")
      :right right
      :left left
      :up up
      :down down
      :player? true
      :living? true
      :x 1
      :y 1
      :width 1
      :height 1
      :camx 1
      :camy 1)))

(defn create-cowboy
  []
  (assoc (texture "cowboy.png")
    :x 2
    :y 4
    :living? true
    :cowboy? true
    :width 1
    :height 2))