(ns day8.re-frame.debux.runner
  (:require [clojure.test :refer [deftest run-all-tests is]]
            [day8.re-frame.debux.common.util-test]))

(run-all-tests #"day8.*")