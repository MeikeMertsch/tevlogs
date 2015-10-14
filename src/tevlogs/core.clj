(ns tevlogs.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [cheshire.core :as json])
  (:gen-class))

(defn parse-line [line]
  (-> line
      (string/split #" : ")
      last
      (json/parse-string true)))

(defn read-lines [file-name]
  (with-open [rdr (io/reader file-name)]
    (map parse-line (line-seq rdr))))

(def example "I, [2015-10-14T09:53:22.248366 #18826]  INFO -- : {\"toilet_name\": \"male-left\", \"time\": \"2015-10-14T09:53:22.229182\", \"door_closed\": true, \"playing\": true, \"song\": \"/home/pi/Rugrats Theme Song.mp3\"}")

(parse-line example)
(read-lines "/Users/djastin/Downloads/events-state.log")


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
