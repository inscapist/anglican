(ns embang.importance
  (:use [embang.inference :only [checkpoint print-predicts]]))

;;; Importance samping

;; The simplest sampling algorithm, runs every 
;; particle independently and outputs the predicts
;; along with their weights. Random choices are sampled
;; from conditional prior distributions.

(derive ::importance :embang.inference/algorithm)

(defn exec [prog]
  (loop [step (trampoline prog nil (map->state {:log-weight 0. 
                                                :predicts []
                                                :mem {}}))]
    (let [next (checkpoint step ::importance)]
      (if (fn? next)
        (recur (trampoline next))
        next))))

(defn run-inference [prog & {:keys [n f number-of-samples output-format]
                             :or {number-of-samples -1
                                  output-format :clojure}}]
  (loop [i 0]
    (when-not (= i number-of-samples)
      (print-predicts (exec prog) output-format)
      (recur (inc i)))))
