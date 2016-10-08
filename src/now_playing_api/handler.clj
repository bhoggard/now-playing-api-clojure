(ns now-playing-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.util.response :refer [response]]
            [now-playing-api.feed :as feed]))

(defroutes app-routes
  (context "/api" []
    (GET "/q2" [] (response (feed/q2))))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-defaults api-defaults)))
