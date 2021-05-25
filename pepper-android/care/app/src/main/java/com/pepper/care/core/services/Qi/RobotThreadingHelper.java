package com.pepper.care.core.services.Qi;

import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.object.conversation.Chat;

/************
 * Code from
 *      https://github.com/bas200o/AvansTI1.4-B4-Pepper/blob/Master/app/src/main/java/com/b4/pepper/model/ThreadingHelper.java
 */

public class RobotThreadingHelper {
    private static Future chatFuture;
    private static Chat chat;

    public static Future getChatFuture() {
        return chatFuture;
    }

    public static void setChatFuture(Future chatFuture) {
        RobotThreadingHelper.chatFuture = chatFuture;
    }

    public static Chat getChat() {
        return chat;
    }

    public static void setChat(Chat chat) {
        RobotThreadingHelper.chat = chat;
    }

    public static void runOffMainThreadSynchronous(Runnable toRun){

        Thread offMainThread = new Thread(toRun);
        offMainThread.start();
        try {
            offMainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void stopChat() throws NullPointerException {

        RobotThreadingHelper.runOffMainThreadSynchronous(() -> {
            if (chat == null) {
                Log.d("Stopping chat",  "chat is null");
            }
            if (chat != null && chatFuture != null){
                Log.d("Stopping chat",  "stopping...");
                chatFuture.cancel(true);
                chatFuture.requestCancellation();
                while (!chat.getSaying().getText().equals("")) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
