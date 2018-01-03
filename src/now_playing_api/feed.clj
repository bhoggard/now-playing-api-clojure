(ns now-playing-api.feed
  (:require [cheshire.core :as json]
            [clojure.xml :as xml]))

(defn counterstream-data
  "special case of simple text processing for Counterstream Radio"
  []
  (let [data (slurp "http://counterstream.newmusicusa.org:8000/currentsong?sid=1")
        items (clojure.string/split data, #" - ")
        title (second items)
        composer (first items)]
    (hash-map :title title :composer composer)))

(defn translate-somafm
  "translate parsed XML into title and composer/creator for SomaFM feeds"
  [data]
  (let [entry (data :content)
        title (-> (filter #(= (:tag %) :title) entry) first :content first)
        composer (-> (filter #(= (:tag %) :artist) entry) first :content first)]
    (hash-map :title title :composer composer)))

(defn translate-q2
  "translate parsed JSON into title and composer for Q2 Music"
  [data]
  (let [entry ((data "current_playlist_item") "catalog_entry")
        title (entry "title")
        composer ((entry "composer") "name")]
    (hash-map :title title :composer composer)))

(defn translate-yle
  "translate parsed XML into title and composer for YLE Klassinen"
  [data]
  (let [entry (-> data :content first)
        title (-> entry :attrs :TITLE)
        personnel (-> entry :content first :content)
        composer-info (first (filter #(= (-> % first :content) ["COMPOSER"]) (map :content personnel)))
        composer (-> composer-info last :content first)]
    (hash-map :title title :composer composer)))

(defn feed-to-data
  "given a url and a format (:json or :xml), retrieve and parse a feed"
  [url feed-format]
  (if (= feed-format :json)
    (-> url slurp json/parse-string)
    (xml/parse url)))

(def feeds {
            :dronezone { :url "http://api.somafm.com/recent/dronezone.tre.xml" :format :xml}
            :earwaves { :url "http://api.somafm.com/recent/earwaves.tre.xml" :format :xml}
            :q2 { :url "https://api.wnyc.org/api/v1/whats_on/q2/" :format :json}
            :silent-channel { :url "http://api.somafm.com/recent/silent.tre.xml" :format :xml}
            :yle { :url "https://yle.fi/radiomanint/LiveXML/r17/item(0).xml" :format :xml}})

(defn feed-data
  "given the name of a feed, retrieve it, process the data, and return title and composer"
  [feed-name]
  (let [feed-url (get-in feeds [feed-name :url])
        feed-format (get-in feeds [feed-name :format])
        data (feed-to-data feed-url feed-format)
        translation-fn (or (resolve (symbol (str "now-playing-api.feed/translate-" (name feed-name)))) translate-somafm)]
    (translation-fn data)))
