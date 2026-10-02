package com.gdmie

/**
 * 🌌 GDMIE COSMOS LOGIC V8
 *
 * LOGIC FIRST
 *
 * COSMOS
 *   ↓
 * WORLD
 *   ↓
 * EVENT
 *   ↓
 * OBSERVE
 *   ↓
 * QUESTION
 *   ↓
 * A / B / C DECISION
 *   ↓
 * NEW INFORMATION
 *   ↓
 * ADAPT
 *   ↓
 * GDMIE
 *   ↓
 * SIGNAL
 *   ↓
 * XP
 *   ↓
 * LEARNING
 */
object CosmosLogic {

    enum class World {
        ENERGY,
        SPACE,
        EARTH,
        TIME
    }

    enum class DecisionPhase {
        EVENT,
        QUESTION,
        DECISION,
        CONSEQUENCE,
        ANALYSIS,
        COMPLETE
    }

    data class WorldState(
        val world: World,
        val title: String,
        val event: String,
        val observation: String,
        val question: String,
        val options: List<String>,
        val presentValue: Double,
        val expectedValue: Double,
        val targetValue: Double,
        val recentMomentum: Double,
        val immediateMomentum: Double,
        val contextSignal: Double,
        val exactMarketLine: Double,
        val oddsMovement: Double,
        val timingFactor: Double,
        val riskFactor: Double
    )

    data class Decision(
        val world: World,
        val optionIndex: Int,
        val answer: String
    )

    data class Consequence(
        val world: World,
        val selectedOption: String,
        val newInformation: String,
        val outcome: String,
        val phase: DecisionPhase
    )

    data class Session(
        val state: WorldState,
        val decision: Decision? = null,
        val consequence: Consequence? = null,
        val phase: DecisionPhase = DecisionPhase.EVENT
    )

    /**
     * 🌍 Create the initial playable state.
     */
    fun createWorldState(
        world: World
    ): WorldState =
        when (world) {

            World.ENERGY ->
                WorldState(
                    world = world,
                    title = "☀️ ENERGY EVENT",
                    event =
                        "Energy output suddenly changes.",
                    observation =
                        "The output is falling while the surrounding conditions remain active.",
                    question =
                        "What would you examine first?",
                    options =
                        listOf(
                            "A  •  Check the current output",
                            "B  •  Compare the recent change",
                            "C  •  Examine the external condition"
                        ),
                    presentValue = 42.0,
                    expectedValue = 55.0,
                    targetValue = 60.0,
                    recentMomentum = 35.0,
                    immediateMomentum = 25.0,
                    contextSignal = 10.0,
                    exactMarketLine = 50.0,
                    oddsMovement = 5.0,
                    timingFactor = 8.0,
                    riskFactor = 25.0
                )

            World.SPACE ->
                WorldState(
                    world = world,
                    title = "🪐 SPACE EVENT",
                    event =
                        "A distant object changes its trajectory.",
                    observation =
                        "The movement is different from the expected path.",
                    question =
                        "What information matters most now?",
                    options =
                        listOf(
                            "A  •  Track the present position",
                            "B  •  Compare the expected path",
                            "C  •  Check the change in movement"
                        ),
                    presentValue = 48.0,
                    expectedValue = 62.0,
                    targetValue = 70.0,
                    recentMomentum = 45.0,
                    immediateMomentum = 30.0,
                    contextSignal = 15.0,
                    exactMarketLine = 58.0,
                    oddsMovement = 7.0,
                    timingFactor = 10.0,
                    riskFactor = 20.0
                )

            World.EARTH ->
                WorldState(
                    world = world,
                    title = "🌍 EARTH EVENT",
                    event =
                        "A local environmental condition begins changing.",
                    observation =
                        "Temperature, wind and surface conditions are moving away from the normal pattern.",
                    question =
                        "What would you evaluate first?",
                    options =
                        listOf(
                            "A  •  Measure the present condition",
                            "B  •  Compare it with the expected state",
                            "C  •  Look for the strongest recent change"
                        ),
                    presentValue = 50.0,
                    expectedValue = 58.0,
                    targetValue = 65.0,
                    recentMomentum = 40.0,
                    immediateMomentum = 28.0,
                    contextSignal = 12.0,
                    exactMarketLine = 55.0,
                    oddsMovement = 4.0,
                    timingFactor = 7.0,
                    riskFactor = 30.0
                )

            World.TIME ->
                WorldState(
                    world = world,
                    title = "⏳ TIME EVENT",
                    event =
                        "A possible future branch appears.",
                    observation =
                        "One decision now could produce different outcomes later.",
                    question =
                        "What would you focus on before deciding?",
                    options =
                        listOf(
                            "A  •  Understand the present",
                            "B  •  Estimate the expected outcome",
                            "C  •  Examine the risk of the next move"
                        ),
                    presentValue = 45.0,
                    expectedValue = 60.0,
                    targetValue = 72.0,
                    recentMomentum = 32.0,
                    immediateMomentum = 35.0,
                    contextSignal = 18.0,
                    exactMarketLine = 57.0,
                    oddsMovement = 6.0,
                    timingFactor = 12.0,
                    riskFactor = 22.0
                )
        }

    /**
     * 👤 User chooses A / B / C.
     */
    fun makeDecision(
        state: WorldState,
        optionIndex: Int
    ): Decision? {

        if (optionIndex !in state.options.indices) {
            return null
        }

        return Decision(
            world = state.world,
            optionIndex = optionIndex,
            answer = state.options[optionIndex]
        )
    }

    /**
     * ⚡ Decision → New Information.
     *
     * This is the playable consequence layer.
     */
    fun processDecision(
        state: WorldState,
        decision: Decision
    ): Consequence {

        val selected =
            state.options
                .getOrNull(decision.optionIndex)
                ?: "Unknown decision"

        val newInformation =
            when (state.world) {

                World.ENERGY ->
                    when (decision.optionIndex) {
                        0 ->
                            "The current output is lower than expected."
                        1 ->
                            "The recent drop is accelerating."
                        else ->
                            "An external condition is affecting the output."
                    }

                World.SPACE ->
                    when (decision.optionIndex) {
                        0 ->
                            "The object is already outside its expected position."
                        1 ->
                            "The predicted path is becoming less reliable."
                        else ->
                            "Its movement has increased unexpectedly."
                    }

                World.EARTH ->
                    when (decision.optionIndex) {
                        0 ->
                            "The present condition is already outside the normal range."
                        1 ->
                            "The gap from the expected state is widening."
                        else ->
                            "Recent momentum is becoming stronger."
                    }

                World.TIME ->
                    when (decision.optionIndex) {
                        0 ->
                            "The present state contains an important constraint."
                        1 ->
                            "The expected outcome has more uncertainty than first assumed."
                        else ->
                            "Risk changes significantly with the next move."
                    }
            }

        val outcome =
            when (decision.optionIndex) {
                0 ->
                    "The decision reveals the PRESENT state."
                1 ->
                    "The decision reveals the EXPECTED path."
                else ->
                    "The decision reveals a new CONTEXT or RISK factor."
            }

        return Consequence(
            world = state.world,
            selectedOption = selected,
            newInformation = newInformation,
            outcome = outcome,
            phase = DecisionPhase.CONSEQUENCE
        )
    }

    /**
     * 🧠 Convert the Cosmos state into the existing GDMIE brain input.
     *
     * GDMEngine.kt itself remains untouched.
     */
    fun toGDMInput(
        state: WorldState,
        consequence: Consequence? = null
    ): GDMInput {

        val consequenceAdjustment =
            when {
                consequence == null -> 0.0
                consequence.selectedOption.startsWith("A") -> 0.0
                consequence.selectedOption.startsWith("B") -> 5.0
                else -> 10.0
            }

        return GDMInput(
            presentValue =
                state.presentValue,

            expectedValue =
                state.expectedValue,

            targetValue =
                state.targetValue,

            recentMomentum =
                state.recentMomentum,

            immediateMomentum =
                state.immediateMomentum +
                    consequenceAdjustment,

            twoMinMarketAdvantage =
                state.contextSignal,

            exactMarketLine =
                state.exactMarketLine,

            oddsMovement =
                state.oddsMovement,

            timingFactor =
                state.timingFactor,

            riskFactor =
                state.riskFactor
        )
    }

    /**
     * 🎯 Human-readable signal.
     *
     * The proprietary engine formula remains hidden.
     */
    fun signalFromResult(
        result: GDMResult
    ): String =
        when (result.decision) {
            "POSITIVE EDGE" -> "POSITIVE"
            "NEGATIVE EDGE" -> "CAUTION"
            else -> "MIXED"
        }

    /**
     * ⭐ XP mapping.
     *
     * Same outcome-based structure used elsewhere.
     */
    fun xpFromSignal(
        signal: String
    ): Int =
        when (signal) {
            "POSITIVE" -> 10
            "MIXED" -> 8
            else -> 5
        }

    /**
     * 🔄 Move the session forward.
     */
    fun advance(
        session: Session,
        decision: Decision? = null,
        consequence: Consequence? = null
    ): Session {

        return when {

            decision == null ->
                session.copy(
                    phase = DecisionPhase.QUESTION
                )

            consequence == null ->
                session.copy(
                    decision = decision,
                    phase = DecisionPhase.CONSEQUENCE
                )

            else ->
                session.copy(
                    decision = decision,
                    consequence = consequence,
                    phase = DecisionPhase.ANALYSIS
                )
        }
    }
}
