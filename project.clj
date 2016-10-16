(defproject now-playing-api "0.1.0"
  :description "Clojure API to retrieve what's playing on my favorite streaming stations"
  :url "https://now-playing-api.herokuapp.com/"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring "1.5.0"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler now-playing-api.handler/app}
  :uberjar-name "now-playing-api-standalone.jar"
  :main ^:skip-aot now-playing-api.handler
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
