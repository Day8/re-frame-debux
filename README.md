# re-frame-debux

## Credit

[Debux](https://github.com/philoskim/debux) is a simple but useful library for debugging Clojure and ClojureScript. *re-frame-debux* is a fork of Debux, that repurposes it for tracing re-frame event and subscription handlers, and integration with link:https://github.com/Day8/re-frame-10x[re-frame-10x] and re-frame's tracing API.

Longer term, we would like to investigate merging back into mainline Debux, but the changes we needed to make required quite deep surgery, so in the interests of time, we started off with this fork.

## Prerequisites

* clojure 1.8.0 or later
* clojurescript 1.9.854 or later


## Installation

To include *re-frame-debux* in your project, simply add the following to your *project.clj*
dependencies:


```
[day8.re-frame/debux "0.5.0"] ;; not released yet
[day8.re-frame/tracing "0.5.0] ;; not released yet
```

Add Closure defines to your config to enable re-frame tracing + the function tracing:

```
:cljsbuild    {:builds {:client {:compiler {:closure-defines {"re_frame.trace.trace_enabled_QMARK_" true
                                                              "debux.cs.core.trace_enabled_QMARK_"  true}}}}}}
```

## How to use

re-frame-debux provides two macros for you to use to trace your re-frame event and subscription handlers:

* traced-fn
* traced-defn

traced-fn and traced-defn replace fn and defn respectively.

Both have zero runtime cost in production builds, and are able to be turned off at dev time too via the Closure define, so you can leave the `traced-fn` and `traced-defn` macros in your code at all times.


## License
Copyright © 2015--2018 Young Tae Kim, 2018 Day8 Technologies

Distributed under the Eclipse Public License either version 1.0 or any later version.
