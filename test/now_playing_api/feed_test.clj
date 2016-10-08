(ns now-playing-api.feed-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer :all]
            [clojure.xml :as xml]
            [now-playing-api.feed :refer :all]))

(deftest test-feed
  (testing "counterstream parsing"
    (let [data (translate-counterstream (parse-string (slurp "test/data/counterstream.json")))]
      (is (= "Walk Through Resonant Landscape No 5.1" (:title data)))
      (is (= "Frances White" (:composer data)))))
  (testing "earwaves parsing"
    (let [data (translate-earwaves (xml/parse "test/data/earwaves.xml"))]
      (is (= "Feldman: Rothko Chapel 2" (:title data)))
      (is (= "William Winant, Deborah Dietrich, Karen Rosenak, David Abel" (:composer data)))))
  (testing "q2 parsing"
    (let [data (translate-q2 (parse-string (slurp "test/data/q2.json")))]
      (is (= "Jellyfish" (:title data)))
      (is (= "Kristin Kuster" (:composer data)))))
)



