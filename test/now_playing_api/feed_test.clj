(ns now-playing-api.feed-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer :all]
            [clojure.xml :as xml]
            [now-playing-api.feed :refer :all]))

(deftest test-feed
  (testing "earwaves parsing"
    (let [data (translate-somafm (xml/parse "test/data/earwaves.xml"))]
      (is (= "Feldman: Rothko Chapel 2" (:title data)))
      (is (= "William Winant, Deborah Dietrich, Karen Rosenak, David Abel" (:composer data)))))
  (testing "q2 parsing"
    (let [data (translate-q2 (parse-string (slurp "test/data/q2.json")))]
      (is (= "Blue Velvet: Mysteries of Love" (:title data)))
      (is (= "Angelo Badalamenti" (:composer data)))))
  (testing "yle parsing"
    (let [data (translate-yle (xml/parse "test/data/yle.xml"))]
      (is (= "Sinfonia nro 1 c-molli " (:title data)))
      (is (= "Brahms, Johannes [1833-1897]" (:composer data))))))




