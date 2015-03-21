(ns clojurelike.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [clojurelike.entities :as en]))
(def speed 1)
(def mode :move)

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
  (doseq [{:keys [camx camy player?]} entities]
    (when player?
      (position! screen camx camy)))
  entities)

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
                 (not (en/isthereanybody new-x, new-y entities)))
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
            (if (not (tiled-map-layer! (tiled-map-layer screen "casa") :get-cell new-x new-y))
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

(defscreen main-screen
           :on-show
           (fn [screen entities]
             (->> (orthogonal-tiled-map "map.tmx" (/ 1 16))
                  (update! screen :camera (orthographic) :renderer))
             (conj entities (create-player) (en/create-cowboy)))

           :on-render
           (fn [screen entities]
             (clear! 63/255 124/255 172/255 1)
             (->> entities
                  (update-screen! screen)
                  (render! screen)))

           :on-key-down
           (fn [screen entities]
             (cond
               (key-pressed? :NUM_0) (.setVisible (tiled-map-layer screen 1) false)
               (key-pressed? :NUM_1) (.setVisible (tiled-map-layer screen 1) true)
               (key-pressed? :NUM_5) (def mode (if (= mode :move) :map :move)))
             (if-let [dir (get-direction)]
               (move-all screen entities dir)
               entities))

           :on-resize
           (fn [screen entities]
             (height! screen 16)
             entities))

(defgame clojurelike
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
