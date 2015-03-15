(ns clojurelike.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]))
(def blue (color 63/255 124/255 172/255 1))
(def speed 1)

(declare main-screen)

(defn get-direction
  "Returns the direction based on the key pressed at the call moment"
  []
  (cond
    (key-pressed? :dpad-right) :right
    (key-pressed? :dpad-left) :left
    (key-pressed? :dpad-up) :up
    (key-pressed? :dpad-down) :down
    :else nil))

(defn update-screen!
  [screen entities]
  (doseq [{:keys [x y player?]} entities]
    (when player?
      (position! screen x y)
      ;(println "x: " x "; y:" y)
      ))
  entities)

(defn update-position
  "Moves the player by checking get-direction and then modifying x or y"
  [screen {:keys [player? x y] :as entity} direction]
  (if player?
    (let [new-x (case direction
                  :right (+ x speed)
                  :left (- x speed)
                  x)
          new-y (case direction
                  :up (+ y speed)
                  :down (- y speed)
                  y)]
      (when-let [anim (get entity direction)]
        (merge entity
               (get entity direction)
               {:x new-x :y new-y})))
    entity))

(defn move-all
  [screen entities direction]
  (map #(update-position screen % direction) entities))

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
      :x 1
      :y 1
      :width 1
      :height 1)))

(defscreen main-screen
           :on-show
           (fn [screen entities]
             (->> (orthogonal-tiled-map "level.tmx" (/ 1 16))
                  (update! screen :camera (orthographic) :renderer))
             (create-player))

           :on-render
           (fn [screen entities]
             (clear! 63/255 124/255 172/255 1)
             (->> entities
                  (update-screen! screen)
                  (render! screen)))

           :on-key-down
           (fn [screen entities]
             (if-let [dir (get-direction)]
               (move-all screen entities dir)
               entities))

           :on-resize
           (fn [screen entities]
            (height! screen 16)))

(defgame clojurelike
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

;; (on-gl (set-screen! clojurelike main-screen))