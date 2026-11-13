#lang racket

(define (calcular-interes deposito)
  (cond
    [(<= deposito 40000) (* deposito 0.035)]
    [(<= deposito 260000) (* deposito 0.04)]
    [else (* deposito 0.045)]))

(displayln (calcular-interes 20000))
(displayln (calcular-interes 50000))
(displayln (calcular-interes 300000))
