package com.example.order.util;

import com.example.order.entity.SendMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Queue;

@Component
public class SendMessageUtil {
    //    public static SendMessage sendMessage;
    public static Queue<SendMessage> sendMessageQueue = new ArrayDeque<>();

    public static void setSendMessageQueue(SendMessage sendMessage){
        SendMessageUtil.sendMessageQueue.add(sendMessage);
    }

    public static SendMessage getSendMessage(){
        return SendMessageUtil.sendMessageQueue.poll();
    }
}
