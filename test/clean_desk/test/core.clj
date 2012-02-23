(ns clean-desk.test.core
  (:use [clean-desk.core])
  (:use [clojure.test]))

(deftest test-detect-mime
  (is (instance? String (detect-mime (java.io.File. "core.clj"))))
  (is (instance? String (detect-mime "foo.txt"))))
