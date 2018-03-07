(ns debux.cs.core
  #?(:cljs (:require-macros
             [debux.dbgn :as dbgn]
             [debux.cs.macro-types :as mt]))
  (:require [debux.common.util :as ut]
            [debux.cs.util :as cs.ut]))

#?(:cljs (enable-console-print!))

(def reset-indent-level! ut/reset-indent-level!)
(def set-print-seq-length! ut/set-print-seq-length!)


;;; debugging APIs
(defmacro dbgn [form & opts]
  (let [opts' (ut/parse-opts opts)]
    `(debux.dbgn/dbgn ~form ~opts')))

;;; macro registering APIs
(defmacro register-macros! [macro-type symbols]
  `(debux.cs.macro-types/register-macros! ~macro-type ~symbols))

(defmacro show-macros
  ([] `(debux.cs.macro-types/show-macros))
  ([macro-type] `(debux.cs.macro-types/show-macros ~macro-type)))


;;; style option API
(def merge-styles cs.ut/merge-styles)

