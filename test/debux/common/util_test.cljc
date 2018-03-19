(ns debux.common.util-test
  (:require [clojure.test :refer :all]
            [debux.common.util :as ut]))

(deftest with-gensyms-names-test
  (testing "auto gensym patterns"
    (is (= (vals (ut/with-gensyms-names `(let [a# 1]
                                           a#)
                                        {}))
           ["a#"]))
    (is (= (vals (ut/with-gensyms-names `(let [a# 1
                                               b# 2]
                                           b#)
                                        {}))
           ["a#" "b#"])))
  (testing "anon gensym patterns"
    (is (= (vals (ut/with-gensyms-names (gensym) {}))
           ["gensym#"])))
  (testing "named gensym patterns"
    (is (= (vals (ut/with-gensyms-names (gensym "abc") {}))
           ["abc#"])))
  (testing "anon param pattern"
    (is (= (vals (ut/with-gensyms-names '#(identity %1) {}))
           ["%1"])))
  (testing "anon param pattern"
    (is (= (vals (ut/with-gensyms-names '#(%1 %2) {}))
           ["%1" "%2"])))

  )

(deftest tidy-macroexpanded-form-test
  (is (= (ut/tidy-macroexpanded-form `(let [a# 1]
                                        a#)
                                     {})
         '(let [a# 1]
            a#)))
  (is (= (ut/tidy-macroexpanded-form '#(let [a (gensym)
                                             b %2]
                                         (gensym "def"))
                                     {})
         '(fn* [%1 %2]
            (let [a (gensym)
                  b %2]
              (gensym "def")))))

  (is (= (ut/tidy-macroexpanded-form '#(inc %)
                                     {})
         '(fn* [%1] (inc %1))))
  (is (= (ut/tidy-macroexpanded-form '#(inc %1)
                                     {})
         '(fn* [%1] (inc %1))))
  )
