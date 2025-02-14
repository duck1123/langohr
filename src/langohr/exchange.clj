;; Copyright (c) 2011 Michael S. Klishin
;;
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns langohr.exchange
  "Exchanges are the entities to which messages are sent. Exchanges match and distribute messages across queues."
  (:refer-clojure :exclude [declare])
  (:import (com.rabbitmq.client Channel AMQP$Exchange$DeclareOk AMQP$Exchange$DeleteOk AMQP$Exchange$BindOk)))

;;
;; API
;;

(defn ^AMQP$Exchange$DeclareOk declare
  "Declares an exchange using exchange.declare AMQP method.

   By default declares non-autodeleted non-durable exchanges.

   Core exhange types:

    - direct: 1:1 form of communication. Routing key defines how broker will direct message from producer to the consumer.
    - fanout: 1:N message delivery pattern. No routing keys are involved. You bind a queue to exchange and messages sent to that exchange are delivered to all bound queues.
    - topic: used for 1:n and n:m communication. In this case, routing key is defined as a pattern. For example \"langohr.#\" will match \"langohr.samples\" and \"langohr.smamples\" or \"#.samples\" will match \"langor.samples\" and \"shmangor.samples\".

   Usage example:

       (lhe/declare channel exchange \"direct\" :auto-delete false, :durable true)

   Options
     :auto-delete (default: false): If set when creating a new exchange, the exchange will be marked as durable. Durable exchanges remain active when a server restarts. Non-durable exchanges (transient exchanges) are purged if/when a server restarts.
     :durable (default: false): indicates wether the exchange is durable. Information about Durable Exchanges is persisted and restored after server restart. Non-durable (transient) exchanges do not survive the server restart.
     :internal (default: false): If set, the exchange may not be used directly by publishers, but only when bound to other exchanges. Internal exchanges are used to construct wiring that is not visible to applications."
  ([^Channel channel ^String name ^String type]
     (.exchangeDeclare channel name type))
  ([^Channel channel ^String name ^String type &{ :keys [durable auto-delete internal arguments] :or {durable false, auto-delete false, internal false} }]
     (.exchangeDeclare channel name type durable auto-delete internal arguments)))


(defn ^AMQP$Exchange$DeleteOk delete
  "Deletes an exchange using exchange.delete AMQP method. When an exchange is deleted all queue bindings on the exchange are cancelled.

  Options:
    :if-unused If set, the server will only delete the exchange if it has no queue bindings. If the exchange has queue bindings the server does not delete it but raises a channel exception instead.

  Usage example:

     (lhe/delete channel exchange true)

  "
  ([^Channel channel ^String name]
     (.exchangeDelete channel name))
  ([^Channel channel ^String name if-unused]
     (.exchangeDelete channel name if-unused)))


(defn ^AMQP$Exchange$BindOk bind
  "Binds a queue to an exchange using exchange.bind AMQP method (a RabbitMQ-specific extension)

  Options:
    :routing-key (default: \"\"): Specifies the routing key for the binding. The routing key is used for routing messages depending on the exchange configuration. Not all exchanges use a routing key - refer to the specific exchange documentation.
    :arguments (default: nil): A hash of optional arguments with the declaration. Headers exchange type uses these metadata attributes for routing matching. In addition, brokers may implement AMQP extensions using x-prefixed declaration arguments."
  ([^Channel channel ^String destination ^String source]
     (.exchangeBind channel destination source ""))
  ([^Channel channel ^String destination ^String source &{ :keys [routing-key arguments] :or { routing-key "", arguments nil } }]
     (.exchangeBind channel destination source routing-key arguments)))
