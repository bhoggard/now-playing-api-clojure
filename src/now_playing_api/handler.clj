(ns now-playing-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.util.response :refer [response]]
            [now-playing-api.feed :as feed]))

(defroutes app-routes
  (context "/api" []
    (GET "/counterstream" [] (response (feed/feed-data :counterstream)))
    (GET "/dronezone" [] (response (feed/feed-data :dronezone)))
    (GET "/earwaves" [] (response (feed/feed-data :earwaves)))
    (GET "/q2" [] (response (feed/feed-data :q2)))
    (GET "/silent-channel" [] (response (feed/feed-data :silent-channel)))
    (GET "/yle" [] (response (feed/feed-data :yle))))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-defaults api-defaults)))
