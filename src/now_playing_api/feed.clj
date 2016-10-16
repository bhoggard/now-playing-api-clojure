(ns now-playing-api.feed
  (:require [cheshire.core :as json]
            [clojure.xml :as xml]))

(defn translate-counterstream
  "translate parsed JSON into title and composer for Counterstream Radi"
  [data]
  (let [entry (data "TrackInfo")
        title (entry "Track")
        composer (entry "Composer")]
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
    (-> url xml/parse)))

(defn- wrap-feed-errors
  "wrap outside http calls so we can trap them"
  [f]
  (try
    (f)
    (catch Exception e (hash-map :title "" :composer ""))))

(def feeds {
  :counterstream { :url "http://counterstreamradio.net/admin/services.php?q=current_track" :format :json }
  :dronezone { :url "http://api.somafm.com/recent/dronezone.tre.xml" :format :xml }
  :earwaves { :url "http://api.somafm.com/recent/earwaves.tre.xml" :format :xml }
  :q2 { :url "http://www.wqxr.org/api/whats_on/q2/2/" :format :json }
  :silent-channel { :url "http://api.somafm.com/recent/silent.tre.xml" :format :xml }
  :yle { :url "http://yle.fi/radiomanint/LiveXML/r17/item(0).xml" :format :xml }
})

(defn feed-data
  "given the name of a feed, retrieve it, process the data, and return title and composer"
  [feed-name]
  (let [feed-url (get-in feeds [feed-name :url])
        feed-format (get-in feeds [feed-name :format])
        data (feed-to-data feed-url feed-format)
        translation-fn (or (resolve (symbol (str "now-playing-api.feed/translate-" (name feed-name)))) translate-somafm)]
        (translation-fn data)))
