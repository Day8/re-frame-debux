(ns debux.dbgn-test
  (:require [clojure.test :refer :all]
            [debux.common.util :as ut]
            [debux.dbgn :as dbgn :refer [dbgn mini-dbgn]]))

(def traces (atom []))
(def form (atom nil))

(use-fixtures :each (fn [f]
                      (with-redefs [ut/send-trace! (fn [code-trace] (swap! traces conj (update code-trace :form ut/tidy-macroexpanded-form {})))
                                    ut/send-form!  (fn [traced-form] (reset! form (ut/tidy-macroexpanded-form traced-form {})))]
                        (f)
                        (reset! traces [])
                        (reset! form nil))))

;; Works in Cursive, fails with lein test
;; See https://github.com/technomancy/leiningen/issues/912
#_(deftest skip-outer-skip-inner-test
  (is (= (macroexpand-1 '(mini-dbgn
                           (-> '())))
         '(do
            (debux.common.util/spy-first
              (quote ())
              (quote (quote ())))))))


;; Commented out as we no longer print the traces, we need to get the traced data instead.
(deftest ->-test
  (is (= (dbgn (-> '())) '()))
  (is (= [{:form '(quote ()), :indent-level 0, :result ()}]
         @traces))
  (is (= '(-> (quote ()))
         @form)))

(deftest ->-test2
  (is (= (dbgn (-> {} 
                   (assoc :a 1) 
                   (get :a (identity :missing)))) 1))
  (is (= [{:form {}, :indent-level 2, :result {}}
          {:form '(assoc :a 1), :indent-level 1, :result {:a 1}}
          {:form '(identity :missing), :indent-level 1, :result :missing}
          {:form '(get :a (identity :missing)), :indent-level 0, :result 1}]
         @traces))
  (is (= '(-> {} 
              (assoc :a 1) 
              (get :a (identity :missing)))
         @form)))

;; Failing test raises an error
(deftest ^:failing cond->>-test
  (is (= (with-out-str (dbgn (cond->> 1 true inc false (+ 2) (= 2 2) (* 45) :always (+ 6))))
         "\ndbgn: (cond->> 1 true inc false (+ 2) (= 2 2) (* 45) :always (+ 6)) =>\n| 1 =>\n|   1\n| true =>\n|   true\n| inc =>\n|   2\n| false =>\n|   false\n| (= 2 2) =>\n|   true\n| (* 45) =>\n|   90\n| :always =>\n|   :always\n| (+ 6) =>\n|   96\n")))

;; TODO: fix this. Failing test raises an error
(deftest ^:failing condp-test
  (is (= (dbgn (condp some [1 2 3 4]
                 #{0 6 7} :>> inc
                 #{4 5 9} :>> dec
                 #{1 2 3} :>> #(+ % 3)))
         3))
  (is (= (dbgn (condp = 3
                 1 "one"
                 2 "two"
                 3 "three"
                 (str "unexpected value, \"" 3 \")))
         "three"))
  (is (= (dbgn (condp = 4
                 1 "one"
                 2 "two"
                 3 "three"
                 (str "unexpected value, \"" 4 \")))
         "unexpected value, \"4\""))
  (is (= (dbgn (condp = 3
                 1 "one"
                 2 "two"
                 3 "three"))
         "three")))

(deftest thread-first-test
    (is
      (= (macroexpand-1 '(debux.dbgn/mini-dbgn
                           (-> {:a 1}
                               (assoc :a 3))))
         '(do
           (debux.common.util/spy-first
            (assoc
             (debux.common.util/spy-first {:a 1} (quote {:a 1}) 1)
             :a
             3)
            (quote (assoc :a 3))
            0))))
          ; Old result
          ; #_'(clojure.core/let
          ;   []
          ;   (debux.dbgn/d
          ;     (debux.common.util/spy-first
          ;       (debux.dbgn/d
          ;         (assoc
          ;           (debux.dbgn/d
          ;             (debux.common.util/spy-first
          ;               (debux.dbgn/d
          ;                 {:a 1})
          ;               (quote
          ;                 {:a 1})
          ;               {}))
          ;           :a
          ;           3))
          ;       (quote
          ;         (assoc
          ;           :a
          ;           3))
          ;       {})))))



    (is
      (= (macroexpand-1 '(debux.dbgn/mini-dbgn
                           (-> {:a 1}
                               (assoc :a 3)
                               frequencies)))
         '(do
           (debux.common.util/spy-first
            (frequencies
             (debux.common.util/spy-first
              (assoc (debux.common.util/spy-first {:a 1} (quote {:a 1}) 2) :a 3)
              (quote (assoc :a 3))
              1))
            (quote frequencies)
            0))))
          ;  Old result
        ; '(clojure.core/let
        ;     []
        ;     (debux.dbgn/d
        ;       (debux.common.util/spy-first
        ;         (debux.dbgn/d
        ;           (frequencies
        ;             (debux.dbgn/d
        ;               (debux.common.util/spy-first
        ;                 (debux.dbgn/d
        ;                   (assoc
        ;                     (debux.dbgn/d
        ;                       (debux.common.util/spy-first
        ;                         (debux.dbgn/d
        ;                           {:a 1})
        ;                         (quote
        ;                           {:a 1})
        ;                         {}))
        ;                     :a
        ;                     3))
        ;                 (quote
        ;                   (assoc
        ;                     :a
        ;                     3))
        ;                 {}))))
        ;         (quote
        ;           frequencies)
        ;         {})))))
    )
