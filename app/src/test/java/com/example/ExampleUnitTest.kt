package com.example

import com.example.data.qubo.EngineConfig
import com.example.data.qubo.QuboPolicyEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCriminalLawPresetOptimization() {
        val solution = QuboPolicyEngine.solve(
            rules = QuboPolicyEngine.CRIMINAL_LAW_RULES,
            constraints = QuboPolicyEngine.CRIMINAL_LAW_CONSTRAINTS,
            config = EngineConfig(seed = 42L, maxIterations = 5000)
        )

        assertTrue("Criminal Law constraints must be satisfied", solution.allConstraintsSatisfied)
        assertTrue("At least 2 rules active", solution.activeCount >= 2)
        assertTrue("Budget utilization within limit", solution.totalCost <= 1000.0)
        assertEquals(5000, solution.iterations)
        assertNotNull(solution.solutionHash)
    }

    @Test
    fun testEuGdprAiActPresetOptimization() {
        val solution = QuboPolicyEngine.solve(
            rules = QuboPolicyEngine.EU_GDPR_AI_ACT_RULES,
            constraints = QuboPolicyEngine.EU_GDPR_AI_ACT_CONSTRAINTS,
            config = EngineConfig(seed = 42L, maxIterations = 5000)
        )

        assertTrue("EU GDPR/AI Act constraints must be satisfied", solution.allConstraintsSatisfied)
        assertTrue("At least 3 rules active", solution.activeCount >= 3)
        assertTrue("Budget within cap", solution.totalCost <= 1500.0)
    }

    @Test
    fun testThaiPdpaPresetOptimization() {
        val solution = QuboPolicyEngine.solve(
            rules = QuboPolicyEngine.THAI_PDPA_RULES,
            constraints = QuboPolicyEngine.THAI_PDPA_CONSTRAINTS,
            config = EngineConfig(seed = 42L, maxIterations = 5000)
        )

        assertTrue("Thai PDPA constraints must be satisfied", solution.allConstraintsSatisfied)
        assertTrue("At least 2 rules active", solution.activeCount >= 2)
        assertTrue("Budget within limit", solution.totalCost <= 800.0)
    }

    @Test
    fun testFinTechPresetOptimization() {
        val solution = QuboPolicyEngine.solve(
            rules = QuboPolicyEngine.FINTECH_RULES,
            constraints = QuboPolicyEngine.FINTECH_CONSTRAINTS,
            config = EngineConfig(seed = 42L, maxIterations = 5000)
        )

        assertTrue("FinTech constraints must be satisfied", solution.allConstraintsSatisfied)
        assertTrue("At least 4 rules active", solution.activeCount >= 4)
        assertTrue("Budget within limit", solution.totalCost <= 1500.0)
    }

    @Test
    fun testDeterminism0BitDrift() {
        val sol1 = QuboPolicyEngine.solve(
            rules = QuboPolicyEngine.CRIMINAL_LAW_RULES,
            constraints = QuboPolicyEngine.CRIMINAL_LAW_CONSTRAINTS,
            config = EngineConfig(seed = 12345L, maxIterations = 3000)
        )

        val sol2 = QuboPolicyEngine.solve(
            rules = QuboPolicyEngine.CRIMINAL_LAW_RULES,
            constraints = QuboPolicyEngine.CRIMINAL_LAW_CONSTRAINTS,
            config = EngineConfig(seed = 12345L, maxIterations = 3000)
        )

        assertEquals("Configurations must match identically", sol1.configuration, sol2.configuration)
        assertEquals("Solution hash must be identical", sol1.solutionHash, sol2.solutionHash)
        assertEquals("Qubo energy must match exactly", sol1.quboEnergy, sol2.quboEnergy, 1e-9)
    }

    @Test
    fun testIsingTransformation() {
        val q = QuboPolicyEngine.buildQUBOMatrix(
            QuboPolicyEngine.THAI_PDPA_RULES,
            QuboPolicyEngine.THAI_PDPA_CONSTRAINTS,
            1000.0
        )
        val ising = QuboPolicyEngine.quboToIsing(q)

        assertEquals("J matrix size must match rule count", QuboPolicyEngine.THAI_PDPA_RULES.size, ising.J.size)
        assertEquals("h vector size must match rule count", QuboPolicyEngine.THAI_PDPA_RULES.size, ising.h.size)
    }
}
