package com.chat.toktalk.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String QUEUE_NAME ="fanout_queue";
    public static final String EXCHANGE_NAME="fanout_exchange";
    @Bean
    Queue messageQueue(){
        return QueueBuilder.durable(QUEUE_NAME).build() ;
    }
    @Bean
    FanoutExchange fanoutExchange(){
        return new FanoutExchange(EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding binding(Queue messageQueue,FanoutExchange fanoutExchange){
         return BindingBuilder.bind(messageQueue).to(fanoutExchange);
    }

    //TODO
    //Delay Q, Dead Message Q
    //Queue_NAME은 Server이름 마다 다르게 처리.

    /*@Bean
    SimpleMessageListenerContainer fanoutContainer(ConnectionFactory connectionFactory,
                                                   MessageListenerAdapter fanoutListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(new String[]{QUEUE_NAME1,QUEUE_NAME2});
//        container.setQueueNames(QUEUE_NAME2);
        container.setMessageListener(fanoutListenerAdapter);
        return container;
    }*/
}
