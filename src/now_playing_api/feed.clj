(ns now-playing-api.feed)

(def counterstream-url "http://counterstreamradio.net/admin/services.php?q=current_track")
(def earwaves-url "http://api.somafm.com/recent/earwaves.tre.xml")
(def dronezone-url "http://api.somafm.com/recent/dronezone.tre.xml")
(def q2-url "http://www.wqxr.org/api/whats_on/q2/2/")
(def silent-channel-url "http://api.somafm.com/recent/silent.tre.xml")
(def yle-url "http://yle.fi/radiomanint/LiveXML/r17/item(0).xml")

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

(defn q2 [] {:title "Asyla" :composer "Thomas Ades"})
