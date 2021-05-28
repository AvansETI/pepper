package com.pepper.care.core.services.robot

import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder
import com.pepper.care.R

class PepperRobot(
    val callback: PepperActionCallback
): RobotLifecycleCallbacks {


    private val resourceIds: IntArray = intArrayOf(R.raw.greet, R.raw.dialog, R.raw.order)
    private val conceptHashMap: HashMap<DynamicConcepts, EditablePhraseSet?> = HashMap()

    private lateinit var future: Future<Void>
    private lateinit var context: QiContext
    private lateinit var chatBot: QiChatbot
    private lateinit var chat: Chat

    override fun onRobotFocusGained(qiContext: QiContext?) {
        Log.d(PepperRobot::class.simpleName, "onRobotFocusGained")
        context = qiContext!!

        /* App is focused */
        runChat()
    }

    override fun onRobotFocusLost() {
        Log.d(PepperRobot::class.simpleName, "onRobotFocusLost")

        /* Removing listeners */
        removeChatListeners()
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.d(PepperRobot::class.simpleName, "Robot is not available: $reason")
    }

    private fun runChat() {
        chatBot = createQiBot()
        setExecutor()
        chat = ChatBuilder.with(context)
            .withChatbot(chatBot)
            .build()
        setDynamicConcepts()
        setChatListeners()
        future = chat.async().run()
        setFutureListeners()
    }

    private fun createQiBot(): QiChatbot {
        return QiChatbotBuilder.with(context)
            .withTopics(getTopics())
            .build();
    }

    private fun getTopics(): MutableList<out Topic> {
        val topics: MutableList<Topic> = ArrayList()
        resourceIds.forEach { resId ->
            topics.add(getTopic(resId))
        }
        return topics
    }

    private fun getTopic(resId: Int): Topic {
        return TopicBuilder.with(context).withResource(resId).build()
    }

    private fun setChatListeners() {
        chat.addOnStartedListener {
            Log.d(PepperRobot::class.simpleName, "Chat started...")
        }

        chat.addOnListeningChangedListener {
            Log.i(PepperRobot::class.simpleName, "Listening changed: $it")
        }

        chat.addOnSayingChangedListener { robotOutput ->
            if (!robotOutput.text.isNullOrBlank()) {
                Log.d(PepperRobot::class.simpleName, "Robot: ${robotOutput.text}")
            }
        }

        chat.addOnHeardListener { humanInput ->
            Log.d(PepperRobot::class.simpleName, "Human: ${humanInput.text}")
            if (humanInput.text.contains("test")) callback.onRobotAction(PepperAction.MOVE_TO_INTRO)
        }

        chat.addOnNoPhraseRecognizedListener {
            Log.d(PepperRobot::class.simpleName, "Phrase recognized...")
        }

        chat.addOnNormalReplyFoundForListener { phrase ->
            Log.d(PepperRobot::class.simpleName, "Normal reply: ${phrase.text}")
        }

        chat.addOnFallbackReplyFoundForListener { phrase ->
            Log.d(PepperRobot::class.simpleName, "Fallback reply: ${phrase.text}")
        }

        chat.addOnNoReplyFoundForListener { phrase ->
            Log.d(PepperRobot::class.simpleName, "No reply: ${phrase.text}")
        }
    }

    private fun setExecutor() {
        val executors: HashMap<String, QiChatExecutor> = HashMap()
        chatBot.executors = executors
    }

    private fun setFutureListeners(){
        future.thenConsume { chatFuture ->
            if (chatFuture.hasError()) {
                Log.e(PepperRobot::class.simpleName,"Discussion finished with error.", future.error)
            }
        }
    }

    private fun setDynamicConcepts() {
        DynamicConcepts.values().forEach { concept ->
            conceptHashMap[concept] = chatBot.dynamicConcept(concept.name)
        }
    }

    fun addContents(concept: DynamicConcepts, list: List<Phrase>) {
        conceptHashMap[concept]?.async()?.addPhrases(list)
    }

    private fun removeChatListeners() {
        chat.removeAllOnStartedListeners()
        chat.removeAllOnListeningChangedListeners()
        chat.removeAllOnSayingChangedListeners()
        chat.removeAllOnHeardListeners()
        chat.removeAllOnNoPhraseRecognizedListeners()
        chat.removeAllOnNormalReplyFoundForListeners()
        chat.removeAllOnFallbackReplyFoundForListeners()
        chat.removeAllOnNoReplyFoundForListeners()
    }
}

enum class DynamicConcepts(name: String) {
    MEALS("meals")
}