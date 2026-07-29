package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Send
import com.example.data.mcp.McpDispatchResult
import com.example.data.mcp.McpEndpointType
import com.example.data.mcp.McpGatewayEngine
import com.example.data.qubo.CounterfactualResult
import com.example.data.qubo.PolicyRule
import com.example.data.qubo.QuboSolution

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuboOptimizerSheet(
    solution: QuboSolution?,
    counterfactualResult: CounterfactualResult?,
    currentBudget: Double,
    activePreset: String,
    isOptimizing: Boolean,
    mcpDispatchResults: Map<McpEndpointType, McpDispatchResult> = emptyMap(),
    isMcpDispatching: Boolean = false,
    onSwitchPreset: (String) -> Unit,
    onBudgetChange: (Double) -> Unit,
    onRunCounterfactual: (Double) -> Unit,
    onApplyToTasks: () -> Unit,
    onDispatchMcp: (McpEndpointType) -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("qubo_optimizer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "QUBO Engine",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column {
                        Text(
                            text = "QUBO Policy Optimizer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Deterministic Ising Simulated Annealing",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_qubo_sheet_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Policy Model Preset Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = activePreset == "CRIMINAL_LAW",
                    onClick = { onSwitchPreset("CRIMINAL_LAW") },
                    label = { Text("⚖️ อาญา", fontSize = 11.sp) },
                    modifier = Modifier.testTag("preset_criminal_law")
                )
                FilterChip(
                    selected = activePreset == "EU_GDPR_AI",
                    onClick = { onSwitchPreset("EU_GDPR_AI") },
                    label = { Text("🇪🇺 EU AI/GDPR", fontSize = 11.sp) },
                    modifier = Modifier.testTag("preset_eu_gdpr_ai")
                )
                FilterChip(
                    selected = activePreset == "THAI_PDPA",
                    onClick = { onSwitchPreset("THAI_PDPA") },
                    label = { Text("🇹🇭 PDPA", fontSize = 11.sp) },
                    modifier = Modifier.testTag("preset_thai_pdpa")
                )
                FilterChip(
                    selected = activePreset == "FINTECH",
                    onClick = { onSwitchPreset("FINTECH") },
                    label = { Text("🔒 FinTech", fontSize = 11.sp) },
                    modifier = Modifier.testTag("preset_fintech")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isOptimizing || solution == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Executing QUBO Matrix Optimization...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Quick Status Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (solution.allConstraintsSatisfied)
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                                else
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (solution.allConstraintsSatisfied) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = "Z3 Verified Status",
                                tint = if (solution.allConstraintsSatisfied) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = if (solution.allConstraintsSatisfied) "Z3 Formal Constraints: SATISFIED" else "Z3 Constraints: VIOLATED",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (solution.allConstraintsSatisfied) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Seed: ${solution.seed}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Budget Selector Row
                    Column {
                        Text(
                            text = "Annual Budget Constraint",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1000.0, 1500.0, 2000.0, 2500.0).forEach { budget ->
                                FilterChip(
                                    selected = currentBudget == budget,
                                    onClick = { onBudgetChange(budget) },
                                    label = { Text("$$budget") },
                                    modifier = Modifier.testTag("budget_chip_${budget.toInt()}")
                                )
                            }
                        }
                    }

                    // Key Metrics Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox(
                            title = "Total Cost",
                            value = "$${solution.totalCost.toInt()}",
                            sub = "Limit: $$currentBudget",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Risk Reduction",
                            value = "${solution.totalRiskReduction.toInt()}%",
                            sub = "Active: ${solution.activeCount} rules",
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "QUBO Energy",
                            value = "%.1f".format(solution.quboEnergy),
                            sub = "Iterations: ${solution.iterations}",
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Budget utilization bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Budget Utilization",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(solution.budgetUtilization * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { solution.budgetUtilization.toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // Tabs (Active Rules / Z3 Verification / What-If Analysis / Audit Chain)
                    Column {
                        PrimaryTabRow(
                            selectedTabIndex = selectedTab,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("Active (${solution.activeCount})") }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("Z3 Verification") }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                text = { Text("What-If?") }
                            )
                            Tab(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3 },
                                text = { Text("Audit Chain") }
                            )
                            Tab(
                                selected = selectedTab == 4,
                                onClick = { selectedTab = 4 },
                                text = { Text("🔌 MCP Gateway") }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (selectedTab) {
                            0 -> ActiveRulesList(solution.activeRules, solution.inactiveRules)
                            1 -> Z3VerificationList(solution.constraintResults)
                            2 -> CounterfactualSection(
                                currentBudget = currentBudget,
                                result = counterfactualResult,
                                onRun = onRunCounterfactual
                            )
                            3 -> AuditChainSection(solution)
                            4 -> McpGatewaySection(
                                solution = solution,
                                mcpDispatchResults = mcpDispatchResults,
                                isMcpDispatching = isMcpDispatching,
                                onDispatchMcp = onDispatchMcp
                            )
                        }
                    }
                }

                // Bottom Action
                Button(
                    onClick = onApplyToTasks,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("apply_qubo_solution_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Apply to tasks")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply ${solution.activeCount} Policy Rules to Action Engine")
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = sub, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ActiveRulesList(
    activeRules: List<PolicyRule>,
    inactiveRules: List<PolicyRule>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "ACTIVATED POLICY CONTROLS",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        activeRules.forEach { rule ->
            RuleItemCard(rule = rule, isActive = true)
        }

        if (inactiveRules.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "INACTIVE POLICY CONTROLS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            inactiveRules.forEach { rule ->
                RuleItemCard(rule = rule, isActive = false)
            }
        }
    }
}

@Composable
private fun RuleItemCard(rule: PolicyRule, isActive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = rule.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${rule.cost.toInt()}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Risk -${rule.riskReduction.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun Z3VerificationList(results: List<com.example.data.qubo.ConstraintResult>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        results.forEach { res ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (res.satisfied)
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (res.satisfied) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = "Constraint status",
                        tint = if (res.satisfied) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = res.detail,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun CounterfactualSection(
    currentBudget: Double,
    result: CounterfactualResult?,
    onRun: (Double) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "What-If Budget Simulator",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Compare policy choices against budget variances dynamically.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { onRun(currentBudget - 300) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Test -$300 Budget")
            }
            Button(
                onClick = { onRun(currentBudget + 500) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Test +$500 Budget")
            }
        }

        if (result != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Simulation Impact:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "• Cost Delta: $${result.costDelta.toInt()}")
                    Text(text = "• Risk Reduction Delta: ${result.riskDelta.toInt()}%")
                    Text(text = "• Business Value Delta: ${result.valueDelta.toInt()}")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Rule Changes: ${result.rulesChanged.joinToString(", ").ifEmpty { "None" }}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AuditChainSection(solution: QuboSolution) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "PROVENANCE & AUDIT HASH CHAIN",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Solution Hash: ${solution.solutionHash}",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )
        Text(
            text = "Chain Length: ${solution.chainLength} SHA-256 Event Blocks",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp
        )
        Text(
            text = "Deterministic Seed: ${solution.seed}",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun McpGatewaySection(
    solution: QuboSolution,
    mcpDispatchResults: Map<McpEndpointType, McpDispatchResult>,
    isMcpDispatching: Boolean,
    onDispatchMcp: (McpEndpointType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "Model Context Protocol (MCP) Endpoints",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Export formal Z3 proof payloads & QUBO solution states to industry standard AI & cloud endpoints.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        McpGatewayEngine.AVAILABLE_ENDPOINTS.forEach { endpoint ->
            val result = mcpDispatchResults[endpoint]

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (result?.success == true)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cable,
                                contentDescription = "MCP Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = endpoint.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = endpoint.providerName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (result?.success == true) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Synced",
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "MCP SYNCED 200",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Endpoint URL: ${endpoint.defaultUrl}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (result != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "✓ ${result.statusMessage}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Payload Hash: ${result.payloadHash.take(24)}...",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "Sync Time: ${result.timestamp}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { onDispatchMcp(endpoint) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isMcpDispatching,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isMcpDispatching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dispatching MCP Payload...", fontSize = 12.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Sync",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (result != null) "Re-Sync to ${endpoint.displayName}" else "Sync via MCP Protocol v2.0",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
