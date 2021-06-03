package com.pepper.care.core.services.robot

import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.actuation.FreeFrame
import com.aldebaran.qi.sdk.`object`.actuation.GoTo
import com.aldebaran.qi.sdk.`object`.actuation.Mapping
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.builder.*
import com.pepper.care.R

class PepperRobot(
    val callback: PepperActionCallback
): RobotLifecycleCallbacks {

    private val resourceIds: IntArray = intArrayOf(R.raw.main, R.raw.dialog)
    private val conceptHashMap: HashMap<DynamicConcepts, EditablePhraseSet?> = HashMap()
    private val savedLocations: HashMap<String, FreeFrame> = HashMap()

    private lateinit var goTo: GoTo

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
        goTo.removeAllOnStartedListeners()
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.d(PepperRobot::class.simpleName, "Robot is not available: $reason")
    }

    fun saveLocation(location: String){
        // Get the Actuation service from the QiContext.
        val actuation = context.actuation

        // Get the robot frame.
        val robotFrameFuture = actuation?.async()?.robotFrame()
        robotFrameFuture?.andThenConsume {
            // Get the Mapping service from the QiContext.
            val mapping = context.mapping

            // Create a FreeFrame with the Mapping service.
            val locationFrame = mapping.makeFreeFrame()

            // Create a transform corresponding to a 1 meter forward translation.
            val transform = TransformBuilder.create().fromXTranslation(1.0)
            locationFrame.update(it, transform, 0L)

            // Store the FreeFrame.
            if (locationFrame != null)
                savedLocations[location] = locationFrame
        }
    }

    fun goToLocation(location: String){
        // Get the FreeFrame from the saved locations.
        val freeFrame = savedLocations[location] ?: return

        // Extract the Frame asynchronously.
        val frameFuture = freeFrame.async()?.frame()
        frameFuture?.andThenCompose { frame ->
            // Create a GoTo action.
            val goTo = GoToBuilder.with(context) // Create the builder with the QiContext.
                .withFrame(frame) // Set the target frame.
                .build() // Build the GoTo action.
                .also { this.goTo = it }

            this.goTo = goTo

            // Execute the GoTo action asynchronously.
            goTo.async().run().thenConsume {
                if (it.isSuccess) {
                    Log.i(PepperRobot::class.simpleName, "Location reached: $location")
                } else if (it.hasError()) {
                    Log.e(PepperRobot::class.simpleName, "Go to location error", it.error)
                }
            }
        }
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
            .build()
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
        executors["navigateScreen"] = PepperQiChatExecutor(context, callback)
        executors["navigateChoice"] = PepperQiChatExecutor(context, callback)
        executors["selectPatientBirthday"] = PepperQiChatExecutor(context, callback)
        executors["selectPatientName"] = PepperQiChatExecutor(context, callback)
        executors["selectMealItem"] = PepperQiChatExecutor(context, callback)
        executors["selectFeedbackNumber"] = PepperQiChatExecutor(context, callback)
        executors["inputFeedbackExplain"] = PepperQiChatExecutor(context, callback)
        executors["inputQuestionExplain"] = PepperQiChatExecutor(context, callback)
        executors["confirmDialogSelect"] = PepperQiChatExecutor(context, callback)
        chatBot.executors = executors
    }

    private fun setFutureListeners(){
        future.thenConsume { chatFuture ->
            if (chatFuture.hasError()) {
                Log.e(PepperRobot::class.simpleName,"Discussion finished with error: ${chatFuture.error}")
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
    MEALS("meals"),
    NAME("name"),
    FAV("fav"),
    REMINDERS("reminders"),
    QUESTIONS("questions"),
    MEAL("meal"),
    ANSWERED("answered"),
    NUMBER("number"),
    EXPLAIN("explain")
}