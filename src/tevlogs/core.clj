(ns tevlogs.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [cheshire.core :as json]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [camel-snake-kebab.core :refer [->kebab-case]])
  (:gen-class))

(def datetime-formatter (f/formatters :date-hour-minute-second-fraction))
(def log-file "resources/events-state.log")


(defn parse-line [line]
  (try
    (->> (-> line
             (string/split #" : ")
             last
             (json/parse-string true)
             (update :time #(f/parse datetime-formatter %)))
         (mapcat (fn [[k v]] [(->kebab-case k) v]))
         (apply hash-map))

    (catch Exception e
      (println (format "%s skipping line: %s" e line)))))


(defn read-lines [file-name]
  (let [rdr (io/reader file-name)]
    (->> (line-seq rdr)
         (drop 1)
         (map parse-line))))

(def example "I, [2015-10-14T09:53:22.248366 #18826]  INFO -- : {\"toilet_name\": \"male-left\", \"time\": \"2015-10-14T09:53:22.229182\", \"door_closed\": true, \"playing\": true, \"song\": \"/home/pi/Rugrats Theme Song.mp3\"}")

(parse-line example)

(defn distribution-by-weekday [lines]
  (->> lines
       (group-by (fn [{:keys [time]}] (t/day-of-week time)))
       (mapcat (fn [[day events]] [day (count events)]))
       (apply hash-map)))

(defn ->visits [events]
  (->> events
       (drop-while (comp not :door-closed))
       (partition-by :door-closed)
       (partition-all 2)
       (map flatten)))

(defn process [lines]
  (->> lines
       (group-by :toilet-name)
       (mapcat (fn [[toilet-name events]] [toilet-name (->visits events)]))
       (apply hash-map)))


(->> (read-lines log-file)
     (take 50)
     x
     (#(get % "male-left"))
     (drop-while (comp not :door-closed))
     (partition-by :door-closed)
     (partition-all 2)
     first
     flatten)

(-> (->> (read-lines log-file)
             (take 50)
             process)
    (get "male-left")
    first)


(distribution-by-weekday (read-lines log-file))

(f/show-formatters)

(let [datetime (f/parse datetime-formatter "2015-10-14T09:53:22.229182")]
  (t/day-of-week datetime))



