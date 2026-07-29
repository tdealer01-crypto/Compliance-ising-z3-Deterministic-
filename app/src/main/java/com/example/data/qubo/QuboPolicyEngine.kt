package com.example.data.qubo

import java.security.MessageDigest
import kotlin.math.exp
import kotlin.math.max

object QuboPolicyEngine {

    val FINTECH_RULES = listOf(
        PolicyRule(0, "MFA_REQUIRED", 200.0, 30.0, 50.0, "IDENTITY", "Multi-Factor Authentication for all users"),
        PolicyRule(1, "IP_WHITELIST", 100.0, 20.0, 30.0, "NETWORK", "Restrict admin access to whitelisted IPs"),
        PolicyRule(2, "RATE_LIMIT_API", 150.0, 25.0, 40.0, "NETWORK", "API gateway rate limiting against DDoS"),
        PolicyRule(3, "ENCRYPTION_AT_REST", 300.0, 40.0, 60.0, "DATA_PROTECTION", "AES-256 encryption for database and storage"),
        PolicyRule(4, "ENCRYPTION_IN_TRANSIT", 250.0, 35.0, 55.0, "DATA_PROTECTION", "TLS 1.3 enforcement on all endpoints"),
        PolicyRule(5, "AUDIT_LOG_ALL", 180.0, 15.0, 35.0, "MONITORING", "Immutable audit trail logging"),
        PolicyRule(6, "DLP_EMAIL", 220.0, 20.0, 25.0, "DATA_PROTECTION", "Data Loss Prevention scanner on outgoing mail"),
        PolicyRule(7, "ZERO_TRUST_NETWORK", 500.0, 50.0, 80.0, "NETWORK", "Micro-segmentation and continuous verification"),
        PolicyRule(8, "SOC2_COMPLIANCE", 400.0, 35.0, 70.0, "COMPLIANCE", "SOC2 Type II certification framework"),
        PolicyRule(9, "PENETRATION_TEST_QUARTERLY", 350.0, 30.0, 45.0, "COMPLIANCE", "Third-party security audit and pen testing"),
        PolicyRule(10, "INCIDENT_RESPONSE_PLAN", 120.0, 10.0, 20.0, "INCIDENT_RESPONSE", "Documented and tested IR playbook"),
        PolicyRule(11, "EMPLOYEE_SECURITY_TRAINING", 80.0, 15.0, 25.0, "TRAINING", "Quarterly anti-phishing training")
    )

    val FINTECH_CONSTRAINTS = listOf<Constraint>(
        Constraint.Implication(7, 0, "Zero Trust requires MFA"),
        Constraint.Equivalence(listOf(3, 4), "Encryption must be both at-rest and in-transit"),
        Constraint.Implication(8, 5, "SOC2 requires Audit Logging"),
        Constraint.MinActive(4, "At least 4 security controls required"),
        Constraint.MaxCost(1500.0, "Annual security budget $1,500"),
        Constraint.Implication(9, 10, "Pen Testing requires Incident Response Plan")
    )

    // Thai Criminal Law (ประมวลกฎหมายอาญาเบื้องต้น) Policy Rules & Z3 Constraints
    val CRIMINAL_LAW_RULES = listOf(
        PolicyRule(0, "COMMITTED_ACT", 100.0, 10.0, 20.0, "COMPLIANCE", "การกระทำครบองค์ประกอบภายนอก (Actus Reus)"),
        PolicyRule(1, "INTENTIONAL_M59", 200.0, 10.0, 30.0, "COMPLIANCE", "กระทำโดยเจตนา (ม.59) รู้สำนึกและประสงค์ต่อผล"),
        PolicyRule(2, "NEGLIGENT_M59", 150.0, 10.0, 20.0, "COMPLIANCE", "กระทำโดยประมาท (ม.59 วรรค 4) ปราศจากความระมัดระวัง"),
        PolicyRule(3, "SELF_DEFENSE_M68", 50.0, 80.0, 90.0, "INCIDENT_RESPONSE", "ป้องกันโดยชอบด้วยกฎหมาย (ม.68) -> ไม่มีความผิด"),
        PolicyRule(4, "NECESSITY_M67", 80.0, 60.0, 70.0, "INCIDENT_RESPONSE", "กระทำด้วยความจำเป็น (ม.67) -> มีความผิดแต่ไม่ต้องรับโทษ"),
        PolicyRule(5, "PROVOCATION_M72", 70.0, 40.0, 50.0, "INCIDENT_RESPONSE", "บันดาลโทสะ (ม.72) ถูกข่มเหงอย่างร้ายแรง -> ศาลลดโทษ"),
        PolicyRule(6, "COMPENSATION_M78", 100.0, 30.0, 60.0, "TRAINING", "เหตุบรรเทาโทษ (ม.78) ชดใช้ค่าเสียหายและสำนึกผิด -> ลดโทษ"),
        PolicyRule(7, "INFANT_UNDER_12_M73", 30.0, 90.0, 80.0, "COMPLIANCE", "เด็กอายุไม่เกิน 12 ปี (ม.73) -> ไม่ต้องรับโทษ")
    )

    val CRIMINAL_LAW_CONSTRAINTS = listOf<Constraint>(
        Constraint.MutualExclusion(listOf(1, 2), "เจตนา (ม.59) และ ประมาท (ม.59ว4) แยกจากกันเด็ดขาด"),
        Constraint.Implication(3, 0, "การอ้างป้องกันชอบด้วยกฎหมาย (ม.68) ต้องมีการกระทำเกิดขึ้นก่อน"),
        Constraint.MutualExclusion(listOf(3, 4), "เหตุยกเว้นความผิด (ม.68) และ เหตุยกเว้นโทษ (ม.67) ไม่เกิดซ้อนกัน"),
        Constraint.MinActive(2, "ต้องพิจารณาองค์ประกอบการกระทำและเจตนาอย่างน้อย 2 เงื่อนไข"),
        Constraint.MaxCost(1000.0, "เพดานงบประมาณประเมินโทษ/ความเสี่ยงทางกฎหมาย $1,000")
    )

    fun buildQUBOMatrix(
        rules: List<PolicyRule>,
        constraints: List<Constraint>,
        penaltyWeight: Double
    ): List<List<Double>> {
        val n = rules.size
        val q = Array(n) { DoubleArray(n) { 0.0 } }

        // Objective: maximize (value + riskReduction) -> minimize negative
        for (i in 0 until n) {
            q[i][i] = -(rules[i].businessValue + rules[i].riskReduction)
        }

        val p = penaltyWeight
        for (c in constraints) {
            when (c) {
                is Constraint.Implication -> {
                    val a = c.ifRule
                    val b = c.thenRule
                    if (a in 0 until n && b in 0 until n) {
                        q[a][a] += p
                        q[a][b] += -p
                    }
                }
                is Constraint.Equivalence -> {
                    val group = c.rules
                    for (i in 0 until group.size - 1) {
                        val a = group[i]
                        val b = group[i + 1]
                        if (a in 0 until n && b in 0 until n) {
                            q[a][a] += p
                            q[b][b] += p
                            q[a][b] += -2 * p
                        }
                    }
                }
                is Constraint.MutualExclusion -> {
                    val group = c.rules
                    for (i in group.indices) {
                        for (j in i + 1 until group.size) {
                            val a = group[i]
                            val b = group[j]
                            if (a in 0 until n && b in 0 until n) {
                                q[a][b] += p
                            }
                        }
                    }
                }
                is Constraint.AtLeastOneOf -> {
                    val group = c.rules
                    for (idx in group) {
                        if (idx in 0 until n) {
                            q[idx][idx] += -p * 0.1
                        }
                    }
                }
                is Constraint.MinActive, is Constraint.MaxCost -> {}
            }
        }

        return q.map { it.toList() }
    }

    fun computeEnergy(x: List<Int>, q: List<List<Double>>): Double {
        val n = x.size
        var energy = 0.0
        for (i in 0 until n) {
            for (j in 0 until n) {
                energy += q[i][j] * x[i] * x[j]
            }
        }
        return energy
    }

    fun computeEnergyDelta(x: List<Int>, q: List<List<Double>>, site: Int): Double {
        val n = x.size
        val flip = if (x[site] == 0) 1 else -1
        var delta = q[site][site] * flip

        for (j in 0 until n) {
            if (j != site) {
                delta += (q[site][j] + q[j][site]) * x[j] * flip
            }
        }
        return delta
    }

    fun verifyConstraints(
        x: List<Int>,
        rules: List<PolicyRule>,
        constraints: List<Constraint>
    ): List<ConstraintResult> {
        val results = mutableListOf<ConstraintResult>()

        for (c in constraints) {
            var satisfied = false
            var detail = ""

            when (c) {
                is Constraint.Implication -> {
                    val a = c.ifRule
                    val b = c.thenRule
                    satisfied = (x.getOrElse(a) { 0 } == 0 || x.getOrElse(b) { 0 } == 1)
                    val nameA = rules.getOrNull(a)?.name ?: "Rule #$a"
                    val nameB = rules.getOrNull(b)?.name ?: "Rule #$b"
                    detail = if (satisfied) "$nameA → $nameB: OK" else "$nameA active but $nameB inactive"
                }
                is Constraint.Equivalence -> {
                    val group = c.rules
                    val firstVal = if (group.isNotEmpty()) x.getOrElse(group[0]) { 0 } else 0
                    satisfied = group.all { idx -> x.getOrElse(idx) { 0 } == firstVal }
                    val vals = group.joinToString(", ") { idx -> "${rules.getOrNull(idx)?.name ?: idx}=${x.getOrElse(idx){0}}" }
                    detail = if (satisfied) "Equivalence met: $vals" else "Mismatch: $vals"
                }
                is Constraint.MinActive -> {
                    val activeCount = x.sum()
                    satisfied = activeCount >= c.minimum
                    detail = "Active=$activeCount, Required≥${c.minimum}"
                }
                is Constraint.MaxCost -> {
                    val totalCost = x.indices.sumOf { i -> x[i] * (rules.getOrNull(i)?.cost ?: 0.0) }
                    satisfied = totalCost <= c.budget
                    detail = "Cost=$$totalCost, Budget=$$${c.budget}"
                }
                is Constraint.MutualExclusion -> {
                    val group = c.rules
                    val activeCount = group.sumOf { idx -> x.getOrElse(idx) { 0 } }
                    satisfied = activeCount <= 1
                    val activeNames = group.filter { idx -> x.getOrElse(idx) { 0 } == 1 }.map { idx -> rules.getOrNull(idx)?.name ?: "$idx" }
                    detail = if (satisfied) "At most 1 active: ${activeNames.ifEmpty { listOf("none") }}" else "$activeCount active (max 1): $activeNames"
                }
                is Constraint.AtLeastOneOf -> {
                    val group = c.rules
                    val activeCount = group.sumOf { idx -> x.getOrElse(idx) { 0 } }
                    satisfied = activeCount >= 1
                    detail = if (satisfied) "$activeCount active in group" else "None active in required group"
                }
            }

            val fullDetail = if (c.description.isNotBlank()) "[${c.description}] $detail" else detail
            results.add(ConstraintResult(c, satisfied, fullDetail))
        }

        return results
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun computeEventHash(
        sequence: Int,
        site: Int,
        proposed: String,
        accepted: Boolean,
        reason: String,
        energy: Double,
        deltaEnergy: Double,
        temperature: Double,
        state: List<Int>,
        prevHash: String
    ): String {
        val payload = """{"sequence":$sequence,"site":$site,"proposed":"$proposed","accepted":$accepted,"reason":"$reason","energy":$energy,"deltaEnergy":$deltaEnergy,"temperature":$temperature,"state":$state,"prevHash":"$prevHash"}"""
        return sha256(payload)
    }

    fun solve(
        rules: List<PolicyRule>,
        constraints: List<Constraint>,
        config: EngineConfig
    ): QuboSolution {
        val n = rules.size
        val rng = DeterministicRNG(config.seed)
        val q = buildQUBOMatrix(rules, constraints, config.penaltyWeight)

        val maxCostConstraint = constraints.filterIsInstance<Constraint.MaxCost>().firstOrNull()
        val budget = maxCostConstraint?.budget ?: Double.MAX_VALUE

        // Initial state by efficiency (value+risk)/cost
        val sortedByEfficiency = rules.sortedByDescending { (it.businessValue + it.riskReduction) / max(it.cost, 1.0) }
        val x = MutableList(n) { 0 }
        var runningCost = 0.0
        for (r in sortedByEfficiency) {
            if (runningCost + r.cost <= budget) {
                x[r.id] = 1
                runningCost += r.cost
            }
        }

        var currentEnergy = computeEnergy(x, q)
        var bestX = x.toList()
        var bestEnergy = currentEnergy
        var temp = config.initialTemperature

        val events = mutableListOf<EvolutionEvent>()
        var prevHash = sha256("""{"genesis":$x,"seed":${config.seed}}""")

        for (step in 0 until config.maxIterations) {
            val site = if (step % n != 0) step % n else rng.nextInt(n)
            val proposed = if (x[site] == 0) "ACTIVATE" else "DEACTIVATE"

            val newCost = x.indices.sumOf { i -> x[i] * rules[i].cost } +
                    (if (proposed == "ACTIVATE") rules[site].cost else -rules[site].cost)

            var accepted = false
            var reason = ""
            var deltaEnergy = 0.0

            if (newCost > budget) {
                reason = "BUDGET_EXCEEDED"
            } else {
                val xNew = x.toMutableList()
                xNew[site] = 1 - xNew[site]

                // Z3 pre-check structural constraints
                val preChecks = verifyConstraints(
                    xNew, rules,
                    constraints.filter { it is Constraint.Implication || it is Constraint.Equivalence || it is Constraint.MutualExclusion }
                )
                if (preChecks.any { !it.satisfied }) {
                    reason = "CONSTRAINT_VIOLATION"
                } else {
                    deltaEnergy = computeEnergyDelta(x, q, site)
                    if (deltaEnergy <= 0) {
                        accepted = true
                        reason = "ENERGY_LOWER"
                    } else {
                        val prob = exp(-deltaEnergy / max(temp, 0.0001))
                        if (rng.next() < prob) {
                            accepted = true
                            reason = "METROPOLIS_ACCEPT"
                        } else {
                            reason = "METROPOLIS_REJECT"
                        }
                    }
                }
            }

            if (accepted) {
                x[site] = 1 - x[site]
                currentEnergy += deltaEnergy
            }

            val timestamp = "2026-07-29T00:00:00Z"
            val hash = computeEventHash(step, site, proposed, accepted, reason, currentEnergy, deltaEnergy, temp, x, prevHash)

            events.add(
                EvolutionEvent(
                    sequence = step,
                    timestamp = timestamp,
                    site = site,
                    proposed = proposed,
                    accepted = accepted,
                    reason = reason,
                    energy = currentEnergy,
                    deltaEnergy = deltaEnergy,
                    temperature = temp,
                    state = x.toList(),
                    prevHash = prevHash,
                    hash = hash
                )
            )
            prevHash = hash

            if (accepted && currentEnergy < bestEnergy) {
                val fullCheck = verifyConstraints(x, rules, constraints)
                if (fullCheck.all { it.satisfied }) {
                    bestX = x.toList()
                    bestEnergy = currentEnergy
                }
            }

            temp *= config.coolingRate
            if (temp < config.minTemperature) break
        }

        val finalVerification = verifyConstraints(bestX, rules, constraints)
        val allSatisfied = finalVerification.all { it.satisfied }

        val activeRules = rules.filterIndexed { i, _ -> bestX.getOrElse(i) { 0 } == 1 }
        val inactiveRules = rules.filterIndexed { i, _ -> bestX.getOrElse(i) { 0 } == 0 }

        val totalCost = activeRules.sumOf { it.cost }
        val totalRisk = activeRules.sumOf { it.riskReduction }
        val totalValue = activeRules.sumOf { it.businessValue }

        val solutionHash = sha256("""{"config":$bestX,"energy":$bestEnergy,"seed":${config.seed},"iterations":${events.size}}""")

        return QuboSolution(
            configuration = bestX,
            activeRules = activeRules,
            inactiveRules = inactiveRules,
            totalCost = totalCost,
            totalRiskReduction = totalRisk,
            totalBusinessValue = totalValue,
            quboEnergy = bestEnergy,
            activeCount = activeRules.size,
            budgetUtilization = if (budget == Double.MAX_VALUE || budget == 0.0) 0.0 else totalCost / budget,
            allConstraintsSatisfied = allSatisfied,
            constraintResults = finalVerification,
            seed = config.seed,
            iterations = events.size,
            solutionHash = solutionHash,
            chainLength = events.size
        )
    }

    fun counterfactual(
        rules: List<PolicyRule>,
        originalConstraints: List<Constraint>,
        alteredConstraints: List<Constraint>,
        config: EngineConfig
    ): CounterfactualResult {
        val orig = solve(rules, originalConstraints, config)
        val alt = solve(rules, alteredConstraints, config)

        val rulesChanged = mutableListOf<String>()
        for (i in rules.indices) {
            val v1 = orig.configuration.getOrElse(i) { 0 }
            val v2 = alt.configuration.getOrElse(i) { 0 }
            if (v1 != v2) {
                val action = if (v2 == 1) "ACTIVATED" else "DEACTIVATED"
                rulesChanged.add("${rules[i].name}: $action")
            }
        }

        return CounterfactualResult(
            originalSolution = orig,
            alteredSolution = alt,
            diverges = rulesChanged.isNotEmpty(),
            costDelta = alt.totalCost - orig.totalCost,
            riskDelta = alt.totalRiskReduction - orig.totalRiskReduction,
            valueDelta = alt.totalBusinessValue - orig.totalBusinessValue,
            rulesChanged = rulesChanged
        )
    }

    fun quboToIsing(q: List<List<Double>>): IsingModel {
        val n = q.size
        val jMatrix = List(n) { MutableList(n) { 0.0 } }
        val hVector = MutableList(n) { 0.0 }
        var offset = 0.0

        for (i in 0 until n) {
            for (j in i + 1 until n) {
                val valJ = (q[i][j] + q[j][i]) / 4.0
                jMatrix[i][j] = valJ
                jMatrix[j][i] = valJ
            }
        }

        for (i in 0 until n) {
            hVector[i] = q[i][i] / 2.0
            for (j in 0 until n) {
                if (j != i) {
                    hVector[i] += (q[i][j] + q[j][i]) / 4.0
                }
            }
        }

        for (i in 0 until n) {
            offset += q[i][i] / 4.0
            for (j in i + 1 until n) {
                offset += (q[i][j] + q[j][i]) / 4.0
            }
        }

        return IsingModel(jMatrix.map { it.toList() }, hVector.toList(), offset)
    }
}
