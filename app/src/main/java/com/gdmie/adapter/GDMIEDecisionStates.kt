package com.gdmie.adapter

enum class CurrentState {
    WEAK,
    UNCERTAIN,
    STABLE,
    STRONG
}

enum class GoalLevel {
    SMALL_IMPROVEMENT,
    MODERATE_IMPROVEMENT,
    MAJOR_IMPROVEMENT,
    LONG_TERM_TRANSFORMATION
}

enum class ContextState {
    FAVOURABLE,
    NORMAL,
    UNCERTAIN,
    UNFAVOURABLE
}

enum class MomentumState {
    IMPROVING,
    STABLE,
    DECLINING
}

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

enum class TimingState {
    NOW,
    SOON,
    LATER
}
