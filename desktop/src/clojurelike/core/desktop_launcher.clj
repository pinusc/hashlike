(ns clojurelike.core.desktop-launcher
  (:require [clojurelike.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. clojurelike "clojurelike" 800 600)
  (Keyboard/enableRepeatEvents true))
