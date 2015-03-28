(ns clojurelike.core
  (:require [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.ui :refer :all]
            [clojurelike.entities :as en]))

(declare main-screen)

(defn get-direction
  "Returns the direction based on the key pressed at the call moment"
  []
  (cond
    (key-pressed? :dpad-right) :right
    (key-pressed? :dpad-left) :left
    (key-pressed? :dpad-up) :up
    (key-pressed? :dpad-down) :down
    (key-pressed? :ENTER) :stay
    :else nil))

(defn update-screen!
  [screen entities]
  (doseq [{:keys [camx camy player?]} entities]
    (when player?
      (position! screen camx camy)))
  entities)

(defscreen main-screen
           :on-show
           (fn [screen entities]
             (->> (orthogonal-tiled-map "map.tmx" (/ 1 16))
                  (update! screen :camera (orthographic) :renderer))
             (conj entities (en/create-player) (en/create-cowboy)))

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
               (en/move-all screen entities dir)
               entities))

           :on-resize
           (fn [screen entities]
             (height! screen 16)
             entities))

(defgame clojurelike
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
